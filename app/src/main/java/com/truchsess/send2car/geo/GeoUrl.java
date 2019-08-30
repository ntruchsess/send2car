package com.truchsess.send2car.geo;

import android.net.Uri;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class GeoUrl {

    private double lat = Double.NaN;
    private double lon = Double.NaN;
    private String description = null;

    public GeoUrl () {}

    public GeoUrl(final double lat, final double lon, final String description) {
        this.lat = lat;
        this.lon = lon;
        this.description = description;
    }

    public void fromUri(Uri uri) {

        String data = uri.getSchemeSpecificPart();

        // format is:
        // <lat>,<lon>?z=<zoom>
        // 0,0?q=<lat>,<lon>(label)

        int qpos = data.indexOf("?");
        String resource = qpos < 0 ? data : data.substring(0, qpos);
        String query = data.substring(qpos+1);

        Matcher resMatcher = Pattern.compile("^(\\d++\\.?\\d*),(\\d++\\.?\\d*)$").matcher(resource);
        boolean resMatches = resMatcher.matches();

        lat = resMatches ? Double.parseDouble(resMatcher.group(1)) : Double.NaN;
        lon = resMatches ? Double.parseDouble(resMatcher.group(2)) : Double.NaN;

        description = null;

        StringTokenizer queryTokenizer = new StringTokenizer(query,"&");
        while (queryTokenizer.hasMoreTokens()) {
            final String nextToken = queryTokenizer.nextToken();
            Matcher paramMatcher = Pattern.compile("^q=(.*)$",Pattern.DOTALL).matcher(nextToken);
            if (paramMatcher.matches()) {
                final String q0 = paramMatcher.group(1);
                final String q1 = q0.replaceAll("[\\n\\r\\t\\f]",", ");
                Matcher locationMatcher = Pattern.compile("^(\\d++\\.?\\d*),(\\d++\\.?\\d*)(\\((.*)\\)|)+$").matcher(q1);
                if (locationMatcher.matches()) {
                    lat = Double.parseDouble(locationMatcher.group(1));
                    lon = Double.parseDouble(locationMatcher.group(2));
                    description = locationMatcher.group(4);
                    break;
                } else {
                    description = q1;
                }
            }
        }
    }

    public boolean isValid() {
        return !Double.isNaN(lat) && !Double.isNaN(lon);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public static String degreeToString(final double degree) {
        return String.format(Locale.US,"%1$.6f",degree);
    }

    public String getDescription() {
        return description;
    }
}
