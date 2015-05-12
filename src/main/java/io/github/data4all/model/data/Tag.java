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

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

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
    private int nameResource;

    /**
     * hintRessource defines the displayed hint/value in the tagging activity.
     */
    private int hintResource;

    /**
     * type defines if the tagging activity should display a keyboard or a
     * numpad as input method.
     */
    private int type;

    /**
     * store the last Tag value e.g the last value for addr:street.
     *  @author:Steeve
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
            this.nameResource =
                    (Integer) R.string.class.getDeclaredField(
                            "name_" + key.replaceAll(":", "_")).get(null);
            if (type != -1) {
                this.hintResource =
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
        return hintResource;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getNameRessource() {
        return nameResource;
    }

    public int getType() {
        return type;
    }

    public void setHintRessource(int hintRessource) {
        this.hintResource = hintRessource;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNameRessource(int nameRessource) {
        this.nameResource = nameRessource;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * toString method just for debug purpose.
     */
    @Override
    public String toString() {
        return "key: " + key + " nameRessource: " + nameResource
                + " hintRessource: " + hintResource + " type: " + type;
    }

    /**
     * Get the localized name of the value.
     * 
     * @author tbrose
     * @param context
     *            the context of the application
     * @param value
     *            the tag value
     * @return the localized name
     */
    public String getNamedValue(Context context, String value) {
        final Resources res = context.getResources();

        final String name = "name_" + key + "_" + value;
        Log.d(LOG_TAG, name);
        final int identifier =
                res.getIdentifier(name, "string", context.getPackageName());

        return getLocalisedString(context, identifier, getTagLanguage(context));
    }

    /**
     * Get the localized name of this tag key.
     * 
     * @author tbrose
     * @param context
     *            the context of the application
     * @return the localized name
     */
    public String getNamedKey(Context context) {
        return getLocalisedString(context, getNameRessource(), getTagLanguage(context));
    }

    public static Locale getTagLanguage(Context context) {
        final String prefKey =
                context.getString(R.string.pref_tag_language_key);
        final String prefDefaut =
                context.getString(R.string.pref_tag_language_default);
        final String locale =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
                prefKey, prefDefaut);
        
        Log.i(LOG_TAG, "Tag language: " + locale);
        
        if(locale.equals("sys")) {
            return null;
        } else {
            return new Locale(locale);
        }
    }

    public static String getLocalisedString(Context context, int id,
            Locale locale) {
        String result = null;

        if (id != 0) {
            final Resources res = context.getResources();
            Configuration conf = null;
            Locale currentLocale = null;

            if (locale != null) {
                conf = res.getConfiguration();
                currentLocale = conf.locale;
                conf.locale = locale;
                res.updateConfiguration(res.getConfiguration(),
                        res.getDisplayMetrics());
            }

            result = res.getString(id);

            if (locale != null) {
                conf.locale = currentLocale;
                res.updateConfiguration(res.getConfiguration(),
                        res.getDisplayMetrics());
            }
        }
        Log.d(LOG_TAG, "Result: " + result + " id: " + id);
        return result;
    }

    /**
     *  @author Steeve
     * @return the last value
     */
    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }
}
