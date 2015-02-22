package io.github.data4all.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
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

    /*private ClassifiedTag classifiedTag;
    private List<String> highwayValues;
    private ClassifiedTag lastChoice;
    Tag highway = new Tag(10, "highway", null, Tag.WAY_TAG );
    
    
    @Before
    public void setUp() {
        highwayValues = new ArrayList<String>(Arrays.asList("motorway",
                "residential", "service", "track", "footway", "road"));
        classifiedTag = new ClassifiedTag(10,"highway",null,highwayValues,Tag.WAY_TAG);
    }

    *//**
     * test the method getClassifiedValues() from the class ClassifiedTag, when
     * there was not suggestion added
     *//*
    @Test
    public void test_getClassifiedValues_WithoutSuggestion() {
        assertEquals(classifiedTag.getClassifiedValues(), highwayValues);
    }

    *//**
     * test the method getClassifiedValues() from the class ClassifiedTag, when
     * a suggestion was added
     *//*
    @Test
    public void test_getClassifiedValues_WithSuggestion() {
        
        lastChoice = Tags.getLastChoice();
        assertNull(lastChoice);
        classifiedTag.addSuggestion("path");
        lastChoice = new ClassifiedTag(10, "Last Choice", null,
                Arrays.asList("path"), Tag.WAY_TAG);
        Tags.TAG_LIST.add(lastChoice);
        lastChoice = Tags.getLastChoice();
        assertNotNull(lastChoice);
        assertEquals(lastChoice.getClassifiedValues(),classifiedTag.getClassifiedValues());    
    }

    
    *//**
     * create a new ArrayList of classifiedValues
     * and test the method getAllClassifiedValues() from the class ClassifiedTag
     *//*
    @Test
    public void test_getAllClassifiedValues() {
        ArrayList<String> classifiedValues1 = new ArrayList<String>(Arrays.asList(
                "citywall", "fence", "wall", "bollard", "gate"));
        classifiedTag.setClassifiedValues(classifiedValues1);
        assertEquals(classifiedValues1, classifiedTag.getAllClassifiedValues());
    }*/

}
