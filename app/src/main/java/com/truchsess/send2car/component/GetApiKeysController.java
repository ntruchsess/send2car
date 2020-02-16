package com.truchsess.send2car.component;

import com.truchsess.send2car.cd.GetPortalFlags;
import com.truchsess.send2car.cd.GetVehicles;
import com.truchsess.send2car.cd.Token;
import com.truchsess.send2car.cd.api.CDApi;
import com.truchsess.send2car.cd.api.ErrorResponse;
import com.truchsess.send2car.cd.api.PortalFlagsResponse;
import com.truchsess.send2car.cd.api.VehicleResponse;
import com.truchsess.send2car.cd.entity.CDApiJSONError;
import com.truchsess.send2car.cd.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
public class GetApiKeysController {

    private final GetPortalFlags getPortalFlags;
    private String apiKey;
    private String apiSecret;

    public GetApiKeysController() {
        getPortalFlags = new GetPortalFlags(CDApi.CD_SERVER);
    }

    public interface GetPortalFlagsListener {
        void onUpdate();
        void onError(String error);
    }

    public void getPortalFlagsResponse(final GetPortalFlagsListener listener) {
        getPortalFlags.getPortalFlagsResponse(new GetPortalFlags.GetPortalFlagsListener() {
            @Override
            public void onSuccess(PortalFlagsResponse response) {
                apiKey = response.getGcdmApiKey();
                apiSecret = response.getGcdmApiSecret();
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

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
