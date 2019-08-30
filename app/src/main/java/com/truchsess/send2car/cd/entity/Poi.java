package com.truchsess.send2car.cd.entity;

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
public class Poi {

    private String city = "";

    private double lat = 0.0;

    private double lon = 0.0;

    private String name = "";

    private long rating = -1;

    private String postalCode = "";

    private String street = "";

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poi poi = (Poi) o;

        if (Double.compare(poi.lat, lat) != 0) return false;
        if (Double.compare(poi.lon, lon) != 0) return false;
        if (rating != poi.rating) return false;
        if (city != null ? !city.equals(poi.city) : poi.city != null) return false;
        if (name != null ? !name.equals(poi.name) : poi.name != null) return false;
        if (postalCode != null ? !postalCode.equals(poi.postalCode) : poi.postalCode != null)
            return false;
        return street != null ? street.equals(poi.street) : poi.street == null;
    }
}
