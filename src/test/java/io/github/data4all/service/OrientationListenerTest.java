package io.github.data4all.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaScannerConnection;



/**
* test cases for the OrientationListener class 
* @author Steeve
*
*/

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OrientationListenerTest {

	// the service to test
	private OrientationListener service;

   
    
    public static final String SERVICE = "io.github.data4all.service.OrientationListener";
    
    
	@Before
	public void setUp() throws Exception {
        service = null;
        try{
        	service = new OrientationListener();
       
        }catch(Exception  e){
        	assertNotNull(service);
        }	
	}
	

	/**
	 * Shut down the Service under Test and make sure that all resources are
	 * cleaned up
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		service = null;
	}
	
	/**
	 * start the service under test this will be automatically stopped by the
	 * method tearDown
	 */
	@Test
	public void should_startService() {
		Intent startIntent = new Intent(Robolectric.application,
				OrientationListener.class);
		service.onStartCommand(startIntent, 0, 0);
		assertEquals(SERVICE, startIntent
                .getComponent().getClassName());
	}
	
	
	@Test
	public void should_bindService_Successfully() {
		Intent intent = new Intent(Robolectric.application,
				OrientationListener.class);
		ServiceConnection serviceConnection = Robolectric
				.newInstanceOf(MediaScannerConnection.class);
		service.bindService(intent,serviceConnection,0);
	}
	
	@Test
	public void should_stopService() {
		
		Intent intent = new Intent(Robolectric.application,
				OrientationListener.class);
		service.stopSelf();
		service.stopService(intent);
	}
	
	
	@Test
	public void should_UnbindService_Successfully() {

		ServiceConnection serviceConnection = Robolectric
				.newInstanceOf(MediaScannerConnection.class);
		service.unbindService(serviceConnection);
	}
	
}
