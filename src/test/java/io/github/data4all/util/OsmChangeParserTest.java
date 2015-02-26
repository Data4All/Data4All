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
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test for parsing Osc
 * 
 * @author Richard
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmChangeParserTest {

    /**
     * This test checks if the XML created by the Parser is not empty and
     * contains the changeset- and element-ids.
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

        final StringBuilder builder = new StringBuilder();

        OsmChangeParser.parseElements(elems, 13, new PrintWriter(
                new OutputStream() {

                    @Override
                    public void write(int oneByte) throws IOException {
                        builder.append((char) oneByte);
                    }
                }));

        assertTrue(builder.length() > 0);
        assertTrue(builder.toString().contains("13"));

        assertTrue(builder.toString().contains("-1"));
        assertTrue(builder.toString().contains("-2"));
        assertTrue(builder.toString().contains("-3"));
        assertTrue(builder.toString().contains("-4"));
    }

}
