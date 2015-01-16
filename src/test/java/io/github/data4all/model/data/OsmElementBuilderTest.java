package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing the main functionality of the OsmElementBuilder.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmElementBuilderTest {
	
    /**
     * Create a new parcial to save/parcelable an osm element (node), 
     * afterwards we check if the new parcel contains an instance of the node class.
     */
	@Test
	public void test_write_node() {
		Parcel newParcel = Parcel.obtain();
		Node node = new Node(1, 1, 10.1234567, 20.1234567);
		
		OsmElementBuilder.write(newParcel, node, 0);
		newParcel.setDataPosition(0);
		
		assertEquals(true, OsmElementBuilder.read(newParcel) instanceof Node); 
	}
	
    /**
     * Create a new parcial to save/parcelable an osm element (way), 
     * afterwards we check if the new parcel contains an instance of the way class.
     */
	@Test
	public void test_write_way() {
		Parcel newParcel = Parcel.obtain();
		Way way = new Way(1, 1);
		
		OsmElementBuilder.write(newParcel, way, 0);
		newParcel.setDataPosition(0);
		
		assertEquals(true, OsmElementBuilder.read(newParcel) instanceof Way); 
	}
	
    /**
     * Create a new parcial to save/parcelable an osm element (relation), 
     * afterwards we check if the new parcel contains an instance of the relation class.
     */
	@Test
	public void test_write_relation() {
		Parcel newParcel = Parcel.obtain();
		Relation relation = new Relation(1, 1);
		
		OsmElementBuilder.write(newParcel, relation, 0);
		newParcel.setDataPosition(0);
		
		assertEquals(true, OsmElementBuilder.read(newParcel) instanceof Relation); 
	}

}
