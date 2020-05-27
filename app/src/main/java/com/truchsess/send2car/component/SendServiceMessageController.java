package com.truchsess.send2car.component;

import android.net.Uri;

import com.truchsess.send2car.cd.SendServiceMessage;
import com.truchsess.send2car.cd.Token;
import com.truchsess.send2car.cd.api.CDApi;
import com.truchsess.send2car.cd.entity.CDApiJSONError;
import com.truchsess.send2car.cd.entity.ServiceMessage;
import com.truchsess.send2car.geo.GeoUrl;
import com.truchsess.send2car.geo.api.NominatimCall;
import com.truchsess.send2car.geo.api.NominatimError;
import com.truchsess.send2car.geo.entity.Address;
import com.truchsess.send2car.geo.entity.ExtraTags;
import com.truchsess.send2car.geo.entity.NameDetails;
import com.truchsess.send2car.geo.entity.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
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
public class SendServiceMessageController {

    public static String NOMINATIM_PROPERTIES = "nominatim.properties";

    private final SendServiceMessage mSendServiceMessage;
    private final NominatimCall mNominatimCall;
    private final List<Place> mPlaceList;
    private List<String> mSubjectList;
    private SubjectListListener mSubjectListListener;
    private final List<String> mVinList;

    private GeoUrl mGeoUrl;
    private Place mPlace;
    private String mPlaceName;
    private String mPlaceDetails;
    private ServiceMessage mServiceMessage;
    private ServiceMessageListener mServiceMessageListener;

    private ResourceBundle categories;

    public interface SubjectListListener {
        void onSubjectListChanged();
    }

    public interface ServiceMessageListener {
        void onServiceMessageChanged();
    }

    public SendServiceMessageController() {
        mSendServiceMessage = new SendServiceMessage(CDApi.CD_SERVER);
        mGeoUrl = new GeoUrl();
        mNominatimCall = new NominatimCall();

        mPlaceList = new ArrayList<>();
        mSubjectList = new ArrayList<>();
        mVinList = new ArrayList<>();
    }

    public void setGeoUrlFromUri(final Uri geoUri) {
        mGeoUrl.fromUri(geoUri);
    }

    public void setGeoUrl(final GeoUrl geoUrl) {
        this.mGeoUrl = geoUrl;
    }

    public interface ServiceMessageUpdateListener {
        void onUpdate();
        void onError(String error);
    }

    public interface ServiceMessageSendListener {
        void onSent();
        void onError(String error);
    }

    public void createServiceMessage() {

        mServiceMessage = new ServiceMessage();

        mServiceMessage.setLat(GeoUrl.degreeToString(mPlace == null ? mGeoUrl.getLat() : mPlace.getLat()));
        mServiceMessage.setLng(GeoUrl.degreeToString(mPlace == null ? mGeoUrl.getLon() : mPlace.getLon()));

        createName();

        createSubjectList();

        createMessage();

        createAddress();

        if (mServiceMessageListener != null) {
            mServiceMessageListener.onServiceMessageChanged();
        }
    }

    private void createName() {
        final String name = mPlaceName;
        mServiceMessage.setName(name == null || name.isEmpty() ? mGeoUrl.getDescription() : name);
    }

    private void parsePlaceNameDetails() {

        mPlaceName = null;
        mPlaceDetails = null;

        if (mPlace == null) {
            return;
        }

        //https://github.com/openstreetmap/Nominatim/blob/master/settings/address-levels.json
        final StringBuilder nameBuilder = new StringBuilder();

        final String displayName = mPlace.getDisplay_name();
        if (displayName != null && !displayName.isEmpty()) {
            final String[] parts = displayName.split(", ",2);
            if (parts.length == 0) {
                nameBuilder.append(displayName);
            } else {
                nameBuilder.append(parts[0]);
                if (parts.length > 1) {
                    mPlaceDetails = parts[1];
                }
            }
        }
        if (nameBuilder.length() == 0) {
            final NameDetails nameDetails = mPlace.getNamedetails();
            if (nameDetails != null) {
                final String nameDetailsName = nameDetails.getName();
                if (nameDetailsName != null && !nameDetailsName.isEmpty()) {
                    nameBuilder.append(nameDetailsName);
                }
            }
        }
        if (nameBuilder.length() == 0) {
            final String name = mPlace.getName();
            if (name != null && !name.isEmpty()) {
                nameBuilder.append(name);
            }
        }
        // https://github.com/openstreetmap/Nominatim/blob/80df4d3b560f5b1fd550dcf8cdc09a992b69fee0/settings/partitionedtags.def
        final String category = getCategory();
        if (category != null) {
            final boolean useParanthesis = nameBuilder.length() > 0;
            if (useParanthesis) {
                nameBuilder.append(" (");
            }
            nameBuilder.append(category);
            if (useParanthesis) {
                nameBuilder.append(")");
            }
        }
        if (nameBuilder.length() > 0) {
            mPlaceName = nameBuilder.toString();
        }

        if (mPlaceDetails == null) {
            final StringBuilder addressBuilder = new StringBuilder();
            final Address address = mPlace.getAddress();
            if (address != null) {
                final String street = getStreetFromAddress(address);
                if (street != null) {
                    addressBuilder.append(street);
                }
                final String number = address.getHouse_number();
                if (number != null && !number.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(' ');
                    }
                    addressBuilder.append(number);
                }
                final String city = getCityFromAddress(address);
                final String zip = address.getPostcode();
                if (zip != null && !zip.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(zip);
                }

                if (city != null) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(zip == null || zip.isEmpty() ? ", " : ' ');
                    }
                    addressBuilder.append(city);
                }

                final String country = address.getCountry();
                if (country != null && !country.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(country);
                }

                final String cityDistrict = address.getCity_district();
                if (cityDistrict != null && !cityDistrict.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(cityDistrict);
                }

                final String countryCode = address.getCountry_code();
                if (countryCode != null && !countryCode.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(countryCode);
                }

                final String state = address.getState();
                if (state != null && !state.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(state);
                }

                final String stateDistrict = address.getState_district();
                if (stateDistrict != null && !stateDistrict.isEmpty()) {
                    if (addressBuilder.length() > 0) {
                        addressBuilder.append(", ");
                    }
                    addressBuilder.append(stateDistrict);
                }
            }
            mPlaceDetails = addressBuilder.length() == 0 ? null : addressBuilder.toString();
        }
    }

    private String getCategory() {
        if (mPlace != null) {
            final String type = mPlace.getType();
            if (type != null && !type.isEmpty() && !type.equals("yes")) {
                if (categories != null) {
                    try {
                        return categories.getString(type);
                    } catch (MissingResourceException mre) {
                    }
                } else {
                    return type;
                }
            }
        }
        return null;
    }

    private String getStreetFromAddress(final Address address) {

        final String road = address.getRoad();
        if (road != null && !road.isEmpty()) {
            return road;
        } else {
            final String footway = address.getFootway();
            if (footway != null && !footway.isEmpty()) {
                return footway;
            } else {
                final String pedestrian = address.getPedestrian();
                if (pedestrian != null && !pedestrian.isEmpty()) {
                    return pedestrian;
                }
            }
        }
        return null;
    }

    private String getCityFromAddress(final Address address) {

        final String city = address.getCity();
        if (city != null && !city.isEmpty()) {
            return city;
        } else {
            final String town = address.getTown();
            if (town != null && !town.isEmpty()) {
                return town;
            } else {
                final String village = address.getVillage();
                if (village != null && !village.isEmpty()) {
                    return village;
                } else {
                    final String suburb = address.getSuburb();
                    if (suburb != null && !suburb.isEmpty()) {
                        return suburb;
                    }
                }
            }
        }
        return null;
    }

    private void createSubjectList() {
        mSubjectList.clear();
        addToSubjectList(mGeoUrl.getDescription());
        addToSubjectList(mPlaceName);
        addToSubjectList(mPlaceDetails);
        addToSubjectList(getCategory());
        addToSubjectListWithCategory(mPlaceName);
        addToSubjectListWithCategory(mPlaceDetails);
        if (mSubjectList.isEmpty()) {
            addToSubjectList("neues Fahrziel");
        }
        if (mSubjectListListener !=null) {
            mSubjectListListener.onSubjectListChanged();
        }
    }

    private void addToSubjectListWithCategory(final String subject) {
        if (subject != null && !subject.isEmpty()) {
            final String category = getCategory();
            if (category != null && !category.isEmpty()) {
                addToSubjectList(category + " " + subject);
            }
        }
    }

    private void addToSubjectList(final String subject) {
        if (subject != null && !subject.isEmpty()) {
            final String truncatedSubject = truncateSubject(subject);
            if (!mSubjectList.contains(truncatedSubject)) {
                mSubjectList.add(truncatedSubject);
            }
        }
    }

    private String truncateSubject(final String subject) {
        return subject == null ? null : subject.length() > 20 ? subject.substring(0, 19) : subject;
    }

    private String truncateMessage(final String message) {
        return message == null ? null : message.length() > 255 ? message.substring(0, 255) : message;
    }

    private void createMessage() {

        boolean first = true;
        final StringBuilder messageBuilder = new StringBuilder();

        final String name = mServiceMessage.getName();
        if (name != null && !name.isEmpty()) {
            messageBuilder.append(name);
            first = false;
        } else {
            final String description = mGeoUrl.getDescription();
            if (description != null && !description.isEmpty()) {
                messageBuilder.append(description);
                first = false;
            }
        }

        final ExtraTags extraTags = mPlace == null ? null : mPlace.getExtratags();
        if (extraTags != null) {
            final String sport = extraTags.getSport();
            if (sport != null && !sport.isEmpty()) {
                if (!first) {
                    messageBuilder.append(" (");
                }
                messageBuilder.append(sport);
                if (!first) {
                    messageBuilder.append(")");
                }
                first = false;
            }
        }

        if (extraTags != null) {

            final String openingHours = extraTags.getOpening_hours();
            if (openingHours!=null && !openingHours.isEmpty()) {
                if (!first) {
                    messageBuilder.append("\n");
                }
                messageBuilder.append(openingHours.replaceAll("\\s*;\\s*","\n").replaceAll(" {2,}"," "));
            }

            final String phone = extraTags.getContactPhone();
            if (phone!=null && !phone.isEmpty()) {
                mServiceMessage.setFormattedPhoneNumber(phone);
                mServiceMessage.setPhone1(phone);
                mServiceMessage.setPhonetype1("UNKNOWN");
            }

            final String website = extraTags.getContactWebsite();
            if (website!=null && !website.isEmpty()) {
                mServiceMessage.setUrl(website);
            }
        }

        setMessage(messageBuilder.toString());
    }

    private void createAddress() {

        final Address address = mPlace == null ? null : mPlace.getAddress();
        if (address != null) {
            final String street = getStreetFromAddress(address);
            if (street != null) {
                mServiceMessage.setStreet(street);
            } else {
                final String name = mPlace.getName();
                if (name != null && !name.isEmpty() && !name.equals(mServiceMessage.getName())) {
                    mServiceMessage.setStreet(name);
                } else {
                    mServiceMessage.setStreet(null);
                }
            }

            final String houseNumber = address.getHouse_number();
            mServiceMessage.setNumber(houseNumber == null || houseNumber.isEmpty() ? null : houseNumber);

            final String city = getCityFromAddress(address);
            mServiceMessage.setCity(city);

            final String suburb = address.getSuburb();
            if (suburb != null && !suburb.isEmpty() && !suburb.equals(city)) {
                mServiceMessage.setQuarter(suburb);
            } else {
                final String neighbourhood = address.getNeighbourhood();
                mServiceMessage.setQuarter(neighbourhood == null || neighbourhood.isEmpty() ? null : neighbourhood);
            }

            final String postcode = address.getPostcode();
            mServiceMessage.setZip(postcode == null || postcode.isEmpty() ? null : postcode);

            final String country = address.getCountry();
            mServiceMessage.setCountry(country == null || country.isEmpty() ? null : country);

            final String cityDistrict = address.getCity_district();
            mServiceMessage.setDistrict(cityDistrict == null || cityDistrict.isEmpty() ? null : cityDistrict);

            final String countryCode = address.getCountry_code();
            mServiceMessage.setCountryCode(countryCode == null || countryCode.isEmpty() ? null : countryCode);

            final String state = address.getState();
            mServiceMessage.setRegion(state == null || state.isEmpty() ? null : state);

            final String stateDistrict = address.getState_district();
            mServiceMessage.setCounty(stateDistrict == null || stateDistrict.isEmpty() ? null : stateDistrict);
        }
    }

    public void lookupGeodata(final ServiceMessageUpdateListener listener) {

        if (mGeoUrl != null) {

            final double lat = mGeoUrl.getLat();
            final double lon = mGeoUrl.getLon();
            final String description = mGeoUrl.getDescription();

            if (Double.isNaN(lat) || Double.isNaN(lon) || (lat==0.0 && lon==0.0) && description != null && !description.isEmpty()) {
                mNominatimCall.requestSearch(description,  new NominatimCall.NominatimCallListener<List<Place>>() {
                    @Override
                    public void onSuccess(final List<Place> places) {
                        mPlaceList.clear();
                        mPlaceList.addAll(places);
                        listener.onUpdate();
                    }

                    @Override
                    public void onError(final NominatimError error) {
                        mPlaceList.clear();
                        listener.onError(Integer.toString(error.getCode())+": "+error.getMessage());
                    }
                });
            } else {
                mNominatimCall.requestReverse(mGeoUrl.getLat(), mGeoUrl.getLon(), new NominatimCall.NominatimCallListener<Place>() {
                    @Override
                    public void onSuccess(final Place place) {
                        mPlaceList.clear();
                        final String error = place.getError();
                        if (error!=null) {
                            listener.onError(error);
                        } else {
                            mPlaceList.add(place);
                            listener.onUpdate();
                        }
                    }

                    @Override
                    public void onError(final NominatimError error) {
                        mPlaceList.clear();
                        listener.onError(Integer.toString(error.getCode())+": "+error.getMessage());
                    }
                });
            }
        }
    }

    public void sendServiceMessage2Car(final Token token, final ServiceMessageSendListener listener) {

        final ServiceMessage serviceMessage = getServiceMessage();
        if (serviceMessage != null) {
            serviceMessage.setVins(mVinList);
            mSendServiceMessage.sendServiceMessage(serviceMessage, token.getAuthorization(), new SendServiceMessage.SendServiceMessageListener() {
                @Override
                public void onSuccess() {
                    listener.onSent();
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
        } else {
            listener.onError("serviceMessage is null");
        }
    }

    public GeoUrl getGeoUrl() {
        return mGeoUrl;
    }

    public void setCategories(ResourceBundle categories) {
        this.categories = categories;
    }

    public void setVin(String vin) {
        mVinList.clear();
        mVinList.add(vin);
    }

    public int getNumSubjects() {
        return mSubjectList.size();
    }

    public String getSubject(final int position) {
        return (position < 0 || position >= mSubjectList.size()) ? null : mSubjectList.get(position);
    }

    public void setSubjectIndex(final int position) {
        setSubject(getSubject(position));
    }

    public void setSubject(final String subject) {
        mServiceMessage.setSubject(truncateSubject(subject));
        if (mServiceMessageListener != null) {
            mServiceMessageListener.onServiceMessageChanged();
        }
    }

    public void setPhone(final String phone) {
        mServiceMessage.setPhone1(phone);
        if (mServiceMessageListener != null) {
            mServiceMessageListener.onServiceMessageChanged();
        }
    }

    public void setMessage(final String message) {
        mServiceMessage.setMessage(truncateMessage(message));
        if (mServiceMessageListener != null) {
            mServiceMessageListener.onServiceMessageChanged();
        }
    }

    public void setSubjectListListener(final SubjectListListener subjectListListener) {
        mSubjectListListener = subjectListListener;
    }

    public int getNumPlaces() {
        return mPlaceList.size();
    }

    public Place getPlace(final int index) {
        return index >=0 && index < mPlaceList.size() ? mPlaceList.get(index) : null;
    }

    public void setPlaceIndex(final int index) {
        mPlace = getPlace(index);
        parsePlaceNameDetails();
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public String getPlaceDetails() {
        return mPlaceDetails;
    }

    public void setServiceMessageListener(ServiceMessageListener serviceMessageListener) {
        mServiceMessageListener = serviceMessageListener;
    }

    public ServiceMessage getServiceMessage() {
        return mServiceMessage;
    }

}
