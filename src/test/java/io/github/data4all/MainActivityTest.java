package io.github.data4all;

import static org.junit.Assert.assertNotNull;
import io.github.data4all.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

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
}