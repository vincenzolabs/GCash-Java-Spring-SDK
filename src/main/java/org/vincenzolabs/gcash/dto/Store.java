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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The store.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    /**
     * The store belongs to a merchant, and the ID assigned by the corresponding merchant to the store is unique under
     * the merchant.
     */
    @Max(64)
    @NotBlank
    private String referenceStoreId;

    /**
     * The name of the store.
     */
    @Max(256)
    private String storeName;

    /**
     * The store business category code.
     */
    @Max(32)
    private String storeMCC;

    /**
     * The display name of the store.
     */
    @Max(64)
    private String storeDisplayName;

    /**
     * The unique identifier of the store's terminal.
     */
    @Max(64)
    private String storeTerminalId;

    /**
     * The unique identifier of the store's terminal operator.
     */
    @Max(64)
    private String storeOperatorId;

    /**
     * The {@link Address} of the store.
     */
    private Address storeAddress;

    /**
     * The phone number of the store.
     */
    @Max(16)
    private String storePhoneNo;
}
