package com.truchsess.send2car.component;

import com.truchsess.send2car.cd.GetVehicles;
import com.truchsess.send2car.cd.Token;
import com.truchsess.send2car.cd.api.CDApi;
import com.truchsess.send2car.cd.api.ErrorResponse;
import com.truchsess.send2car.cd.api.VehicleResponse;
import com.truchsess.send2car.cd.entity.CDApiJSONError;
import com.truchsess.send2car.cd.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
public class GetVehiclesController {

    private final GetVehicles getVehicles;
    private final List<Vehicle> vehiclesList;

    private int vehiclesIndex = -1;

    private ResourceBundle categories;

    public GetVehiclesController() {
        getVehicles = new GetVehicles(CDApi.API_SERVER);
        vehiclesList = new ArrayList<>();
    }

    public interface GetVehiclesListener {
        void onUpdate();
        void onError(String error);
    }

    public void getVehiclesResponse(final Token token, final GetVehiclesListener listener) {
        token.checkToken(new Token.AuthenticationListener() {
            @Override
            public void onAuthentication() {
                getVehicles.getVehiclesResponse(token.getAuthorization(), new GetVehicles.GetVehiclesListener() {
                    @Override
                    public void onSuccess(VehicleResponse response) {
                        vehiclesList.clear();
                        vehiclesList.addAll(response.getVehicles());
                        listener.onUpdate();
                    }

                    @Override
                    public void onError(CDApiJSONError error) {
                        final StringBuilder builder = new StringBuilder();
                        boolean first = true;
                        for (String reason : error.getReasons()) {
                            if (first) {
                                first = false;
                            } else {
                                builder.append(", ");
                                first = false;
                            }
                            builder.append(reason);
                        }
                        listener.onError(builder.toString());
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(ErrorResponse errorResponse) {
                listener.onError(errorResponse.getError()+": "+errorResponse.getError_description());
            }
        });
    }

    public void setVehiclesIndex(final int index) {
        vehiclesIndex = index;
    }

    public int getNumVehicles() {
        return vehiclesList.size();
    }

    public Vehicle getVehicle() {
        return getVehicle(vehiclesIndex);
    }

    public Vehicle getVehicle(int position) {
        if (position >= 0 && position < vehiclesList.size()) {
            return vehiclesList.get(position);
        } else {
            return null;
        }
    }
}
