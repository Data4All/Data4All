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
import io.github.data4all.activity.ResultViewActivity;
import io.github.data4all.handler.TagSuggestionHandler;
import io.github.data4all.model.data.Address;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.DataElement;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.Optimizer;
import io.github.data4all.util.upload.Callback;

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
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * this class represents the view of all suggestions addresses
 * 
 * @author Steeve
 *
 */
public class AddressSuggestionView implements OnClickListener,
        android.view.View.OnClickListener {

    private static final String TAG = "Adressview";

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

    // the map with the string and integer
    private Map<String, Integer> keyMapView = new HashMap<String, Integer>();

    // the button which load addresses
    private Button btnSelectAddress;

    private ResultViewActivity activity;

    // The Dialog which show a list of suggestion addresses
    private AlertDialog alert;

    // listView of all Addresses
    private ListView listView;
    // The OSM Element
    private DataElement element;
    // The Map with the String and the Tag
    private Map<String, Tag> mapTag;

    private AlertDialog.Builder alertDialog;
    private ArrayAdapter<String> adapter;
    private List<String> keyList;
    private Location location;
    private Resources res;

    //maximum of addresses  allowed
    private static final int MAXNUMBER_OFADDRESSES = 5;

    // id of road
    private static final int ROAD_ID = 401;
    // id of house_number
    private static final int HOUSE_NUMBER_ID = 402;
    // id of postCode
    private static final int POSTCODE_ID = 403;
    // id of city
    private static final int CITY_ID = 404;
    // id of country
    private static final int COUNTRY_ID = 405;

    private Callback<Void> onAddressSelect;

    /**
     * Default constructor for AddressSuggestionView
     * 
     * @param activity
     * @param context
     * @param button
     */
    public AddressSuggestionView(ResultViewActivity activity, Button button,
            Callback<Void> onAddressSelect) {
        this.onAddressSelect = onAddressSelect;
        this.setContext(activity);
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
        this.activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

    }

    /**
     * filled dialog with a list of addresses
     * 
     * @param activity
     */
    public void fillDialog() {

        if (this.addresses == null || this.addresses.isEmpty()) {
            array = new String[] {activity.getResources().getString(R.string.addressNoAvailable)};
            return;
        }

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
     * 
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
            new AddressLoader().execute();
        }

    }

    @Override
    public void onClick(DialogInterface arg0, int which) {
        final String value = array[which];
        final Address selectedAddress = this.getSelectedAddress(value);

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
        onAddressSelect.callback(null);
        alert.dismiss();
    }

    /**
     * 
     * @param textview
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

    // all setter and getter methods
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

    public void setElement(DataElement element) {
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

    public void setActivity(ResultViewActivity activity) {
        this.activity = activity;
    }

    /**
     * show addresses in dialog
     */
    public void show() {
        adapter = new ArrayAdapter<String>(context,
                R.layout.view_adress_suggestions, array);
        alertDialog.setAdapter(adapter, this);
        alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
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
        adapter.notifyDataSetChanged();
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
        List<Boolean> booleanList = value.getAllUnclassifiedBooleans();

        for (int i = 0; i < booleanList.size(); i++) {
            if (booleanList.get(i)
                    && tagList.size() > i
                    && !keyMapView.containsValue(res.getString(tagList.get(i)
                            .getNameRessource()))) {
                final Tag tag = tagList.get(i);
                keyMapView.put(res.getString(tag.getNameRessource()),
                        tag.getId());
            }
        }
    }

    /**
     * saved two line list item
     * 
     * @param key
     * @param text2
     *            value of a tag
     */
    public void savedTwoLineListItem(String key, TextView text2) {
        final Integer tagid = keyMapView.get(key);

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

    public void setLocation(Location l) {
        if (l == null) {
            this.location = Optimizer.currentBestLoc();
        } else {
            this.location = l;
        }
    }

    public Location getLocation() {
        return location;
    }
    
    /**
     * fill addresses based on location of OsmElement
     */
    public void fillAddress () {
    	final List<Address> currentAdresses = TagSuggestionHandler
                .getLastSuggestions();
        Log.i(TAG, "filldialog");
        if (currentAdresses != null && !currentAdresses.isEmpty()) {
            this.addresses = new LinkedHashSet<Address>(currentAdresses);
            Log.i(TAG, "fillcurrent");
        }
        else{
             Log.i(TAG, "showing progressbar");
             while( TagSuggestionHandler
                .getLastSuggestions()==null ||  TagSuggestionHandler
                .getLastSuggestions().isEmpty()){
            	 //Loading
            	 
            	 Log.i(TAG, "loading");
             }
             this.addresses=new LinkedHashSet<Address>(TagSuggestionHandler
                     .getLastSuggestions());
             Log.i(TAG, "dismiss progessbar");
        }
    }
   
    private class AddressLoader extends AsyncTask<Void, Void,Void> {
      
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			fillAddress();
			return null;
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			activity.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
			fillDialog();
			show();
		}
    }
}

