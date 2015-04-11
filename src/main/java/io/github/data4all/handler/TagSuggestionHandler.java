package io.github.data4all.handler;

import io.github.data4all.suggestion.Addresse;
import io.github.data4all.util.Optimizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
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
	private String addresseNr = "";
	private String road = "";
	private String city = "";
	private String country = "";
	private String postCode = "";
    private String display_name ="";
	private String full_address = "";
	Location lastLocation;
	
    private static final String TAG = "TagSuggestion";

	private static List<Addresse> addressList = new LinkedList<Addresse>();

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
	 * get the current best address based on latitude and longitude
	 * 
	 * @param location
	 * @return
	 */
	public Addresse getAddress(Location location) {
		try {
			JSONObject jsonObj = getJSONfromURL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
					+ location.getLatitude()
					+ "&lon=" + location.getLongitude()
					+ "&zoom=18&addressdetails=1");
		      
			display_name = jsonObj.getString("display_name");
			if (display_name.contains(",")) {
			    // Split it.
				String[] separate = display_name.split(",");	
				addresseNr = separate[0];
				road = separate[1];
				city = separate[3];
				postCode = separate[6];
			}
			JSONObject address = jsonObj.getJSONObject("address");
			country = address.getString("country");
			
			
			
				/*JSONObject address = jsonObj.getJSONObject("address");

				postCode = address.getString("postcode");
				//addresseNr = address.getString("house_number");
				road = address.getString("road");
				city = address.getString("city");
				country = address.getString("country");
			*/
			Addresse addresse = new Addresse();
			addresse.setAddresseNr(addresseNr);
			addresse.setRoad(road);
			addresse.setCity(city);
			addresse.setPostCode(postCode);
			addresse.setCountry(country);
			Log.i(TAG, addresse.getFullAddress());
			return addresse;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

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
			address = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting address " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObject = new JSONObject(address);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data [" + e.getMessage()+"] "+address);
		}

		return jObject;
	}

	/**
	 * 
	 * @return city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @return country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * 
	 * @return road
	 */
	public String getRoad() {
		return road;
	}

	/**
	 * 
	 * @return postcode
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * 
	 * @return full_address
	 */
	public String getFull_address() {
		return full_address;
	}

	/**
	 * @return housenumber
	 */
	public String getAddresseNr() {
		return addresseNr;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	// list of location
	static List<Location> locations = new LinkedList<Location>();
	// current location
	static Location current = null;

	/**
	 * 
	 * @return a list of location
	 */
	private static List<Location> locationSuggestions() {
		if (current == null) {
			current = Optimizer.currentBestLoc();
		} else {
			if (current.equals(Optimizer.currentBestLoc())) {
				return locations;
			}
		}
		locations = new LinkedList<Location>();
		if (current == null) {
			return locations;
		}
		locations.clear();
		locations.add(current);
		while (locations.size() < 10) {
			Location location = getLocation(current.getLongitude(),
					current.getLatitude(), 100);
			if (!locationsExist(locations, location)) {
				locations.add(location);
			}
		}
		return locations;
	}

	/**
	 * check if a location exist
	 * 
	 * @param locations
	 * @param location
	 * @return
	 */
	private static boolean locationsExist(List<Location> locations,
			Location location) {
		for (Location l : locations) {
			if (l.getLatitude() == location.getLatitude()
					&& l.getLongitude() == location.getLongitude()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * this method generate random locations nearby a given Location
	 * @param x0 longitude
	 * @param y0 lattitude
	 * @param radius
	 * @return location
	 */
	public static Location getLocation(double x0, double y0, int radius) {
		Location l = new Location("");
		Random random = new Random();

		// Convert radius from meters to degrees
		double radiusInDegrees = radius / 111000f;

		double u = random.nextDouble();
		double v = random.nextDouble();
		double w = radiusInDegrees * Math.sqrt(u);
		double t = 2 * Math.PI * v;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);

		// Adjust the x-coordinate for the shrinking of the east-west distances
		double new_x = x / Math.cos(y0);

		double foundLongitude = new_x + x0;
		double foundLatitude = y + y0;
		System.out.println("Longitude: " + foundLongitude + "  Latitude: "
				+ foundLatitude);
		l.setLatitude(foundLatitude);
		l.setLongitude(foundLongitude);
		return l;
	}

	/**
	 * collect Address every 30 seconds
	 */
	public static void startAdresseCollector() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					TagSuggestionHandler tagSuggestionHandler = new TagSuggestionHandler();
					tagSuggestionHandler.execute();
					if (Optimizer.currentBestLoc() != null) {
						try {
							sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
		};
		thread.start();

	}

	/**
	 * suggest a list of addresses
	 */
	private void getlistOfSuggestionAddress() {
		List<Addresse> addressListTemp = new LinkedList<Addresse>();
		for (Location location : locationSuggestions()) {
			Addresse addr = getAddress(location);
			if (addr != null && !addressListTemp.contains(addr)) {
				addressListTemp.add(addr);
			}
		}
		if (!addressListTemp.isEmpty()) {
			addressList.clear();
			addressList.addAll(addressListTemp);
		}
	}

	/**
	 * @return get a list of address
	 */
	public static List<Addresse> getAddressList() {
		return addressList;
	}

	@Override
	protected String doInBackground(String... params) {
		getlistOfSuggestionAddress();
		return "";
	}

}
