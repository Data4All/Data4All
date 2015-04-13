package io.github.data4all.handler;

import io.github.data4all.suggestion.AddressSuggestionView;
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

import android.content.Context;
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
	
	//house_number
	private String addresseNr = "";
	//road
	private String road = "";
	//city
	private String city = "";
	//country
	private String country = "";
	//postCode
	private String postCode = "";
	
	private String display_name = "";
	
	//object for AddressSuggestion View
	private AddressSuggestionView view;

	private static final String TAG = "TagSuggestion";

	//represent the list of all suggestions Adresses
	private static List<Addresse> addressList = new LinkedList<Addresse>();

	private Context context;

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
	public Addresse getAddress(Location location) {
		try {
			JSONObject jsonObj = getJSONfromURL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
					+ location.getLatitude()
					+ "&lon="
					+ location.getLongitude() + "&zoom=18&addressdetails=1");

			/*
			 * display_name = jsonObj.getString("display_name"); if
			 * (display_name.contains(",")) { // Split it. String[] separate =
			 * display_name.split(","); addresseNr = separate[0]; road =
			 * separate[1]; city = separate[3]; postCode = separate[6]; }
			 * JSONObject address = jsonObj.getJSONObject("address"); country =
			 * address.getString("country");
			 */

			JSONObject address = jsonObj.getJSONObject("address");

				//addresseNr = address.getString("house_number");
				postCode = address.getString("postcode");
				//road = address.getString("road");
				city = address.getString("city");
				country = address.getString("country");	
			
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
			Log.e("log_tag", "Error parsing data [" + e.getMessage() + "] "
					+ address);
		}

		return jObject;
	}


	// list of location
	static List<Location> locations = new LinkedList<Location>();
	// current location
	static Location current = null;

	/**
	 * 
	 * @return a list of location nearby of the current location
	 */
	private static List<Location> locationSuggestions() {
		if (current == null) {
			current = Optimizer.currentBestLoc();
		} else {
			if (current.equals(Optimizer.currentBestLoc())) {
				return locations;
			}
		}

		locations.clear();
		locations.add(current);
		while (locations.size() < 5) {
			Location location = getLocation(current.getLongitude(),
					current.getLatitude(), 25);
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
	 * 
	 * @param x0
	 *            longitude
	 * @param y0
	 *            lattitude
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
	 * suggest a list of addresses
	 */
	private void getlistOfSuggestionAddress() {
		List<Addresse> addressListTemp = new LinkedList<Addresse>();
		DataBaseHandler db = new DataBaseHandler(context);
		for (Location location : locationSuggestions()) {
			Addresse addr = db.getAddressFromDb(location);
			boolean isneu = false;
			//when an address is not in database, then load address based from nominatim
			
			if (addr == null) {
				addr = getAddress(location);
				isneu = true;
			}
			if (addr != null && !addressListTemp.contains(addr)) {
				//when a address was already in database, then update this address
				//when not so insert this address in database
				if (isneu) {
					db.insertOrUpdateAddressInDb(location,
							addr.getAddresseNr(), addr.getRoad(),
							addr.getRoad(), addr.getCity(), addr.getCountry());
				}
				addressListTemp.add(addr);
			}
		}
		if (!addressListTemp.isEmpty()) {
			addressList.clear();
			addressList.addAll(addressListTemp);
		}
	}

	public void setContext(Context context) {
		this.context = context;
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

}
