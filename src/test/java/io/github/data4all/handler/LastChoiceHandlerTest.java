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
 * This class tests methods of the LastChoiceHandler.
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
        
        Tag city = Tags.getTagWithId(4);
        lastChoice.put(city, "bremen");
        
        Tag country = Tags.getTagWithId(6);
        lastChoice.put(country, "Germany");
       
        lcHandler.setLastChoice(3, lastChoice);    
        assertTrue(LastChoiceHandler.hasLastChoice(3)); 
        lcHandler.save(Robolectric.application);        
        
        ArrayList<Integer> tagIDs = new ArrayList<Integer>();
        
        tagIDs.add(street.getId());
        tagIDs.add(housenummer.getId());
        tagIDs.add(city.getId());
        tagIDs.add(tag.getId());
        tagIDs.add(country.getId());
        
        assertEquals(tagIDs, dbhandler.getLastChoiceId(3)); 
        assertEquals(lastChoice,lcHandler.getLastChoice(3));
        
    }
    
    
    /**
     * check if a type(node,track,area, and building) has a lastChoice
     */
    @Test
    public void check_If_SpecificType_Has_LastChoice(){
        LastChoiceHandler.load(dbhandler);
        assertFalse(LastChoiceHandler.hasLastChoice(1));
        assertFalse(LastChoiceHandler.hasLastChoice(2));
        assertTrue(LastChoiceHandler.hasLastChoice(3));
    }
}
