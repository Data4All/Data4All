package io.github.data4all.service;




import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OrientationListenerTest {

	private OrientationListener myService;

	// examine if the Service was started
	private boolean myServiceStarted = false;

	// examine if the service was created
	private boolean myServiceCreated = false;
	
	private boolean myServiceStopped = true;

	private boolean myServiceBound = false;

	private boolean myServiceAttached = false;

	private Intent myServiceIntent = null;
    
	private int myServiceId;
	/*
	 * return the actual service under Test
	 */

	public OrientationListener getService() {
		return myService;
	}

	
	@Before
	public void setUp() throws Exception {

		Intent startIntent = new Intent(Robolectric.application,
				OrientationListener.class);
		OrientationListener orientationListener = new OrientationListener();
		orientationListener.onStartCommand(startIntent, 0, 0);
	}

	/*
	 * create the service under test
	 */
	@Test
	public void setUpService() {
		myService = null;
		try {
			myService = new OrientationListener();
		} catch (Exception exception) {
			assertNotNull(myService);
		}
		 myServiceId = new Random().nextInt();
	        myServiceAttached = true;
	}

	/**
	 * start the service under test
	 * this will be automatically stopped by the method tearDown
	 */
	@Test
	public void startServiceTest() {
		Intent startIntent = new Intent(Robolectric.application,
				OrientationListener.class);
		assertFalse(myServiceStarted);
		assertFalse(myServiceBound);

		if (!myServiceAttached) {
			setUpService();
		}
		assertNotNull(myService);
		myService.onStartCommand(startIntent, 0, 0);
		myServiceCreated = true;
		assertTrue(myServiceCreated);
	}
    
	/**
	 *  this method will be called to stop the service under test
	 *  this will stop the service,when this service was created or when he was started
	 */
	@Test
	public void ShutDownServiceTest() {
		if (myServiceCreated) {
			myService.onDestroy();
		}
		else if (myServiceStarted) {
			myService.stopSelf();
			myServiceStarted = false;
		} else if (myServiceBound) {
			myService.onUnbind(myServiceIntent);
			myServiceBound = false;
		}
		
	}
	
	/**
	 * Shut down the Service under Test and make sure that all resources are cleaned up
	 * @throws Exception
	 */
	@Test
	public void tearDown() throws Exception{
		ShutDownServiceTest();
		myService = null;
		assertTrue(myServiceStopped);
	}
    
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIfServiceWasLaunched() throws Exception {
		setUpService();
		assertNotNull("service should be launched successfully", myService);
	}

}
