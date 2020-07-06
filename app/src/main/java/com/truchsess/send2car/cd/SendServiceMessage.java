package com.truchsess.send2car.cd;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.truchsess.send2car.cd.api.CDApi;
import com.truchsess.send2car.cd.api.SendServiceMessageResponse;
import com.truchsess.send2car.cd.entity.CDApiJSONError;
import com.truchsess.send2car.cd.entity.ServiceMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
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
public class SendServiceMessage {

    private final CDApi cdApi;

    public SendServiceMessage(final String baseUrl) {
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

    public interface SendServiceMessageListener {
        public void onSuccess(SendServiceMessageResponse sendServiceMessageResponse);
        public void onError(CDApiJSONError error);
    }

    public void sendServiceMessage(final ServiceMessage serviceMessage, final String authorization, final SendServiceMessageListener listener) {

        cdApi.sendServiceMessage(serviceMessage, authorization).enqueue(new Callback<SendServiceMessageResponse>() {
            @Override
            public void onResponse(Call<SendServiceMessageResponse> call, Response<SendServiceMessageResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    final RequestBody requestBody = call.request().body();
                    final Buffer buffer = new Buffer();
                    String requestBodyString = null;
                    try {
                        requestBody.writeTo(buffer);
                        InputStreamReader reader = new InputStreamReader(buffer.inputStream(), "UTF-8");
                        StringWriter writer = new StringWriter();
                        while (reader.ready()) {
                            writer.write(reader.read());
                        }
                        requestBodyString = writer.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final ResponseBody responseBody = response.errorBody();
                    CDApiJSONError error = null;
                    try {
                        error = new Gson().fromJson(responseBody.charStream(), CDApiJSONError.class);
                    } catch (JsonSyntaxException jse) {
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
                    if (requestBodyString != null && !requestBodyString.isEmpty()) {
                        error.getReasons().add(requestBodyString);
                    }
                    listener.onError(error);
                }
            }

            @Override
            public void onFailure(Call<SendServiceMessageResponse> call, Throwable t) {
                final CDApiJSONError error = new CDApiJSONError();
                final List<String> reasons = new ArrayList<>();
                reasons.add("0");
                reasons.add("error sending servicemessage");
                reasons.add(t.getLocalizedMessage());
                error.setReasons(reasons);
                listener.onError(error);
            }
        });
    }
}
