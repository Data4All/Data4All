/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.handler;

import io.github.data4all.model.data.Address;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

/**
 * This class represent values for unclassifiedTag. e.g country:usa. these
 * values are determined based on Reverse Geocoding. It uses the API of
 * nominatim.
 * 
 * @author Steeve
 *
 */
public class TagSuggestionHandler {

    /**
     * earth radius in km
     */
    private static final int EARTH_RADIUS = 6371;

    private static final String TAG = "TagSuggestion";

    public static final Map<Location, Address> cache = new LinkedHashMap<Location, Address>();

    private static Location location;

    private static List<Address> lastSuggestions;

    private static int id = 0;

    public static List<Address> getLastSuggestions() {
        return lastSuggestions;
    }

    public static void setLocation(Location location) {
        Log.i(TAG, "setLocation: " + location);
        TagSuggestionHandler.location = location;
        final int myId = ++id;
        new Thread(new Runnable() {
            public void run() {
                lastSuggestions = null;
                List<Address> suggestions = getSuggestion();
                if (myId == id) {
                    lastSuggestions = suggestions;
                    Log.i(TAG, "suggestions set");
                }
            }
        }).start();
    }

    private static List<Address> getSuggestion() {
        List<Location> near = getNearestLocations(location);
        near.add(location);
        Log.i(TAG, "getSuggestion: " + near.size());
        List<Address> result = new ArrayList<Address>(near.size());
        for (int i = 0; i < Math.min(near.size(), 10); i++) {
            Location location = near.get(i);
            Address address = getCached(location);
            if (address == null) {
                address = getAddress(location);
            }
            Log.i(TAG, "getSuggestion addressNull: " + (address == null));
            if (address != null) {
                cache.put(location, address);
                result.add(address);
            }
        }
        return result;
    }

    private static Address getCached(Location loc) {
        for (Location l : cache.keySet()) {
            if (Math.abs(l.getLatitude() - loc.getLatitude()) < 1e-5
                    && Math.abs(l.getLongitude() - loc.getLongitude()) < 1e-5) {
                return cache.get(l);
            }
        }
        return null;
    }

    /**
     * @param location
     * @return an address based on latitude and longitude (Reverse Geocoding)
     */
    private static Address getAddress(Location location) {
        try {
            final JSONObject jsonObj = getJSONfromURL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + location.getLatitude()
                    + "&lon="
                    + location.getLongitude() + "&zoom=18&addressdetails=1");

            final JSONObject address = jsonObj.getJSONObject("address");
            final Address addresse = new Address();
            addresse.setAddresseNr(getJsonValue(address, "house_number"));
            addresse.setRoad(getJsonValue(address, "road"));
            if (getJsonValue(jsonObj, "road") == null) {
                addresse.setRoad(getJsonValue(address, "pedestrian"));
            }
            addresse.setCity(getJsonValue(address, "city"));
            addresse.setPostCode(getJsonValue(address, "postcode"));
            addresse.setCountry(getJsonValue(address, "country"));
            Log.i(TAG, "getAddress: " + addresse.getFullAddress());
            return addresse;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 
     * @param jsonObject
     * @param key
     * @return the value of a jsonObject
     */
    private static String getJsonValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 
     * @param url
     * @return the JSONObject from an url
     */
    private static JSONObject getJSONfromURL(String url) {

        // initialize
        InputStream is = null;
        String address = "";
        JSONObject jObject = null;

        // http post
        try {
            final HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                    System.getProperty("http.agent"));
            final HttpGet httppost = new HttpGet(url);
            final HttpResponse response = httpclient.execute(httppost);
            final HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            address = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting address " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(address);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data [" + e.getMessage() + "] "
                    + address);
        }
        return jObject;
    }

    /**
     * Get a list of locations near by the given location.
     * 
     * @param location
     * @return locations near by a given Location
     */
    private static List<Location> getNearestLocations(Location location) {
        final List<Location> locations = new LinkedList<Location>();
        try {
            final double boundingbox[] = getBoundingBox(location.getLatitude(),
                    location.getLongitude(), 0.020);
            StringBuilder url = new StringBuilder(
                    "http://overpass-api.de/api/interpreter?data=[out:json];");
            StringBuilder param = new StringBuilder("");
            param.append("node(").append(boundingbox[0]).append(",")
                    .append(boundingbox[1]).append(",");
            param.append(boundingbox[2]).append(",").append(boundingbox[3])
                    .append(");out;");
            final String urlParam = url.toString()
                    + Uri.encode(param.toString(), "UTF-8");
            final JSONObject jsonObj = getJSONfromURL(urlParam);
            final JSONArray elements = jsonObj.getJSONArray("elements");
            int index = 0;
            while (!elements.isNull(index)) {
                final JSONObject obj = elements.getJSONObject(index);
                final String lat = getJsonValue(obj, "lat");
                final String lon = getJsonValue(obj, "lon");
                if (!lat.isEmpty() && !lon.isEmpty()) {
                    final Location loc = new Location("");
                    loc.setLatitude(Double.valueOf(lat));
                    loc.setLongitude(Double.valueOf(lon));
                    locations.add(loc);
                }
                index++;
            }

            Log.i(TAG, "getNear: " + index);
        } catch (Exception e) {
            Log.i(TAG, "getNear: ", e);
        }
        return locations;
    }

    /**
     * Get a bounding box of the location.
     * 
     * @param lat
     *            latitude of the location
     * @param lon
     *            longitude of the location
     * @param radius
     *            distance to bounds from location
     * @return a boundingBox
     */
    private static double[] getBoundingBox(double lat, double lon, double radius) {
        final double result[] = new double[4];
        result[0] = lat - Math.toDegrees(radius / (double) EARTH_RADIUS); // s
        result[1] = lon
                - Math.toDegrees(radius / (double) EARTH_RADIUS
                        / Math.cos(Math.toRadians(lat))); // w
        result[2] = lat + Math.toDegrees(radius / (double) EARTH_RADIUS); // n
        result[3] = lon
                + Math.toDegrees(radius / (double) EARTH_RADIUS
                        / Math.cos(Math.toRadians(lat))); // e
        return result;
    }
}
