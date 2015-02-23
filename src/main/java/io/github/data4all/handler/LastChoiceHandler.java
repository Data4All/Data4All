package io.github.data4all.handler;

import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.util.Tagging;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;


/**
 * this class represent the lastchoice from a category
 * this lastchoice appear,when the user taggt a object which belong to the same Category as the last. 
 * @author Steeve
 *
 */
public class LastChoiceHandler {

    private static final int LAST_CHOICE_ID_PREFIX = 5000;
    
    // The Key of the Classified Tag
    private ClassifiedTag key;
    
    //the classifiedValue
    private String realValue;
    
    //the map were the last address is saved
    private Map<Tag, String> adressTags;
    
    //the map were the last contact is saved
    private Map<Tag, String> contactTags;
    
    
    private static LastChoiceHandler handler;
    
    // The map were the last selected Tag  are saved with his type 
    private Map<Integer, Map<Tag, String>> typWithLastchoice;

    /**
     * constructor for the class lastChoiceHandler
     */
    private LastChoiceHandler() {
        // Singleton
        typWithLastchoice = new LinkedHashMap<Integer, Map<Tag, String>>();
    }

    
    /**
     * this method take all informations from last tag
     * this informations will be suggest for the next tag
     * @param typ
     * @param lastChoice
     */
    public void setLastChoice(int typ, Map<Tag, String> lastChoice) {
        this.adressTags = new LinkedHashMap<Tag, String>();
        this.contactTags = new LinkedHashMap<Tag, String>();
        for (Map.Entry<Tag, String> entry : lastChoice.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            if (entry.getKey() instanceof ClassifiedTag) {
                this.key = (ClassifiedTag) entry.getKey();
                this.realValue = entry.getValue();

            } else {
                if (entry.getKey().getId() < 6) {
                    adressTags.put(entry.getKey(), entry.getValue());
                } else {
                    contactTags.put(entry.getKey(), entry.getValue());
                }
            }
        }
        typWithLastchoice.put(typ, lastChoice);
    }

    
    /**
     * store the last choice(tag) in database
     * @param context
     */
    public void save(Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        for (Map.Entry<Integer, Map<Tag, String>> entry : typWithLastchoice
                .entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                Map<Tag, String> lastChoice = entry.getValue();
              //  db.updateTagMap(lastChoice);
                
                Map<Tag, String> lastChoiceCopy = new LinkedHashMap<Tag, String>();
                for (Map.Entry<Tag, String> entry1 : lastChoice.entrySet()) {
                    Tag key = entry1.getKey();
                    Integer id=key.getId()+LAST_CHOICE_ID_PREFIX;
                    if(key instanceof ClassifiedTag){
                       lastChoiceCopy.put(new ClassifiedTag(id, key.getKey(), key.getType(), ((ClassifiedTag)key).getClassifiedValues(), key.getOsmObjects()), entry1.getValue());
                    }else{
                        lastChoiceCopy.put( new Tag(id,
                            key.getKey(), key.getType(), key.getOsmObjects()),entry1.getValue());
                    }
                    
                    db.deleteTagMap(Arrays.asList(id));
                }
                db.updateTagMap(lastChoiceCopy);
            }
        }
        db.close();
    }

    /**
     * read the last tag from database with address and contacts
     * @param db
     */
    public static void load(DataBaseHandler db) {
        LastChoiceHandler handler = getInstance();
        for (int i = 1; i <= 4; i++) {
            
            List<Integer> lastChoiceKey = new LinkedList<Integer>();
          
           
            List<Tag> tags = Tagging.getKeys(i);
            for (Tag tag : tags) {
                lastChoiceKey.add(tag.getId());
            }
            
            for (Tag tag : Tags.getAllAddressTags()) { 
                lastChoiceKey.add(tag.getId());
            }
            
            for (Tag tag : Tags.getAllContactTags()) { 
                lastChoiceKey.add(tag.getId());
            }
          
            
            Map<Tag, String> tagMap = db.getTagMap(lastChoiceKey);
            if (tagMap != null && !tagMap.isEmpty()) {
                handler.setLastChoice(i, tagMap);
            }
        }

    }


    /**
     * get last choice
     * @param typ
     * @return
     */
    public Map<Tag, String> getLastChoice(Integer typ) {
        return getInstance().typWithLastchoice.get(typ);
    }

    /**
     * get a Instance for lastChoiceHandler
     * @return lastChoiceHandler
     */
    public static LastChoiceHandler getInstance() {
        if (handler == null) {
            handler = new LastChoiceHandler();
        }
        return handler;
    }

    /**
     * check if a type has a last choice
     * @param typ
     * @return
     */
    public static boolean hasLastChoice(Integer typ) {
        return getInstance().typWithLastchoice.containsKey(typ);
    }

    /**
     * add a last choice for a specific type
     * @param type
     * @param array
     * @return
     */
    public static String[] addLastChoiceForType(int type, String[] array) {
        if (LastChoiceHandler.hasLastChoice(type)) {
            String[] arrayCopy = new String[array.length + 1];

            for (int i = 0; i < array.length; i++) {
                arrayCopy[i] = array[i];
            }
            arrayCopy[array.length] = "Last Choice";
            return arrayCopy;
        }
        return array;
    }

}
