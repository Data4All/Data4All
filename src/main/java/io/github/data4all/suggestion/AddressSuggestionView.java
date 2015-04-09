package io.github.data4all.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * this class represents the view of all suggestions addresses
 * @author Steeve
 *
 */
public class AddressSuggestionView implements OnItemSelectedListener {
	
	/*Spinners provide a quick way to select one value from a set. 
	* In the default state, a spinner shows its currently selected value.
	*Touching the spinner displays a dropdown menu with all other available values, 
	*from which the user can select a new one
	*/
	private Spinner spinner;
	
	private List<Addresse> addresses = new LinkedList<Addresse>();
	
	private EditText road;
	private EditText houseNumber;
	private EditText postCode;
	private EditText city;
	private EditText country;
	private Addresse bestAdresse;

	/**
	 * get the bestAddress with the best location
	 * @return bestAdresse
	 */
	public Addresse getBestAddresse() {
		return bestAdresse;
	}

	private ArrayAdapter<String> dataAdapter;

	public ArrayAdapter<String> getDataAdapter() {
		return dataAdapter;
	}
    
	/**
	 * Default constructor for AddressSuggestionView
	 * @param context
	 */
	public AddressSuggestionView(Context context) {
		spinner = new Spinner(context);
		spinner.setOnItemSelectedListener(this);
		dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		spinner.setAdapter(dataAdapter);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
    
	/**
	 * get Spinner
	 * @return spinner
	 */
	public Spinner getSpinner() {
		return spinner;
	}

	public void setSpinner(Spinner spinner) {
		this.spinner = spinner;
	}
    
	/**
	 * get a list of address
	 * @return
	 */
	public List<Addresse> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Addresse> addresses) {
		this.addresses.clear();
		this.addresses.addAll(addresses);
		if(!this.addresses.isEmpty()){
			// set bestaddress an first position
			bestAdresse=this.addresses.get(0);
		}
	}
   
	/**
	 * filled Spinner with a list of addresses
	 * @param activity
	 */
	public void fillSpinner(Activity activity) {
		if (addresses == null || addresses.isEmpty()) {
			return;
		}
		if (dataAdapter.getCount() > 0) {
			dataAdapter.clear();
			dataAdapter.notifyDataSetChanged();
		}
		dataAdapter.add("Adresse auswählen:");
		List<String> fullAdresses=new ArrayList<String>();
		
		for (Addresse a : addresses) {
			fullAdresses.add(a.getFullAddress());
		}
		//sort full address
		Collections.sort(fullAdresses, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
		
		//add all address in dataAdapter
		for (String a : fullAdresses) {
			dataAdapter.add(a);
		}
		dataAdapter.notifyDataSetChanged();
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Addresse selectedAddress = getSelectedAddress(parent.getItemAtPosition(
				position).toString());
		if (selectedAddress != null) {
			houseNumber.setText(selectedAddress.getAddresseNr());
			road.setText(selectedAddress.getRoad());
			postCode.setText(selectedAddress.getPostCode());
			city.setText(selectedAddress.getCity());
			country.setText(selectedAddress.getCountry());
		}else{
			houseNumber.setText("");
			road.setText("");
			postCode.setText("");
			city.setText("");
			country.setText("");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	/**
	 * set road
	 * @param road
	 */
	public void setRoad(EditText road) {
		this.road = road;
	}
    
	/**
	 * set house_number
	 * @param houseNumber
	 */
	public void setHouseNumber(EditText houseNumber) {
		this.houseNumber = houseNumber;
	}
    
	/**
	 * set postCode
	 * @param postCode
	 */
	public void setPostCode(EditText postCode) {
		this.postCode = postCode;
	}
   
	/**
	 * set city
	 * @param city
	 */
	public void setCity(EditText city) {
		this.city = city;
	}

	/**
	 * set country
	 * @param country
	 */
	public void setCountry(EditText country) {
		this.country = country;
	}
}
