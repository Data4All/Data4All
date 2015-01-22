package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

/**
 * Testing the main functionality of the relation member objects.
 * 
 * @author fkirchge
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RelationMemberTest {
	
    /**
     * Create a new parcial to save/parcelable the testRelationMember, afterwards 
     * a new relation member is created from the parcel and we check if it contains all attributes.
     */
	@Test 
    public void test_parcelable_relationmember() {
    	Parcel newParcel = Parcel.obtain();
    	RelationMember testRelationMember = new RelationMember("type", 12345, "role");
        
    	Node newNode = new Node(1, 2, 10.1234567, 20.1234567);
    	testRelationMember.setElement(newNode);
    	
    	testRelationMember.writeToParcel(newParcel, 0);
    	newParcel.setDataPosition(0);
    	RelationMember deParcelRelationMember = RelationMember.CREATOR.createFromParcel(newParcel);
    	
    	assertEquals(testRelationMember.getType(), deParcelRelationMember.getType());
    	assertEquals(testRelationMember.getRef(), deParcelRelationMember.getRef());
    	assertEquals(testRelationMember.getRole(), deParcelRelationMember.getRole());
    	
    	assertEquals(newNode.getOsmId(), deParcelRelationMember.getElement().getOsmId());
    	assertEquals(newNode.getOsmVersion(), deParcelRelationMember.getElement().getOsmVersion());
    }

}
