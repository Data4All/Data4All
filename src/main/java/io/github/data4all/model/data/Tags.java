package io.github.data4all.model.data;

import java.util.HashMap;
import java.util.Map;

/**
 * List of tags for osm elements: - unclassifiedTags: user have to input text
 * manually to set the value - classifiedTags: user can choose the input from
 * listed values
 * 
 * @author fkirchge
 *
 */
public class Tags {

	
	 /* Unclassified Tags: */
	
    /**
     * Tags with address values
     * tag: addressTags[i][0]
     * hint: addressTags[i][1]
     * input: addressTags[i][2]
     */
    public static String[][] addressTags = {
    	{"addr:street","Street", "1"},
    	{"addr:housenumber","HouseNumber", "2"},
    	{"addr:postcode","Postcode", "2"},
    	{"addr:city", "City", "1"},
    	{"addr:country","Country", "1"}
    };
   
    
    /**
     * Tags with contact values
     * tag: contactTags[i][0]
     * hint: contactTags[i][1]
     * input: addressTags[i][2]
     */
    public static String[][] contactTags = {
    	{"contact:phone","Phone", "2"},
    	{"contact:fax","Fax", "2"},
    	{"contact:website", "Website", "1"},
    	{"contact:email", "Email", "1"}
    };
    
    /**
     * Tags with predefined values.
     */
    public static Map<String, String> classifiedTags = new HashMap<String, String>() {
        {
            put("highway",
                    "motorway,residential,service,track,footway,road,path");
            put("barrier", "citywall,fence,wall,bollard,gate");
            put("amenity",
                    "bar,cafe,restaurant,fast_food,pub,collage,"
                            + "library,school,university,parking,taxi,fuel,bank,"
                            + "hospital,pharmacy,cinema,bench,embassy,marketplace,"
                            + "police,post_office,toilets,water_point,fire_station,public_building");
            put("building",
                    "apartments,farm,hotel,house,commercial,industrial,"
                            + "retail,warehouse,church,hospital,train_station,university");
            put("landuse",
                    "commercial,construction,farmland,forest,grass,industrial,millitary,residential");
            // put("tourism", "")
            // put("natural", "")
        };
    };

    /**
     * Returns all classified tags
     * 
     * @return
     */
    public static Map<String, String> getClassifiedTags() {
        return classifiedTags;
    }

    /**
     * Returns all address tags
     * 
     * @return
     */
    public static String[][] getAddressTags() {
        return addressTags;
    }

    /**
     * Returns all contact tags
     * 
     * @return
     */
    public static String[][] getContactTags() {
        return contactTags;
    }

}