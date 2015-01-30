/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
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

    private Parcel newParcel;

    @Before
    public void setUp() {
        newParcel = Parcel.obtain();
    }

    /**
     * Create a new Parcel to save/parcelable an OsmElement (node), afterwards
     * we check if the new parcel contains an instance of the node class.
     */
    @Test
    public void test_write_node() {
        Node node = new Node(1, 1, 10.1234567, 20.1234567);

        OsmElementBuilder.write(newParcel, node, 0);
        newParcel.setDataPosition(0);

        assertTrue(OsmElementBuilder.read(newParcel) instanceof Node);
    }

    /**
     * Create a new Parcel to save/parcelable an OsmElement (way), afterwards we
     * check if the new parcel contains an instance of the way class.
     */
    @Test
    public void test_write_way() {
        Way way = new Way(1, 1);

        OsmElementBuilder.write(newParcel, way, 0);
        newParcel.setDataPosition(0);

        assertTrue(OsmElementBuilder.read(newParcel) instanceof Way);
    }

    /**
     * Create a new Parcel to save/parcelable an OsmElement (relation),
     * afterwards we check if the new parcel contains an instance of the
     * relation class.
     */
    @Test
    public void test_write_relation() {
        Relation relation = new Relation(1, 1);

        OsmElementBuilder.write(newParcel, relation, 0);
        newParcel.setDataPosition(0);

        assertTrue(OsmElementBuilder.read(newParcel) instanceof Relation);
    }

    /**
     * Create a new Parcel to save/parcelable an OsmElement (in this case null),
     * tests if an illegalstateexception is thrown.
     */
    @Test
    public void test_write_null_element() {
        OsmElementBuilder.write(newParcel, null, 0);
        newParcel.setDataPosition(0);
        assertEquals(null, OsmElementBuilder.read(newParcel));
    }
}
