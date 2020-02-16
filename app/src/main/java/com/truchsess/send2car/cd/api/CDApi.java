package com.truchsess.send2car.cd.api;

import com.truchsess.send2car.cd.entity.ServiceMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**********************************************************************************************
 Copyright (C) 2020 Norbert Truchsess norbert.truchsess@t-online.de

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
public interface CDApi {

    // API Gateway
    public static String API_SERVER = "https://b2vapi.bmwgroup.com";

    public static String CD_SERVER = "https://www.bmw-connecteddrive.de";

    @FormUrlEncoded
    @POST("webapi/oauth/token/")
    Call<TokenResponse> getToken(@Field("grant_type") String grandType,
                                 @Field("username") String username,
                                 @Field("password") String password,
                                 @Field("scope") String scope,
                                 @Header("Authorization") String authorization);

    @GET("webapi/v1/user/vehicles")
    Call<VehicleResponse> getVehicles(@Header("Authorization") String authorization);

    @GET("webapi/v1/user/vehicles/{VIN}/status")
    Call<VehicleStatusResponse> getVehicleStatus(@Path("VIN") String vin, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("webapi/v1/user/vehicles/{VIN}/sendpoi")
    Call<ErrorResponse> sendPoi(@Path("VIN") String vin, @Field("data") String poiMessageData, @Header("Authorization") String authorization);

    @POST("api/vehicle/myinfo/v1")
    //@Headers("Content-Type: application/json;charset=utf-8")
    Call<Void> sendServiceMessage(@Body ServiceMessage serviceMessage, @Header("Authorization") String authorization);

    @GET("cms/BMW/de/flags.json")
    Call<PortalFlagsResponse> getPortalFlags();
}
