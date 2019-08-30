package com.truchsess.send2car.cd;

import com.truchsess.send2car.cd.api.CDApiCall;
import com.truchsess.send2car.cd.api.ErrorResponse;
import com.truchsess.send2car.cd.api.TokenResponse;

import java.util.Base64;

import static com.truchsess.send2car.cd.api.CDApi.API_SERVER;

/**********************************************************************************************
 Copyright (C) 2018 Norbert Truchsess norbert.truchsess@t-online.de

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************************/
public class Token extends CDApiCall<TokenResponse> {

    private static String GRANT_TYPE = "password";
    private static String SCOPE = "remote_services vehicle_data";
    private static long expireBefore = 60 * 1000L; //ms

    private String token = null;
    private String tokenType;
    private long tokenExpires = 0;

    public Token() {};

    public void setCredentials(final String username, final String password, final String api_key, final String api_secret) {

        final String gcdm = api_key+":"+api_secret;
        final String gcdm_key = android.util.Base64.encodeToString(gcdm.getBytes(), android.util.Base64.NO_WRAP);
        apiCall = this.createApi(API_SERVER).getToken(
                GRANT_TYPE,
                username,
                password,
                SCOPE,
                "Basic "+gcdm_key);
    }

    public void clearToken() {
        token = null;
        tokenExpires = 0;
    }

    public interface AuthenticationListener {

        public void onAuthentication();

        public void onAuthenticationFailure(final ErrorResponse errorResponse);
    }

    public void checkToken(final AuthenticationListener listener) {
        if (System.currentTimeMillis() + expireBefore > tokenExpires || token == null) {
            requestResult(new CDApiServiceListener<TokenResponse>() {
                @Override
                public void onSuccess(final TokenResponse tokenResponse) {
                    tokenType = tokenResponse.getToken_type();
                    token = tokenResponse.getAccess_token();
                    tokenExpires = System.currentTimeMillis() + 1000L * tokenResponse.getExpires_in();
                    if (listener != null) {
                        listener.onAuthentication();
                    }
                }

                @Override
                public void onError(final ErrorResponse errorResponse) {
                    clearToken();
                    if (listener != null) {
                        listener.onAuthenticationFailure(errorResponse);
                    }
                }
            });
        } else if (listener != null) {
            listener.onAuthentication();
        }
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public boolean isAuthenticated() {
        return token != null && tokenType != null;
    }

    public String getAuthorization() {
        return isAuthenticated() ? tokenType + " " + token : "";
    }
}
