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
package io.github.data4all.util;

import static org.junit.Assert.assertTrue;
import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.network.OscUploadHelper;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

/**
 * Test for getting changeSetID, parsing Osc and uploading Osc
 * 
 * @author Richard
 */

// Use Robolectric
@RunWith(RobolectricTestRunner.class)
// EmulateSdk-18 because Robolectric cannot handle Sdk-19+
@Config(emulateSdk = 18)
public class OsmChangeParserTest {

    /**
     * This test doesn't work in JUnit, because of missing Authorization, copy
     * test on Button to test it (please only on the development API)
     */
    @Test
    public void uploadTest() {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        ArrayList<AbstractDataElement> elems = new ArrayList<AbstractDataElement>();
        Node node1 = new Node(-1, 23, 23);
        Node node2 = new Node(-2, 24, 24);
        Node node3 = new Node(-3, 25, 25);
        PolyElement way1 = new PolyElement(-4, PolyElementType.WAY);
        way1.addNode(node1);
        way1.addNode(node2);
        elems.add(node1);
        elems.add(node3);
        elems.add(way1);
        Context act = Robolectric.buildActivity(MapViewActivity.class).get()
                .getApplicationContext();
        Log.d("test", "start the Request");
        new OscUploadHelper(act, elems, "upload test");

    }

    /**
     * This test checks if the File created by the Parser exists and is not
     * empty
     */
    @Test
    public void parseTest() {
        ArrayList<AbstractDataElement> elems = new ArrayList<AbstractDataElement>();
        Node node1 = new Node(-1, 23, 23);
        Node node2 = new Node(-2, 24, 24);
        Node node3 = new Node(-3, 25, 25);
        PolyElement way1 = new PolyElement(-4, PolyElementType.WAY);
        way1.addNode(node1);
        way1.addNode(node2);
        elems.add(node1);
        elems.add(node3);
        elems.add(way1);
        Context act = Robolectric.buildActivity(MapViewActivity.class).get()
                .getApplicationContext();

        File file = new File(act.getFilesDir().getAbsolutePath()
                + "/OsmChangeUpload.osc");
        OsmChangeParser.parseElements(act, elems, 13);

        assertTrue(file.length() > 0);
        assertTrue(file.exists());
    }

}
