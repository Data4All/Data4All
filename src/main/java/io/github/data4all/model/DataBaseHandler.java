/**
 * 
 */
package io.github.data4all.model;

import java.util.ArrayList;
import java.util.Hashtable;
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
import io.github.data4all.model.data.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class handles all database requests for the OSM objects that have to be
 * saved, such as create, read, update and delete.
 * 
 * @author Kristin Dahnken
 * 
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
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

    /**
     * Default constructor for the database handler.
     * 
     * @param context
     *            the application
     */
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATAELEMENTS_TABLE = "CREATE TABLE " + TABLE_DATAELEMENT
                + " (" + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_TAGIDS
                + " TEXT" + ")";
        String CREATE_NODES_TABLE = "CREATE TABLE " + TABLE_NODE + " ("
                + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_LAT + " REAL,"
                + KEY_LON + " REAL" + ")";
        String CREATE_TAGMAP_TABLE = "CREATE TABLE " + TABLE_TAGMAP + " ("
                + KEY_TAGID + " INTEGER PRIMARY KEY," + KEY_VALUE + " TEXT"
                + ")";
        String CREATE_POLYELEMENT_TABLE = "CREATE TABLE " + TABLE_POLYELEMENT
                + " (" + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_TYPE
                + " TEXT," + KEY_NODEIDS + " TEXT" + ")";
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_USERNAME + " TEXT PRIMARY KEY," + KEY_TOKEN + " TEXT,"
                + KEY_TOKENSECRET + " TEXT" + ")";

        db.execSQL(CREATE_DATAELEMENTS_TABLE);
        db.execSQL(CREATE_NODES_TABLE);
        db.execSQL(CREATE_TAGMAP_TABLE);
        db.execSQL(CREATE_POLYELEMENT_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGMAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATAELEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLYELEMENT);

        // Recreate tables
        onCreate(db);
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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_TOKEN, user.getOAuthToken());
        values.put(KEY_TOKENSECRET, user.getOauthTokenSecret());

        db.insert(TABLE_USER, null, values);

        // db.close();
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
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_USERNAME,
                KEY_TOKEN, KEY_TOKENSECRET }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(cursor.getString(0), cursor.getString(1),
                cursor.getString(2));

        cursor.close();
        // db.close();
        return user;
    }

    /**
     * This method deletes a specific user from the database.
     * 
     * @param user
     *            the {@link User} object whose data should be deleted
     */
    public void deleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_USER, KEY_USERNAME + "=?",
                new String[] { user.getUsername() });

        // db.close();
    }

    /**
     * This method returns the number of users currently stored in the database.
     * 
     * @return the number of users
     */
    public int getUserCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_TOKEN, user.getOAuthToken());
        values.put(KEY_TOKENSECRET, user.getOauthTokenSecret());

        int count = db.update(TABLE_USER, values, KEY_USERNAME + "=?",
                new String[] { user.getUsername() });
        // db.close();
        return count;
    }

    /**
     * This method returns a list of all users stored in the database and
     * creates corresponding {@link User} objects.
     * 
     * @return a list of users
     */
    public List<User> getAllUser() {
        List<User> users = new ArrayList<User>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2));
                users.add(user);
            } while (cursor.moveToNext());
        }

        // db.close();
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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, node.getOsmId());
        values.put(KEY_LAT, node.getLat());
        values.put(KEY_LON, node.getLon());

        db.insert(TABLE_NODE, null, values);

        // db.close();
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
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NODE, new String[] { KEY_OSMID, KEY_LAT,
                KEY_LON }, KEY_OSMID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Node node = new Node(Long.parseLong(cursor.getString(0)),
                Double.parseDouble(cursor.getString(1)),
                Double.parseDouble(cursor.getString(2)));

        cursor.close();
        // db.close();

        return node;
    }

    /**
     * This method deletes a specific node from the database.
     * 
     * @param node
     *            the {@link Node} object whose data should be deleted
     */
    public void deleteNode(Node node) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NODE, KEY_OSMID + "=?",
                new String[] { String.valueOf(node.getOsmId()) });

        // db.close();
    }

    /**
     * This method returns the number of nodes currently stored in the database.
     * 
     * @return the number of nodes
     */
    public int getNodeCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, node.getOsmId());
        values.put(KEY_LAT, node.getLat());
        values.put(KEY_LON, node.getLon());
        int count = db.update(TABLE_NODE, values, KEY_OSMID + "=?",
                new String[] { String.valueOf(node.getOsmId()) });
        // db.close();

        return count;
    }

    /**
     * This method returns a list of all nodes stored in the database and
     * creates corresponding {@link Node} objects.
     * 
     * @return a list of nodes
     */
    public List<Node> getAllNode() {
        List<Node> nodes = new ArrayList<Node>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = new Node(Long.parseLong(cursor.getString(0)),
                        Double.parseDouble(cursor.getString(1)),
                        Double.parseDouble(cursor.getString(2)));
                nodes.add(node);
            } while (cursor.moveToNext());
        }

        // db.close();
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
    public void createPolyElement(PolyElement polyElement) throws JSONException {
        List<Long> nodeIDs = new ArrayList<Long>();

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, polyElement.getOsmId());
        values.put(KEY_TYPE, polyElement.getType().toString());

        for (Node node : polyElement.getNodes()) {
            nodeIDs.add(node.getOsmId());
            createNode(node);
        }

        JSONObject json = new JSONObject();
        json.put("nodeIDarray", new JSONArray(nodeIDs));
        String arrayList = json.toString();

        values.put(KEY_NODEIDS, arrayList);

        db.insert(TABLE_POLYELEMENT, null, values);

        // db.close();
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

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_POLYELEMENT, new String[] { KEY_OSMID,
                KEY_TYPE, KEY_NODEIDS }, KEY_OSMID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        PolyElement polyElement = new PolyElement(Long.parseLong(cursor
                .getString(0)), PolyElementType.valueOf(cursor.getString(1)));

        JSONObject json = new JSONObject(cursor.getString(2));
        JSONArray jArray = json.optJSONArray("nodeIDarray");

        ArrayList<Node> nodes = new ArrayList<Node>();

        for (int i = 0; i < jArray.length(); i++) {
            long nodeID = jArray.optLong(i);
            Node node = getNode(nodeID);
            nodes.add(node);
        }

        polyElement.addNodes(nodes, false);

        cursor.close();
        // db.close();

        return polyElement;
    }

    /**
     * This method deletes a specific poly element from the database.
     * 
     * @param polyElement
     *            the {@link PolyElement} object whose data should be deleted
     */
    public void deletePolyElement(PolyElement polyElement) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_POLYELEMENT, KEY_OSMID + "=?",
                new String[] { String.valueOf(polyElement.getOsmId()) });

        for (Node node : polyElement.getNodes()) {
            deleteNode(node);
        }

        // db.close();
    }

    /**
     * This method returns the number of poly elements currently stored in the
     * database.
     * 
     * @return the number of poly elements
     */
    public int getPolyElementCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POLYELEMENT, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

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

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, polyElement.getOsmId());
        values.put(KEY_TYPE, polyElement.getType().toString());

        List<Long> nodeIDs = new ArrayList<Long>();

        for (Node node : polyElement.getNodes()) {
            nodeIDs.add(node.getOsmId());
            if (checkIfRecordExists(TABLE_NODE, KEY_OSMID, node.getOsmId())) {
                updateNode(node);
            } else {
                createNode(node);
            }
        }

        JSONObject json = new JSONObject();
        json.put("nodeIDarray", new JSONArray(nodeIDs));
        String arrayList = json.toString();

        values.put(KEY_NODEIDS, arrayList);

        int count = db.update(TABLE_POLYELEMENT, values, KEY_OSMID + "=?",
                new String[] { String.valueOf(polyElement.getOsmId()) });
        // db.close();

        return count;
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

        List<PolyElement> polyElements = new ArrayList<PolyElement>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_POLYELEMENT, null);

        if (cursor.moveToFirst()) {
            do {
                PolyElement polyElement = new PolyElement(Long.parseLong(cursor
                        .getString(0)), PolyElementType.valueOf(cursor
                        .getString(1)));

                JSONObject json = new JSONObject(cursor.getString(2));
                JSONArray jArray = json.optJSONArray("nodeIDarray");

                ArrayList<Node> nodes = new ArrayList<Node>();

                for (int i = 0; i < jArray.length(); i++) {
                    long nodeID = jArray.optLong(i);
                    Node node = getNode(nodeID);
                    nodes.add(node);
                }
                polyElement.addNodes(nodes, false);

                polyElements.add(polyElement);
            } while (cursor.moveToNext());
        }

        // db.close();
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
    public void createDataElement(AbstractDataElement dataElement)
            throws JSONException {

        SQLiteDatabase db = getWritableDatabase();

        Map<Tag, String> tagMap = dataElement.getTags();
        List<Integer> tagIDs = new ArrayList<Integer>();
        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, dataElement.getOsmId());

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            tagIDs.add(tag.getKey().getId());
        }

        createTagMap(tagMap);

        JSONObject json = new JSONObject();
        json.put("tagIDarray", new JSONArray(tagIDs));
        String arrayList = json.toString();

        values.put(KEY_TAGIDS, arrayList);

        db.insert(TABLE_DATAELEMENT, null, values);

        if (dataElement instanceof PolyElement) {
            createPolyElement((PolyElement) dataElement);
        } else {
            createNode((Node) dataElement);
        }

        // db.close();
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
    public AbstractDataElement getDataElement(long id) throws JSONException {

        SQLiteDatabase db = getReadableDatabase();

        AbstractDataElement dataElement;

        Cursor cursor = db.query(TABLE_DATAELEMENT, new String[] { KEY_OSMID,
                KEY_TAGIDS }, KEY_OSMID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (checkIfRecordExists(TABLE_POLYELEMENT, KEY_OSMID, id)) {
            dataElement = getPolyElement(id);
        } else {
            dataElement = getNode(id);
        }

        JSONObject json = new JSONObject(cursor.getString(1));
        JSONArray jArray = json.optJSONArray("tagIDarray");

        ArrayList<Integer> tagIDs = new ArrayList<Integer>();

        for (int i = 0; i < jArray.length(); i++) {
            int tagID = jArray.optInt(i);
            tagIDs.add(tagID);
        }

        Map<Tag, String> tagMap = getTagMap(tagIDs);
        dataElement.addTags(tagMap);

        cursor.close();
        // db.close();

        return dataElement;
    }

    /**
     * This method deletes a specific data element from the database.
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object whose data should be
     *            deleted
     */
    public void deleteDataElement(AbstractDataElement dataElement) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_DATAELEMENT, KEY_OSMID + "=?",
                new String[] { String.valueOf(dataElement.getOsmId()) });

        ArrayList<Integer> tagIDs = new ArrayList<Integer>();

        for (Map.Entry<Tag, String> tag : dataElement.getTags().entrySet()) {
            tagIDs.add(tag.getKey().getId());
        }
        deleteTagMap(tagIDs);

        if (dataElement instanceof PolyElement) {
            deletePolyElement((PolyElement) dataElement);
        } else {
            deleteNode((Node) dataElement);
        }

        // db.close();
    }

    /**
     * This method returns the number of data elements currently stored in the
     * database.
     * 
     * @return the number of data elements
     */
    public int getDataElementCount() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATAELEMENT, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

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
    public int updateDataElement(AbstractDataElement dataElement)
            throws JSONException {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_OSMID, dataElement.getOsmId());

        int count = 0;

        count += db.update(TABLE_DATAELEMENT, values, KEY_OSMID + "=?",
                new String[] { String.valueOf(dataElement.getOsmId()) });

        count += updateTagMap(dataElement.getTags());

        if (dataElement instanceof PolyElement) {
            count += updatePolyElement((PolyElement) dataElement);
        } else {
            count += updateNode((Node) dataElement);
        }

        // db.close();

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

        List<AbstractDataElement> dataElements = new ArrayList<AbstractDataElement>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DATAELEMENT, null);

        AbstractDataElement dataElement;

        if (cursor.moveToFirst()) {
            do {

                if (checkIfRecordExists(TABLE_POLYELEMENT, KEY_OSMID,
                        cursor.getInt(0))) {
                    dataElement = getPolyElement(cursor.getInt(0));
                } else {
                    dataElement = getNode(cursor.getInt(0));
                }

                JSONObject json = new JSONObject(cursor.getString(1));
                JSONArray jArray = json.optJSONArray("tagIDarray");

                ArrayList<Integer> tagIDs = new ArrayList<Integer>();

                for (int i = 0; i < jArray.length(); i++) {
                    int tagID = jArray.optInt(i);
                    tagIDs.add(tagID);
                }

                Map<Tag, String> tagMap = getTagMap(tagIDs);
                dataElement.addTags(tagMap);

                dataElements.add(dataElement);
            } while (cursor.moveToNext());
        }

        // db.close();
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

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            values.put(KEY_TAGID, tag.getKey().getId());
            values.put(KEY_VALUE, tag.getValue());
            db.insert(TABLE_TAGMAP, null, values);
        }

        // db.close();
    }

    /**
     * This method returns the data for specific tags stored in the database and
     * creates the corresponding {@link Map} object.
     * 
     * @param tagIDs
     *            the ids of the desired tags
     * @return a {@link Map} object for the desired tags
     */
    public Map<Tag, String> getTagMap(ArrayList<Integer> tagIDs) {

        SQLiteDatabase db = getReadableDatabase();

        Map<Tag, String> tagMap = new Hashtable<Tag, String>();

        for (int id : tagIDs) {
            Cursor cursor = db
                    .query(TABLE_TAGMAP, new String[] { KEY_TAGID, KEY_VALUE },
                            KEY_TAGID + "=?",
                            new String[] { String.valueOf(id) }, null, null,
                            null, null);

            if (cursor != null)
                cursor.moveToFirst();

            tagMap.put(Tags.getTagWithId(id), cursor.getString(1));
            cursor.close();
        }
        // db.close();
        return tagMap;
    }

    /**
     * This method deletes specific tags from the database.
     * 
     * @param tagIDs
     *            the tags whose data should be deleted
     */
    public void deleteTagMap(ArrayList<Integer> tagIDs) {
        SQLiteDatabase db = getWritableDatabase();

        for (int id : tagIDs) {
            db.delete(TABLE_TAGMAP, KEY_TAGID + "=?",
                    new String[] { String.valueOf(id) });
        }
        // db.close();
    }

    /**
     * This method returns the number of tags currently stored in the database.
     * 
     * @return the number of tags
     */
    public int getTagMapCount() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAGMAP, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close();

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        int count = 0;

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            values.put(KEY_TAGID, tag.getKey().getId());
            values.put(KEY_VALUE, tag.getValue());

            count += db.update(TABLE_TAGMAP, values, KEY_TAGID + "=?",
                    new String[] { String.valueOf(tag.getKey().getId()) });
        }
        // db.close();
        return count;
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

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE " + field + " = "
                + value;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }
}