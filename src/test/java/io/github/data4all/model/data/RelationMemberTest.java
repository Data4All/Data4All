/* 
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * Create a new Parcel to save/parcelable the testRelationMember, afterwards
     * a new relation member is created from the parcel and we check if it
     * contains all attributes.
     */
    @Test
    public void test_parcelable_relationmember() {
        Parcel newParcel = Parcel.obtain();
        RelationMember testRelationMember = new RelationMember("type", 12345,
                "role");

        Node newNode = new Node(1, 2, 10.1234567, 20.1234567);
        testRelationMember.setElement(newNode);

        testRelationMember.writeToParcel(newParcel, 0);
        newParcel.setDataPosition(0);
        RelationMember deParcelRelationMember = RelationMember.CREATOR
                .createFromParcel(newParcel);

        assertEquals(testRelationMember.getType(),
                deParcelRelationMember.getType());
        assertEquals(testRelationMember.getRef(),
                deParcelRelationMember.getRef());
        assertEquals(testRelationMember.getRole(),
                deParcelRelationMember.getRole());

        assertEquals(newNode.getOsmId(), deParcelRelationMember.getElement()
                .getOsmId());
        assertEquals(newNode.getOsmVersion(), deParcelRelationMember
                .getElement().getOsmVersion());
    }
}
