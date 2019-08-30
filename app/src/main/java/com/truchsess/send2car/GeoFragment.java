package com.truchsess.send2car;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.truchsess.send2car.geo.GeoUrl;

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
public class GeoFragment extends Fragment {

    private EditText mEditTextLat;
    private EditText mEditTextLon;
    private EditText mEditTextDescription;

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onLookupGeoDataClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_geo, container, false);

        mEditTextLat = view.findViewById(R.id.editTextLat);
        mEditTextLon = view.findViewById(R.id.editTextLon);
        mEditTextDescription = view.findViewById(R.id.editTextDescription);

        view.findViewById(R.id.buttonClearLat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextLat.setText("");
            }
        });

        view.findViewById(R.id.buttonClearLon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextLon.setText("");
            }
        });

        view.findViewById(R.id.buttonClearDescription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextDescription.setText("");
            }
        });

        view.findViewById(R.id.buttonLookupGeodata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLookupGeoDataClicked();
                }
            }
        });

        return view;
    }

    public void setGeoUrl(final GeoUrl geoUrl) {
        mEditTextLat.setText(geoUrl != null && geoUrl.isValid() ? GeoUrl.degreeToString(geoUrl.getLat()) : " - ");
        mEditTextLon.setText(geoUrl != null && geoUrl.isValid() ? GeoUrl.degreeToString(geoUrl.getLon()) : " - ");
        mEditTextDescription.setText(geoUrl == null || geoUrl.getDescription() == null ? " - " : geoUrl.getDescription());
    }

    public GeoUrl getGeoUrl() {
        double lat = Double.NaN;
        double lon = Double.NaN;

        try {
            lat = Double.parseDouble(mEditTextLat.getText().toString());
            lon = Double.parseDouble(mEditTextLon.getText().toString());
        } catch (NumberFormatException nfe) {
        }

        return new GeoUrl(lat,lon,mEditTextDescription.getText().toString());
    }
}
