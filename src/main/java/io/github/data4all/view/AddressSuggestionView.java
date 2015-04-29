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
package io.github.data4all.view;

import io.github.data4all.R;
import io.github.data4all.handler.TagSuggestionHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Address;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * this class represents the view of all addresses suggestion
 * 
 * @author Steeve
 *
 */
public class AddressSuggestionView implements OnClickListener,
		android.view.View.OnClickListener {

	// Proposed List of addresses
	private Set<Address> addresses = new LinkedHashSet<Address>();

	// textView for road
	private TextView road;
	// textView for houseNumber
	private TextView houseNumber;
	// textView for postCode
	private TextView postCode;
	// textView for city
	private TextView city;
	// textView for country
	private TextView country;

	// context for AddressSuggestionview
	private Context context;

	// will be used to save addresses
	private String[] array;

	private Map<String, Integer> keyMapView = new HashMap<String, Integer>();

	// the button which load addresses
	private Button btnSelectAddress;

	private Activity activity;

	// The Dialog which show a list of suggestion addresses
	private AlertDialog alert;

	// listView of all Addresses
	private ListView listView;
	// The OSM Element
	private AbstractDataElement element;
	// The Map with the String and the Tag
	private Map<String, Tag> mapTag;

	private AlertDialog.Builder alertDialog;
	private ArrayAdapter<String> adapter;
	private List<String> keyList;

	private final int MAXNUMBER_OFADDRESSES = 5;

	// id of road
	private final int ROAD_ID = 401;
	// id of house_number
	private final int HOUSE_NUMBER_ID = 402;
	// id of postCode
	private final int POSTCODE_ID = 403;
	// id of city
	private final int CITY_ID = 404;
	// id of country
	private final int COUNTRY_ID = 405;

	/**
	 * Default constructor for AddressSuggestionView
	 * 
	 * @param activity
	 * @param context
	 * @param button
	 */
	public AddressSuggestionView(Activity activity, Context context,
			Button button) {
		this.setContext(context);
		this.activity = activity;
		this.btnSelectAddress = button;
		array = new String[MAXNUMBER_OFADDRESSES];
		btnSelectAddress.setOnClickListener(this);
		alertDialog = new AlertDialog.Builder(activity,
				android.R.style.Theme_Holo_Dialog_MinWidth);
		alertDialog.setCancelable(false);
		alertDialog.setTitle(R.string.selectAddress);
		adapter = new ArrayAdapter<String>(context,
				R.layout.view_adress_suggestions, array);
		alertDialog.setAdapter(adapter, this);

	}

	/**
	 * filled dialog with a list of addresses
	 * 
	 * @param activity
	 */
	public void fillDialog() {
		this.addresses = new LinkedHashSet<Address>(
				TagSuggestionHandler.addressList);

		final Set<String> fullAdresses = new TreeSet<String>(
		// sort fullAddresses
				new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareTo(rhs);
					}
				});

		// add all addresses in fullAddresses
		for (Address a : this.addresses) {
			if (!fullAdresses.contains(a.getFullAddress())) {
				fullAdresses.add(a.getFullAddress());
			}
		}

		// add all fullAdresses in array
		array = new String[fullAdresses.size()];
		int i = 0;
		for (String string : fullAdresses) {
			array[i] = string;
			i++;
		}
	}

	/**
	 * @param fullAddress
	 *            is road + house_number + postCode + city + country
	 * @return a selected Address
	 */
	public Address getSelectedAddress(String fullAddress) {

		for (Address a : addresses) {
			if (a.getFullAddress().equalsIgnoreCase(fullAddress)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void onClick(View arg0) {
		final Button selectAddress = (Button) arg0;
		// get a list of addresses when user click on the button selectAddress
		if (selectAddress.getText().equals(btnSelectAddress.getText())) {
			this.fillDialog();
			this.show();

		}

	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		final String value = array[which];
		final Address selectedAddress = getSelectedAddress(value);

		if (selectedAddress != null) {
			this.setValue(road, selectedAddress.getRoad(), ROAD_ID);
			this.setValue(houseNumber, selectedAddress.getAddresseNr(),
					HOUSE_NUMBER_ID);
			this.setValue(postCode, selectedAddress.getPostCode(), POSTCODE_ID);
			this.setValue(city, selectedAddress.getCity(), CITY_ID);
			this.setValue(country, selectedAddress.getCountry(), COUNTRY_ID);
		} else {
			this.setValue(road, "", ROAD_ID);
			this.setValue(houseNumber, "", HOUSE_NUMBER_ID);
			this.setValue(postCode, "", POSTCODE_ID);
			this.setValue(city, "", CITY_ID);
			this.setValue(country, "", COUNTRY_ID);
		}
		alert.dismiss();
	}

	/**
	 * 
	 * @param textview
	 *            for an address
	 * @param value
	 * @param id
	 *            represents the id of a tag(e.g
	 *            :street,house_number,road,city,country)
	 */
	private void setValue(TextView textview, String value, int id) {
		if (textview != null && value != null) {
			textview.setText(value);
			Tags.getTagWithId(id).setLastValue(value);
			element.addOrUpdateTag(Tags.getTagWithId(id), value);
		}

	}

	//all setter and getter methods
	public void setRoad(TextView road) {
		this.road = road;
	}

	public void setHouseNumber(TextView houseNumber) {
		this.houseNumber = houseNumber;
	}

	public void setPostCode(TextView postCode) {
		this.postCode = postCode;
	}

	public void setCity(TextView city) {
		this.city = city;
	}

	public void setCountry(TextView country) {
		this.country = country;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setListview(ListView listView) {
		this.listView = listView;
	}

	public ListView getListView() {
		return listView;
	}

	public List<String> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}

	public void setElement(AbstractDataElement element) {
		this.element = element;
	}

	public Map<String, Tag> getMapTag() {
		return mapTag;
	}

	public void setMapTag(Map<String, Tag> mapTag) {
		this.mapTag = mapTag;

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * show addresses in dialog
	 */
	public void show() {
		adapter = new ArrayAdapter<String>(context,
				R.layout.view_adress_suggestions, array);
		alertDialog.setAdapter(adapter, this);
		adapter.notifyDataSetChanged();
		alert = alertDialog.create();

		alert.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					alert.dismiss();
					return true;
				}
				return true;
			}
		});
		alert.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alert.show();

	}

	/**
	 * Gives a list of tags(e.g street, postCode,country) for each
	 * ClassifiedValue
	 * 
	 * @param res
	 *            the Resource
	 * @param value
	 *            the classifiedValue
	 */
	public void addKeyMapEntry(Resources res, ClassifiedValue value) {
		if (value == null) {
			return;
		}
		final List<Tag> tagList = new LinkedList<Tag>();
		tagList.addAll(Tags.getAllAddressTags());
		List<Boolean> booleanList = new ArrayList<Boolean>();
		booleanList = value.getAllUnclassifiedBooleans();

		for (int i = 0; i < booleanList.size(); i++) {
			if (booleanList.get(i)
					&& !keyMapView.containsValue(res.getString(tagList.get(i)
							.getNameRessource()))) {
				Tag tag = tagList.get(i);
				keyMapView.put(res.getString(tag.getNameRessource()),
						tag.getId());
			}
		}
	}

	/**
	 * saved two line list item
	 * 
	 * @param key
	 * @param text1
	 *            for a tag
	 * @param text2
	 *            value of a tag
	 */
	public void savedTwoLineListItem(String key, TextView text1, TextView text2) {
		Integer tagid = keyMapView.get(key);
		if (tagid == null) {
			return;
		}
		if (tagid.intValue() == ROAD_ID) {
			road = text2;

		}
		if (tagid.intValue() == HOUSE_NUMBER_ID) {
			houseNumber = text2;
		}
		if (tagid.intValue() == POSTCODE_ID) {
			postCode = text2;
		}
		if (tagid.intValue() == CITY_ID) {
			city = text2;
		}
		if (tagid.intValue() == COUNTRY_ID) {
			country = text2;
		}

	}

}
