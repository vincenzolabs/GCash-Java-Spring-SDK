/*
 * Copyright (c) 2023 VincenzoLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.vincenzolabs.gcash.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.vincenzolabs.gcash.dto.AccessTokenRequest;
import org.vincenzolabs.gcash.dto.AccessTokenResponse;
import org.vincenzolabs.gcash.dto.PaymentInquiryRequest;
import org.vincenzolabs.gcash.dto.PaymentInquiryResponse;
import org.vincenzolabs.gcash.dto.PaymentNotificationRequest;
import org.vincenzolabs.gcash.dto.PaymentNotificationResponse;
import org.vincenzolabs.gcash.dto.PaymentRequest;
import org.vincenzolabs.gcash.dto.PaymentResponse;
import org.vincenzolabs.gcash.dto.RefundInquiryRequest;
import org.vincenzolabs.gcash.dto.RefundInquiryResponse;
import org.vincenzolabs.gcash.dto.RefundRequest;
import org.vincenzolabs.gcash.dto.RefundResponse;
import org.vincenzolabs.gcash.dto.Result;
import org.vincenzolabs.gcash.dto.UserInformationResponse;
import org.vincenzolabs.gcash.exception.ApiException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The GCash v1 client.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Component
@Slf4j
public class GCashV1Client {

    private static final String ACCESS_TOKEN_PATH = "/v1/authorizations/applyToken";

    private static final String ACCESS_TOKEN_CANCELLATION_PATH = "/v1/authorizations/cancelToken";

    private static final String PAYMENT_PATH = "/v1/payments/pay";

    private static final String PAYMENT_INQUIRY_PATH = "/v1/payments/inquiryPayment";

    private static final String PAYMENT_NOTIFICATION_PATH = "/v1/payments/notifyPayment";

    private static final String REFUND_PATH = "/v1/payments/refund";

    private static final String REFUND_INQUIRY_PATH = "/v1/payments/inquiryRefund";

    private static final String USER_INFORMATION_INQUIRY_PATH = "/v1/customers/user/inquiryUserInfoByAccessToken";

    private final ObjectMapper objectMapper;

    @Value("${gcash.signing.publicKey:}")
    private String publicKey;

    @Value("${gcash.signing.privateKey:}")
    private String privateKey;

    @Value("${gcash.signing.keyVersion:0}")
    private String keyVersion;

    @Value("${gcash.signing.algorithm:RSA256}")
    private String algorithm;

    @Value("${gcash.paymentGatewayUrl:}")
    private String paymentGatewayUrl;

    @Value("${gcash.clientId:}")
    private String clientId;

    @Value("${gcash.zoneId:Asia/Manila}")
    private String zoneId;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private WebClient webClient;

    /**
     * Default constructor.
     *
     * @param objectMapper the {@link ObjectMapper}
     */
    @Autowired
    public GCashV1Client(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Requests for an access token.
     *
     * @param accessTokenRequest the {@link AccessTokenRequest}
     * @return the {@link Mono} containing the {@link AccessTokenResponse}
     */
    public Mono<AccessTokenResponse> applyAccessToken(AccessTokenRequest accessTokenRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(accessTokenRequest);
        String signedPayload = sign(ACCESS_TOKEN_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(ACCESS_TOKEN_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(AccessTokenResponse.class, ACCESS_TOKEN_PATH));
    }

    /**
     * Cancels/revokes the access token.
     *
     * @param accessToken the access token
     * @param extendInfo  the extended information/metadata
     * @return the {@link Mono} containing the {@link Result}
     */
    public Mono<Result> cancelAccessToken(final String accessToken, final String extendInfo) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(Map.of("accessToken", accessToken, "extendInfo", extendInfo));
        String signedPayload = sign(ACCESS_TOKEN_CANCELLATION_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(ACCESS_TOKEN_CANCELLATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(Result.class, ACCESS_TOKEN_CANCELLATION_PATH));
    }

    /**
     * Creates a payment.
     *
     * @param paymentRequest the {@link PaymentRequest}
     * @return the {@link Mono} containing the {@link PaymentResponse}
     */
    public Mono<PaymentResponse> createPayment(PaymentRequest paymentRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(paymentRequest);
        String signedPayload = sign(PAYMENT_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(PAYMENT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(PaymentResponse.class, PAYMENT_PATH));
    }

    /**
     * Retrieves a payment.
     *
     * @param paymentInquiryRequest the {@link PaymentInquiryRequest}
     * @return the {@link Mono} containing the {@link PaymentInquiryResponse}
     */
    public Mono<PaymentInquiryResponse> retrievePayment(PaymentInquiryRequest paymentInquiryRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(paymentInquiryRequest);
        String signedPayload = sign(PAYMENT_INQUIRY_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(PAYMENT_INQUIRY_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(PaymentInquiryResponse.class, PAYMENT_INQUIRY_PATH));
    }

    /**
     * Retrieves a payment notification.
     *
     * @param paymentNotificationRequest the {@link PaymentNotificationRequest}
     * @return the {@link Mono} containing the {@link PaymentNotificationResponse}
     */
    public Mono<PaymentNotificationResponse> retrievePaymentNotification(PaymentNotificationRequest paymentNotificationRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(paymentNotificationRequest);
        String signedPayload = sign(PAYMENT_NOTIFICATION_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(PAYMENT_NOTIFICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(PaymentNotificationResponse.class, PAYMENT_NOTIFICATION_PATH));
    }

    /**
     * Creates a refund.
     *
     * @param refundRequest the {@link RefundRequest}
     * @return the {@link Mono} containing the {@link RefundResponse}
     */
    public Mono<RefundResponse> createRefund(RefundRequest refundRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(refundRequest);
        String signedPayload = sign(REFUND_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(REFUND_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(RefundResponse.class, REFUND_PATH));
    }

    /**
     * Retrieves a refund.
     *
     * @param refundInquiryRequest the {@link RefundInquiryRequest}
     * @return the {@link Mono} containing the {@link RefundInquiryResponse}
     */
    public Mono<RefundInquiryResponse> retrieveRefund(RefundInquiryRequest refundInquiryRequest) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(refundInquiryRequest);
        String signedPayload = sign(REFUND_INQUIRY_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(REFUND_INQUIRY_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(RefundInquiryResponse.class, REFUND_INQUIRY_PATH));
    }

    /**
     * Retrieves the user information.
     *
     * @param accessToken the access token
     * @param extendInfo  the extended information/metadata
     * @return the {@link Mono} containing the {@link UserInformationResponse}
     */
    public Mono<UserInformationResponse> retrieveUserInformation(final String accessToken, final String extendInfo) {
        String requestTime = OffsetDateTime.now(ZoneId.of(zoneId)).toString();
        String payload = serialize(Map.of("accessToken", accessToken, "extendInfo", extendInfo));
        String signedPayload = sign(USER_INFORMATION_INQUIRY_PATH, requestTime, payload);
        String signature = "algorithm=" + algorithm + ", keyVersion=" + keyVersion + ", signature=" + signedPayload;

        return getWebClient()
                .post()
                .uri(USER_INFORMATION_INQUIRY_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add("Signature", signature);
                    httpHeaders.add("Client-Id", clientId);
                    httpHeaders.add("Request-Time", requestTime);
                })
                .bodyValue(payload)
                .exchangeToMono(getResponseMono(UserInformationResponse.class, USER_INFORMATION_INQUIRY_PATH));
    }

    private WebClient getWebClient() {
        if (webClient != null) {
            return webClient;
        }

        boolean debugMode = Pattern.compile("local|dev|test").matcher(activeProfile).matches();
        if (debugMode) {
            HttpClient httpClient = HttpClient.create()
                    .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

            return WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(paymentGatewayUrl)
                    .build();
        }

        return WebClient.builder()
                .baseUrl(paymentGatewayUrl)
                .build();
    }

    private <T> Function<ClientResponse, Mono<T>> getResponseMono(Class<T> clazz, String path) {
        return clientResponse -> {
            if (clientResponse.statusCode().is2xxSuccessful()) {
                ClientResponse.Headers headers = clientResponse.headers();

                String clientId = headers.asHttpHeaders().getFirst("Client-Id");
                if (StringUtils.isBlank(clientId)) {
                    throw new ApiException(HttpStatus.BAD_GATEWAY, "", "Client-Id response header is missing", null);
                }

                String responseTime = headers.asHttpHeaders().getFirst("Response-Time");
                if (StringUtils.isBlank(responseTime)) {
                    throw new ApiException(HttpStatus.BAD_GATEWAY, "", "Response-Time response header is missing", null);
                }

                String signature = headers.asHttpHeaders().getFirst("Signature");
                if (StringUtils.isBlank(signature)) {
                    throw new ApiException(HttpStatus.BAD_GATEWAY, "", "Signature response header is missing", null);
                }

                String[] pairs = signature.split(",");
                Optional<String> optionalSignedPayload = Arrays.stream(pairs)
                        .filter(s -> s.trim().startsWith("signature="))
                        .findFirst();
                String signedPayload = optionalSignedPayload
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_GATEWAY, "", "Signature is missing", null))
                        .replace("signature=", "");

                return clientResponse
                        .bodyToMono(String.class)
                        .flatMap(s -> {
                            boolean verified = verify(path, responseTime, s, signedPayload);

                            if (verified) {
                                try {
                                    return Mono.just(objectMapper.readValue(s, clazz));
                                } catch (JsonProcessingException e) {
                                    return Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "",
                                            "Failed to deserialize response payload", null));
                                }
                            }

                            return Mono.error(new ApiException(clientResponse.statusCode(), "",
                                    "Signature verification failed", null));
                        });
            } else if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(Result.class)
                        .switchIfEmpty(Mono.error(new ApiException(clientResponse.statusCode(), null, null, null)))
                        .flatMap(body -> Mono.error(new ApiException(clientResponse.statusCode(), body.getResultStatus(),
                                body.getResultMessage(), null)));
            } else {
                return clientResponse
                        .createException()
                        .flatMap(Mono::error);
            }
        };
    }

    private String sign(String path, String requestTime, String payload) {
        String contentToBeSigned = "POST " + path + "\n" + clientId + "." + requestTime + "." + payload;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace(System.lineSeparator(), "");
            byte[] bytes = Base64.decodeBase64(privateKey);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            bytes = signature.sign();

            return Base64.encodeBase64URLSafeString(bytes);
        } catch (GeneralSecurityException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "", "Failed to sign request payload", e);
        }
    }

    private boolean verify(String path, String time, String payload, String signedPayload) {
        String contentToBeValidated = "POST " + path + "\n" + clientId + "." + time + "." + payload;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace(System.lineSeparator(), "");
            byte[] bytes = Base64.decodeBase64(publicKey);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            PublicKey key = keyFactory.generatePublic(spec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(key);
            signature.update(contentToBeValidated.getBytes(StandardCharsets.UTF_8));

            return signature.verify(signedPayload.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "", "Failed to verify response payload", e);
        }
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "", "Failed to serialize request payload", e);
        }
    }
}
