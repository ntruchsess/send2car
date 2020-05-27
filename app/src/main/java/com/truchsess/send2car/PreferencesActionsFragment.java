package com.truchsess.send2car;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
public class PreferencesActionsFragment extends Fragment {

    private Button mButtonLoadKeys;
    private Button mButtonGetVins;
    private Listener mListener;

    public interface Listener {
        void onLoadKeysClicked();
        void onGetVinsClicked();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference_actions, container, false);

        mButtonLoadKeys = view.findViewById(R.id.buttonGetApiKeys);
        mButtonLoadKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLoadKeysClicked();
                }
            }
        });

        mButtonGetVins = view.findViewById(R.id.buttonGetVins);
        mButtonGetVins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGetVinsClicked();
                }
            }
        });
        return view;
    }
}

