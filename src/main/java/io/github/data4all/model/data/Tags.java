package io.github.data4all.model.data;

import java.util.HashMap;
import java.util.Map;

/**
 * List of tags for osm elements:
 *  - unclassifiedTags: user have to input text manually to set the value
 *  - classifiedTags: user can choose the input from listed values 
 *  
 * @author fkirchge
 *
 */
public class Tags {

	/**
	 * Tags with undefined user input. 
	 */
	public static String[] unclassifiedTags = (
			"addr:housenumber,addr:street,addr:postcode,addr:city,addr:country, addr:full,"
					+ "contact:phone,contact:fax,contact:website,contact:email"
			).split(",");

	/**
	 * Tags with predefined values.
	 */
	public static Map<String, String> classifiedTags = new HashMap<String, String>() {
		{
			put("highway", "motorway,residential,service,track,footway,road,path");
			put("barrier", "citywall,fence,wall,bollard,gate");
			put("amenity", "bar,cafe,restaurant,fast_food,pub,collage,"
					+ "library,school,university,parking,taxi,fuel,bank,"
					+ "hospital,pharmacy,cinema,bench,embassy,marketplace,"
					+ "police,post_office,toilets,water_point,fire_station,public_building");
			put("building", "apartments,farm,hotel,house,commercial,industrial,"
					+ "retail,warehouse,church,hospital,train_station,university");
			put("landuse", "commercial,construction,farmland,forest,grass,industrial,millitary,residential");
            //put("tourism", "") 
			//put("natural", "")
		};
	};
	
	/**
	 * Returns all unclassified tags 
	 * @return
	 */
	public static String[] getUnclassifiedTags() {
		return unclassifiedTags;
	}
	
	/**
	 * Returns all classified tags
	 * @return
	 */
	public static Map<String, String> getClassifiedTags() {
		return classifiedTags;
	}
}