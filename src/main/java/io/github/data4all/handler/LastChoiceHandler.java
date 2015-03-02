package io.github.data4all.handler;

import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * this class represent the lastchoice from a category
 * this lastchoice appear,when the user taggt a object which belong
 * to the same Category as the last.
 * @author Steeve
 *
 */
public class LastChoiceHandler {

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
        Map<Tag, String> lastChoiceCopie = sortiereMap(lastChoice);

        typWithLastchoice.put(typ, lastChoiceCopie);
    }

    /**
    * this method sort lastchoice by tag
    *
    */
    public static Map<Tag, String> sortiereMap(Map<Tag, String> lastChoice) {
        List<Tag> keyset = new ArrayList<Tag>(lastChoice.keySet());
        Collections.sort(keyset, getagMapComparator());
        Map<Tag, String> lastChoiceCopie = new LinkedHashMap<Tag, String>();
        for (Tag tag : keyset) {
            
            lastChoiceCopie.put(tag, lastChoice.get(tag));
            tag.setLastValue(lastChoice.get(tag));
        }
        return lastChoiceCopie;
    }

    /**
     * store the last choice(tag) in database
     * 
     * @param context
     */
    public void save(Context context) {
        final DataBaseHandler db = new DataBaseHandler(context);
        for (Map.Entry<Integer, Map<Tag, String>> entry : typWithLastchoice
                .entrySet()) {
            Integer kategorie = entry.getKey();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                List<Integer> ids = new LinkedList<Integer>();
                for (Map.Entry<Tag, String> entry1 : entry.getValue()
                        .entrySet()) {
                    // only one element in map
                    ids.add(entry1.getKey().getId());
                }
                db.insertOrUpdateLastChoice(kategorie, ids);
            }
        }
        db.close();
    }

    /**
     * read the last tag from database with address and contacts
     * @param db
     */
    public static void load(DataBaseHandler db) {
        final LastChoiceHandler handler = getInstance();
        for (int i = 1; i <= 4; i++) {

            final List<Integer> lastChoiceKey = db.getLastChoiceId(i);
            if (lastChoiceKey == null) {
                continue;
            }

            final Map<Tag, String> tagMap = db.getTagMap(lastChoiceKey);
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
           final String[] arrayCopy = new String[array.length + 1];

            for (int i = 0; i < array.length; i++) {
                arrayCopy[i] = array[i];
            }
            arrayCopy[array.length] = "Last Choice";
            return arrayCopy;
        }
        return array;
    }

    /**
    *
    * sort by tag
    */
    private static Comparator<Tag> getagMapComparator() {
        Comparator<Tag> comparator = new Comparator<Tag>() {

            @Override
            public int compare(Tag lhs, Tag rhs) {
                if (lhs instanceof ClassifiedTag
                        && !(rhs instanceof ClassifiedTag)) {
                    return -1;
                }
                if (rhs instanceof ClassifiedTag
                        && !(lhs instanceof ClassifiedTag)) {
                    return 1;
                }
                return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());
            }

        };
        return comparator;
    }

}
