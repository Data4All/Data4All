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
package io.github.data4all.model.data;

import io.github.data4all.R;
import io.github.data4all.logger.Log;

import java.util.Arrays;

/**
 * This class represents a predefined osm tag. The name and hint for a specific
 * tag is defined in the tag_name.xml and tag_hint.xml.
 * 
 * @author fkirchge, tbrose
 *
 */
public class Tag {

    /**
     * The log-tag for this class.
     */
    private static final String LOG_TAG = Tag.class.getSimpleName();

    /**
     * id to identify the tag.
     */
    private int id;

    /**
     * key for the internal representation in osm e.g. addr:street.
     */
    private String key;

    /**
     * nameRessource defines the displayed name/value in the tagging activity.
     */
    private int nameRessource;

    /**
     * hintRessource defines the displayed hint/value in the tagging activity.
     */
    private int hintRessource;

    /**
     * type defines if the tagging activity should display a keyboard or a
     * numpad as input method.
     */
    private int type;

    /**
     * store the last Tag value e.g the last value for addr:street.
     */
    private String lastValue;

    /**
     * Constructor to create nameRessource and hintRessource from the key.
     * 
     * @param id
     *            The ID of the Tag.
     * @param key
     *            The Key of the Tag.
     * @param type
     *            The InputType method.
     */
    public Tag(int id, String key, int type) {
        this.id = id;
        this.key = key;
        this.type = type;
        try {
            this.nameRessource =
                    (Integer) R.string.class.getDeclaredField(
                            "name_" + key.replaceAll(":", "_")).get(null);
            if (type != -1) {
                this.hintRessource =
                        (Integer) R.string.class.getDeclaredField(
                                "hint_" + key.replaceAll(":", "_")).get(null);
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "IllegalArgumentException", e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "IllegalAccessException", e);
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, "NoSuchFieldException", e);
        }
    }

    public int getHintRessource() {
        return hintRessource;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getNameRessource() {
        return nameRessource;
    }

    public int getType() {
        return type;
    }

    public void setHintRessource(int hintRessource) {
        this.hintRessource = hintRessource;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNameRessource(int nameRessource) {
        this.nameRessource = nameRessource;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * toString method just for debug purpose.
     */
    @Override
    public String toString() {
        return "key: " + key + " nameRessource: " + nameRessource
                + " hintRessource: " + hintRessource +" type: " + type;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

}
