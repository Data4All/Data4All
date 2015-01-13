package io.github.data4all.util;

import io.github.data4all.model.data.Tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Maurice Boyke
 *
 */
public abstract class Tagging {
    /**
     * 
     * @return the Keys of Tags
     */
    public static List<String> getKeys() {
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        map = Tags.getClassifiedTags();
        Iterator<String> keySetIterator = map.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            list.add(key);
        }
        return list;
    }

    /**
     * 
     * @param key
     *            the Key of the Hashmap
     * @return the Values of the Key
     */
    public static List<String> getValues(String key) {
        Map<String, String> map = new HashMap<String, String>();
        map = Tags.getClassifiedTags();
        String[] split;
        split = map.get(key).split(",");
        List<String> list = new ArrayList<String>(Arrays.asList(split));
        return list;
    }

    /**
     * 
     * @param key
     * @param value
     * @return a HashMap with the Keys and Values
     */
    public static Map<String, String> hashMapTag(String key, String value) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }

    public static Map<String, String> addressToTag(List<String> addressTags,
            Map<String, String> map) {
        String[] [] tag;
        tag = Tags.getAddressTags();
        for (int i = 0; i < tag.length; i++) {
            if (!addressTags.get(i).equals("")) {
                map.put(tag[i] [0], addressTags.get(i));
            }
        }
        return map;

    }

    public static Map<String, String> contactToTag(List<String> contactTags,
            Map<String, String> map) {
        String[] [] tag;
        tag = Tags.getContactTags();
        for (int i = 0; i < tag.length; i++) {
            if (!contactTags.get(i).equals("")) {
                map.put(tag[i] [0], contactTags.get(i));
            }
        }
        return map;

    }

}
