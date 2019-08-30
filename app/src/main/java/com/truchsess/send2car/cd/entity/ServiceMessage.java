package com.truchsess.send2car.cd.entity;

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
public class ServiceMessage {

    private List<String> vins;           // [ "ASDF1234567890123" ]
    private String message;              // "powered by Postman",
    private String subject;              // "AquariUSH - Familien",
    private String number;               // "2",
    private String street;               // "Hartmut-Hermann-Weg",
    private String quarter;              // "Lohhof",
    private String city;                 // "Unterschleißheim",
    private String district;             // "München",
    private String county;               // "Oberbayern",
    private String countyCode;           // "Oberbayern",
    private String region;               // "Bayern",
    private String regionCode;           // "BY",
    private String country;              // " ",
    private String countryCode;          // "DE",
    private String zip;                  // "85716",
    private String lat;                  // "48.268322",
    private String lng;                  // "11.583435000000009",
    private String url;                  // "http://www.aquariush.de/",
    private String name;                 // "AquariUSH - Familien und Thermalbad & Sauna Oase",
    private String formattedPhoneNumber; // "+49 89 310094500",
    private String phonetype1;           // "UNKNOWN",
    private String phone1;               // "+49 89 310094500"

    public List<String> getVins() {
        return vins;
    }

    public void setVins(List<String> vins) {
        this.vins = vins;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public String getPhonetype1() {
        return phonetype1;
    }

    public void setPhonetype1(String phonetype1) {
        this.phonetype1 = phonetype1;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }
}
