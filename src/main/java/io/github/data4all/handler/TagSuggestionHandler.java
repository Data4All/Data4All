package io.github.data4all.handler;

import io.github.data4all.util.Optimizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


/**
 * this class represent a default values for unclassifiedTag
 * these values are determined based on GPS (longitude and latitude)
 * @author Steeve
 *
 */
public class TagSuggestionHandler extends AsyncTask<String, Void, String>{
    String addresseNr="";
    String address1 = "";
    String address2 = "";
    String city = "";
    String state = "";
    String country = "";
    String county = "";
    String PIN = "";
    String full_address = "";
    Location lastLocation;
    public String getAddress() {
        
        try {
            
            Location location=Optimizer.currentBestLoc();
            if(lastLocation!=null && lastLocation.equals(location)){
                return "";
            }
            lastLocation=location;

            JSONObject jsonObj = getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + location.getLatitude()+"" + "," + location.getLongitude()+"" + "&sensor=true");
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero
                        .getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (TextUtils.isEmpty(long_name) == false
                            || !long_name.equals(null)
                            || long_name.length() > 0 || long_name != "") {
                        if (Type.equalsIgnoreCase("street_number")) {
                            addresseNr = long_name + " ";
                        } else if (Type.equalsIgnoreCase("route")) {
                            address1 =  long_name;
                        } else if (Type.equalsIgnoreCase("sublocality")) {
                            address2 = long_name;
                        } else if (Type.equalsIgnoreCase("locality")) {
                            // Address2 = Address2 + long_name + ", ";
                            city = long_name;
                        } else if (Type
                                .equalsIgnoreCase("administrative_area_level_2")) {
                            county = long_name;
                        } else if (Type
                                .equalsIgnoreCase("administrative_area_level_1")) {
                            state = long_name;
                        } else if (Type.equalsIgnoreCase("country")) {
                            country = long_name;
                        } else if (Type.equalsIgnoreCase("postal_code")) {
                            PIN = long_name;
                        }

                    }

                    full_address = address1 + "," + address2 + "," + city + ","
                            + state + "," + country + "," + PIN;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return full_address;

    }

    public static JSONObject getJSONfromURL(String url) {

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
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
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }
    
    public static void main(String[] args) {
        TagSuggestionHandler handler=new TagSuggestionHandler();
        
        System.out.println(handler.getAddress());
    }

    @Override
    protected String doInBackground(String... params) {
        return getAddress();
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getCounty() {
        return county;
    }

    public String getPIN() {
        return PIN;
    }

    public String getFull_address() {
        return full_address;
    }

    public String getAddresseNr() {
        return addresseNr;
    }

    public Location getLastLocation() {
        return lastLocation;
    }
    

}
