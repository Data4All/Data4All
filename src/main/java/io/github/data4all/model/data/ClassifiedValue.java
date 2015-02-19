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

/**
 * This class represents a classified value. A classified tag can contain one or
 * more classified values.
 * 
 * @author fkirchge
 *
 */
public class ClassifiedValue {
    
    /**
     * The log-tag for this class.
     */
    private static final String TAG = ClassifiedValue.class.getSimpleName();

    /**
     * id to identify the tag.
     */
    private int id;
    
    /**
     * key for the internal representation in osm e.g. addr:street.
     */
    private String key;

    /**
     * value for the internal representation in osm e.g. residential.
     */
    private String value;

    /**
     * nameRessource defines the displayed name/value in the tagging activity.
     */
    private int nameRessource;

    /**
     * Constructor to create nameRessource and hintRessource from the key.
     * 
     * @param id The id of the ClassifiedValue 
     * @param key The key of the ClassifiedValue
     * @param value The value of the ClassifiedValue
     */
    public ClassifiedValue(int id, String key, String value) {
        this.id = id;
        this.setKey(key); 
        this.value = value;
        try {
            this.setNameRessource((Integer) R.string.class.getDeclaredField(
                    "name_" + key + "_" + value).get(null));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException", e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException", e);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getNameRessource() {
        return nameRessource;
    }

    public void setNameRessource(int nameRessource) {
        this.nameRessource = nameRessource;
    }

}
