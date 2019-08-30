package com.truchsess.send2car;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class StatusFragment extends Fragment {

    private TextView mTextViewError;
    private TextView mTextViewErrorDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        mTextViewError = view.findViewById(R.id.textViewError);
        mTextViewErrorDescription = view.findViewById(R.id.textViewErrorDescription);

        return view;
    }

    public void setStatus(final String status, final String description) {
        mTextViewError.setText(status);
        mTextViewErrorDescription.setText(description);
    }

    public void clear() {
        setStatus("","");
    }

    public boolean isSet() {
        return mTextViewError.getText().length() > 0 || mTextViewErrorDescription.getText().length() > 0;
    }
}

