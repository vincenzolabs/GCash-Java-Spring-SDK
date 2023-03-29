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
import org.vincenzolabs.gcash.enumeration.GrantType;

/**
 * The access token request.
 *
 * @author <a href="mailto:rvbabilonia@gmail.com">Rey Vincent Babilonia</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest {

    /**
     * The reference client ID represents the next-level client id. When multiple auth codes need to be assigned to the
     * same client, different reference client IDs can be passed in to distinguish them.
     */
    @Max(128)
    private String referenceClientId;

    /**
     * The grant type indicating which parameter is to be used to obtain the access token.
     */
    @NotNull
    private GrantType grantType;

    /**
     * The authorization code which is used by confidential and public clients to exchange an authorization code for
     * an access token. After the user returns to the client via the Mini Program API, the Mini Program will get the
     * authorization code from the response of and use it to request an access token.
     * <p>
     * It is required when {@link #grantType} is {@link GrantType#AUTHORIZATION_CODE}.
     */
    @Max(32)
    private String authCode;

    /**
     * The refresh token which is used by the auth client to exchange for a new access token when the access token
     * expires. By using the refresh token, new access tokens can be obtained without further interaction with the user.
     * <p>
     * It is required when {@link #grantType} is {@link GrantType#REFRESH_TOKEN}.
     */
    @Max(128)
    private String refreshToken;

    /**
     * The extend info. Wallet and merchant can put extend info here.
     */
    @Max(4096)
    private String extendInfo;
}
