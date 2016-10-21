/*
 * Copyright (c) 2015 Vimeo (https://vimeo.com)
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

package com.vimeo.networking.model;

import com.google.gson.Gson;
import com.vimeo.networking.utils.VimeoNetworkUtil;
import com.vimeo.stag.GsonAdapterKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * This class represents an authenticated account with Vimeo. It can be through client credentials or a
 * truly authenticated {@link User}
 * <p/>
 * Created by alfredhanssen on 4/12/15.
 */
public class VimeoAccount implements Serializable {

    private static final long serialVersionUID = -8341071767843490585L;

    @Nullable
    @GsonAdapterKey("access_token")
    public String mAccessToken;

    @Nullable
    @GsonAdapterKey("token_type")
    public String mTokenType;

    @Nullable
    @GsonAdapterKey("scope")
    public String mScope;

    @Nullable
    @GsonAdapterKey("user")
    public User mUser;

    @Nullable
    private String mUserJson;

    public VimeoAccount() {
        //constructor for stag TypeAdapter generation
    }

    /**
     * Constructor for use for developers making requests with only one account. Provide your access
     * token to the Configuration Builder and this constructor will be used to set up your authentication.
     * <p>
     * See the README of this library for more information.
     *
     * @param accessToken your access token.
     * @see "https://github.com/vimeo/vimeo-networking-java#initialization"
     */
    public VimeoAccount(@NotNull String accessToken) {
        //noinspection ConstantConditions
        if (accessToken == null || accessToken.isEmpty()) {
            throw new AssertionError("Account must be created with token");
        }

        mAccessToken = accessToken;
    }

    /**
     * Use this constructor to create an account manually. If you cannot obtain a valid access token,
     * it is recommended to have a VimeoAccount constructed automatically using one of the
     * authentication methods in VimeoClient rather than calling this constructor directly.
     *
     * @param accessToken The authentication token
     * @param tokenType   The token type. May be set to a value to pair with an access token.
     * @param scope       The scope of the access token
     * @param userJson    A {@link User} represented in a JSON string
     */
    public VimeoAccount(@NotNull String accessToken, @NotNull String tokenType, @NotNull String scope,
                        @Nullable String userJson) {
        //noinspection ConstantConditions
        if (accessToken == null || accessToken.isEmpty() || tokenType == null || tokenType.isEmpty() ||
            scope == null || scope.isEmpty()) {
            throw new AssertionError("Account can only be created with token, tokenType, scope");
        }

        mAccessToken = accessToken;
        mTokenType = tokenType;
        mScope = scope;

        if (userJson != null) {
            Gson gson = VimeoNetworkUtil.getGson();

            mUser = gson.fromJson(userJson, User.class);
            mUserJson = userJson;
        }
    }

    /**
     * @return true if the access token is not empty, false if it is null or empty
     */
    public boolean isAuthenticated() {
        return (mAccessToken != null && !mAccessToken.isEmpty());
    }

    /**
     * @return the access (auth) token stored with this account
     */
    @Nullable
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * @return the token type that is stored with this account
     */
    @Nullable
    public String getTokenType() {
        return mTokenType;
    }

    /**
     * @return The scope that is stored with this account
     */
    @Nullable
    public String getScope() {
        return mScope;
    }

    /**
     * Get the user represented by this account
     *
     * @return the user, or null if no user is represented by the account
     */
    @Nullable
    public User getUser() {
        return mUser;
    }

    /**
     * Set the user on the account. When changing the user, the account should be set and saved by the client
     *
     * @param user the new user on the account
     */
    public void setUser(@Nullable User user) {
        mUser = user;

        createUserJson();
    }

    /**
     * Get the user represented as a JSON string
     *
     * @return the user in a JSON format, null if there is no user
     */
    @Nullable
    public String getUserJSON() {
        if (mUser == null) {
            return null;
        }

        if (mUserJson != null) {
            return mUserJson;
        }

        createUserJson();

        return mUserJson;
    }

    private void createUserJson() {
        if (mUser == null) {
            mUserJson = null;
        } else {
            Gson gson = VimeoNetworkUtil.getGson();
            mUserJson = gson.toJson(mUser);
        }
    }

    /**
     * Copies one VimeoAccount into another
     *
     * @param other the account to copy
     * @return a new VimeoAccount, with the details of the other
     */
    @Nullable
    public static VimeoAccount copy(final VimeoAccount other) {
        String accessToken = other.getAccessToken();
        String tokenType = other.getTokenType();
        String scope = other.getScope();
        String userJson = other.getUserJSON();

        if (accessToken != null && tokenType != null && scope != null) {
            return new VimeoAccount(accessToken, tokenType, scope, userJson);
        } else if (accessToken != null) {
            return new VimeoAccount(accessToken);
        } else {
            return null;
        }
    }
}
