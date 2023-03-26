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
package org.vincenzolabs.gcash.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * The payment request.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /**
     * The partner ID allocated by the wallet.
     */
    @Max(32)
    @NotBlank
    private String partnerId;

    /**
     * The Mini Program ID.
     */
    @Max(32)
    @NotBlank
    private String appId;

    /**
     * Defined by the wallet, the wallet will use productCode to get the contract config which includes fee
     * and limit info.
     */
    @Max(32)
    private String productCode;

    /**
     * The order title of this payment.
     */
    @Max(256)
    @NotBlank
    private String paymentOrderTitle;

    /**
     * This field is used for the idempotence control. For the payment requests which are initiated with the same
     * paymentRequestId and reach a final status (S or F), the wallet must return the unique result.
     */
    @Max(64)
    @NotBlank
    private String paymentRequestId;

    /**
     * The order {@link Amount} for display of user consumption records and payment results page.
     */
    @NotNull
    private Amount paymentAmount;

    /**
     * The payment method used to collect fund by the wallet.
     */
    private PaymentMethod paymentMethod;

    /**
     * If {@link PaymentFactor#getIsAgreementPay()} is {@code true}, then it's the accessToken of the wallet user.
     * If {@link PaymentFactor#getIsPaymentCode()} is {@code true}, then it's the authcode of the wallet user.
     */
    @Max(128)
    private String paymentAuthCode;

    /**
     * The {@link PaymentFactor}.
     */
    private PaymentFactor paymentFactor;

    /**
     * The payment order close time defined by the merchant, which follows the ISO 8601 standard.
     */
    private OffsetDateTime paymentExpiryTime;

    /**
     * The redirect URL defined by the merchant.
     */
    @Max(1024)
    private String paymentReturnUrl;

    /**
     * The payment success notify URL defined by the merchant.
     */
    @Max(1024)
    private String paymentNotifyUrl;

    /**
     * The merchant category code (MCC).
     */
    @Max(32)
    private String mcc;

    /**
     * The specific payment ability which is provided by the wallet. Currently, only ORDER key is supported.
     */
    private Map<String, String> extraParams;

    /**
     * The extend info. Wallet and merchant can put extend info here.
     */
    @Max(4096)
    private String extendInfo;

    /**
     * The {@link EnvInfo} of mobile phone.
     */
    private EnvInfo envInfo;
}
