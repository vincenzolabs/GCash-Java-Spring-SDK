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

/**
 * The order.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * The unique identification of the order on the merchant side. It is used for the display of user consumption
     * records, and the subsequent payment operations such as customer complaints and disputes track.
     */
    @Max(64)
    @NotBlank
    private String referenceOrderId;

    /**
     * The description of the order used to display user consumption records, etc.
     */
    @Max(256)
    private String orderDescription;

    /**
     * The {@link Amount} of an order or how much to charge in the specified currency unit for an order.
     */
    @NotNull
    private Amount orderAmount;

    /**
     * The order create time from merchant which follows the ISO 8601 standard.
     */
    private OffsetDateTime orderCreateTime;

    /**
     * The {@link Merchant} information.
     */
    private Merchant referenceMerchant;

    /**
     * The {@link Goods} information;
     */
    private Goods goods;

    /**
     * The {@link Shipping} information.
     */
    private Shipping shipping;

    /**
     * The {@link Buyer} information.
     */
    private Buyer buyer;

    /**
     * The extended information data. This field includes information that are not common but needed for special use
     * cases.
     */
    @Max(2048)
    private String extendInfo;
}
