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
import io.github.data4all.util.Optimizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
 * this class represent values for unclassifiedTag. e.g country:usa. these values
 * are determined based on Reverse Geocoding
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
	private Set<Location> locations = new LinkedHashSet<Location>();
	// current location
	private Location current;

	/** constants for earth radius in km. */
	private static final double R = 6371;

	/** constant for array length */
	private static final int ARRAY_LENGTH_BOUNDINGBOX = 4;

	/**
	 * get a address based on latitude and longitude(Reverse Geocoding)
	 * 
	 * @param location
	 *            for actual position
	 * @return address for a given location
	 */
	public Address getAddress(Location location) {
		try {
			final JSONObject jsonObj = getJSONfromURL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
					+ location.getLatitude()
					+ "&lon="
					+ location.getLongitude() + "&zoom=18&addressdetails=1");

			final JSONObject address = jsonObj.getJSONObject("address");
			final Address addresse = new Address();
			addresse.setAddresseNr(this.getJsonValue(address, "house_number"));
			addresse.setRoad(this.getJsonValue(address, "road"));
			addresse.setCity(this.getJsonValue(address, "city"));
			addresse.setPostCode(this.getJsonValue(address, "postcode"));
			addresse.setCountry(this.getJsonValue(address, "country"));
			Log.i(TAG, addresse.getFullAddress());
			return addresse;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * check if a jsonObject contains a key. If it is the case then return the
	 * value of the key
	 * if not then return ""
	 * @param jsonObject
	 * @param key
	 * @return the value of a key
	 */
	public String getJsonValue(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (Exception e) {
			return "";
		}
	}

	/**
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
		locations.addAll(this.getNearestLocations(current));
		locations.add(current);

		return locations;
	}

	/**
	 * check if the current location and each location suggestion(closest to the
	 * current position) have an address in database. if it is not the case then
	 * search with reverse geocoding the address corresponding to this location
	 * and insert it in database. if it is the case then update this address in
	 * database and then get a list of addresses
	 * 
	 */
	public synchronized void getlistOfSuggestionAddress() {
		DataBaseHandler db = new DataBaseHandler(context);
		for (Location location : this.locationSuggestions()) {
			Address addr = db.getAddressFromDb(location);
			boolean isneu = false;
			// when an address is not in database, then load address from
			// Nominatim
			if (addr == null) {
				addr = this.getAddress(location);
				isneu = true;
			}
			if (addr != null && !addressList.contains(addr)) {
				// when an address was already in database, then update this
				// address
				// if it is not the case then insert this address in database
				if (isneu) {
					db.insertOrUpdateAddressInDb(location,
							addr.getAddresseNr(), addr.getRoad(),
							addr.getPostCode(), addr.getCity(),
							addr.getCountry());
				}
				addressList.add(addr);
				if (addressList.size() > 7) {
					addressList.remove();
				}

			}
		}
		db.close();
	}

	public static void setContext(Context context) {
		TagSuggestionHandler.context = context;
	}

	public Queue<Address> getAddressList() {
		return addressList;
	}

	@Override
	protected synchronized String doInBackground(String... params) {
		this.getlistOfSuggestionAddress();

		return "";
	}

	/**
	 * @param location
	 * @return a list of locations nearby a given Location
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
		final double result[] = new double[ARRAY_LENGTH_BOUNDINGBOX];

		final double x1 = lon
				- Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

		final double x2 = lon
				+ Math.toDegrees(radius / R / Math.cos(Math.toRadians(lat)));

		final double y1 = lat + Math.toDegrees(radius / R);

		final double y2 = lat - Math.toDegrees(radius / R);

		result[0] = y2; // s
		result[1] = x1; // w
		result[2] = y1; // n
		result[3] = x2; // e
		return result;
	}

}
