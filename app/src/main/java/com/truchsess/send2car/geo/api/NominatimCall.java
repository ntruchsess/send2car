package com.truchsess.send2car.geo.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.truchsess.send2car.geo.GeoUrl;
import com.truchsess.send2car.geo.entity.Place;

import java.util.List;
import java.util.Locale;

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
public class NominatimCall {

    private final NominatimApi api;
    private boolean callRunning = false;

    public NominatimCall() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NominatimApi.NOMINATIM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(NominatimApi.class);
    }

    public static interface NominatimCallListener<T> {

        public void onSuccess(T result);

        public void onError(NominatimError error);

    }

    private class PlaceCallback<T> implements Callback<T> {

        private final NominatimCallListener listener;

        PlaceCallback(NominatimCallListener listener) {
            this.listener = listener;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            final T result = response.body();
            if (response.isSuccessful()) {
                listener.onSuccess(result);
            } else {
                final ResponseBody errorBody = response.errorBody();
                NominatimErrorResponse errorResponse = null;
                if (errorBody != null) {
                    try {
                        errorResponse = new Gson().fromJson(errorBody.charStream(), NominatimErrorResponse.class);
                    } catch (JsonSyntaxException jse) {
                    }
                }
                if (errorResponse != null && errorResponse.getError() != null) {
                    listener.onError(errorResponse.getError());
                } else {
                    final NominatimError error = new NominatimError();
                    error.setCode(response.code());
                    error.setMessage(response.message());
                    listener.onError(error);
                }
            }
            synchronized (api) {
                callRunning = false;
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            final NominatimError error = new NominatimError();
            error.setCode(-1);
            error.setMessage(t.getLocalizedMessage());
            listener.onError(error);
            synchronized (api) {
                callRunning = false;
            }
        }
    }

    public void requestReverse(double lat, double lon, final NominatimCallListener<Place> listener) {

        synchronized (api) {
            if (!callRunning) {
                callRunning = true;
                api.reverse(GeoUrl.degreeToString(lat),GeoUrl.degreeToString(lon)).enqueue(new PlaceCallback<Place>(listener));
            }
        }
    }

    public void requestSearch(String query, final NominatimCallListener<List<Place>> listener) {

        synchronized (api) {
            if (!callRunning) {
                callRunning = true;
                api.search(query).enqueue(new PlaceCallback<List<Place>>(listener));
            }
        }
    }

    public boolean isCallRunning() {
        synchronized (api) {
            return callRunning;
        }
    }
}
