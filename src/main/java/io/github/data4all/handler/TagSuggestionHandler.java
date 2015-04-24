package io.github.data4all.handler;

import io.github.data4all.model.data.Address;
import io.github.data4all.util.Optimizer;
import io.github.data4all.view.AddressSuggestionView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
 * this class represent a default values for unclassifiedTag these values are
 * determined based on GPS (longitude and latitude)
 * 
 * @author Steeve
 *
 */
public class TagSuggestionHandler extends AsyncTask<String, Void, String> {

    // object for AddressSuggestion View
    private AddressSuggestionView view;

    private static final String TAG = "TagSuggestion";

    // represent the list of all suggestions Addresses
    private Set<Address> addressList = new LinkedHashSet<Address>();

    public Context context;

    // list of locations
    Set<Location> locations = new LinkedHashSet<Location>();
    // current location
    Location current = null;

    /**
     * 
     * @return a full address based on latitude an longitude
     */
    public String getAddress() {
        if (Optimizer.currentBestLoc() == null) {
            return "";
        }
        return getAddress(Optimizer.currentBestLoc()).getFullAddress();
    }

    /**
     * get a address based on latitude and longitude(nominatim api)
     * 
     * @param location
     * @return
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
    public String getJsonValue(JSONObject jsonObject, String key) {
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
     * @return a list of locations nearby of the current location
     */
    private Set<Location> locationSuggestions() {
        if (current == null) {
            current = Optimizer.currentBestLoc();
        } else {
            if (current.equals(Optimizer.currentBestLoc())) {
                return locations;
            }
        }
        if (current == null) {
            return locations;
        }
        locations.clear();
        locations.add(current);
        locations.addAll(getNearestLocations(current));
        // while (locations.size() < 5) {
        // Location location = getLocation(current.getLongitude(),
        // current.getLatitude(), 30);
        // if (!locationsExist(locations, location)) {
        // locations.add(location);
        // }
        // }
        return locations;
    }

    
    /**
     * get a list of addresses
     */
    public void getlistOfSuggestionAddress() {
        Set<Address> addressListTemp = new LinkedHashSet<Address>();
        DataBaseHandler db = new DataBaseHandler(context);
        for (Location location : locationSuggestions()) {
            Address addr = db.getAddressFromDb(location);
            boolean isneu = false;
            // when an address is not in database, then load address from
            // nominatim
            if (addr == null) {
                addr = getAddress(location);
                isneu = true;
            }
            if (addr != null && !addressListTemp.contains(addr)) {
                // when a address was already in database, then update this
                // address
                // when not so insert this address in database
                if (isneu) {
                    db.insertOrUpdateAddressInDb(location,
                            addr.getAddresseNr(), addr.getRoad(),
                            addr.getPostCode(), addr.getCity(),
                            addr.getCountry());
                }
                addressListTemp.add(addr);

            }
        }
        db.close();
        if (!addressListTemp.isEmpty()) {
            addressList.clear();
            addressList.addAll(addressListTemp);
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * @return get a list of addresses
     */
    public Set<Address> getAddressList() {
        return addressList;
    }

    @Override
    protected String doInBackground(String... params) {
        getlistOfSuggestionAddress();

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!addressList.isEmpty()) {
            view.setAddresses(addressList);
            view.fillDialog();
            view.show();
        }
    }

    public void setView(AddressSuggestionView addressSuggestionView) {
        this.view = addressSuggestionView;
    }

    /**
     * @param location
     * @return a locations nearby a given Location
     */
    public List<Location> getNearestLocations(Location location) {
        List<Location> locations = new LinkedList<Location>();
        try {
            double boundingbox[] = getBoundingBox(location.getLatitude(),
                    location.getLongitude(), 0.010);
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

        double R = 6371; // earth radius in km
        double x1 = lon
                - Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

        double x2 = lon
                + Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

        double y1 = lat + Math.toDegrees(radius / R);

        double y2 = lat - Math.toDegrees(radius / R);

        result[0] = y2;// s
        result[1] = x1;// w
        result[2] = y1;// n
        result[3] = x2;// e
        return result;
    }

}
