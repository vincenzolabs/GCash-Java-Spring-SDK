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
 * The goods.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    /**
     * The unique ID of goods.
     */
    @Max(64)
    @NotBlank
    private String referenceGoodsId;

    /**
     * The name of goods.
     */
    @Max(256)
    @NotBlank
    private String goodsName;

    /**
     * The category of goods.
     */
    @Max(256)
    private String goodsCategory;

    /**
     * The brand of goods.
     */
    @Max(32)
    private String goodsBrand;

    /**
     * The order {@link Amount} for display of user consumption records and payment results page.
     */
    private Amount goodsUnitAmount;

    /**
     * The quantity of goods.
     */
    @Max(32)
    private String goodsQuantity;

    /**
     * The goods URL.
     */
    @Max(1024)
    private String goodsUrl;

    /**
     * The extend information of goods.
     */
    @Max(2048)
    private String extendInfo;
}
