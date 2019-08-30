package com.truchsess.send2car.geo.entity;

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
public class Address {

    private String house_number;

    private String pedestrian;

    private String footway;

    private String road;

    private String village;

    private String town;

    private String city;

    private String county;

    private String state_district;

    private String state;

    private String postcode;

    private String country;

    private String country_code;

    private String city_district;

    private String suburb;

    private String neighbourhood;

    public String getHouse_number() {
        return house_number;
    }

    public String getPedestrian() {
        return pedestrian;
    }

    public String getFootway() {
        return footway;
    }

    public String getRoad() {
        return road;
    }

    public String getVillage() {
        return village;
    }

    public String getTown() {
        return town;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getState_district() {
        return state_district;
    }

    public String getState() {
        return state;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getCity_district() {
        return city_district;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }
}
