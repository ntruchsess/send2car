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
public class SendServiceMessageController {

    public static String NOMINATIM_PROPERTIES = "nominatim.properties";

    private final SendServiceMessage sendServiceMessage;
    private final NominatimCall nominatimCall;
    private final List<ServiceMessage> serviceMessageList;
    private final List<String> vinList;

    private GeoUrl geoUrl;
    private int serviceMessageIndex = -1;

    private ResourceBundle categories;

    public SendServiceMessageController() {
        sendServiceMessage = new SendServiceMessage(CDApi.CD_SERVER);
        geoUrl = new GeoUrl();
        nominatimCall = new NominatimCall();
        serviceMessageList = new ArrayList<>();
        vinList = new ArrayList<>();
    }

    public void setGeoUrlFromUri(final Uri geoUri) {
        geoUrl.fromUri(geoUri);
    }

    public void setGeoUrl(final GeoUrl geoUrl) {
        this.geoUrl = geoUrl;
    }

    public interface ServiceMessageUpdateListener {
        void onUpdate();
        void onError(String error);
    }

    public interface ServiceMessageSendListener {
        void onSent();
        void onError(String error);
    }

    private ServiceMessage createServiceMessageFromPlace(final Place place) {

        final ServiceMessage serviceMessage = new ServiceMessage();

        serviceMessage.setLat(GeoUrl.degreeToString(place.getLat()));
        serviceMessage.setLng(GeoUrl.degreeToString(place.getLon()));

        final String description = geoUrl.getDescription();

        createName(serviceMessage, place, description);

        createSubject(serviceMessage, description);

        createMessage(serviceMessage, place, description);

        createAddress(serviceMessage, place);

        return serviceMessage;
    }

    private void createName(final ServiceMessage serviceMessage, final Place place, final String description) {
        //https://github.com/openstreetmap/Nominatim/blob/master/settings/address-levels.json
        final NameDetails nameDetails = place.getNamedetails();
        String name = null;
        if (nameDetails != null) {
            name = nameDetails.getName();
        }
        if (name == null || name.isEmpty()) {
            name = place.getName();
        }
        if (name != null && !name.isEmpty()) {
            // https://github.com/openstreetmap/Nominatim/blob/80df4d3b560f5b1fd550dcf8cdc09a992b69fee0/settings/partitionedtags.def
            final String type = place.getType();
            if (type != null && !type.isEmpty() && !type.equals("yes")) {
                if (categories != null) {
                    try {
                        serviceMessage.setName(name + " (" + categories.getString(type) + ")");
                    } catch (MissingResourceException mre) {
                        serviceMessage.setName(name + " (" + type + ")");
                    }
                } else {
                    serviceMessage.setName(name + " (" + type + ")");
                }
            } else {
                serviceMessage.setName(name);
            }
        } else {
            final String displayName = place.getDisplay_name();
            if (displayName != null && !displayName.isEmpty()) {
                serviceMessage.setName(displayName);
            } else {
                serviceMessage.setName(description);
            }
        }
    }

    private void createSubject(final ServiceMessage serviceMessage, final String description) {
        final String subject = description == null || description.isEmpty()
                ? serviceMessage.getName()
                : description;
        if (subject != null && !subject.isEmpty()) {
            serviceMessage.setSubject(subject.length() > 20 ? subject.substring(0, 19) : subject);
        } else {
            serviceMessage.setSubject("neues Fahrziel");
        }
    }

    private void createMessage(final ServiceMessage serviceMessage, final Place place, final String description) {

        boolean first = true;
        final StringBuilder messageBuilder = new StringBuilder();

        final String name = serviceMessage.getName();
        if (name != null && !name.isEmpty()) {
            messageBuilder.append(name);
            first = false;
        } else {
            if (description != null && !description.isEmpty()) {
                messageBuilder.append(description);
                first = false;
            }
        }

        final ExtraTags extraTags = place.getExtratags();
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
                serviceMessage.setFormattedPhoneNumber(phone);
                serviceMessage.setPhone1(phone);
                serviceMessage.setPhonetype1("UNKNOWN");
            }

            final String website = extraTags.getContactWebsite();
            if (website!=null && !website.isEmpty()) {
                serviceMessage.setUrl(website);
            }
        }

        final int messageLength = messageBuilder.length();
        if (messageLength>255) {
            serviceMessage.setMessage(messageBuilder.substring(0,255));
        } else if (messageLength > 0){
            serviceMessage.setMessage(messageBuilder.toString());
        } else {
            serviceMessage.setMessage(null);
        }
    }

    private void createAddress(ServiceMessage serviceMessage, Place place) {
        final Address address = place.getAddress();

        if (address != null) {

            final String road = address.getRoad();
            if (road != null && !road.isEmpty()) {
                serviceMessage.setStreet(road);
            } else {
                final String footway = address.getFootway();
                if (footway != null && !footway.isEmpty()) {
                    serviceMessage.setStreet(footway);
                } else {
                    final String pedestrian = address.getPedestrian();
                    if (pedestrian != null && !pedestrian.isEmpty()) {
                        serviceMessage.setStreet(pedestrian);
                    } else {
                        final String name = place.getName();
                        if (name != null && !name.isEmpty() && !name.equals(serviceMessage.getName())) {
                            serviceMessage.setStreet(name);
                        } else {
                            serviceMessage.setStreet(null);
                        }
                    }
                }
            }

            final String houseNumber = address.getHouse_number();
            if (houseNumber != null && !houseNumber.isEmpty()) {
                serviceMessage.setNumber(houseNumber);
            } else {
                serviceMessage.setNumber(null);
            }

            final String city = address.getCity();
            if (city != null && !city.isEmpty()) {
                serviceMessage.setCity(address.getCity());
            } else {
                final String town = address.getTown();
                if (town != null && !town.isEmpty()) {
                    serviceMessage.setCity(town);
                } else {
                    final String village = address.getVillage();
                    if (village != null && !village.isEmpty()) {
                        serviceMessage.setCity(village);
                    } else {
                        serviceMessage.setCity(null);
                    }
                }
            }

            final String suburb = address.getSuburb();
            if (suburb != null && !suburb.isEmpty()) {
                if (serviceMessage.getCity() == null) {
                    serviceMessage.setCity(suburb);
                } else {
                    serviceMessage.setQuarter(suburb);
                }
            } else {
                final String neighbourhood = address.getNeighbourhood();
                if (neighbourhood != null && !neighbourhood.isEmpty()) {
                    serviceMessage.setQuarter(neighbourhood);
                } else {
                    serviceMessage.setQuarter(null);
                }
            }

            final String postcode = address.getPostcode();
            if (postcode != null && !postcode.isEmpty()) {
                serviceMessage.setZip(postcode);
            } else {
                serviceMessage.setZip(null);
            }

            final String country = address.getCountry();
            if (country != null && !country.isEmpty()) {
                serviceMessage.setCountry(country);
            } else {
                serviceMessage.setCountry(null);
            }

            final String cityDistrict = address.getCity_district();
            if (cityDistrict != null && !cityDistrict.isEmpty()) {
                serviceMessage.setDistrict(cityDistrict);
            } else {
                serviceMessage.setDistrict(null);
            }

            final String countryCode = address.getCountry_code();
            if (countryCode != null && !countryCode.isEmpty()) {
                serviceMessage.setCountryCode(countryCode);
            } else {
                serviceMessage.setCountryCode(null);
            }

            final String state = address.getState();
            if (state != null && !state.isEmpty()) {
                serviceMessage.setRegion(state);
            } else {
                serviceMessage.setRegion(null);
            }

            final String stateDistrict = address.getState_district();
            if (stateDistrict != null && !stateDistrict.isEmpty()) {
                serviceMessage.setCounty(stateDistrict);
            } else {
                serviceMessage.setCounty(null);
            }
        }
    }

    public void lookupGeodata(final ServiceMessageUpdateListener listener) {

        if (geoUrl != null) {

            final double lat = geoUrl.getLat();
            final double lon = geoUrl.getLon();
            final String description = geoUrl.getDescription();

            if (Double.isNaN(lat) || Double.isNaN(lon) || (lat==0.0 && lon==0.0) && description != null && !description.isEmpty()) {
                nominatimCall.requestSearch(description,  new NominatimCall.NominatimCallListener<List<Place>>() {
                    @Override
                    public void onSuccess(final List<Place> places) {
                        serviceMessageList.clear();
                        serviceMessageIndex = -1;
                        for(final Place place : places) {
                            serviceMessageList.add(createServiceMessageFromPlace(place));
                        }
                        listener.onUpdate();
                    }

                    @Override
                    public void onError(NominatimError error) {
                        serviceMessageList.clear();
                        serviceMessageIndex = -1;
                        listener.onError(Integer.toString(error.getCode())+": "+error.getMessage());
                    }
                });
            } else {
                nominatimCall.requestReverse(geoUrl.getLat(), geoUrl.getLon(), new NominatimCall.NominatimCallListener<Place>() {
                    @Override
                    public void onSuccess(Place result) {
                        serviceMessageList.clear();
                        serviceMessageIndex = -1;
                        final String error = result.getError();
                        if (error!=null) {
                            listener.onError(error);
                        } else {
                            final ServiceMessage serviceMessage = createServiceMessageFromPlace(result);
                            serviceMessage.setLat(GeoUrl.degreeToString(lat));
                            serviceMessage.setLng(GeoUrl.degreeToString(lon));
                            serviceMessageList.add(serviceMessage);
                            listener.onUpdate();
                        }
                    }

                    @Override
                    public void onError(NominatimError error) {
                        serviceMessageList.clear();
                        serviceMessageIndex = -1;
                        listener.onError(Integer.toString(error.getCode())+": "+error.getMessage());
                    }
                });
            }
        }
    }

    public void sendServiceMessage2Car(final Token token, final ServiceMessageSendListener listener) {

        final ServiceMessage serviceMessage = getServiceMessage();
        if (serviceMessage != null) {
            serviceMessage.setVins(vinList);
            sendServiceMessage.sendServiceMessage(serviceMessage, token.getAuthorization(), new SendServiceMessage.SendServiceMessageListener() {
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
        return geoUrl;
    }

    public ResourceBundle getCategories() {
        return categories;
    }

    public void setCategories(ResourceBundle categories) {
        this.categories = categories;
    }

    public void setVin(String vin) {
        vinList.clear();
        vinList.add(vin);
    }

    public void setServiceMessageIndex(final int index) {
        serviceMessageIndex = index;
    }

    public int getNumServiceMessages() {
        return serviceMessageList.size();
    }

    public ServiceMessage getServiceMessage() {
        return getServiceMessage(serviceMessageIndex);
    }

    public ServiceMessage getServiceMessage(int position) {
        if (position >= 0 && position < serviceMessageList.size()) {
            return serviceMessageList.get(position);
        } else {
            return null;
        }
    }
}
