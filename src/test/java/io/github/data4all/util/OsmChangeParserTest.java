package io.github.data4all.util;

import java.util.ArrayList;

import io.github.data4all.activity.MapViewActivity;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.OsmElement;
import io.github.data4all.model.data.Way;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * @author Richard
 *
 */

//Use Robolectric
@RunWith(RobolectricTestRunner.class)
// EmulateSdk-18 because Robolectric cannot handle Sdk-19+
@Config(emulateSdk = 18)
public class OsmChangeParserTest {

	@Test
	public void parseTest(){
		String filepath = Robolectric.buildActivity(MapViewActivity.class).get().getApplicationContext().getFilesDir().getAbsolutePath() 
				+ "/OsmChange/upload.osc";
		ArrayList<OsmElement> elems = new ArrayList<OsmElement>();
		Node node1 = new Node(1,1,23,23);
		Node node2 = new Node(2,1,24,24);
		Node node3 = new Node(3,1,25,25);
		Way way1 = new Way(4,1);
		way1.addNode(node1);
		way1.addNode(node2);
		
		elems.add(node1);
		elems.add(node3);
		elems.add(way1);
		OsmChangeParser.parse(filepath,elems,13);
	}
	
	
}
