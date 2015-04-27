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
package io.github.data4all.model.data;

import io.github.data4all.handler.TagSuggestionHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class delivers all the data needed for the calculation
 * 
 * @author burghardt
 * @version 1.0
 *
 */

public class TransformationParamBean implements Parcelable {

    private double height;
    private double verticalViewAngle;
    private double horizontalViewAngle;
    private int photoWidth;
    private int photoHeight;
    private Location location;
    
	private Queue<Address>addresslist=new LinkedList<Address>();
	private String[] fullAddresslist;
    /**
     * CREATOR that generates instances of {@link TransformationParamBean} from
     * a Parcel.
     */
    public static final Parcelable.Creator<TransformationParamBean> CREATOR = new Parcelable.Creator<TransformationParamBean>() {
        public TransformationParamBean createFromParcel(Parcel in) {
            return new TransformationParamBean(in);
        }

        public TransformationParamBean[] newArray(int size) {
            return new TransformationParamBean[size];
        }
    };

    /**
     * @param height
     *            camera height in m
     * @param cameraMaxRotationAngle
     *            the maximum of the camerarotationangle
     * @param cameraMaxPitchAngle
     *            the maximum of the camerapitchangle
     * @param photoWidth
     *            the width of the devicedisplay
     * @param photoHeight
     *            the height of the devicedisplay
     * @param location
     *            location of the device
     */
    public TransformationParamBean(double height, double verticalViewAngle,
            double horizontalViewAngle, int photoWidth, int photoHeight,
            Location location) {
        this.height = height;
        this.horizontalViewAngle = horizontalViewAngle;
        this.verticalViewAngle = verticalViewAngle;
        this.photoHeight = photoHeight;
        this.photoWidth = photoWidth;
        this.location = location;
        TagSuggestionHandler.ladeBeanAddresse(this);
        
    }

    /**
     * Constructor to create a TransformationParamBean from a parcel.
     * 
     * @param in
     *            The parcel to read from
     */
    private TransformationParamBean(Parcel in) {
        height = in.readDouble();
        verticalViewAngle = in.readDouble();
        horizontalViewAngle = in.readDouble();
        photoWidth = in.readInt();
        photoHeight = in.readInt();
        if (in.readInt() != 0) {
            location = new Location(in.readString());
            location.setLatitude(in.readDouble());
            location.setLongitude(in.readDouble());
            TagSuggestionHandler.ladeBeanAddresse(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double getCameraMaxHorizontalViewAngle() {
        return horizontalViewAngle;
    }

    public double getCameraMaxVerticalViewAngle() {
        return verticalViewAngle;
    }

    public double getHeight() {
        return height;
    }

    public Location getLocation() {
        return location;
    }

    public int getPhotoHeight() {
        return photoHeight;
    }

    public int getPhotoWidth() {
        return photoWidth;
    }

    public void setMaxHorizontalViewAngle(float horizontalViewAngle) {
        this.horizontalViewAngle = horizontalViewAngle;
    }

    public void setCameraMaxVerticalViewAngle(float verticalViewAngle) {
        this.verticalViewAngle = verticalViewAngle;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPhotoHeight(int photoHeight) {
        this.photoHeight = photoHeight;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoWidth = photoWidth;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(height);
        dest.writeDouble(verticalViewAngle);
        dest.writeDouble(horizontalViewAngle);
        dest.writeInt(photoWidth);
        dest.writeInt(photoHeight);
        if (location != null) {
            dest.writeInt(1);
            dest.writeString(location.getProvider());
            dest.writeDouble(location.getLatitude());
            dest.writeDouble(location.getLongitude());
        } else {
            dest.writeInt(0);
        }
    }

    public String toString() {
        return "Height: " + height + " VerticalAngle: " + verticalViewAngle
                + " HorizntalAngle: " + horizontalViewAngle + " Width: "
                + photoWidth + " PhotoHeight: " + photoHeight;
    }

    public JSONArray toJSON() throws JSONException {
        List<Object> attributes = new ArrayList<Object>(9);
        attributes.add(height);
        attributes.add(verticalViewAngle);
        attributes.add(horizontalViewAngle);
        attributes.add(photoWidth);
        attributes.add(photoHeight);
        if (getLocation() == null)
            attributes.add(0);
        else {
            attributes.add(1);
            attributes.add(location.getProvider());
            attributes.add(location.getLatitude());
            attributes.add(location.getLongitude());
        }
        return new JSONArray(attributes);
    }

    public static TransformationParamBean fromJSON(JSONArray json)
            throws JSONException {
        Location location;
        if (json.getInt(5) == 0) {
            location = null;
        } else {
            location = new Location(json.getString(6));
            location.setLatitude(json.getDouble(7));
            location.setLongitude(json.getDouble(8));
        }
        return new TransformationParamBean(json.getDouble(0),
                json.getDouble(1), json.getDouble(2), json.getInt(3),
                json.getInt(4), location);
        
    }
    public Queue<Address> getAddresslist() {
		return addresslist;
	}
    public String[] getFullAdresseList(){
    	if(addresslist==null){
    		return null;
    	}
    	fullAddresslist=new String[addresslist.size()];
    	int i=0;
    	for (Address address : addresslist) {
    		
			fullAddresslist[i]=address.toJson();
			i++;
		}
    	return fullAddresslist;
    }


	public void setAddresslist(Queue<Address> addresslist) {
		this.addresslist = addresslist;
	}

}
