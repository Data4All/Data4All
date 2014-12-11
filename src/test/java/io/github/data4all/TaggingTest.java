package io.github.data4all;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaggingTest {

    private Tagging tag;
    private List<String> list;

    @Before
    public void setUp() throws Exception {
        tag = new Tagging();
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
