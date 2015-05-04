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
import io.github.data4all.util.LocationWrapper;
import io.github.data4all.util.Optimizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * this class represent values for unclassifiedTag. e.g country:usa. these
 * values are determined based on Reverse Geocoding
 * 
 * @author Steeve
 *
 */
public class TagSuggestionHandler extends AsyncTask<String, Void, String> {

    private static final String TAG = "TagSuggestion";

    // represent the list of all suggestions Addresses
    public static Queue<Address> addressList = new LinkedList<Address>();

    public static Context context;

    // list of locations
    Set<Location> locations = new LinkedHashSet<Location>();
    // current location
    Location current = null;

    // The Map with a location and a list of addresses
    public static Map<LocationWrapper, Queue<Address>> locationSuggestions = new HashMap<LocationWrapper, Queue<Address>>();

    /**
     * @param location
     * @return an address based on latitude and longitude (Reverse Geocoding)
     */
    public Address getAddress(Location location) {
        try {
            JSONObject jsonObj = getJSONfromURL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + location.getLatitude()
                    + "&lon="
                    + location.getLongitude() + "&zoom=18&addressdetails=1");

            JSONObject address = jsonObj.getJSONObject("address");
            Address addresse = new Address();
            addresse.setAddresseNr(getJsonValue(address, "house_number"));
            addresse.setRoad(getJsonValue(address, "road"));
            if(!jsonObj.has("road")) {
                addresse.setRoad(getJsonValue(address, "pedestrian"));
            }
            addresse.setCity(getJsonValue(address, "city"));
            addresse.setPostCode(getJsonValue(address, "postcode"));
            addresse.setCountry(getJsonValue(address, "country"));
            Log.i(TAG, addresse.getFullAddress());
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
    public static String getJsonValue(JSONObject jsonObject, String key) {
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
    public static JSONObject getJSONfromURL(String url) {

        // initialize
        InputStream is = null;
        String address = "";
        JSONObject jObject = null;

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                    System.getProperty("http.agent"));
            HttpGet httppost = new HttpGet(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
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
     * @return a list of locations nearby the current location
     */
    private Set<Location> locationSuggestions() {
        if (current == null) {
            current = Optimizer.currentBestLoc();
            if (current == null) {
                return locations;
            }
        }
        locations.clear();
        locations.addAll(getNearestLocations(current));
        locations.add(current);
        return locations;
    }

    /**
     * get a list of addresses
     */
    public synchronized void getlistOfSuggestionAddress() {
        if (current == null) {
            return;
        }
        DataBaseHandler db = new DataBaseHandler(context);

        Queue<Address> adresses = TagSuggestionHandler.locationSuggestions
                .get(new LocationWrapper(current));
        if (adresses == null || adresses.isEmpty()) {
            adresses = new LinkedList<Address>();
        } else {
            if (adresses.size() >= 10) {
                return;
            }
        }
        TagSuggestionHandler.locationSuggestions.put(new LocationWrapper(
                current), adresses);

        for (Location location : locationSuggestions()) {
            Address addr = db.getAddressFromDb(location);
            boolean isneu = false;
            // when an address is not in database, then load address from
            // nominatim api
            if (addr == null) {
                addr = getAddress(location);
                isneu = true;
            }
            addNewAdressTolist(db, location, addr, isneu, adresses);
        }
        db.close();

    }

    /**
     * add a new address in database and to the list.
     * 
     * @param db
     *            database
     * @param location
     *            of an address
     * @param addr
     *            address
     * @param isneu
     *            to check if this address was already in database
     * @param address
     *            the list of addresses
     */
    private void addNewAdressTolist(DataBaseHandler db, Location location,
            Address addr, boolean isneu, Queue<Address> address) {
        if (addr != null && !address.contains(addr)) {
            // when a address was already in database, then update this
            // address
            // when not so insert this address in database
            if (isneu) {
                db.insertOrUpdateAddressInDb(location, addr.getAddresseNr(),
                        addr.getRoad(), addr.getPostCode(), addr.getCity(),
                        addr.getCountry());
            }
            address.add(addr);
        }
    }

    public static void setContext(Context context) {
        TagSuggestionHandler.context = context;
    }

    /**
     * @return a list of addresses
     */
    public Queue<Address> getAddressList() {
        return addressList;
    }

    @Override
    protected synchronized String doInBackground(String... params) {
        getlistOfSuggestionAddress();

        return "";
    }

    /**
     * @param location
     * @return locations nearby a given Location
     */
    public List<Location> getNearestLocations(Location location) {
        List<Location> locations = new LinkedList<Location>();
        try {
            double boundingbox[] = getBoundingBox(location.getLatitude(),
                    location.getLongitude(), 0.020);
            StringBuilder url = new StringBuilder(
                    "http://overpass-api.de/api/interpreter?data=[out:json];");
            StringBuilder param = new StringBuilder("");
            param.append("node(").append(boundingbox[0]).append(",")
                    .append(boundingbox[1]).append(",");
            param.append(boundingbox[2]).append(",").append(boundingbox[3])
                    .append(");out;");
            String urlParam = url.toString()
                    + Uri.encode(param.toString(), "UTF-8");
            JSONObject jsonObj = getJSONfromURL(urlParam);
            JSONArray elements = jsonObj.getJSONArray("elements");
            int index = 0;
            while (!elements.isNull(index)) {
                JSONObject obj = elements.getJSONObject(index);
                String lat = getJsonValue(obj, "lat");
                String lon = getJsonValue(obj, "lon");
                if (!lat.isEmpty() && !lon.isEmpty()) {
                    Location loc = new Location("");
                    loc.setLatitude(Double.valueOf(lat));
                    loc.setLongitude(Double.valueOf(lon));
                    locations.add(loc);
                }
                index++;
            }
            return locations;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

    /**
     * 
     * @param lat
     *            latitude
     * @param lon
     *            longitude
     * @param radius
     *            which helps to find a location
     * @return a boundingBox
     */
    public static double[] getBoundingBox(double lat, double lon, double radius) {
        double result[] = new double[4];

        final double R = 6371; // earth radius in km
        final double x1 = lon
                - Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

        final double x2 = lon
                + Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

        final double y1 = lat + Math.toDegrees(radius / R);

        final double y2 = lat - Math.toDegrees(radius / R);

        result[0] = y2;// s
        result[1] = x1;// w
        result[2] = y1;// n
        result[3] = x2;// e
        return result;
    }

    /**
     * calculates the distance between two locations
     * 
     * @param location1
     * @param location2
     * @return the distance between two locations
     */
    public static float distFrom(Location location1, Location location2) {
        final double earthRadius = 6371000; // meters
        final double dLat = Math.toRadians(location2.getLatitude()
                - location1.getLatitude());
        final double dLng = Math.toRadians(location2.getLongitude()
                - location1.getLongitude());
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(location1.getLatitude()))
                * Math.cos(Math.toRadians(location2.getLatitude()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final float dist = (float) (earthRadius * c);

        return Math.abs(dist);
    }

    /**
     * 
     * @param current
     *            for the current location
     */
    public void setCurrent(Location current) {
        this.current = current;
        if (current != null) {
            execute();
        }
    }

    /**
     * 
     * @param location
     * @return a list of addresses for an given location
     */
    public static Queue<? extends Address> get(Location location) {
        for (Map.Entry<LocationWrapper, Queue<Address>> list : locationSuggestions
                .entrySet()) {
            final LocationWrapper key = list.getKey();
            if (key.equals(location)) {
                return list.getValue();
            }
        }
        
        for (Map.Entry<LocationWrapper, Queue<Address>> list : locationSuggestions
                .entrySet()) {
            final LocationWrapper key = list.getKey();
            if (distFrom(key.getLocation(), location) <= 20) {
                locationSuggestions.put(new LocationWrapper(location),
                        list.getValue());
                return list.getValue();
            }
        }
        return null;
    }
}
