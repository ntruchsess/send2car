package com.truchsess.send2car;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.truchsess.send2car.cd.entity.ServiceMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class PlaceFragment extends Fragment {

    private ServiceMessage mServiceMessage;
    private TextView mTextViewPoiName;
    private TextView mTextViewPoiLat;
    private TextView mTextViewPoiLon;
    private TextView mTextViewPoiCity;
    private TextView mTextViewPoiStreet;
    private TextView mTextViewPoiNumber;
    private TextView mTextViewPoiPostalCode;

    private EditText mEditTextPhone;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    private RadioGroup mRadioGroupVins;

    private Button mButtonSend2Car;

    private Listener mListener;

    private Map<String,String> mVins = new HashMap<>();
    private String mCurrentVin;

    public interface Listener {
        void onSend2CarClicked();
    }

    public void setPlaceFragmentListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        mTextViewPoiName = view.findViewById(R.id.textViewPoiName);
        mTextViewPoiLat = view.findViewById(R.id.textViewPoiLat);
        mTextViewPoiLon = view.findViewById(R.id.textViewPoiLon);
        mTextViewPoiCity = view.findViewById(R.id.textViewPoiCity);
        mTextViewPoiPostalCode = view.findViewById(R.id.textViewPoiPostalCode);
        mTextViewPoiStreet = view.findViewById(R.id.textViewPoiStreet);
        mTextViewPoiNumber = view.findViewById(R.id.textViewPoiNumber);

        mEditTextPhone = view.findViewById(R.id.editTextPoiPhone);
        mEditTextSubject = view.findViewById(R.id.editTextSubject);
        mEditTextMessage = view.findViewById(R.id.editTextMessage);

        mRadioGroupVins = view.findViewById(R.id.radioGroupVins);
        mRadioGroupVins.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final View view = group.findViewById(checkedId);
                mCurrentVin = view == null ? null : (String) view.getTag();
            }
        });
        mButtonSend2Car = view.findViewById(R.id.buttonSend2Car);

        mButtonSend2Car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mServiceMessage.setPhone1(mEditTextPhone.getText().toString());
                mServiceMessage.setSubject(mEditTextSubject.getText().toString());
                mServiceMessage.setMessage(mEditTextMessage.getText().toString());

                if (mListener != null) {
                    mListener.onSend2CarClicked();
                }
            }
        });
        return view;
    }

    public void setServiceMessage(final ServiceMessage serviceMessage) {
        mServiceMessage = serviceMessage;
        updateView();
    }

    public void setVins(Map<String,String> vins) {
        mVins.clear();
        mVins.putAll(vins);
        updateView();
    }

    public void setCurrentVin(String vin) {
        mCurrentVin = vin;
        updateView();
    }

    public String getSelectedVin() {
        final int id = mRadioGroupVins.getCheckedRadioButtonId();
        if (id != -1) {
            final View view = mRadioGroupVins.findViewById(id);
            if (view != null) {
                return (String) view.getTag();
            }
        }
        return null;
    }

    public void updateView() {
        if (mServiceMessage != null) {
            mTextViewPoiName.setText(mServiceMessage.getName());
            mTextViewPoiLat.setText(mServiceMessage.getLat());
            mTextViewPoiLon.setText(mServiceMessage.getLng());
            mTextViewPoiCity.setText(mServiceMessage.getCity());
            mTextViewPoiPostalCode.setText(mServiceMessage.getZip());
            mTextViewPoiStreet.setText(mServiceMessage.getStreet());
            mTextViewPoiNumber.setText(mServiceMessage.getNumber());
            mEditTextPhone.setText(mServiceMessage.getPhone1());
            mEditTextSubject.setText(mServiceMessage.getSubject());
            mEditTextMessage.setText(mServiceMessage.getMessage());

            final int numButtons = mRadioGroupVins.getChildCount();
            final Set<View> viewsToRemove = new HashSet<>();
            final Set<Object> tags = new HashSet<>();
            for (int i=0; i<numButtons; i++) {
                final View child = mRadioGroupVins.getChildAt(i);
                if (child instanceof RadioButton) {
                    final Object tag = child.getTag();
                    if (mVins.containsKey(tag)) {
                        tags.add(tag);
                    } else {
                        viewsToRemove.add(child);
                    }
                }
            }
            for (final View view:viewsToRemove) {
                view.setTag(null);
                mRadioGroupVins.removeView(view);
            }

            final Context context = mRadioGroupVins.getContext();
            for (final Map.Entry<String,String> entry : mVins.entrySet()) {
                final String vin = entry.getKey();
                if (!tags.contains(vin)) {
                    final RadioButton radioButton = new RadioButton(context);
                    radioButton.setText(entry.getValue());
                    radioButton.setTag(vin);
                    mRadioGroupVins.addView(radioButton);
                    tags.add(vin);
                }
            }
            final View view = mRadioGroupVins.findViewWithTag(mCurrentVin);
            if (view == null) {
                mRadioGroupVins.clearCheck();
            } else {
                if (!((RadioButton)view).isChecked()) {
                    mRadioGroupVins.check(view.getId());
                }
            }
            mButtonSend2Car.setActivated(true);
        } else {
            mTextViewPoiName.setText("");
            mTextViewPoiLat.setText("");
            mTextViewPoiLon.setText("");
            mTextViewPoiCity.setText("");
            mTextViewPoiPostalCode.setText("");
            mTextViewPoiStreet.setText("");
            mTextViewPoiNumber.setText("");
            mEditTextPhone.setText("");
            mEditTextSubject.setText("");
            mEditTextMessage.setText("");
            mButtonSend2Car.setActivated(false);
        }
    }
}

