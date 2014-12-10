/**
 * 
 */
package io.github.data4all.util;

/**
 * Convert the geo data longitude and latitude from degree to an DMS string and
 * from a DMS string to degree
 * 
 * @author sbollen
 *
 */
public class GeoDataConverter {

    // Constructor
    public GeoDataConverter() {
    }

    /*
     * Convert the given latitude and longitude from degrees into DMS (degree
     * minute second) format. For example -79.948862 becomes
     * 79/1,56/1,55903/1000
     * 
     * @param deg the given latitude or longitude in degrees
     * 
     * @return the given latitude or longitude in DMS
     */
    public String convertToDMS(double deg) {
        StringBuilder sb = new StringBuilder(20);
        deg = Math.abs(deg);
        int degree = (int) deg;
        deg *= 60;
        deg -= (degree * 60.0d);
        int minute = (int) deg;
        deg *= 60;
        deg -= (minute * 60.0d);
        int second = (int) (deg * 1000.0d);

        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    /*
     * Convert the latitude or latitude from DMS into degrees
     * 
     * @param stringDMS the given latitude or longitude in DMS
     * 
     * @return the given latitude or longitude in degrees
     */
    public Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        // Get the degree value
        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;
        // Get the minute value
        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;
        // Get the second value
        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1].substring(0, stringS[1].length()));
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }

}
