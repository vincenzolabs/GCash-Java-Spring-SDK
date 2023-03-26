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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vincenzolabs.gcash.enumeration.UserStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * The open user information.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenUserInfo {

    /**
     * The hashed unique identifier allocated for user.
     */
    @Max(64)
    private String userId;

    /**
     * The user status.
     */
    private UserStatus status;

    /**
     * The nickname.
     */
    @JsonProperty("nickName")
    @Max(256)
    private String nickname;

    /**
     * The {@link UserName}.
     */
    private UserName username;

    /**
     * The user's address information.
     */
    private List<Address> userAddresses;

    /**
     * The avatar URL.
     */
    @Max(256)
    private String avatar;

    /**
     * The gender.
     */
    @Max(32)
    private String gender;

    /**
     * The birthday which follows the ISO 8601 standard.
     */
    private LocalDate birthday;

    /**
     * The nationality. The alpha-2 code according to ISO 3166 e.g. JP, US.
     */
    @Max(2)
    private String nationality;

    /**
     * The {@link OpenLoginIdInfo} {@link List}.
     */
    private List<OpenLoginIdInfo> loginIdInfos;

    /**
     * The {@link ContactInfo} {@link List}.
     */
    private List<ContactInfo> contactInfos;

    /**
     * The extend info. Wallet and merchant can put extend info here.
     */
    @Max(4096)
    private String extendInfo;
}
