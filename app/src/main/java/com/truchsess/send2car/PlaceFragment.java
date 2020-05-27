package com.truchsess.send2car;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
    private Spinner mSpinnerPoiSubject;
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

    private SubjectTextEditListener mSubjectTextEditListener;
    private PhoneTextEditListener mPhoneTextEditListener;
    private MessageTextEditListener mMessageTextEditListener;
    private Send2CarButtonListener mSend2CarButtonListener;

    private SubjectListAdapter mSubjectListAdapter;
    private Set<DataSetObserver> mSubjectListObserver = new HashSet<>();
    private Map<String,String> mVins = new HashMap<>();
    private String mCurrentVin;

    public interface SubjectListAdapter {
        int getNumSubjects();
        String getSubject(int position);
        void onSubjectSelected(int position);
    }

    public interface Send2CarButtonListener {
        void onSend2CarClicked();
    }

    public interface PhoneTextEditListener {
        void onPhoneChanged(String phoneNumber);
    }

    public interface SubjectTextEditListener {
        void onSubjectChanged(String subject);
    }

    public interface MessageTextEditListener {
        void onMessageChanged(String message);
    }

    public void setSend2CarButtonListener(Send2CarButtonListener send2CarButtonListener) {
        mSend2CarButtonListener = send2CarButtonListener;
    }

    public void setSubjectTextEditListener(SubjectTextEditListener subjectTextEditListener) {
        this.mSubjectTextEditListener = subjectTextEditListener;
    }

    public void setPhoneTextEditListener(PhoneTextEditListener phoneTextEditListener) {
        mPhoneTextEditListener = phoneTextEditListener;
    }

    public void setMessageTextEditListener(MessageTextEditListener messageTextEditListener) {
        this.mMessageTextEditListener = messageTextEditListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        mSpinnerPoiSubject = view.findViewById(R.id.spinnerPoiSubject);

        mSpinnerPoiSubject.setAdapter(new SpinnerAdapter() {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                final View view = convertView == null ?
                        getLayoutInflater().inflate(R.layout.spinner_subject, null, true) :
                        convertView;
                final TextView textSubject = (TextView) view.findViewById(R.id.textSpinnerSubject);
                textSubject.setText(mSubjectListAdapter == null ? null : mSubjectListAdapter.getSubject(position));
                return view;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {
                mSubjectListObserver.add(observer);
            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {
                mSubjectListObserver.remove(observer);
            }

            @Override
            public int getCount() {
                return mSubjectListAdapter == null ? 0 : mSubjectListAdapter.getNumSubjects();
            }

            @Override
            public Object getItem(int position) {
                return mSubjectListAdapter == null ? null : mSubjectListAdapter.getSubject(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getDropDownView(position,convertView,parent);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return mSubjectListAdapter == null ? true : mSubjectListAdapter.getNumSubjects() == 0;
            }
        });

        mSpinnerPoiSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSubjectListAdapter != null) {
                    mSubjectListAdapter.onSubjectSelected(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                if (mPhoneTextEditListener!=null) {
                    final String phone = mEditTextPhone.getText().toString();
                    if (!phone.equals(mServiceMessage.getPhone1())) {
                        mPhoneTextEditListener.onPhoneChanged(phone);
                    }
                }
                if (mSubjectTextEditListener!=null) {
                    final String subject = mEditTextSubject.getText().toString();
                    if (!subject.equals(mServiceMessage.getSubject())) {
                        mSubjectTextEditListener.onSubjectChanged(subject);
                    }
                }
                if (mMessageTextEditListener!=null) {
                    final String message = mEditTextMessage.getText().toString();
                    if (!message.equals(mServiceMessage.getMessage())) {
                        mMessageTextEditListener.onMessageChanged(message);
                    }
                }
                if (mSend2CarButtonListener != null) {
                    mSend2CarButtonListener.onSend2CarClicked();
                }
            }
        });
        return view;
    }

    public void setServiceMessage(final ServiceMessage serviceMessage) {
        mServiceMessage = serviceMessage;
        updateView();
    }

    public void setSubjectListAdapter(final SubjectListAdapter subjectListAdapter) {
        mSubjectListAdapter = subjectListAdapter;
    }

    public void onSubjectListChanged() {
        for (DataSetObserver observer: mSubjectListObserver) {
            observer.onChanged();
        }
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
            if (!mEditTextPhone.getText().equals(mServiceMessage.getPhone1())) {
                mEditTextPhone.setText(mServiceMessage.getPhone1());
            }
            if (!mEditTextSubject.getText().equals(mServiceMessage.getSubject())) {
                mEditTextSubject.setText(mServiceMessage.getSubject());
            }
            if (!mEditTextMessage.getText().equals(mServiceMessage.getMessage())) {
                mEditTextMessage.setText(mServiceMessage.getMessage());
            }

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

