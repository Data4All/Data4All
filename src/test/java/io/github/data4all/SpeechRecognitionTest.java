package io.github.data4all;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SpeechRecognitionTest {

	private SpeechRecognition speech;
	private List<String> list;
	@Before
	public void setUp() throws Exception {
		speech = new SpeechRecognition(); 
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
		speech.splitStrings(list);
		assertTrue(list.equals(list1));
	}
	
	@Test
	public void splitStringsWrongOrderTest() {
		List<String> list1 = new ArrayList<String>();
		list1.add("Hello my Name");
		list1.add("Hello");
		list1.add("Name");
		list1.add("my");
		speech.splitStrings(list);
		assertFalse(list.equals(list1));
	}
	
	@Test
	public void splitStringsWrongStringsTest() {
		List<String> list1 = new ArrayList<String>();
		list1.add("Hello my Name");
		list1.add("Hell");
		list1.add("my");
		list1.add("Name");
		speech.splitStrings(list);
		assertFalse(list.equals(list1));
	}
	
	@Test
	public void splitStringsEmptyListTest() {
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		speech.splitStrings(list1);
		assertTrue(list1.equals(list2));
	}
	
	@Test
	public void speechToTagTest() {
		List<String> list1 = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		list1.add("Motorway");
		map.put("highway", "motorway");
		assertEquals(speech.speechToTag(list1), map);

	}
	
	
	

}
