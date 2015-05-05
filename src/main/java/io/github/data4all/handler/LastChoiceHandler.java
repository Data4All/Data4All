package io.github.data4all.handler;

import io.github.data4all.R;
import io.github.data4all.model.data.ClassifiedTag;
import io.github.data4all.model.data.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * this class represent the lastChoice from a category. this lastChoice appears,
 * when the user tagged a object which belongs to the same Category as the last.
 * 
 * @author Steeve
 *
 */
public final class LastChoiceHandler {

    private static LastChoiceHandler handler;

    private static final String TAG = "LastChoiceHandler";

    // The map were the last selected Tag are saved with his type
    private Map<Integer, Map<Tag, String>> typWithLastchoice;

    /**
     * constructor for the class lastChoiceHandler
     */
    private LastChoiceHandler() {
        // Singleton
        typWithLastchoice = new LinkedHashMap<Integer, Map<Tag, String>>();
    }

    /**
     * this method takes all information from last tag this information will be
     * suggest for the next tag
     * 
     * @param typ
     *            e.g node,area, track
     * @param lastChoice
     *            {@link Map} containing the last choices.
     */
    public void setLastChoice(int typ, Map<Tag, String> lastChoice) {
        Log.i(TAG, "element.getTAgs" + lastChoice.toString());
        final Map<Tag, String> lastChoiceCopie = sortMap(lastChoice);

        typWithLastchoice.put(typ, lastChoiceCopie);
        Log.i(TAG, "map " + typWithLastchoice.toString());
    }

    /**
     * this method sort lastChoice by tag
     * 
     * @param lastChoice
     *            as map
     * @return lastChoice as map
     */
    public static Map<Tag, String> sortMap(Map<Tag, String> lastChoice) {
        final List<Tag> keyset = new ArrayList<Tag>(lastChoice.keySet());
        Collections.sort(keyset, getagMapComparator());
        final Map<Tag, String> lastChoiceCopie =
                new LinkedHashMap<Tag, String>();
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
            final int category = entry.getKey();
            final Map<Tag, String> tags = entry.getValue();
            if (tags != null && !tags.isEmpty()) {
                db.setLastChoice(category, tags);
            }
        }
        db.close();
    }

    /**
     * read the last tag from database with address and contacts.
     * 
     * @param db
     *            instance of the {@link DataBaseHandler}
     */
    public static void load(DataBaseHandler db) {
        final LastChoiceHandler handler = getInstance();
        for (int i = 1; i <= 4; i++) {
            final Map<Tag, String> tagMap = db.getLastChoice(i);
            if (tagMap != null && !tagMap.isEmpty()) {
                handler.setLastChoice(i, tagMap);
            }
        }

    }

    /**
     * get last choice for a given type
     * 
     * @param typ
     *            e.g node, track, area
     * @return a map for the given type
     */
    public Map<Tag, String> getLastChoice(Integer typ) {
        return getInstance().typWithLastchoice.get(typ);
    }

    /**
     * get a Instance for lastChoiceHandler
     * 
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
     * 
     * @param typ
     *            e.g node, track, area
     * @return true when a type has a lastChoice otherwise false
     */
    public static boolean hasLastChoice(Integer typ) {
        return getInstance().typWithLastchoice.containsKey(typ);
    }

    /**
     * add a last choice for a specific type
     * 
     * @param type
     *            e.g node,track, area
     * @param array
     *            e.g list of classifiedtTag
     * @return either a list of classifiedTag with lastChoice or a list of
     *         classifiedTag without lastChoice
     */
    public static String[] addLastChoiceForType(int type, String[] array,
            Resources res) {

        if (LastChoiceHandler.hasLastChoice(type)) {
            final String[] arrayCopy = new String[array.length + 1];

            for (int i = 0; i < array.length; i++) {
                arrayCopy[i] = array[i];
            }
            String lastChoice = res.getString(R.string.name_lastchoice);
            arrayCopy[array.length] = lastChoice;
            return arrayCopy;
        }
        return array;
    }

    /**
     * this method compares the id of two tags
     * 
     */
    private static Comparator<Tag> getagMapComparator() {
        return new Comparator<Tag>() {

            @Override
            public int compare(Tag lhs, Tag rhs) {
                // compare lhs and rhs. when lhs is a classifiedTag and rhs is
                // not a classifiedTag,
                // then is lhs the the smallest
                if (lhs instanceof ClassifiedTag
                        && !(rhs instanceof ClassifiedTag)) {
                    return -1;
                }
                // compare lhs and rhs. when rhs is a classifiedTag and lhs is
                // not a classifiedTag,
                // then is rhs the the smallest
                if (rhs instanceof ClassifiedTag
                        && !(lhs instanceof ClassifiedTag)) {
                    return 1;
                }
                // compare id of lhs and rhs
                return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());
            }

        };
    }

    /**
     * this method update a tag and a value for a given type
     * 
     * @param typ
     *            e.g node, track, area
     * @param tag
     *            e.g addrr, street
     * @param value
     *            e.g usa
     */
    public void updateTag(Integer typ, Tag tag, String value) {
        if (typWithLastchoice.get(typ) != null) {
            typWithLastchoice.get(typ).put(tag, value);
        } else {

            final Map<Tag, String> actualLastChoice =
                    new LinkedHashMap<Tag, String>();
            actualLastChoice.put(tag, value);
            typWithLastchoice.put(typ, actualLastChoice);
        }
    }

    /**
     * this method update a map for a given type
     * 
     * @param typ
     *            e.g node, track, area
     * @param map
     */
    public void updateTag(int typ, Map<Tag, String> map) {
        final Map<Tag, String> actualLastChoice = typWithLastchoice.get(typ);
        if (actualLastChoice == null || actualLastChoice.isEmpty()) {
            typWithLastchoice.put(typ, map);
        } else {
            for (Map.Entry<Tag, String> mapEntry : map.entrySet()) {
                actualLastChoice.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

    }

}
