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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;

/**
 * This class represents a classified value. A classified tag can contain one or
 * more classified values.
 * 
 * @author fkirchge
 *
 */
public class ClassifiedValue implements Localizeable{

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
    private int nameResource;

    /**
     * descriptionRessource defines the displayed description for the classified
     * value.
     */
    private int descriptionResource;

    /**
     * Attributes from the propertie file /res/raw/tag_values.txt.
     */
    private boolean canBeNode;
    private boolean canBeWay;
    private boolean canBeArea;
    private boolean canBeBuilding;
    private boolean hasAddrStreet;
    private boolean hasAddrHousnumber;
    private boolean hasAddrPostcode;
    private boolean hasAddrCity;
    private boolean hasAddrCountry;
    private boolean hasContactPhone;
    private boolean hasContactFax;
    private boolean hasContactWebsite;
    private boolean hasContactEmail;

    /**
     * Constructor to create nameRessource and hintRessource from the key.
     * 
     * @param id
     *            The id of the ClassifiedValue
     * @param key
     *            The key of the ClassifiedValue
     * @param value
     *            The value of the ClassifiedValue
     */
    public ClassifiedValue(int id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
        try {
            this.setNameRessource((Integer) R.string.class.getDeclaredField(
                    "name_" + key + "_" + value).get(null));
            this.setDescriptionResource((Integer) R.string.class
                    .getDeclaredField("description_" + key + "_" + value).get(
                            null));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException", e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException", e);
        }
    }

    public List<Boolean> getAllUnclassifiedBooleans() {
        final List<Boolean> booleanList = new ArrayList<Boolean>();
        booleanList.add(hasAddrStreet);
        booleanList.add(hasAddrHousnumber);
        booleanList.add(hasAddrPostcode);
        booleanList.add(hasAddrCity);
        booleanList.add(hasAddrCountry);
        booleanList.add(hasContactPhone);
        booleanList.add(hasContactFax);
        booleanList.add(hasContactWebsite);
        booleanList.add(hasContactEmail);

        return booleanList;

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
        return nameResource;
    }

    public void setNameRessource(int nameRessource) {
        this.nameResource = nameRessource;
    }

    public String getLocalizedName(Context context) {
        final String prefKey =
                context.getString(R.string.pref_english_tags_key);
        final boolean englishName =
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(prefKey, false);

        Resources resources = context.getResources();
        final int id =
                resources.getIdentifier("name_" + getKey().replace(":", "_")
                        + "_" + value, "string", context.getPackageName());

        return Tag.getLocalisedString(context, id, englishName);
    }

    public boolean canBeNode() {
        return canBeNode;
    }

    public void setCanBeNode(boolean canBeNode) {
        this.canBeNode = canBeNode;
    }

    public boolean canBeWay() {
        return canBeWay;
    }

    public void setCanBeWay(boolean canBeWay) {
        this.canBeWay = canBeWay;
    }

    public boolean canBeArea() {
        return canBeArea;
    }

    public void setCanBeArea(boolean canBeArea) {
        this.canBeArea = canBeArea;
    }

    public boolean canBeBuilding() {
        return canBeBuilding;
    }

    public void setCanBeBuilding(boolean canBeBuilding) {
        this.canBeBuilding = canBeBuilding;
    }

    public boolean hasAddrStreet() {
        return hasAddrStreet;
    }

    public void setHasAddrStreet(boolean hasAddrStreet) {
        this.hasAddrStreet = hasAddrStreet;
    }

    public boolean hasAddrHousnumber() {
        return hasAddrHousnumber;
    }

    public void setHasAddrHousnumber(boolean hasAddrHousnumber) {
        this.hasAddrHousnumber = hasAddrHousnumber;
    }

    public boolean hasAddrPostcode() {
        return hasAddrPostcode;
    }

    public void setHasAddrPostcode(boolean hasAddrPostcode) {
        this.hasAddrPostcode = hasAddrPostcode;
    }

    public boolean hasAddrCity() {
        return hasAddrCity;
    }

    public void setHasAddrCity(boolean hasAddrCity) {
        this.hasAddrCity = hasAddrCity;
    }

    public boolean hasAddrCountry() {
        return hasAddrCountry;
    }

    public void setHasAddrCountry(boolean hasAddrCountry) {
        this.hasAddrCountry = hasAddrCountry;
    }

    public boolean hasContactPhone() {
        return hasContactPhone;
    }

    public void setHasContactPhone(boolean hasContactPhone) {
        this.hasContactPhone = hasContactPhone;
    }

    public boolean hasContactFax() {
        return hasContactFax;
    }

    public void setHasContactFax(boolean hasContactFax) {
        this.hasContactFax = hasContactFax;
    }

    public boolean hasContactWebsite() {
        return hasContactWebsite;
    }

    public void setHasContactWebsite(boolean hasContactWebsite) {
        this.hasContactWebsite = hasContactWebsite;
    }

    public boolean hasContactEmail() {
        return hasContactEmail;
    }

    public void setHasContactEmail(boolean hasContactEmail) {
        this.hasContactEmail = hasContactEmail;
    }

    public int getDescriptionResource() {
        return descriptionResource;
    }

    public void setDescriptionResource(int descriptionResource) {
        this.descriptionResource = descriptionResource;
    }

}
