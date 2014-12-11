package io.github.data4all.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaggingTest {
    private List<String> list;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList<String>();
        list.add("highway");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}
