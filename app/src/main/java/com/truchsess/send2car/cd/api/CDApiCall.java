package com.truchsess.send2car.cd.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
public abstract class CDApiCall<T extends ErrorResponse> {

    protected Call<T> apiCall;
    private boolean callRunning = false;

    protected T value;

    protected CDApi createApi(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(CDApi.class);
    }

    public interface CDApiServiceListener<T> {

        public void onSuccess(T result);

        public void onError(ErrorResponse error);
    }

    public void requestResult(final CDApiServiceListener listener) {

        synchronized (apiCall) {
            if (!callRunning) {
                callRunning = true;
                apiCall.clone().enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        synchronized (apiCall) {
                            callRunning = false;
                        }
                        T body = response.body();
                        if (response.isSuccessful() && (body == null || body.getError() == null)) {
                            listener.onSuccess(body);
                        } else {
                            ErrorResponse errorResponse = null;
                            if (body != null) {
                                errorResponse = body;
                            } else {
                                ResponseBody errorBody = response.errorBody();
                                if (errorBody != null) {
                                    try {
                                        errorResponse = new Gson().fromJson(errorBody.charStream(), ErrorResponse.class);
                                    } catch (JsonSyntaxException jse) {
                                    }
                                }
                                if (errorResponse == null) {
                                    errorResponse = new ErrorResponse();
                                    errorResponse.setError(Integer.toString(response.code()));
                                    errorResponse.setError_description(response.message());
                                }
                            }
                            listener.onError(errorResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        synchronized (apiCall) {
                            callRunning = false;
                        }
                        ErrorResponse error = new ErrorResponse();
                        error.setError("request error");
                        error.setError_description(t.getLocalizedMessage());
                        listener.onError(error);
                    }
                });
            }
        }
    }
}
