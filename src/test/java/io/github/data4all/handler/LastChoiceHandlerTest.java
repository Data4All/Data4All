/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Hashtable;
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
           
        Tag street = Tags.getTagWithId(401);
        lastChoice.put(street, "findorf");
        
        Tag housenummer = Tags.getTagWithId(402);
        lastChoice.put(housenummer, "40");
        

        Tag postCode = Tags.getTagWithId(403);
        lastChoice.put(postCode, "28277");
        
        Tag city = Tags.getTagWithId(404);
        lastChoice.put(city,"bremen");
        
        Tag country = Tags.getTagWithId(405);
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
        
        dbhandler.createTagMap((long)1.0,lastChoice);
        
        dbhandler.getTagMap((long)1.0);
        
        assertTrue(dbhandler.getTagMap((long)1.0).containsKey(tag));
        assertTrue(dbhandler.getTagMap((long)1.0).containsValue("Germany"));
        assertTrue(dbhandler.getTagMap((long)1.0).containsKey(housenummer));
        assertTrue(dbhandler.getTagMap((long)1.0).containsValue("bremen"));
        assertTrue(dbhandler.getTagMap((long)1.0).containsKey(postCode));
       
        
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
