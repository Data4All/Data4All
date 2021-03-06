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
package io.github.data4all.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SpeechRecognitionTest {
    private List<String> list;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList<String>();
        list.add("Hello my Name");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void splitStringsTest() {
        List<String> list1 = new ArrayList<String>();
        list1.add("Hello my Name");
        list1.add("Hello");
        list1.add("my");
        list1.add("Name");
        SpeechRecognition.splitStrings(list);
        assertTrue(list.equals(list1));
    }

    @Test
    public void splitStringsWrongOrderTest() {
        List<String> list1 = new ArrayList<String>();
        list1.add("Hello my Name");
        list1.add("Hello");
        list1.add("Name");
        list1.add("my");
        SpeechRecognition.splitStrings(list);
        assertFalse(list.equals(list1));
    }

    @Test
    public void splitStringsWrongStringsTest() {
        List<String> list1 = new ArrayList<String>();
        list1.add("Hello my Name");
        list1.add("Hell");
        list1.add("my");
        list1.add("Name");
        SpeechRecognition.splitStrings(list);
        assertFalse(list.equals(list1));
    }
    /**
     * @Test public void splitStringsEmptyListTest() { List<String> list1 = new
     *       ArrayList<String>(); List<String> list2 = new ArrayList<String>();
     *       SpeechRecognition.splitStrings(list1);
     *       assertTrue(list1.equals(list2)); }
     * @Test public void speechToTagTest() { List<String> list1 = new
     *       ArrayList<String>(); Map<String, String> map = new HashMap<String,
     *       String>(); list1.add("adsf"); list1.add("Motorway");
     *       list1.add("hotel"); map.put("highway", "motorway");
     *       assertEquals(map, SpeechRecognition.speechToTag(list1)); }
     * @Test public void speechToTagSimpleTest() { List<String> list1 = new
     *       ArrayList<String>(); Map<String, String> map = new HashMap<String,
     *       String>(); list1.add("motorway"); map.put("highway", "motorway");
     *       assertEquals(map, SpeechRecognition.speechToTag(list1)); }
     * @Test public void speechToTagSimpleUpperCaseTest() { List<String> list1 =
     *       new ArrayList<String>(); Map<String, String> map = new
     *       HashMap<String, String>(); list1.add("Motorway");
     *       map.put("highway", "motorway"); assertEquals(map,
     *       SpeechRecognition.speechToTag(list1)); }
     * @Test public void speechToTagTwoKeysTest() { List<String> list1 = new
     *       ArrayList<String>(); Map<String, String> map = new HashMap<String,
     *       String>(); list1.add("motorway"); list1.add("road");
     *       list1.add("footway"); map.put("highway", "motorway");
     *       assertEquals(map, SpeechRecognition.speechToTag(list1)); }
     */
}
