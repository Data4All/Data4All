package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test cases for the class ClassifiedTag
 * 
 * @author Steeve
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ClassifiedTagTest {

    private ClassifiedTag classifiedTag;
    private ArrayList<String> classifiedValues;

    @Before
    public void setUp() {
        classifiedValues = new ArrayList<String>(Arrays.asList("motorway",
                "residential", "service", "track", "footway", "road"));
        classifiedTag = new ClassifiedTag("highway", null, classifiedValues,
                Tag.getWayTag());
    }

    /**
     * test the method getClassifiedValues() from the class ClassifiedTag, when
     * there was not suggestion added
     */
    @Test
    public void test_getClassifiedValues_WithoutSuggestion() {

        assertTrue(classifiedTag.getClassifiedValuesSuggestion().isEmpty());
        assertEquals(classifiedTag.getClassifiedValues(), classifiedValues);
    }

    /**
     * test the method getClassifiedValues() from the class ClassifiedTag, when
     * a suggestion was added
     */
    @Test
    public void test_getClassifiedValues_WithSuggestion() {

        classifiedTag.addSuggestion("path");
        assertFalse(classifiedTag.getClassifiedValuesSuggestion().isEmpty());
        assertEquals(classifiedTag.getClassifiedValues(),
                classifiedTag.getClassifiedValuesSuggestion());
    }
    
    /**
     * create a new ArrayList of classifiedValues
     * and test the method getAllClassifiedValues() from the class ClassifiedTag
     */
    @Test
    public void test_getAllClassifiedValues() {
        ArrayList<String> classifiedValues1 = new ArrayList<String>(Arrays.asList(
                "citywall", "fence", "wall", "bollard", "gate"));
        classifiedTag.setClassifiedValues(classifiedValues1);
        assertEquals(classifiedValues1, classifiedTag.getAllClassifiedValues());
    }

}
