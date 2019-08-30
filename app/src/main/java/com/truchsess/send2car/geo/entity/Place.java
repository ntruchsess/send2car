package com.truchsess.send2car.geo.entity;

import java.util.List;

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
public class Place {

    private String error;

    private long place_id;

    private String licence;

    private String osm_type;

    private long osm_id;

    private double lat;

    private double lon;

    private int place_rank;

    private String category;

    private String type;

    private double importance;

    private String addresstype;

    private String name;

    private String display_name;

    private Address address;

    private List<Double> boundingbox;

    private NameDetails namedetails;

    private ExtraTags extratags;

    public String getError() {
        return error;
    }

    public long getPlace_id() {
        return place_id;
    }

    public String getLicence() {
        return licence;
    }

    public String getOsm_type() {
        return osm_type;
    }

    public long getOsm_id() {
        return osm_id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getPlace_rank() {
        return place_rank;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public double getImportance() {
        return importance;
    }

    public String getAddresstype() {
        return addresstype;
    }

    public String getName() {
        return name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public Address getAddress() {
        return address;
    }

    public List<Double> getBoundingbox() {
        return boundingbox;
    }

    public NameDetails getNamedetails() {
        return namedetails;
    }

    public ExtraTags getExtratags() {
        return extratags;
    }
}
