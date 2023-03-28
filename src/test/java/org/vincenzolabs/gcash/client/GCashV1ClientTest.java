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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.vincenzolabs.gcash.dto.AccessTokenRequest;
import org.vincenzolabs.gcash.dto.AccessTokenResponse;
import org.vincenzolabs.gcash.dto.ActionForm;
import org.vincenzolabs.gcash.dto.Amount;
import org.vincenzolabs.gcash.dto.ContactInfo;
import org.vincenzolabs.gcash.dto.EnvInfo;
import org.vincenzolabs.gcash.dto.OpenLoginIdInfo;
import org.vincenzolabs.gcash.dto.OpenUserInfo;
import org.vincenzolabs.gcash.dto.PaymentFactor;
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
import org.vincenzolabs.gcash.dto.UserName;
import org.vincenzolabs.gcash.enumeration.ActionFormType;
import org.vincenzolabs.gcash.enumeration.GrantType;
import org.vincenzolabs.gcash.enumeration.LoginIdType;
import org.vincenzolabs.gcash.enumeration.OsType;
import org.vincenzolabs.gcash.enumeration.PaymentStatus;
import org.vincenzolabs.gcash.enumeration.RefundStatus;
import org.vincenzolabs.gcash.enumeration.TerminalType;
import org.vincenzolabs.gcash.enumeration.UserStatus;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * The test case for {@link GCashV1Client}.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class GCashV1ClientTest {

    private static final String PRIVATE_KEY = """
            -----BEGIN PRIVATE KEY-----
            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
            DBQOvgHEcKl2BI2wAehA+qpOZVd0Bog=
            -----END PRIVATE KEY-----
            """;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Spy
    private ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private GCashV1Client client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "webClient", webClient);
        ReflectionTestUtils.setField(client, "publicKey", "publicKey");
        ReflectionTestUtils.setField(client, "privateKey", PRIVATE_KEY);
        ReflectionTestUtils.setField(client, "keyVersion", "0");
        ReflectionTestUtils.setField(client, "algorithm", "RSA256");
        ReflectionTestUtils.setField(client, "paymentGatewayUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(client, "clientId", "clientId");
        ReflectionTestUtils.setField(client, "zoneId", "Asia/Manila");
    }

    @Test
    @DisplayName("Verify that authorization code is exchanged for an access token")
    void applyAccessTokenWithAuthorizationCode() {
        // GIVEN
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .grantType(GrantType.AUTHORIZATION_CODE)
                .referenceClientId("305XST2CSG0N4P0xxxx")
                .authCode("2810111301lGZcM9CjlF91WH00039190xxxx")
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .build();

        AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .accessToken("281010033AB2F588D14B43238637264FCA5AAF35xxxx")
                .accessTokenExpiryTime(OffsetDateTime.parse("2019-06-08T12:12:12+08:00"))
                .refreshToken("2810100334F62CBC577F468AAC87CFC6C9107811xxxx")
                .refreshTokenExpiryTime(OffsetDateTime.parse("2019-06-08T12:12:12+08:00"))
                .customerId("1000001119398804xxxx")
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/authorizations/applyToken")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(accessTokenResponse));

        // WHEN
        Mono<AccessTokenResponse> accessTokenResponseMono = client.applyAccessToken(accessTokenRequest);

        // THEN
        assertThat(accessTokenResponseMono).isNotNull();
        AccessTokenResponse actual = accessTokenResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(accessTokenResponse);
    }

    @Test
    @DisplayName("Verify that refresh token is exchanged for an access token")
    void applyAccessTokenWithRefreshToken() {
        // GIVEN
        AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
                .grantType(GrantType.REFRESH_TOKEN)
                .refreshToken("2810111301lGZcM9CjlF91WH00039190xxxx")
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .build();

        AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .accessToken("281010033AB2F588D14B43238637264FCA5AAF35xxxx")
                .accessTokenExpiryTime(OffsetDateTime.parse("2019-06-08T12:12:12+08:00"))
                .refreshToken("2810100334F62CBC577F468AAC87CFC6C9107811xxxx")
                .refreshTokenExpiryTime(OffsetDateTime.parse("2019-06-08T12:12:12+08:00"))
                .customerId("1000001119398804xxxx")
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/authorizations/applyToken")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(accessTokenResponse));

        // WHEN
        Mono<AccessTokenResponse> accessTokenResponseMono = client.applyAccessToken(accessTokenRequest);

        // THEN
        assertThat(accessTokenResponseMono).isNotNull();
        AccessTokenResponse actual = accessTokenResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(accessTokenResponse);
    }

    @Test
    @DisplayName("Verify that access token is revoked")
    void cancelAccessToken() {
        // GIVEN
        Result result = Result.builder()
                .resultCode("SUCCESS")
                .resultStatus("S")
                .resultMessage("success")
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/authorizations/cancelToken")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(result));

        // WHEN
        Mono<Result> resultMono = client.cancelAccessToken("281010033AB2F588D14B43238637264FCA5Axxxx",
                "{\"customerBelongsTo\":\"siteNameExample\"}");

        // THEN
        assertThat(resultMono).isNotNull();
        Result actual = resultMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(result);
    }

    @Test
    @DisplayName("Verify that payment is created")
    void createPayment() {
        // GIVEN
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .partnerId("P000000000000001xxxx")
                .paymentRequestId("2019112719074101000700000077771xxxx")
                .paymentOrderTitle("SHOES")
                .productCode("PC_5800000001")
                .mcc("4399")
                .paymentAmount(Amount.builder()
                        .currency("USD")
                        .value("10000")
                        .build())
                .paymentFactor(PaymentFactor.builder()
                        .isCashierPayment(true)
                        .build())
                .paymentReturnUrl("https://www.merchant.com/redirectxxx")
                .paymentNotifyUrl("https://www.merchant.com/paymentNotifyxxx")
                .extraParams(Map.of("ORDER",
                        "{\"referenceOrderId\":\"ID_000001\",\"orderAmount\":\"{\"currency\":\"USD\",\"value\":\"10000\"}\"}"))
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .envInfo(EnvInfo.builder()
                        .osType(OsType.IOS)
                        .terminalType(TerminalType.APP)
                        .build())
                .build();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .result(Result.builder()
                        .resultCode("ACCEPT")
                        .resultStatus("A")
                        .resultMessage("accept")
                        .build())
                .paymentId("string")
                .actionForm(ActionForm.builder()
                        .actionFormType(ActionFormType.REDIRECTION)
                        .redirectionUrl("http://www.merchant.com/cashier?orderId=xxxxxxx")
                        .build())
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/pay")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(paymentResponse));

        // WHEN
        Mono<PaymentResponse> paymentResponseMono = client.createPayment(paymentRequest);

        //THEN
        assertThat(paymentResponseMono).isNotNull();
        PaymentResponse actual = paymentResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(paymentResponse);
    }

    @Test
    @DisplayName("Verify that payment is retrieved by payment request ID")
    void retrievePaymentByPaymentRequestId() {
        // GIVEN
        PaymentInquiryRequest paymentInquiryRequest = PaymentInquiryRequest.builder()
                .paymentRequestId("1022172000000000001xxxx")
                .partnerId("20200101234567890132xxxx")
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .build();

        PaymentInquiryResponse paymentInquiryResponse = PaymentInquiryResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .paymentId("20200101234567890133333xxxx")
                .paymentRequestId("20200101234567890133333xxxx")
                .paymentTime(OffsetDateTime.parse("2020-01-01T12:01:01+08:30"))
                .paymentAmount(Amount.builder()
                        .value("100")
                        .currency("USD")
                        .build())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/inquiryPayment")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(paymentInquiryResponse));

        // WHEN
        Mono<PaymentInquiryResponse> paymentInquiryResponseMono = client.retrievePayment(paymentInquiryRequest);

        //THEN
        assertThat(paymentInquiryResponseMono).isNotNull();
        PaymentInquiryResponse actual = paymentInquiryResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(paymentInquiryResponse);
    }

    @Test
    @DisplayName("Verify that payment is retrieved by payment ID")
    void retrievePaymentByPaymentId() {
        // GIVEN
        PaymentInquiryRequest paymentInquiryRequest = PaymentInquiryRequest.builder()
                .paymentId("1022172000000000001xxxx")
                .partnerId("20200101234567890132xxxx")
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .build();

        PaymentInquiryResponse paymentInquiryResponse = PaymentInquiryResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .paymentId("20200101234567890133333xxxx")
                .paymentRequestId("20200101234567890133333xxxx")
                .paymentTime(OffsetDateTime.parse("2020-01-01T12:01:01+08:30"))
                .paymentAmount(Amount.builder()
                        .value("100")
                        .currency("USD")
                        .build())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/inquiryPayment")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(paymentInquiryResponse));

        // WHEN
        Mono<PaymentInquiryResponse> paymentInquiryResponseMono = client.retrievePayment(paymentInquiryRequest);

        //THEN
        assertThat(paymentInquiryResponseMono).isNotNull();
        PaymentInquiryResponse actual = paymentInquiryResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(paymentInquiryResponse);
    }

    @Test
    @DisplayName("Verify that payment notification is retrieved")
    void retrievePaymentNotification() {
        // GIVEN
        PaymentNotificationRequest paymentNotificationRequest = PaymentNotificationRequest.builder()
                .paymentId("201911271907410100070000009999xxxx")
                .partnerId("P000000000000001xxxx")
                .paymentRequestId("2019112719074101000700000088881xxxx")
                .paymentAmount(Amount.builder()
                        .currency("USD")
                        .value("10000")
                        .build())
                .paymentTime(OffsetDateTime.parse("2019-11-27T12:02:01+08:30"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        PaymentNotificationResponse paymentNotificationResponse = PaymentNotificationResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/notifyPayment")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(paymentNotificationResponse));

        // WHEN
        Mono<PaymentNotificationResponse> paymentNotificationResponseMono = client.retrievePaymentNotification(paymentNotificationRequest);

        //THEN
        assertThat(paymentNotificationResponseMono).isNotNull();
        PaymentNotificationResponse actual = paymentNotificationResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(paymentNotificationResponse);
    }

    @Test
    @DisplayName("Verify that refund is created")
    void createRefund() {
        // GIVEN
        RefundRequest refundRequest = RefundRequest.builder()
                .partnerId("P000000000000001xxxx")
                .refundRequestId("2019112719074101000700000088881xxxx")
                .paymentId("201911271907410100070000009999xxxx")
                .refundAmount(Amount.builder()
                        .currency("USD")
                        .value("10000")
                        .build())
                .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
                .build();

        RefundResponse refundResponse = RefundResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .refundId("2019112719074101000700000019000xxxx")
                .refundTime(OffsetDateTime.parse("2019-11-27T12:01:01+08:30"))
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/refund")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(refundResponse));

        // WHEN
        Mono<RefundResponse> refundResponseMono = client.createRefund(refundRequest);

        //THEN
        assertThat(refundResponseMono).isNotNull();
        RefundResponse actual = refundResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(refundResponse);
    }

    @Test
    @DisplayName("Verify that refund is retrieved")
    void retrieveRefund() {
        // GIVEN
        RefundInquiryRequest refundInquiryRequest = RefundInquiryRequest.builder()
                .refundId("1022188000000000001xxxx")
                .partnerId("1022172000000000001xxxx")
                .refundRequestId("20200101234567890132xxxx")
                .build();

        RefundInquiryResponse refundInquiryResponse = RefundInquiryResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .refundId("20200101234567890144444xxxx")
                .refundRequestId("20200101234567890155555xxxx")
                .refundAmount(Amount.builder()
                        .value("100")
                        .currency("USD")
                        .build())
                .refundReason("refund reason.")
                .refundTime(OffsetDateTime.parse("2020-01-02T12:01:01+08:30"))
                .refundStatus(RefundStatus.SUCCESS)
                .extendInfo("")
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/inquiryRefund")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(refundInquiryResponse));

        // WHEN
        Mono<RefundInquiryResponse> refundInquiryResponseMono = client.retrieveRefund(refundInquiryRequest);

        //THEN
        assertThat(refundInquiryResponseMono).isNotNull();
        RefundInquiryResponse actual = refundInquiryResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(refundInquiryResponse);
    }

    @Test
    @DisplayName("Verify that user information is retrieved")
    void retrieveUserInformation() {
        // GIVEN
        UserInformationResponse userInformationResponse = UserInformationResponse.builder()
                .result(Result.builder()
                        .resultCode("SUCCESS")
                        .resultStatus("S")
                        .resultMessage("success")
                        .build())
                .userInfo(OpenUserInfo.builder()
                        .userId("1000001119398804xxxx")
                        .loginIdInfos(List.of(OpenLoginIdInfo.builder()
                                .loginId("1116874199xxxx")
                                .loginIdType(LoginIdType.MOBILE_PHONE)
                                .extendInfo("{}")
                                .build()))
                        .status(UserStatus.ACTIVE)
                        .nickname("Jack")
                        .username(UserName.builder()
                                .fullName("Jack Sparrow")
                                .firstName("Jack")
                                .lastName("Sparrow")
                                .build())
                        .avatar("http://example.com/avatar.htm?avatarId=FBF16F91-28FB-47EC-B9BE-27B285C23CD3xxxx")
                        .gender("MALE")
                        .birthday(LocalDate.of(2020, 7, 25))
                        .nationality("US")
                        .contactInfos(List.of(ContactInfo.builder()
                                .contactNo("1116874199xxxx")
                                .contactType("MOBILE_PHONE")
                                .extendInfo("{}")
                                .build()))
                        .extendInfo("{}")
                        .build())
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/customers/user/inquiryUserInfoByAccessToken")).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(stringArgumentCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any(Function.class))).thenReturn(Mono.just(userInformationResponse));

        // WHEN
        Mono<UserInformationResponse> userInformationResponseMono = client.retrieveUserInformation("281010033AB2F588D14B43238637264FCA5Axxxx",
                "{\"customerBelongsTo\":\"siteNameExample\"}");

        // THEN
        assertThat(userInformationResponseMono).isNotNull();
        UserInformationResponse actual = userInformationResponseMono.block();
        assertThat(actual)
                .isNotNull()
                .isEqualTo(userInformationResponse);
    }
}