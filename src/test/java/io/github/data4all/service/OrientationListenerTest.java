
package io.github.data4all.service;
import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import android.content.Intent;

/**
 * 
 * @author Steeve
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OrientationListenerTest {

	private OrientationListener myService;

	// examine if the Service was started
	private boolean myServiceStarted = false;

	// check if the service was created
	private boolean myServiceCreated = false;
	
	//check if the service was stopped
	private boolean myServiceStopped = false;

	private boolean myServiceBound = false;

	private Intent myServiceIntent = null;

	/*
	 * return the actual service under Test
	 */

	public OrientationListener getService() {
		return myService;
	}

	
	@Before
	public void setUp() throws Exception {

		myService = null;
		try {
			myService = new OrientationListener();
		} catch (Exception exception) {
			assertNotNull(myService);
		}
	}
	
	/**
	 * start the service under test
	 * this will be automatically stopped by the method tearDown
	 */
	@Test
	public void startServiceTest() {
		Intent startIntent = new Intent(Robolectric.application,
				OrientationListener.class);
		myService.onStartCommand(startIntent, 0, 0);
		myServiceCreated = true;
		myServiceStarted= true;
		myServiceBound = true;
	}
    
	
	/**
	 * check if the Service was launched
	 * @throws Exception
	 */
	@Test
	public void testIfServiceWasLaunched() throws Exception {
		assertNotNull("service should be launched successfully", myService);
	}
	
	
	/**
	 *  this method will be called to stop the service under test
	 *  this will stop the service,when this service was created or when he was started
	 */
	@Test
	public void ShutDownServiceTest() {
		if (myServiceCreated) {
			myService.onDestroy();
			myServiceCreated = false;
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
	@After
	public void tearDown() throws Exception{
		myService = null;
		myServiceStopped = true;
	}
    
}
