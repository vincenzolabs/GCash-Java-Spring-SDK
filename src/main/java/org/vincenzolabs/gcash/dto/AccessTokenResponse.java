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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * The access token response.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    /**
     * The {@link Result} which contains information such as status and error codes.
     */
    private Result result;

    /**
     * The access token that can be used to access the user resource scope.
     * <p>
     * When authorization application is successful [{@link Result#getResultStatus()} == S], the auth client might use
     * #accessToken to access the corresponding user's resource scope.
     */
    @Max(128)
    private String accessToken;

    /**
     * The access token expiration time, which follows the ISO 8601 standard. After this time, authClient will not be
     * able to use this token to deduct from user's account.
     * <p>
     * This parameter must be returned when authorization application is successful [result.resultStatus == S],
     * and the accessToken will be invalid after accessTokenExpiryTime.
     */
    private OffsetDateTime accessTokenExpiryTime;

    /**
     * The refresh token that is used by the auth client to exchange for a new access token when the access token
     * expires. By using the refresh token, new access tokens can be obtained without further interaction with the user.
     * <p>
     * This parameter must be returned when authorization application is successful [{@link Result#getResultStatus()} == S],
     * and the merchant can use the refreshToken to request for a new {@link #accessToken}.
     */
    @Max(128)
    private String refreshToken;

    /**
     * The refresh token expiration time, after which the auth client cannot use this token to retrieve a new access
     * token. The value follows the ISO 8601 standard.
     * <p>
     * This parameter must be returned when authorization application is successful [{@link Result#getResultStatus()} == S],
     * and the merchant will not be able to use the {@link #refreshToken} to retrieve a new {@link #accessToken} after
     * refreshTokenExpiryTime.
     */
    private OffsetDateTime refreshTokenExpiryTime;

    /**
     * The resource owner ID, maybe user ID, app ID of merchant's application or merchant ID.
     */
    @Max(64)
    private String customerId;

    /**
     * The extend info. Wallet and merchant can put extend info here.
     */
    @Max(4096)
    private String extendInfo;
}
