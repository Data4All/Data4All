package io.github.data4all.util;

import java.util.ArrayList;

import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.network.OscUploadHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.util.Log;

/**
 * Test for getting changeSetID, parsing Osc and uploading Osc
 */

/**
 * @author Richard
 *
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
	public void parseTest() {
		Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
		ArrayList<AbstractDataElement> elems = new ArrayList<AbstractDataElement>();
		Node node1 = new Node(-1, 23, 23);
		Node node2 = new Node(-2, 24, 24);
		Node node3 = new Node(-3, 25, 25);
		PolyElement way1 = new PolyElement(-4, PolyElementType.WAY);
		way1.addNode(node1);
		way1.addNode(node2);
		Log.i("test", "before oauth");
		elems.add(node1);
		elems.add(node3);
		elems.add(way1);
		Context act = Robolectric.buildActivity(MapViewActivity.class).get()
				.getApplicationContext();
		Log.i("test", "start the Request");
		new OscUploadHelper(act, elems, "upload test");

	}

}
