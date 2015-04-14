package io.github.data4all.suggestion;

import io.github.data4all.R;
import io.github.data4all.activity.ResultViewActivity;
import io.github.data4all.handler.TagSuggestionHandler;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.ClassifiedValue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * this class represents the view of all suggestions addresses
 * 
 * @author Steeve
 *
 */
public class AddressSuggestionView implements OnClickListener,
		android.view.View.OnClickListener {

	// Proposed List of all addresses
	private Set<Addresse> addresses = new LinkedHashSet<Addresse>();

	// textview for road
	private TextView road;
	// textview for houseNumber
	private TextView houseNumber;
	// textView for postCode
	private TextView postCode;
	// textview for city
	private TextView city;
	// textview for country
	private TextView country;

	// the current best address
	private Addresse bestAdresse;
	
	//context for AddressSuggestionview
	private Context context;

	// to saved addresses 
	private String[] array;

	Map<String, Integer> keyMapView = new HashMap<String, Integer>();

	// the button which suggest addresses
	private Button btnSelectAddress;

	private Activity activity;
	
	// The Dialog which show a list of suggestion addresses
	AlertDialog alert;
	
	
	/**
	 * get the bestAddress with the best location
	 * 
	 * @return bestAdresse
	 */
	public Addresse getBestAddresse() {
		return bestAdresse;
	}

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
		btnSelectAddress.setOnClickListener(this);

	}

	/**
	 * get a list of address
	 * 
	 * @return
	 */
	public Set<Addresse> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Addresse> addresses) {
		this.addresses.clear();
		this.addresses.addAll(addresses);
		if (!this.addresses.isEmpty()) {
		    List<Addresse> addressList =new LinkedList<Addresse>(addresses);
			// set best address an first position
			bestAdresse = addressList.get(0); 
		}
	}

	/**
	 * filled dialog with a list of addresses
	 * 
	 * @param activity
	 */
	public void fillDialog() {
		if (addresses == null || addresses.isEmpty()) {
			return;
		}

		final Set<String> fullAdresses = new HashSet<String>();

		for (Addresse a : addresses) {
			if (!fullAdresses.contains(a)) {
				fullAdresses.add(a.getFullAddress());
			}
		}
		
		List<String> list = new ArrayList<String>(fullAdresses);

		///Using Collections.sort() to sort addresses
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});

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
	 * @return a selected Address
	 */
	public Addresse getSelectedAddress(String fullAddress) {

		for (Addresse a : addresses) {
			if (a.getFullAddress().equalsIgnoreCase(fullAddress)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Button selectAddress = (Button) arg0;      
		TagSuggestionHandler handler = new TagSuggestionHandler();
		/*
		 * load addresses after user click on the button textView,
		 */
		handler.execute();
		if (selectAddress.getText().equals(btnSelectAddress.getText())) {
			handler.setContext(this.context);
			handler.setView(this);
			TagSuggestionHandler.getAddressList();
		}
		
	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		// TODO Auto-generated method stub
		final String value = (String) array[which];
		Addresse selectedAddress = getSelectedAddress(value);

		if(selectedAddress != null) {
		        if(selectedAddress.getAddresseNr()==null&& selectedAddress.getRoad()==null){
		            houseNumber.setText("");
	                road.setText("");
	                postCode.setText(selectedAddress.getPostCode());
	                city.setText(selectedAddress.getCity());
	                country.setText(selectedAddress.getCountry()); 
		        } else if(selectedAddress.getAddresseNr()==null&& selectedAddress.getRoad()!=null
		              && selectedAddress.getPostCode()!=null 
		              && selectedAddress.getCity()!=null && selectedAddress.getCountry()!=null) {
		            houseNumber.setText("");
                    road.setText(selectedAddress.getRoad());
                    postCode.setText(selectedAddress.getPostCode());
                    city.setText(selectedAddress.getCity());
                    country.setText(selectedAddress.getCountry());
		        }else if (selectedAddress.getAddresseNr()!=null&& selectedAddress.getRoad()!=null
	                      && selectedAddress.getPostCode()!=null 
	                      && selectedAddress.getCity()!=null && selectedAddress.getCountry()!=null) {
				houseNumber.setText(selectedAddress.getAddresseNr());
				road.setText(selectedAddress.getRoad());
				postCode.setText(selectedAddress.getPostCode());
				city.setText(selectedAddress.getCity());
				country.setText(selectedAddress.getCountry());
		        }
		}else {
			houseNumber.setText("");
			road.setText("");
			postCode.setText("");
			city.setText("");
			country.setText("");
		}
		alert.dismiss();
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

	/*
	 * show addresses in dialog
	 */
	public void show() {
	    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity,
                android.R.style.Theme_Holo_Dialog_MinWidth);
		alertDialog.setCancelable(false);
		alertDialog.setTitle(R.string.selectAddress);
		alertDialog.setItems(array, this);
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

	public Button getBtnSelectAddress() {
		return btnSelectAddress;
	}
	
	//view of all Addresses
	private ListView listView;


	public void setListview(ListView listView) {
		this.listView = listView;

	}

	public ListView getListView() {
	    return listView;
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
					&& !keyMapView.containsValue(res.getString(tagList.get(i)
							.getNameRessource()))) {
				Tag tag = tagList.get(i);
				keyMapView.put(res.getString(tag.getNameRessource()),
						tag.getId());
			}
		}
	}
  
	private List<String> keyList;
	
	public List<String> getKeyList() {
        return keyList;
    }
	
	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
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
}
