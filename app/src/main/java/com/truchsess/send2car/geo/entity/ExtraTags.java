package com.truchsess.send2car.geo.entity;

import com.google.gson.annotations.SerializedName;

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
public class ExtraTags {

    private String sport;
    private String opening_hours;

    @SerializedName("contact:fax")
    private String contactFax;

    @SerializedName("contact:phone")
    private String contactPhone;

    @SerializedName("contact:website")
    private String contactWebsite;

    public String getSport() {
        return sport;
    }

    public String getOpening_hours() {
        return opening_hours;
    }

    public String getContactFax() {
        return contactFax;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactWebsite() {
        return contactWebsite;
    }
}
