package io.github.data4all.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.github.data4all.util.SpeechRecognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void splitStringsEmptyListTest() {
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        SpeechRecognition.splitStrings(list1);
        assertTrue(list1.equals(list2));
    }

    @Test
    public void speechToTagTest() {
        List<String> list1 = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        list1.add("adsf");
        list1.add("Motorway");
        list1.add("hotel");
        map.put("highway", "motorway");
        map.put("building", "hotel");
        assertEquals(map, SpeechRecognition.speechToTag(list1));
    }

    @Test
    public void speechToTagSimpleTest() {
        List<String> list1 = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        list1.add("motorway");
        map.put("highway", "motorway");
        assertEquals(map, SpeechRecognition.speechToTag(list1));
    }

    @Test
    public void speechToTagSimpleUpperCaseTest() {
        List<String> list1 = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        list1.add("Motorway");
        map.put("highway", "motorway");
        assertEquals(map, SpeechRecognition.speechToTag(list1));
    }

    @Test
    public void speechToTagTwoKeysTest() {
        List<String> list1 = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        list1.add("motorway");
        list1.add("road");
        list1.add("footway");
        map.put("highway", "motorway");
        assertEquals(map, SpeechRecognition.speechToTag(list1));
    }

}
