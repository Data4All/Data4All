/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package io.github.data4all.util;

/**
 * Convert the geo data longitude and latitude from degree to an DMS string and
 * from a DMS string to degree.
 * 
 * @author sbollen
 *
 */
public class GeoDataConverter {

    // Constructor
    private GeoDataConverter() {
    }

    /**
     * Convert the given latitude and longitude from degrees into DMS (degree
     * minute second) format. For example -79.948862 becomes
     * 79/1,56/1,55903/1000
     * 
     * @param deg
     *            the given latitude or longitude in degrees
     * 
     * @return the given latitude or longitude in DMS
     */
    public static String convertToDMS(double deg) {
        deg = Math.abs(deg);
        final int degree = (int) deg;
        deg *= 60;
        deg -= (degree * 60.0d);
        final int minute = (int) deg;
        deg *= 60;
        deg -= (minute * 60.0d);
        final int second = (int) (deg * 1000.0d);
        final StringBuilder sb = new StringBuilder(20);
        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    /**
     * Convert the latitude or latitude from DMS into degrees.
     * 
     * @param stringDMS
     *            the given latitude or longitude in DMS
     * 
     * @return the given latitude or longitude in degrees
     */
    public static Double convertToDegree(String stringDMS) {
        Double result = null;
        final String[] dms = stringDMS.split(",", 3);

        // Get the degree value
        final String[] stringD = dms[0].split("/", 2);
        final Double d0 = Double.valueOf(stringD[0]);
        final Double d1 = Double.valueOf(stringD[1]);
        final Double floatD = d0 / d1;
        // Get the minute value
        final String[] stringM = dms[1].split("/", 2);
        final Double m0 = Double.valueOf(stringM[0]);
        final Double m1 = Double.valueOf(stringM[1]);
        final Double floatM = m0 / m1;
        // Get the second value
        final String[] stringS = dms[2].split("/", 2);
        final Double s0 = Double.valueOf(stringS[0]);
        final Double s1 = Double.valueOf(stringS[1].substring(0,
                stringS[1].length()));
        final Double floatS = s0 / s1;

        result = Double.valueOf(floatD + (floatM / 60) + (floatS / 3600));

        return result;
    }

}
