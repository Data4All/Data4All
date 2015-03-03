package io.github.data4all.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * This class tests all methods of the LastChoiceHandler.
 * 
 * @author Steeve
 * 
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LastChoiceHandlerTest {

    private LastChoiceHandler lcHandler;
    private DataBaseHandler dbhandler;
    
    @Before
    public void setUp(){
        lcHandler = LastChoiceHandler.getInstance();
        dbhandler = new DataBaseHandler(Robolectric.application);
    }
    
    
    
    /**
     * add a lastChoice in Database and check if database contain this lastChoice
     */
    @Test
    public void test_LastChoice(){
        
        Map<Tag, String> lastChoice = new Hashtable<Tag, String>();
         
        Tag tag = Tags.getTagWithId(5);
        lastChoice.put(tag, "building");
           
        Tag street = Tags.getTagWithId(1);
        lastChoice.put(street, "findorf");
        
        Tag housenummer = Tags.getTagWithId(2);
        lastChoice.put(housenummer, "40");
        
        Tag city = Tags.getTagWithId(3);
        lastChoice.put(city, "bremen");
        
        Tag country = Tags.getTagWithId(6);
        lastChoice.put(country, "Germany");
       
        lcHandler.setLastChoice(4, lastChoice);    
        assertTrue(LastChoiceHandler.hasLastChoice(4)); 
        lcHandler.save(Robolectric.application);        
        
        ArrayList<Integer> tagIDs = new ArrayList<Integer>();
        
        tagIDs.add(street.getId());
        tagIDs.add(housenummer.getId());
        tagIDs.add(city.getId());
        tagIDs.add(tag.getId());
        tagIDs.add(country.getId());
        
        assertEquals(tagIDs, dbhandler.getLastChoiceId(4)); 
        
        dbhandler.createTagMap(lastChoice);
        assertTrue(dbhandler.getTagMap(tagIDs).containsKey(tag));
        assertTrue(dbhandler.getTagMap(tagIDs).containsValue("bremen"));
        assertTrue(dbhandler.getTagMap(tagIDs).containsKey(country));
        assertTrue(dbhandler.getTagMap(tagIDs).containsKey(street));
    }
    
    /**
     * add a lastChoice for a specific type and check if a another type contains this lastChoice
     */
    @Test
    public void test_LastChoiceForSpecificType(){
        Map<Tag, String> lastChoice = new Hashtable<Tag, String>();
        
        Tag tag1 = Tags.getTagWithId(10);
        lastChoice.put(tag1, "highway");
           
        Tag street1 = Tags.getTagWithId(9);
        lastChoice.put(street1, "street");
        
        lcHandler.setLastChoice(2, lastChoice);
        assertTrue(LastChoiceHandler.hasLastChoice(2));
        lcHandler.save(Robolectric.application);
        
        ArrayList<Integer> tagIDs = new ArrayList<Integer>();
        
        tagIDs.add(tag1.getId());
        tagIDs.add(street1.getId());
        
        assertEquals(tagIDs, dbhandler.getLastChoiceId(2)); 
        
        dbhandler.createTagMap(lastChoice);
        //type 2 = "way" has a lastChoice
        assertTrue(dbhandler.getTagMap(tagIDs).containsKey(tag1));
        assertTrue(dbhandler.getTagMap(tagIDs).containsKey(street1));     
       
        //type 3 = "area" don't have a lastChoice
        List<Integer> tagIDs1 = dbhandler.getLastChoiceId(3); 
        assertFalse(LastChoiceHandler.hasLastChoice(3));
        assertEquals(null,tagIDs1);
    }
    
    /**
     * check if a type(node,track,area, and building) has a lastChoice
     */
    @Test
    public void check_If_SpecificType_Has_LastChoice(){
        LastChoiceHandler.load(dbhandler);
        assertFalse(LastChoiceHandler.hasLastChoice(1));
        assertTrue(LastChoiceHandler.hasLastChoice(2));
        assertFalse(LastChoiceHandler.hasLastChoice(3));
        assertTrue(LastChoiceHandler.hasLastChoice(4));
    }
}
