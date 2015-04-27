package io.github.data4all.model.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains all the informations of an Address e.g road,
 * house_number, postCode, city, country
 * 
 * @author Steeve
 *
 */
public class Address implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6836778014617785812L;

    // attribute to save a house_number
    private String addresseNr = "";
    // attribute to save a road
    private String road = "";
    // attribute to save a city
    private String city = "";
    // attribute to save a country
    private String country = "";
    // attribute to store a postCode
    private String postCode = "";

    // id of one address
    private int addressId;

    // all getter and setter method
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setAddresseNr(String addresseNr) {
        this.addresseNr = addresseNr;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * 
     * @return the Id of an address
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * 
     * @return a city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @return a country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @return a road
     */
    public String getRoad() {
        return road;
    }

    /**
     * 
     * @return a postcode
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * 
     * @return a addressNr
     */
    public String getAddresseNr() {
        return addresseNr;
    }

    /**
     * get a full address
     * 
     * @return road + addresseNr + postCode + city + country
     */
    public String getFullAddress() {
        return getRoad() + " " + getAddresseNr() + " " + getPostCode() + " "
                + getCity() + " " + getCountry();
    }

    /**
     * compare two full address
     */
    @Override
    public boolean equals(Object o) {
        return getFullAddress().equals(((Address) o).getFullAddress());
    }

    @Override
    public int hashCode() {
        return getFullAddress().hashCode();
    }
    
    /**
     * 
     * @return 
     */
    public String toJson(){
    	JSONObject json=new JSONObject();
    	try {
			json.put("addresseNr", addresseNr);
			json.put("road", road);
			json.put("city", city);
			json.put("postcode", postCode);
			json.put("country",country);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return json.toString();
    }
}
