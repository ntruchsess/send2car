package com.truchsess.send2car.cd;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.truchsess.send2car.cd.api.CDApi;
import com.truchsess.send2car.cd.api.VehicleResponse;
import com.truchsess.send2car.cd.entity.CDApiJSONError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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
public class GetVehicles {

    private final CDApi cdApi;

    public GetVehicles(final String baseUrl) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cdApi = retrofit.create(CDApi.class);
    }

    public interface GetVehiclesListener {
        public void onSuccess(final VehicleResponse response);
        public void onError(final CDApiJSONError error);
    }

    public void getVehiclesResponse(final String authorization, final GetVehiclesListener listener) {

        cdApi.getVehicles(authorization).enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    final ResponseBody responseBody = response.errorBody();
                    CDApiJSONError error = null;
                    try {
                        error = new Gson().fromJson(responseBody.charStream(), CDApiJSONError.class);
                    } catch (JsonSyntaxException jse) {
                        error = new CDApiJSONError();
                    }
                    if (error == null) {
                        error = new CDApiJSONError();
                    }
                    if (error.getReasons() == null) {
                        error.setReasons(new ArrayList<String>());
                    }
                    try {
                        final String bodyContent = responseBody.string();
                        if (bodyContent != null && !bodyContent.isEmpty()) {
                            error.getReasons().add(bodyContent);
                        }
                    } catch (IOException ioe) {
                    }
                    error.getReasons().add(0, Integer.toString(response.code()));
                    error.getReasons().add(1, response.message());
                    listener.onError(error);
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                final CDApiJSONError error = new CDApiJSONError();
                final List<String> reasons = new ArrayList<>();
                reasons.add("0");
                reasons.add("error getting vehicles");
                reasons.add(t.getLocalizedMessage());
                error.setReasons(reasons);
                listener.onError(error);
            }
        });
    }
}
