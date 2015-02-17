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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.model.data.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

/**
 * This class handles all database requests for the OSM objects that have to be
 * saved, such as create, read, update and delete.
 * 
 * @author Kristin Dahnken
 * 
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Data4AllDB";

    // Table Names
    private static final String TABLE_NODE = "nodes";
    private static final String TABLE_DATAELEMENT = "dataelements";
    private static final String TABLE_TAGMAP = "tagmap";
    private static final String TABLE_POLYELEMENT = "polyelements";
    private static final String TABLE_USER = "users";
    private static final String TABLE_WAY = "ways";
    private static final String TABLE_GPSTRACK = "gpstracks";
    private static final String TABLE_TRACKPOINT = "trackpoints";

    // General Column Names
    private static final String KEY_OSMID = "osmid";

    // DataElement Column Names
    private static final String KEY_TAGIDS = "tagids";

    // Node Column Names
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";

    // TagMap Column Names
    private static final String KEY_TAGID = "tagid";
    private static final String KEY_VALUE = "value";

    // PolyElement Column Names
    private static final String KEY_TYPE = "type";
    private static final String KEY_NODEIDS = "nodeids";

    // User Column Names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKENSECRET = "tokensecret";

    // GPS Track Column Names
    private static final String KEY_TRACKNAME = "trackname";
    private static final String KEY_TRACKPOINTS = "trackpointids";

    // GPS Trackpoint Column Names
    private static final String KEY_ALT = "altitude";
    private static final String KEY_TIME = "timestamp";

    /**
     * Default constructor for the database handler.
     * 
     * @param context
     *            the application
     */
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Table creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATAELEMENTS_TABLE = "CREATE TABLE "
                + TABLE_DATAELEMENT + " (" + KEY_OSMID
                + " INTEGER PRIMARY KEY," + KEY_TAGIDS + " TEXT" + ")";
        final String CREATE_NODES_TABLE = "CREATE TABLE " + TABLE_NODE + " ("
                + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_LAT + " REAL,"
                + KEY_LON + " REAL" + ")";
        final String CREATE_TAGMAP_TABLE = "CREATE TABLE " + TABLE_TAGMAP
                + " (" + KEY_TAGID + " INTEGER PRIMARY KEY," + KEY_VALUE
                + " TEXT" + ")";
        final String CREATE_POLYELEMENT_TABLE = "CREATE TABLE "
                + TABLE_POLYELEMENT + " (" + KEY_OSMID
                + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," + KEY_NODEIDS
                + " TEXT" + ")";
        final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_USERNAME + " TEXT PRIMARY KEY," + KEY_TOKEN + " TEXT,"
                + KEY_TOKENSECRET + " TEXT" + ")";
        final String CREATE_GPSTRACK_TABLE = "CREATE TABLE " + TABLE_GPSTRACK
                + " ("
                + KEY_TRACKNAME + " TEXT PRIMARY KEY," + KEY_TRACKPOINTS
                + " TEXT" + ")";
        final String CREATE_TRACKPOINT_TABLE = "CREATE TABLE "
                + TABLE_TRACKPOINT + " ("
                + KEY_LAT + " REAL," + KEY_LON + " REAL," + KEY_ALT + " REAL,"
                + KEY_TIME + " REAL" + ")";

        db.execSQL(CREATE_DATAELEMENTS_TABLE);
        db.execSQL(CREATE_NODES_TABLE);
        db.execSQL(CREATE_TAGMAP_TABLE);
        db.execSQL(CREATE_POLYELEMENT_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_GPSTRACK_TABLE);
        db.execSQL(CREATE_TRACKPOINT_TABLE);
    }

    // Database handling on upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables that already exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGMAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATAELEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLYELEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSTRACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKPOINT);

        // Recreate tables
        this.onCreate(db);
    }

    // USER CRUD

    /**
     * This method creates and stores a new user in the database. The data is
     * taken from the {@link User} object that is passed to the method.
     * 
     * @param user
     *            the {@link User} object from which the data will be taken
     */
    public void createUser(User user) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_TOKEN, user.getOAuthToken());
        values.put(KEY_TOKENSECRET, user.getOauthTokenSecret());

        db.insert(TABLE_USER, null, values);
    }

    /**
     * This method returns the data for a specific user stored in the database
     * and creates the corresponding {@link User} object.
     * 
     * @param username
     *            the name of the desired user
     * @return a {@link User} object for the desired user
     */
    public User getUser(String username) {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.query(TABLE_USER, new String[] {KEY_USERNAME,
                KEY_TOKEN, KEY_TOKENSECRET, }, KEY_USERNAME + "=?",
                new String[] {username }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        final User user = new User(cursor.getString(0), cursor.getString(1),
                cursor.getString(2));

        cursor.close();

        return user;
    }

    /**
     * This method deletes a specific user from the database.
     * 
     * @param user
     *            the {@link User} object whose data should be deleted
     */
    public void deleteUser(User user) {
        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_USER, KEY_USERNAME + "=?",
                new String[] {user.getUsername() });
    }

    /**
     * This method returns the number of users currently stored in the database.
     * 
     * @return the number of users
     */
    public int getUserCount() {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific user stored in the database.
     * 
     * @param user
     *            the {@link User} object for which the data should be updated
     * @return the number of rows that have been updated
     */
    public int updateUser(User user) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_TOKEN, user.getOAuthToken());
        values.put(KEY_TOKENSECRET, user.getOauthTokenSecret());

        return db.update(TABLE_USER, values, KEY_USERNAME + "=?",
                new String[] {user.getUsername() });
    }

    /**
     * This method returns a list of all users stored in the database and
     * creates corresponding {@link User} objects.
     * 
     * @return a list of users
     */
    public List<User> getAllUser() {
        final List<User> users = new ArrayList<User>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);

        if (cursor.moveToFirst()) {
            do {
                final User user = new User(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                users.add(user);
            } while (cursor.moveToNext());
        }

        return users;
    }

    // -------------------------------------------------------------------------
    // NODE CRUD

    /**
     * This method creates and stores a new node in the database. The data is
     * taken from the {@link Node} object that is passed to the method.
     * 
     * @param node
     *            the {@link Node} object from which the data will be taken
     */
    public void createNode(Node node) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(KEY_OSMID, node.getOsmId());
        values.put(KEY_LAT, node.getLat());
        values.put(KEY_LON, node.getLon());

        db.insert(TABLE_NODE, null, values);
    }

    /**
     * This method returns the data for a specific node stored in the database
     * and creates the corresponding {@link Node} object.
     * 
     * @param id
     *            the id of the desired node
     * @return a {@link Node} object for the desired node
     */
    public Node getNode(long id) {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.query(TABLE_NODE, new String[] {KEY_OSMID,
                KEY_LAT, KEY_LON, }, KEY_OSMID + "=?",
                new String[] {String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        final Node node = new Node(Long.parseLong(cursor.getString(0)),
                Double.parseDouble(cursor.getString(1)),
                Double.parseDouble(cursor.getString(2)));

        cursor.close();

        return node;
    }

    /**
     * This method deletes a specific node from the database.
     * 
     * @param node
     *            the {@link Node} object whose data should be deleted
     */
    public void deleteNode(Node node) {
        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NODE, KEY_OSMID + "=?",
                new String[] {String.valueOf(node.getOsmId()) });
    }

    /**
     * This method returns the number of nodes currently stored in the database.
     * 
     * @return the number of nodes
     */
    public int getNodeCount() {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific node stored in the database.
     * 
     * @param node
     *            the {@link Node} object for which the data should be updated
     * @return the number of rows that have been updated
     */
    public int updateNode(Node node) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_OSMID, node.getOsmId());
        values.put(KEY_LAT, node.getLat());
        values.put(KEY_LON, node.getLon());
        return db.update(TABLE_NODE, values, KEY_OSMID + "=?",
                new String[] {String.valueOf(node.getOsmId()) });
    }

    /**
     * This method returns a list of all nodes stored in the database and
     * creates corresponding {@link Node} objects.
     * 
     * @return a list of nodes
     */
    public List<Node> getAllNode() {
        final List<Node> nodes = new ArrayList<Node>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);

        if (cursor.moveToFirst()) {
            do {
                final Node node = new Node(Long.parseLong(cursor.getString(0)),
                        Double.parseDouble(cursor.getString(1)),
                        Double.parseDouble(cursor.getString(2)));
                nodes.add(node);
            } while (cursor.moveToNext());
        }

        return nodes;
    }

    // -------------------------------------------------------------------------
    // POLY ELEMENT CRUD

    /**
     * This method creates and stores a new poly element in the database. The
     * data is taken from the {@link PolyElement} object that is passed to the
     * method.
     * 
     * @param polyElement
     *            the {@link PolyElement} object from which the data will be
     *            taken
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public void createPolyElement(PolyElement polyElement)
            throws JSONException {
        final List<Long> nodeIDs = new ArrayList<Long>();

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_OSMID, polyElement.getOsmId());
        values.put(KEY_TYPE, polyElement.getType().toString());

        for (Node node : polyElement.getNodes()) {
            nodeIDs.add(node.getOsmId());
            this.createNode(node);
        }

        final JSONObject json = new JSONObject();
        json.put("nodeIDarray", new JSONArray(nodeIDs));
        final String arrayList = json.toString();

        values.put(KEY_NODEIDS, arrayList);

        db.insert(TABLE_POLYELEMENT, null, values);
    }

    /**
     * This method returns the data for a specific poly element stored in the
     * database and creates the corresponding {@link PolyElement} object.
     * 
     * @param id
     *            the id of the desired poly element
     * @return a {@link PolyElement} object for the desired poly element
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public PolyElement getPolyElement(long id) throws JSONException {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.query(TABLE_POLYELEMENT, new String[] {
                KEY_OSMID, KEY_TYPE, KEY_NODEIDS, }, KEY_OSMID + "=?",
                new String[] {String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        final PolyElement polyElement = new PolyElement(Long.parseLong(cursor
                .getString(0)), PolyElementType.valueOf(cursor.getString(1)));

        final JSONObject json = new JSONObject(cursor.getString(2));
        final JSONArray jArray = json.optJSONArray("nodeIDarray");

        final List<Node> nodes = new ArrayList<Node>();

        for (int i = 0; i < jArray.length(); i++) {
            final long nodeID = jArray.optLong(i);
            final Node node = this.getNode(nodeID);
            nodes.add(node);
        }

        polyElement.addNodes(nodes, false);

        cursor.close();

        return polyElement;
    }

    /**
     * This method deletes a specific poly element from the database.
     * 
     * @param polyElement
     *            the {@link PolyElement} object whose data should be deleted
     */
    public void deletePolyElement(PolyElement polyElement) {

        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_POLYELEMENT, KEY_OSMID + "=?",
                new String[] {String.valueOf(polyElement.getOsmId()) });

        for (Node node : polyElement.getNodes()) {
            this.deleteNode(node);
        }
    }

    /**
     * This method returns the number of poly elements currently stored in the
     * database.
     * 
     * @return the number of poly elements
     */
    public int getPolyElementCount() {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POLYELEMENT,
                null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific poly element stored in the
     * database.
     * 
     * @param polyElement
     *            the {@link PolyElement} object for which the data should be
     *            updated
     * @return the number of rows that have been updated
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public int updatePolyElement(PolyElement polyElement) throws JSONException {

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_OSMID, polyElement.getOsmId());
        values.put(KEY_TYPE, polyElement.getType().toString());

        final List<Long> nodeIDs = new ArrayList<Long>();

        for (Node node : polyElement.getNodes()) {
            nodeIDs.add(node.getOsmId());
            if (this.checkIfRecordExists(TABLE_NODE, KEY_OSMID,
                    node.getOsmId())) {
                this.updateNode(node);
            } else {
                this.createNode(node);
            }
        }

        final JSONObject json = new JSONObject();
        json.put("nodeIDarray", new JSONArray(nodeIDs));
        final String arrayList = json.toString();

        values.put(KEY_NODEIDS, arrayList);

        return db.update(TABLE_POLYELEMENT, values, KEY_OSMID + "=?",
                new String[] {String.valueOf(polyElement.getOsmId()) });
    }

    /**
     * This method returns a list of all poly elements stored in the database
     * and creates corresponding {@link PolyElement} objects.
     * 
     * @return a list of poly elements
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public List<PolyElement> getAllPolyElements() throws JSONException {

        final List<PolyElement> polyElements = new ArrayList<PolyElement>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POLYELEMENT,
                null);

        if (cursor.moveToFirst()) {
            do {
                final PolyElement polyElement = new PolyElement(
                        Long.parseLong(cursor.getString(0)),
                        PolyElementType.valueOf(cursor.getString(1)));

                final JSONObject json = new JSONObject(cursor.getString(2));
                final JSONArray jArray = json.optJSONArray("nodeIDarray");

                final List<Node> nodes = new ArrayList<Node>();

                for (int i = 0; i < jArray.length(); i++) {
                    final long nodeID = jArray.optLong(i);
                    final Node node = this.getNode(nodeID);
                    nodes.add(node);
                }
                polyElement.addNodes(nodes, false);

                polyElements.add(polyElement);
            } while (cursor.moveToNext());
        }

        return polyElements;
    }

    // -------------------------------------------------------------------------
    // DATA ELEMENT CRUD

    /**
     * This method creates and stores a new data element in the database. The
     * data is taken from the {@link AbstractDataElement} object that is passed
     * to the method.
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object from which the data
     *            will be taken
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public void createDataElement(AbstractDataElement dataElement) // NOSONAR
            throws JSONException {

        final SQLiteDatabase db = getWritableDatabase();

        final Map<Tag, String> tagMap = dataElement.getTags();
        final List<Integer> tagIDs = new ArrayList<Integer>();
        final ContentValues values = new ContentValues();

        values.put(KEY_OSMID, dataElement.getOsmId());

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            tagIDs.add(tag.getKey().getId());
        }

        this.createTagMap(tagMap);

        final JSONObject json = new JSONObject();
        json.put("tagIDarray", new JSONArray(tagIDs));
        final String arrayList = json.toString();

        values.put(KEY_TAGIDS, arrayList);

        db.insert(TABLE_DATAELEMENT, null, values);

        if (dataElement instanceof PolyElement) {
            this.createPolyElement((PolyElement) dataElement);
        } else {
            this.createNode((Node) dataElement);
        }
    }

    /**
     * This method returns the data for a specific data element stored in the
     * database and creates the corresponding {@link AbstractDataElement}
     * object.
     * 
     * @param id
     *            the id of the desired data element
     * @return a {@link AbstractDataElement} object for the desired data element
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public AbstractDataElement getDataElement(long id) throws JSONException { // NOSONAR

        final SQLiteDatabase db = getReadableDatabase();

        AbstractDataElement dataElement; // NOSONAR

        final Cursor cursor = db.query(TABLE_DATAELEMENT, new String[] {
                KEY_OSMID, KEY_TAGIDS, }, KEY_OSMID + "=?",
                new String[] {String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (this.checkIfRecordExists(TABLE_POLYELEMENT, KEY_OSMID, id)) {
            dataElement = this.getPolyElement(id);
        } else {
            dataElement = this.getNode(id);
        }

        final JSONObject json = new JSONObject(cursor.getString(1));
        final JSONArray jArray = json.optJSONArray("tagIDarray");

        final List<Integer> tagIDs = new ArrayList<Integer>();

        for (int i = 0; i < jArray.length(); i++) {
            final int tagID = jArray.optInt(i);
            tagIDs.add(tagID);
        }

        final Map<Tag, String> tagMap = this.getTagMap(tagIDs);
        dataElement.addTags(tagMap);

        cursor.close();

        return dataElement;
    }

    /**
     * This method deletes a specific data element from the database.
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object whose data should be
     *            deleted
     */
    public void deleteDataElement(AbstractDataElement dataElement) { // NOSONAR

        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_DATAELEMENT, KEY_OSMID + "=?",
                new String[] {String.valueOf(dataElement.getOsmId()) });

        final List<Integer> tagIDs = new ArrayList<Integer>();

        for (Map.Entry<Tag, String> tag : dataElement.getTags().entrySet()) {
            tagIDs.add(tag.getKey().getId());
        }
        this.deleteTagMap(tagIDs);

        if (dataElement instanceof PolyElement) {
            this.deletePolyElement((PolyElement) dataElement);
        } else {
            this.deleteNode((Node) dataElement);
        }
    }

    /**
     * This method returns the number of data elements currently stored in the
     * database.
     * 
     * @return the number of data elements
     */
    public int getDataElementCount() {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATAELEMENT,
                null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific data element stored in the
     * database.
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object for which the data
     *            should be updated
     * @return the number of rows that have been updated
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public int updateDataElement(AbstractDataElement dataElement) // NOSONAR
            throws JSONException {

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_OSMID, dataElement.getOsmId());

        int count = 0;

        count += db.update(TABLE_DATAELEMENT, values, KEY_OSMID + "=?",
                new String[] {String.valueOf(dataElement.getOsmId()) });

        count += this.updateTagMap(dataElement.getTags());

        if (dataElement instanceof PolyElement) {
            count += this.updatePolyElement((PolyElement) dataElement);
        } else {
            count += this.updateNode((Node) dataElement);
        }

        return count;
    }

    /**
     * This method returns a list of all data elements stored in the database
     * and creates corresponding {@link AbstractDataElement} objects.
     * 
     * @return a list of data elements
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public List<AbstractDataElement> getAllDataElements() throws JSONException {

        final List<AbstractDataElement> dataElements =
                new ArrayList<AbstractDataElement>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATAELEMENT,
                null);

        AbstractDataElement dataElement; // NOSONAR

        if (cursor.moveToFirst()) {
            do {

                if (this.checkIfRecordExists(TABLE_POLYELEMENT, KEY_OSMID,
                        cursor.getInt(0))) {
                    dataElement = this.getPolyElement(cursor.getInt(0));
                } else {
                    dataElement = this.getNode(cursor.getInt(0));
                }

                final JSONObject json = new JSONObject(cursor.getString(1));
                final JSONArray jArray = json.optJSONArray("tagIDarray");

                final List<Integer> tagIDs = new ArrayList<Integer>();

                for (int i = 0; i < jArray.length(); i++) {
                    final int tagID = jArray.optInt(i);
                    tagIDs.add(tagID);
                }

                final Map<Tag, String> tagMap = this.getTagMap(tagIDs);
                dataElement.addTags(tagMap);

                dataElements.add(dataElement);
            } while (cursor.moveToNext());
        }

        return dataElements;
    }

    // -------------------------------------------------------------------------
    // TAG MAP CRUD

    /**
     * This method creates and stores a new tag map in the database. The data is
     * taken from the {@link Map} object that is passed to the method.
     * 
     * @param tagMap
     *            the {@link Map} object from which the data will be taken
     */
    public void createTagMap(Map<Tag, String> tagMap) {

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            values.put(KEY_TAGID, tag.getKey().getId());
            values.put(KEY_VALUE, tag.getValue());
            db.insert(TABLE_TAGMAP, null, values);
        }
    }

    /**
     * This method returns the data for specific tags stored in the database and
     * creates the corresponding {@link Map} object.
     * 
     * @param tagIDs
     *            the IDs of the desired tags
     * @return a {@link Map} object for the desired tags
     */
    public Map<Tag, String> getTagMap(List<Integer> tagIDs) {

        final SQLiteDatabase db = getReadableDatabase();

        final Map<Tag, String> tagMap = new HashMap<Tag, String>();

        for (int id : tagIDs) {
            final Cursor cursor = db
                    .query(TABLE_TAGMAP, new String[] {KEY_TAGID, KEY_VALUE },
                            KEY_TAGID + "=?",
                            new String[] {String.valueOf(id) }, null, null,
                            null, null);

            if (cursor != null) {
                cursor.moveToFirst();
            }
            tagMap.put(Tags.getTagWithId(id), cursor.getString(1));
            cursor.close();
        }
        return tagMap;
    }

    /**
     * This method deletes specific tags from the database.
     * 
     * @param tagIDs
     *            the tags whose data should be deleted
     */
    public void deleteTagMap(List<Integer> tagIDs) {
        final SQLiteDatabase db = getWritableDatabase();

        for (int id : tagIDs) {
            db.delete(TABLE_TAGMAP, KEY_TAGID + "=?",
                    new String[] {String.valueOf(id) });
        }
    }

    /**
     * This method returns the number of tags currently stored in the database.
     * 
     * @return the number of tags
     */
    public int getTagMapCount() {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db
                .rawQuery("SELECT * FROM " + TABLE_TAGMAP, null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific tag map stored in the
     * database.
     * 
     * @param tagMap
     *            the {@link map} object for which the data should be updated
     * @return the number of rows that have been updated
     */
    public int updateTagMap(Map<Tag, String> tagMap) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        int count = 0;

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            values.put(KEY_TAGID, tag.getKey().getId());
            values.put(KEY_VALUE, tag.getValue());

            if (this.checkIfRecordExists(TABLE_TAGMAP, KEY_TAGID, tag.getKey()
                    .getId())) {
                count += db.update(TABLE_TAGMAP, values, KEY_TAGID + "=?",
                        new String[] {String.valueOf(tag.getKey().getId()) });
            } else {
                db.insert(TABLE_TAGMAP, null, values);
            }

        }
        return count;
    }

    // -------------------------------------------------------------------------
    // GPS-TRACK CRUD

    /**
     * This method creates and stores a new GPS track in the database. The data
     * is taken from the {@link Track} object that is passed to the method.
     * 
     * @param track
     *            the {@link Track} object from which the data will be taken
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public void createGPSTrack(Track track) throws JSONException {

        final SQLiteDatabase db = getWritableDatabase();

        final List<Long> timestamps = new ArrayList<Long>();
        final ContentValues values = new ContentValues();

        values.put(KEY_TRACKNAME, track.getTrackName());

        for (TrackPoint trackpoint : track.getTrackPoints()) {
            timestamps.add(trackpoint.getTime());
        }

        // this.createTagMap(tagMap);

        final JSONObject json = new JSONObject();
        json.put("timestamparray", new JSONArray(timestamps));
        final String arrayList = json.toString();

        values.put(KEY_TRACKPOINTS, arrayList);

        db.insert(TABLE_GPSTRACK, null, values);
    }

    /**
     * This method returns the data for a specific GPS track stored in the
     * database and creates the corresponding {@link Track} object.
     * 
     * @param name
     *            the name of the desired GPS track
     * @return a {@link Track} object for the desired GPS track
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public Track getGPSTrack(String name) throws JSONException { // NOSONAR

        final SQLiteDatabase db = getReadableDatabase();

        Track track = new Track();

        final Cursor cursor = db.query(TABLE_GPSTRACK, new String[] {
                KEY_TRACKNAME, KEY_TRACKPOINTS, }, KEY_TRACKNAME + "=?",
                new String[] {name }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        track.setTrackName(cursor.getString(0));

        final JSONObject json = new JSONObject(cursor.getString(1));
        final JSONArray jArray = json.optJSONArray("timestamparray");

        final List<Long> timestamps = new ArrayList<Long>();

        for (int i = 0; i < jArray.length(); i++) {
            final long timestamp = jArray.optInt(i);
            timestamps.add(timestamp);
        }

        // final Map<Tag, String> tagMap = this.getTagMap(tagIDs); -> get track
        // points!
        // track.addTrackPoint(location);

        cursor.close();

        return track;
    }

    /**
     * This method deletes a specific GPS track from the database.
     * 
     * @param track
     *            the {@link Track} object whose data should be deleted
     */
    public void deleteGPSTrack(Track track) { // NOSONAR

        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_GPSTRACK, KEY_TRACKNAME + "=?",
                new String[] {track.getTrackName() });

        final List<Long> timestamps = new ArrayList<Long>();

        for (TrackPoint trackpoint : track.getTrackPoints()) {
            timestamps.add(trackpoint.getTime());
        }
        // this.deleteTagMap(tagIDs); -> delete track points!
    }

    /**
     * This method returns the number of GPS tracks currently stored in the
     * database.
     * 
     * @return the number of GPS tracks
     */
    public int getGPSTrackCount() {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GPSTRACK,
                null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for a specific GPS track stored in the
     * database.
     * 
     * @param track
     *            the {@link Track} object for which the data should be updated
     * @return the number of rows that have been updated
     * @throws JSONException
     *             if the JSON object can't be initialized
     */
    public int updateGPSTrack(Track track) // NOSONAR
            throws JSONException {

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(KEY_TRACKNAME, track.getTrackName());

        int count = 0;

        count += db.update(TABLE_GPSTRACK, values, KEY_TRACKNAME + "=?",
                new String[] {track.getTrackName() });

        // count += this.updateTagMap(dataElement.getTags()); -> update track
        // points!

        return count;
    }

    /**
     * This method returns a list of all GPS tracks stored in the database and
     * creates corresponding {@link Track} objects.
     * 
     * @return a list of GPS tracks
     * @throws JSONException
     *             if JSON object can't be initialized
     */
    public List<Track> getAllGPSTracks() throws JSONException {

        final List<Track> gpsTracks = new ArrayList<Track>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GPSTRACK,
                null);

        Track track = new Track(); // NOSONAR

        if (cursor.moveToFirst()) {
            do {

                final JSONObject json = new JSONObject(cursor.getString(1));
                final JSONArray jArray = json.optJSONArray("timestamparray");

                final List<Long> timestamps = new ArrayList<Long>();

                for (int i = 0; i < jArray.length(); i++) {
                    final long timestamp = jArray.optInt(i);
                    timestamps.add(timestamp);
                }

                // final Map<Tag, String> tagMap = this.getTagMap(tagIDs);
                // dataElement.addTags(tagMap); -> get track points!

                gpsTracks.add(track);
            } while (cursor.moveToNext());
        }
        return gpsTracks;
    }

    // -------------------------------------------------------------------------
    // TRACKPOINT CRUD

    /**
     * This method creates and stores new trackpoints in the database. The data
     * is taken from the {@link TrackPoint} objects that are passed to the
     * method.
     * 
     * @param trackPoints
     *            the {@link List} from which the trackpoints will be taken
     */
    public void createTrackPoints(List<TrackPoint> trackPoints) {

        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        for (TrackPoint point : trackPoints) {
            values.put(KEY_LAT, point.getLat());
            values.put(KEY_LON, point.getLon());
            values.put(KEY_ALT, point.getAlt());
            values.put(KEY_TIME, point.getTime());
            db.insert(TABLE_TRACKPOINT, null, values);
        }
    }

    /**
     * This method returns the data for specific trackpointss stored in the
     * database and creates a list of corresponding {@link TrackPoint} objects.
     * 
     * @param timestamps
     *            the timestamps of the desired trackpoints
     * @return a {@link List} of the desired trackpoints
     */
    public List<TrackPoint> getTrackPoints(List<Long> timestamps) {

        final SQLiteDatabase db = getReadableDatabase();

        final List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();

        for (long time : timestamps) {
            final Cursor cursor = db
                    .query(TABLE_TRACKPOINT, new String[] {KEY_LAT, KEY_LON,
                            KEY_ALT, KEY_TIME },
                            KEY_TIME + "=?",
                            new String[] {String.valueOf(time) }, null, null,
                            null, null);

            if (cursor != null) {
                cursor.moveToFirst();
            }

            Location loc = new Location("provider");
            loc.setLatitude(cursor.getDouble(0));
            loc.setLongitude(cursor.getDouble(1));
            loc.setAltitude(cursor.getDouble(2));
            loc.setTime(cursor.getLong(3));

            TrackPoint point = new TrackPoint(loc);

            trackPoints.add(point);
            cursor.close();
        }
        return trackPoints;
    }

    /**
     * This method deletes specific trackpoints from the database.
     * 
     * @param timestamps
     *            the timestamps of the trackpoints that should be deleted
     */
    public void deleteTrackPoints(List<Long> timestamps) {
        final SQLiteDatabase db = getWritableDatabase();

        for (long time : timestamps) {
            db.delete(TABLE_TRACKPOINT, KEY_TIME + "=?",
                    new String[] {String.valueOf(time) });
        }
    }

    /**
     * This method returns the number of trackpoints currently stored in the
     * database.
     * 
     * @return the number of trackpoints
     */
    public int getTrackPointCount() {
        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db
                .rawQuery("SELECT * FROM " + TABLE_TRACKPOINT, null);
        final int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * This method updates the data for specific trackpoints stored in the
     * database.
     * 
     * @param trackPoints
     *            a list of {@link TrackPoint}s that should be updated
     * @return the number of rows that have been updated
     */
    public int updateTrackPoints(List<TrackPoint> trackPoints) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();

        int count = 0;

        for (TrackPoint point : trackPoints) {
            values.put(KEY_LAT, point.getLat());
            values.put(KEY_LON, point.getLon());
            values.put(KEY_ALT, point.getAlt());
            values.put(KEY_TIME, point.getTime());

            if (this.checkIfRecordExists(TABLE_TRACKPOINT, KEY_TIME,
                    point.getTime())) {
                count += db.update(TABLE_TRACKPOINT, values, KEY_TIME + "=?",
                        new String[] {String.valueOf(point.getTime()) });
            } else {
                db.insert(TABLE_TRACKPOINT, null, values);
            }
        }
        return count;
    }

    /**
     * This method returns a list of all trackpoints stored in the database and
     * creates corresponding {@link TrackPoint} objects.
     * 
     * @return a list of trackpoints
     */
    public List<TrackPoint> getAllTrackPoints() {
        final List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRACKPOINT, null);

        if (cursor.moveToFirst()) {
            do {
                
                Location loc = new Location("provider");
                loc.setLatitude(cursor.getDouble(0));
                loc.setLongitude(cursor.getDouble(1));
                loc.setAltitude(cursor.getDouble(2));
                loc.setTime(cursor.getLong(3));
                
                final TrackPoint point = new TrackPoint(loc);
                trackPoints.add(point);
            } while (cursor.moveToNext());
        }
        return trackPoints;
    }

    // ---auxiliary functions---------------------------------------------------

    /**
     * This method checks if a given record exists in a table.
     * 
     * @param tableName
     *            the name of the table
     * @param field
     *            the column that will be searched
     * @param value
     *            the given record
     * @return true if the given record exists, false otherwise
     */
    public boolean checkIfRecordExists(String tableName, String field,
            long value) {

        final SQLiteDatabase db = getReadableDatabase();
        final String query = "SELECT * FROM " + tableName + " WHERE " + field
                + " = " + value;
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }
}
