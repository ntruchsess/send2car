package com.truchsess.send2car;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.truchsess.send2car.cd.Token;
import com.truchsess.send2car.cd.api.ErrorResponse;
import com.truchsess.send2car.cd.entity.ServiceMessage;
import com.truchsess.send2car.cd.entity.Vehicle;
import com.truchsess.send2car.component.GetApiKeysController;
import com.truchsess.send2car.component.GetVehiclesController;
import com.truchsess.send2car.component.SendServiceMessageController;
import com.truchsess.send2car.geo.GeoUrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private enum Story {
        ePreferences,
        eAbout,
        eArguments,
        eCurrentMessage
    }

    private Story currentStory;

    private PreferencesFragment mPreferencesFragment;
    private PreferencesActionsFragment mGetVinsFragment;
    private GeoFragment mGeoFragment;
    private ListPlacesFragment mListPlacesFragment;
    private PlaceFragment mPlaceFragment;
    private StatusFragment mStatusFragment;
    private AboutFragment mAboutFragment;
    private ActionBar mActionBar;

    private Token mToken;
    private GetVehiclesController mGetVehiclesController;
    private GetApiKeysController mGetApiKeysController;
    private SendServiceMessageController mServiceMessageController;
    private Set<DataSetObserver> mServiceMessageDataSetObservers = new HashSet<>();
    //need to store a strong reference to OnSharedPreferenceChangeListener. For Details see:
    //https://developer.android.com/reference/android/content/SharedPreferences.html#registerOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener)
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof PreferencesFragment) {
            mPreferencesFragment = (PreferencesFragment) fragment;
            return;
        }
        if (fragment instanceof PreferencesActionsFragment) {
            mGetVinsFragment = (PreferencesActionsFragment) fragment;
            return;
        }
        if (fragment instanceof AboutFragment) {
            mAboutFragment = (AboutFragment) fragment;
            return;
        }
        if (fragment instanceof GeoFragment) {
            mGeoFragment = (GeoFragment) fragment;
            return;
        }
        if (fragment instanceof ListPlacesFragment) {
            mListPlacesFragment = (ListPlacesFragment) fragment;
            return;
        }
        if (fragment instanceof PlaceFragment) {
            mPlaceFragment = (PlaceFragment) fragment;
            return;
        }
        if (fragment instanceof StatusFragment) {
            mStatusFragment = (StatusFragment) fragment;
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mToken = new Token();
        mGetApiKeysController = new GetApiKeysController();
        mGetVehiclesController = new GetVehiclesController();
        mServiceMessageController = new SendServiceMessageController();

        try {
            final InputStream in = getBaseContext().getAssets().open(SendServiceMessageController.NOMINATIM_PROPERTIES);
            if (in != null) {
                Reader reader = new InputStreamReader(in,"UTF-8");
                final PropertyResourceBundle nominatimCategories = new PropertyResourceBundle(reader);
                mServiceMessageController.setCategories(nominatimCategories);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mGetVinsFragment.setListener(new PreferencesActionsFragment.Listener() {

            @Override
            public void onLoadKeysClicked() {
                getApiKeys();
            }

            @Override
            public void onGetVinsClicked() {
                getVehicles();
            }
        });

        setupListPlacesFragment();

        setupPlaceFragement();

        mGeoFragment.setListener(new GeoFragment.Listener() {
            @Override
            public void onLookupGeoDataClicked() {
                mServiceMessageController.setGeoUrl(mGeoFragment.getGeoUrl());
                getGeoData();
            }
        });

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getString(R.string.key_preference_vins))) {
                    setVinsFromPreferences(sharedPreferences);
                }
            }
        };
        sp.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        setVinsFromPreferences(sp);

        currentStory = Story.eArguments;

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            final Uri uri = intent.getData();

            mServiceMessageController.setGeoUrlFromUri(uri);

            final GeoUrl geoUrl = mServiceMessageController.getGeoUrl();

            if (mGeoFragment != null) {
                mGeoFragment.setGeoUrl(geoUrl);
            }

            getGeoData();
        }
    }

    private void setupListPlacesFragment() {

        mListPlacesFragment.setListAdapter(new ListAdapter() {

            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {
                mServiceMessageDataSetObservers.add(observer);
            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {
                mServiceMessageDataSetObservers.remove(observer);
            }

            @Override
            public int getCount() {
                return mServiceMessageController.getNumPlaces();
            }

            @Override
            public Object getItem(int position) {
                return mServiceMessageController.getPlace(position);
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
                final View view = convertView == null ?
                        getLayoutInflater().inflate(R.layout.list_place, null, true) :
                        convertView;
                mServiceMessageController.setPlaceIndex(position);
                final TextView textPlaceName = (TextView) view.findViewById(R.id.textListPlaceName);
                textPlaceName.setText(mServiceMessageController.getPlaceName());
                final TextView textPlaceDetails = (TextView) view.findViewById(R.id.textListPlaceAddress);
                textPlaceDetails.setText(mServiceMessageController.getPlaceDetails());
                return view;
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
                return mServiceMessageController.getNumPlaces() == 0;
            }
        });

        mListPlacesFragment.setListPlacesListener(new ListPlacesFragment.Listener() {
            @Override
            public void onListItemClick(int position) {
                mServiceMessageController.setPlaceIndex(position);
                mServiceMessageController.createServiceMessage();
                mStatusFragment.clear();
                currentStory = Story.eCurrentMessage;
                updateView();
            }
        });
    }

    private void setupPlaceFragement() {

        mServiceMessageController.setSubjectListListener(new SendServiceMessageController.SubjectListListener() {
            @Override
            public void onSubjectListChanged() {
                mPlaceFragment.onSubjectListChanged();
            }
        });

        mServiceMessageController.setServiceMessageListener(new SendServiceMessageController.ServiceMessageListener() {
            @Override
            public void onServiceMessageChanged() {
                mPlaceFragment.setServiceMessage(mServiceMessageController.getServiceMessage());
            }
        });

        mPlaceFragment.setSubjectListAdapter(new PlaceFragment.SubjectListAdapter() {
            @Override
            public int getNumSubjects() {
                return mServiceMessageController.getNumSubjects();
            }

            @Override
            public String getSubject(final int position) {
                return mServiceMessageController.getSubject(position);
            }

            @Override
            public void onSubjectSelected(final int position) {
                mServiceMessageController.setSubjectIndex(position);
            }
        });

        mPlaceFragment.setSubjectTextEditListener(new PlaceFragment.SubjectTextEditListener() {
            @Override
            public void onSubjectChanged(final String subject) {
                mServiceMessageController.setSubject(subject);
            }
        });

        mPlaceFragment.setPhoneTextEditListener(new PlaceFragment.PhoneTextEditListener() {
            @Override
            public void onPhoneChanged(final String phoneNumber) {
                mServiceMessageController.setPhone(phoneNumber);
            }
        });

        mPlaceFragment.setMessageTextEditListener(new PlaceFragment.MessageTextEditListener() {
            @Override
            public void onMessageChanged(final String message) {
                mServiceMessageController.setMessage(message);
            }
        });

        mPlaceFragment.setSend2CarButtonListener(new PlaceFragment.Send2CarButtonListener() {

            @Override
            public void onSend2CarClicked() {

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                final String vin = mPlaceFragment.getSelectedVin();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.key_preference_selected_vin),vin);
                editor.commit();

                if (vin == null) {
                    mStatusFragment.setStatus(getString(R.string.status_servicemessage),getString(R.string.status_novin));
                    updateView();
                } else {
                    mStatusFragment.setStatus(getString(R.string.status_servicemessage),getString(R.string.status_authenticating));
                    updateView();

                    setTokenCredentials(sp);
                    mToken.checkToken(new Token.AuthenticationListener() {
                        @Override
                        public void onAuthentication() {
                            mStatusFragment.setStatus(getString(R.string.status_servicemessage),getString(R.string.status_sending));
                            updateView();

                            mServiceMessageController.setVin(vin);
                            mServiceMessageController.sendServiceMessage2Car(mToken, new SendServiceMessageController.ServiceMessageSendListener() {
                                @Override
                                public void onSent(String status) {
                                    final String statusTxt = status == null ? getString(R.string.status_unknown)
                                            : status.equals("OK") ? getString(R.string.status_sent)
                                            : status;
                                    mStatusFragment.setStatus(getString(R.string.status_servicemessage),statusTxt);
                                    updateView();
                                }

                                @Override
                                public void onError(String error) {
                                    mStatusFragment.setStatus(getString(R.string.status_servicemessage_error), error);
                                    updateView();
                                }
                            });
                        }

                        @Override
                        public void onAuthenticationFailure(ErrorResponse errorResponse) {
                            mStatusFragment.setStatus(errorResponse.getError(), errorResponse.getError_description());
                            updateView();
                        }
                    });
                }
            }
        });
    }

    private void setVinsFromPreferences(SharedPreferences sharedPreferences) {

        final String keyPreferenceVins = getString(R.string.key_preference_vins);
        final String vinsPreference = sharedPreferences.getString(keyPreferenceVins,"");

        final EditTextPreference editTextPreferenceVins = (EditTextPreference) mPreferencesFragment.findPreference(keyPreferenceVins);
        if (editTextPreferenceVins != null) {
            final String editTextPreferenceVinsText = editTextPreferenceVins.getText();
            if (editTextPreferenceVins != null && !editTextPreferenceVins.equals(vinsPreference)) {
                editTextPreferenceVins.setText(vinsPreference);
            }
        }

        final Map<String,String> vins = new HashMap();
        final Pattern pattern = Pattern.compile("([A-Z0-9]+)(\\(([^\\n]*)\\)){0,1}");
        Matcher matcher = pattern.matcher(vinsPreference);
        while(matcher.find()) {
            final int num = matcher.groupCount();
            final String vin = matcher.group(1);
            final String description = matcher.group(3);
            vins.put(vin,description == null ? vin : description);
        }
        mPlaceFragment.setVins(vins);
        mPlaceFragment.setCurrentVin(sharedPreferences.getString(getString(R.string.key_preference_selected_vin),""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_help)));
                startActivity(browserIntent);
                break;
            case R.id.preferences:
                mStatusFragment.clear();
                currentStory = Story.ePreferences;
                updateView();
                break;
            case R.id.about:
                mStatusFragment.clear();
                currentStory = Story.eAbout;
                updateView();
                break;
            case android.R.id.home:
                mStatusFragment.clear();
                currentStory = Story.eArguments;
                updateView();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        updateView(true);
    }

    private void getGeoData() {

        mStatusFragment.setStatus(getString(R.string.status_lookup_geo),getString(R.string.status_loading));
        updateView();

        mServiceMessageController.lookupGeodata(new SendServiceMessageController.ServiceMessageUpdateListener() {
            @Override
            public void onUpdate() {
                for (DataSetObserver observer : mServiceMessageDataSetObservers) {
                    observer.onChanged();
                }
                mStatusFragment.setStatus(getString(R.string.status_lookup_geo),getString(R.string.status_success));
                if (mServiceMessageController.getNumPlaces() <= 1) {
                    createAndDisplayCurrentMessage();
                }
                updateView();
            }

            @Override
            public void onError(String error) {
                for (DataSetObserver observer : mServiceMessageDataSetObservers) {
                    observer.onInvalidated();
                }
                mStatusFragment.setStatus(getString(R.string.status_lookup_geo_error), error);
                createAndDisplayCurrentMessage();
                updateView();
            }
        });
    }

    private void createAndDisplayCurrentMessage() {
        mServiceMessageController.setPlaceIndex(0);
        mServiceMessageController.createServiceMessage();
        mServiceMessageController.setSubjectIndex(0);
        currentStory = Story.eCurrentMessage;
    }

    private void getApiKeys() {

        mStatusFragment.setStatus(getString(R.string.status_get_keys),getString(R.string.status_loading));
        updateView();

        mGetApiKeysController.getPortalFlagsResponse(new GetApiKeysController.GetPortalFlagsListener() {
            @Override
            public void onUpdate() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.key_preference_apikey), mGetApiKeysController.getApiKey());
                editor.putString(getString(R.string.key_preference_apisecret), mGetApiKeysController.getApiSecret());
                editor.commit();
                mStatusFragment.setStatus(getString(R.string.status_get_keys),getString(R.string.status_success));
                updateView();
            }

            @Override
            public void onError(String error) {
                mStatusFragment.setStatus(getString(R.string.status_get_keys_error), error);
                updateView();
            }
        });
    }

    private void getVehicles() {

        mStatusFragment.setStatus(getString(R.string.status_get_vehicles),getString(R.string.status_loading));
        updateView();

        setTokenCredentials(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

        mToken.checkToken(new Token.AuthenticationListener() {
            @Override
            public void onAuthentication() {
                mGetVehiclesController.getVehiclesResponse(mToken, new GetVehiclesController.GetVehiclesListener() {
                    @Override
                    public void onUpdate() {
                        List<String> vins = new ArrayList<>();
                        int num = mGetVehiclesController.getNumVehicles();
                        boolean isNotFirst = false;
                        StringBuilder vinBuilder = new StringBuilder();
                        for (int i = 0; i < num; i++) {
                            Vehicle vehicle = mGetVehiclesController.getVehicle(i);
                            if (isNotFirst) {
                                vinBuilder.append('\n');
                            } else {
                                isNotFirst = true;
                            }
                            vinBuilder.append(vehicle.getVin())
                                    .append("(")
                                    .append(vehicle.getModel())
                                    .append(")");
                        }
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(getString(R.string.key_preference_vins), vinBuilder.toString());
                        editor.commit();
                        mStatusFragment.setStatus(getString(R.string.status_get_vehicles),getString(R.string.status_success));
                        updateView();
                    }

                    @Override
                    public void onError(String error) {
                        mStatusFragment.setStatus(getString(R.string.status_get_vehicles_error), error);
                        updateView();
                    }
                });
            }

            @Override
            public void onAuthenticationFailure(ErrorResponse errorResponse) {
                mStatusFragment.setStatus(errorResponse.getError(),errorResponse.getError_description());
                updateView();
            }
        });
    }

    private void setTokenCredentials(SharedPreferences sharedPreferences) {
        final String api_key = sharedPreferences.getString(getString(R.string.key_preference_apikey), "");
        final String api_secret = sharedPreferences.getString(getString(R.string.key_preference_apisecret), "");
        final String username = sharedPreferences.getString(getString(R.string.key_preference_username), "");
        final String password = sharedPreferences.getString(getString(R.string.key_preference_password), "");
        mToken.setCredentials(username, password, api_key, api_secret);
    }

    private void updateView(boolean force) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (currentStory) {
            case ePreferences:
                mActionBar.setDisplayHomeAsUpEnabled(true);
                showFragment(ft,mPreferencesFragment,force);
                hideFragment(ft,mAboutFragment,force);
                showFragment(ft,mGetVinsFragment,force);
                hideFragment(ft,mGeoFragment,force);
                hideFragment(ft,mListPlacesFragment,force);
                hideFragment(ft,mPlaceFragment,force);
                break;

            case eAbout:
                mActionBar.setDisplayHomeAsUpEnabled(true);
                hideFragment(ft,mPreferencesFragment,force);
                showFragment(ft,mAboutFragment,force);
                hideFragment(ft,mGetVinsFragment,force);
                hideFragment(ft,mGeoFragment,force);
                hideFragment(ft,mListPlacesFragment,force);
                hideFragment(ft,mPlaceFragment,force);
                break;

            case eArguments:
                mActionBar.setDisplayHomeAsUpEnabled(false);
                hideFragment(ft,mPreferencesFragment,force);
                hideFragment(ft,mAboutFragment,force);
                hideFragment(ft,mGetVinsFragment,force);
                showFragment(ft,mGeoFragment,force);
                showFragment(ft,mListPlacesFragment,force);
                hideFragment(ft,mPlaceFragment,force);
                break;

            case eCurrentMessage:
                mActionBar.setDisplayHomeAsUpEnabled(true);
                hideFragment(ft,mPreferencesFragment,force);
                hideFragment(ft,mAboutFragment,force);
                hideFragment(ft,mGetVinsFragment,force);
                hideFragment(ft,mGeoFragment,force);
                hideFragment(ft,mListPlacesFragment,force);
                showFragment(ft,mPlaceFragment,force);

                final ServiceMessage serviceMessage = mServiceMessageController.getServiceMessage();
                mPlaceFragment.setServiceMessage(serviceMessage);
                break;
        }
        if (mStatusFragment.isSet()) {
            showFragment(ft,mStatusFragment,force);
        } else {
            hideFragment(ft,mStatusFragment,force);
        }
        ft.commit();
    }

    private void updateView() {
        updateView(false);
    }

    private void hideFragment(final FragmentTransaction ft, final Fragment f, final boolean force) {
        if (f != null && (f.isVisible() || force)) {
            ft.hide(f);
        }
    }

    private void showFragment(final FragmentTransaction ft, final Fragment f, final boolean force) {
        if (f != null && (f.isHidden() || force)) {
            ft.show(f);
        }
    }
}
