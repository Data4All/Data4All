package io.github.data4all;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenuItem;

import android.view.MenuItem;

@RunWith(RobolectricTestRunner.class)
public class MainMenuTest {

    MainActivity activity;

    @Before
    public void setup() {
        this.activity = Robolectric.buildActivity(MainActivity.class).create()
                .get();
    }

    @Test
    public void shouldNotNull() throws Exception {
        assertNotNull(activity);
    }
    
    @Test
    public void testMenuClickOnCamera() throws Exception {
        MenuItem camera = new TestMenuItem(R.id.action_camera);
        
        activity.onOptionsItemSelected(camera);        
        assertEquals(("Camera"), ShadowToast.getTextOfLatestToast());
    }
    
    @Test
    public void testMenuClickOnHelp() throws Exception {
        MenuItem help = new TestMenuItem(R.id.action_help);
        
        activity.onOptionsItemSelected(help);        
        assertEquals(("Help"), ShadowToast.getTextOfLatestToast());
    }
    
    @Test
    public void testMenuClickOnMap() throws Exception {
        MenuItem map = new TestMenuItem(R.id.action_map);
        
        activity.onOptionsItemSelected(map);        
        assertEquals(("Map"), ShadowToast.getTextOfLatestToast());
    }
    
    @Test
    public void testMenuClickOnSettings() throws Exception {
        MenuItem settings = new TestMenuItem(R.id.action_settings);
        
        activity.onOptionsItemSelected(settings);        
        assertEquals(("Settings"), ShadowToast.getTextOfLatestToast());
    }
}