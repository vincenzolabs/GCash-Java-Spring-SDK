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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vincenzolabs.gcash.enumeration.LoginIdType;
import org.vincenzolabs.gcash.enumeration.OsType;
import org.vincenzolabs.gcash.enumeration.TerminalType;

/**
 * The open login ID information.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenLoginIdInfo {

    /**
     * The login ID is an identification for a user, which can be mobile number or email. User can use login ID to
     * login Wallet.
     */
    @Max(64)
    private String loginId;

    /**
     * The {@link LoginIdType}.
     */
    @NotNull
    private LoginIdType loginIdType;

    /**
     * The masked login ID. Several bits of the phone number will be hidden to protect users' privacy.
     */
    @Max(64)
    private String maskLoginId;

    /**
     * The hashed login ID. The login id hashed by hash algorithm. The external system can use it to compare its
     * login ID hashed by the same hash algorithm to see if the login ID is the same.
     */
    @Max(256)
    private String hashLoginId;

    /**
     * The extend info. Wallet and merchant can put extend info here.
     */
    @Max(4096)
    private String extendInfo;
}
