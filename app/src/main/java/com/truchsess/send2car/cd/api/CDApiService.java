package com.truchsess.send2car.cd.api;

import com.truchsess.send2car.cd.Token;
import com.truchsess.send2car.cd.entity.VehicleStatus;

import retrofit2.Call;

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
public abstract class CDApiService<T extends ErrorResponse> extends CDApiCall<T> {

    private Token token;
    private String bearer;
    private String tokenType;
    private boolean requiresNewService = true;

    protected CDApiService(Token token) {
        this.token = token;
    }

    abstract protected Call<T> createService(String authorization);

    protected void requestService(final CDApiServiceListener<T> listener) {
        String bearer = token.getToken();
        String tokenType = token.getTokenType();
        if (bearer == null || tokenType == null) {
            apiCall = null;
        } else {
            if (!bearer.equals(this.bearer)
                    || !tokenType.equals(this.tokenType)
                    || requiresNewService) {
                this.bearer = bearer;
                this.tokenType = tokenType;
                apiCall = createService(this.tokenType + " " + bearer);
                this.requiresNewService = false;
            }
            if (apiCall != null) {
                requestResult(listener);
            }
        }
    }

    public void setRequiresNewService() {
        this.requiresNewService = true;
    }
}
