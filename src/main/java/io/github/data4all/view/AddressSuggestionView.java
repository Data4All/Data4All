package io.github.data4all.view;

import io.github.data4all.R;
import io.github.data4all.handler.TagSuggestionHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Address;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.LocationWrapper;
import io.github.data4all.util.Optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
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

    // Proposed List of all addresses
    private Queue<Address> addresses = new LinkedList<Address>();

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

    // this array will be used to save addresses
    private String[] array;

    Map<String, Integer> keyMapView = new HashMap<String, Integer>();

    // the button which load addresses
    private Button btnSelectAddress;

    private Activity activity;

    // The Dialog which show a list of suggestion addresses
    AlertDialog alert;

    // listView of all Addresses
    private ListView listView;
    // The OSM Element
    private AbstractDataElement element;
    // The Map with the String and the Tag
    Map<String, Tag> mapTag;

    final AlertDialog.Builder alertDialog;
    private ArrayAdapter<String> adapter;
    private List<String> keyList;

    /**
     * Default constructor for AddressSuggestionView
     * 
     * @param context
     */
    public AddressSuggestionView(Activity activity, Context context,
            Button button) {
        this.setContext(context);
        this.activity = activity;
        this.btnSelectAddress = button;
        array = new String[5];
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
        if (location != null) {
            Queue<? extends Address> address = TagSuggestionHandler.get(location);
            if (address != null) {
            this.addresses = new LinkedList<Address>(
                    address);
            }
        }
        if (this.addresses == null || this.addresses.isEmpty()) {
            Queue<? extends Address> currentAdresses = TagSuggestionHandler
                    .get(Optimizer.currentBestLoc());
            if (currentAdresses != null) {
                this.addresses = new LinkedList<Address>(currentAdresses);
            }
            if (this.addresses == null || this.addresses.isEmpty()) {
                array = new String[] { "Currently no possibility avalaible" };
                return;
            }
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
        Button selectAddress = (Button) arg0;
        // handler.execute();
        // load addresses when user click of the button selectAddress
        if (selectAddress.getText().equals(btnSelectAddress.getText())) {
            fillDialog();
            show();
        }

    }

    @Override
    public void onClick(DialogInterface arg0, int which) {
        final String value = (String) array[which];
        Address selectedAddress = getSelectedAddress(value);

        if (selectedAddress != null) {
            setValue(road, selectedAddress.getRoad(), 401);
            setValue(houseNumber, selectedAddress.getAddresseNr(), 402);
            setValue(postCode, selectedAddress.getPostCode(), 403);
            setValue(city, selectedAddress.getCity(), 404);
            setValue(country, selectedAddress.getCountry(), 405);
        } else {
            setValue(road, "", 401);
            setValue(houseNumber, "", 402);
            setValue(postCode, "", 403);
            setValue(city, "", 404);
            setValue(country, "", 405);
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

    /**
     * set road
     * 
     * @param road
     */
    public void setRoad(TextView road) {
        this.road = road;
    }

    /**
     * set house_number
     * 
     * @param houseNumber
     */
    public void setHouseNumber(TextView houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * set postCode
     * 
     * @param postCode
     */
    public void setPostCode(TextView postCode) {
        this.postCode = postCode;
    }

    /**
     * set city
     * 
     * @param city
     */
    public void setCity(TextView city) {
        this.city = city;
    }

    /**
     * set country
     * 
     * @param country
     */
    public void setCountry(TextView country) {
        this.country = country;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * show addresses in dialog
     */
    public void show() {
        adapter = new ArrayAdapter<String>(context,
                R.layout.view_adress_suggestions, array);
        alertDialog.setAdapter(adapter, this);
        // adapter.addAll(array);
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

    public void setMapTag(Map<String, Tag> mapTag) {
        this.mapTag = mapTag;

    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void addKeyMapEntry(Resources res, ClassifiedValue value) {
        if (value == null) {
            return;
        }
        List<Tag> tagList = new LinkedList<Tag>();
        tagList.addAll(Tags.getAllAddressTags());
        List<Boolean> booleanList = new ArrayList<Boolean>();
        booleanList = value.getAllUnclassifiedBooleans();

        for (int i = 0; i < booleanList.size(); i++) {
            if (booleanList.get(i)
                    && tagList.size() > i
                    && !keyMapView.containsValue(res.getString(tagList.get(i)
                            .getNameRessource()))) {
                Tag tag = tagList.get(i);
                keyMapView.put(res.getString(tag.getNameRessource()),
                        tag.getId());
            }
        }
    }

    public void registriereTwoLineListItem(String key, TextView text1,
            TextView text2) {
        Integer tagid = keyMapView.get(key);
        if (tagid == null) {
            return;
        }
        if (tagid.intValue() == 401) {
            road = text2;

        }
        if (tagid.intValue() == 402) {
            houseNumber = text2;
        }
        if (tagid.intValue() == 403) {
            postCode = text2;
        }
        if (tagid.intValue() == 404) {
            city = text2;
        }
        if (tagid.intValue() == 405) {
            country = text2;
        }

    }

    private Location location;

    public void setLocation(Location l) {
        if (l == null) {
            this.location = Optimizer.currentBestLoc();
        } else {
            this.location = l;
        }

        if (this.location != null && TagSuggestionHandler.get(location) == null) {
            TagSuggestionHandler handler = new TagSuggestionHandler();
            handler.setCurrent(this.location);
        }
    }

}
