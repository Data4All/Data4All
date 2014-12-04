package io.github.data4all.service;

import org.junit.Before;

import android.app.Service;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class GPSserviceTest extends ServiceTestCase<Service>{
    
    
    public GPSserviceTest(Class<Service> serviceClass) {
        super(serviceClass);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }
    
    /**
     * The name 'test preconditions' is a convention to signal that if this
     * test doesn't pass, the test case was not set up properly and it might
     * explain any and all failures in other tests.  This is not guaranteed
     * to run before other tests, as junit uses reflection to find the tests.
     */
    @SmallTest
    public void testPreconditions() {
    }

    /**
     * Test basic startup/shutdown of Service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), Service.class);
        startService(startIntent); 
    }
    @SmallTest
    public void testStopable() {
       shutdownService(); 
    }

}
