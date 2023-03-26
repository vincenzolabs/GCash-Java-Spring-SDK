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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The payment factor.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFactor {

    /**
     * An indicator of the payment evaluation. If the value is TRUE, the payment is only to evaluate whether the
     * payment can be successful and no actual funds deduction occurs. The default value is FALSE.
     */
    private Boolean isPaymentEvaluation;

    /**
     * If the payment scenario is the user scan the code presented by the merchant and identify the order, and make
     * payment. The default value is FALSE.
     */
    private Boolean isOrderCode;

    /**
     * An indicator of whether the payment scenario is the merchant scan the user payment code. The default value is
     * FALSE.
     */
    private Boolean isPaymentCode;

    /**
     * An indicator of whether the payment is an agreement payment. The default value is FALSE.
     */
    private Boolean isAgreementPay;

    /**
     * An indicator of whether the payment is a cashier payment. The default value is FALSE.
     */
    private Boolean isCashierPayment;

    /**
     * An indicator of whether to do agreementPay authorization during the payment. The default value is FALSE.
     */
    private Boolean isAuthorizationAndPay;

    /**
     * An indicator of whether the payment is an authorization payment. The default value is FALSE.
     */
    private Boolean isAuthorizationPayment;
}
