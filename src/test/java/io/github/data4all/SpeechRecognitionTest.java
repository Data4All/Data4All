package io.github.data4all;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
		assertEquals(list, list1);
	}

}
