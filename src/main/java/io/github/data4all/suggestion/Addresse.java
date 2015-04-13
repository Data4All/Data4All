package io.github.data4all.suggestion;


/**
 * This class contains all the informations of an Address
 * e.g road, house_number, postCode, city, country
 * @author Steeve
 *
 */
public class Addresse {
    
	//house_number
    private String addresseNr="";
    //road
    private String road = "";
    //city
    private String city = "";
    //country
    private String country = "";
    //postCode
    private String postCode = "";
   
    //id of address
    private int addressId;
    
    // private Location location; 
    
    
    //all getter and setter method
    
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

	public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
    
    public String getRoad() {
		return road;
	}

	public String getPostCode() {
		return postCode;
	}

    public String getAddresseNr() {
        return addresseNr;
    }
/*
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location ){
    	this.location = location;
    }*/
    
    /**
     * get a full address 
     * @return road + addresseNr + postCode + city + country
     */
    public String getFullAddress(){
    	return getRoad() +" " + getAddresseNr() + " " +getPostCode() + " " +getCity() + " " + getCountry();
    }
    
    /**
     * compare two full address
     */
    @Override
    public boolean equals(Object o) {
    	// TODO Auto-generated method stub
    	return getFullAddress().equals(((Addresse)o).getFullAddress());
    }   
   

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
    


}
