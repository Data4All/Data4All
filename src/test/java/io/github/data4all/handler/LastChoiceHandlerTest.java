package io.github.data4all.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;


import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.Tagging;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * This class tests all methods of the LastChoiceHandler.
 * 
 * @author Steeve
 * 
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LastChoiceHandlerTest {
     
    private Map<Tag,String> lastChoice;
    private String[] listKey = new String[]{"absperrung","Einrichtung"};
    
    
    
    
    @Before
    public void setUp(){
        LastChoiceHandler.getInstance();
    }
    
    /**
     * test the method addLastChoiceForType 
     * assuming there are any lastChoice
     */
    @Test
    public void test_addLastChoiceForType(){
       LastChoiceHandler.addLastChoiceForType(1, listKey);
       assertEquals(listKey[listKey.length-1],"Einrichtung"); 
    }

}
