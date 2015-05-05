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

import io.github.data4all.logger.Log;
import io.github.data4all.model.data.AbstractDataElement;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.PolyElement;
import io.github.data4all.model.data.PolyElement.PolyElementType;
import io.github.data4all.model.data.Tag;
import io.github.data4all.model.data.Tags;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.model.data.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * @author fkirchge
 * @author sbrede
 * @author steeve
 * @author tbrose
 */
public class DataBaseHandler extends SQLiteOpenHelper {
    /**
     * The DataElementType is used by the DatabaseHandler internally to map a
     * sub-type of DataElement to an integer and vice versa.
     * 
     * @author tbrose
     */
    private static enum DataElementType {
        NODE(0, Node.class), POLYELEMENT(1, PolyElement.class);

        private final int id;
        private Class<? extends AbstractDataElement> clazz;

        private DataElementType(int id,
                Class<? extends AbstractDataElement> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        private static DataElementType fromId(int id) {
            for (DataElementType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }

        private static DataElementType fromElement(AbstractDataElement element) {
            for (DataElementType type : values()) {
                if (type.clazz == element.getClass()) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final String WHERE = " WHERE ";
    private static final String FROM = " FROM ";
    private static final String SELECT = "SELECT ";
    private static final String SELECT_ALL = "SELECT * FROM ";

    private static final String TAG = "DataBaseHandler";
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Data4AllDB";

    // Table Names
    private static final String TABLE_USER = "users";
    private static final String TABLE_DATAELEMENT = "dataelements";
    private static final String TABLE_POLYELEMENT = "polyelements";
    private static final String TABLE_NODE = "nodes";
    private static final String TABLE_TAGMAP = "tagmap";
    private static final String TABLE_LASTCHOICE = "lastChoice";
    private static final String TABLE_GPSTRACK = "gpstracks";
    private static final String TABLE_TRACKPOINT = "trackpoints";

    // User Column Names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKENSECRET = "tokensecret";

    // General Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ELEMENT = "element";

    // Node Column Names
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";

    // TagMap Column Names
    private static final String KEY_TAGID = "tagid";
    private static final String KEY_VALUE = "value";

    // GPS Track Column Names
    private static final String KEY_TRACKNAME = "trackname";
    private static final String KEY_FINISHED = "finished";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TAGS = "tags";

    // GPS Trackpoint Column Names
    private static final String KEY_ALT = "altitude";
    private static final String KEY_TIME = "timestamp";

    /**
     * Default constructor for the database handler.
     * 
     * @param context
     *            the application.
     */
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Table creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" + KEY_USERNAME
                + " TEXT PRIMARY KEY," + KEY_TOKEN + " TEXT," + KEY_TOKENSECRET
                + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_DATAELEMENT + " (" + KEY_ID
                + " INTEGER PRIMARY KEY, " + KEY_TYPE + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_POLYELEMENT + " (" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_TYPE + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_NODE + " (" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_ELEMENT + " INTEGER," + KEY_LAT
                + " REAL," + KEY_LON + " REAL)");
        db.execSQL("CREATE TABLE " + TABLE_TAGMAP + " (" + KEY_ELEMENT
                + " INTEGER," + KEY_TAGID + " INTEGER," + KEY_VALUE + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_LASTCHOICE + " (" + KEY_TYPE
                + " INTEGER," + KEY_TAGID + " INTEGER," + KEY_VALUE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_GPSTRACK + " (" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_TRACKNAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_TAGS + " TEXT,"
                + KEY_FINISHED + " BOOLEAN" + ")");
        db.execSQL("CREATE TABLE " + TABLE_TRACKPOINT + " (" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_ELEMENT + " INTEGER," + KEY_LAT
                + " REAL," + KEY_LON + " REAL," + KEY_ALT + " REAL," + KEY_TIME
                + " REAL" + ")");

        Log.i(TAG, "Tables have been created.");
    }

    // Database handling on upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String drop = "DROP TABLE IF EXISTS ";

        // Drop tables that already exist
        db.execSQL(drop + TABLE_USER);

        db.execSQL(drop + TABLE_DATAELEMENT);
        db.execSQL(drop + TABLE_POLYELEMENT);
        db.execSQL(drop + TABLE_NODE);
        db.execSQL(drop + TABLE_TAGMAP);
        db.execSQL(drop + TABLE_LASTCHOICE);

        db.execSQL(drop + TABLE_GPSTRACK);
        db.execSQL(drop + TABLE_TRACKPOINT);

        Log.i(TAG, "Tables have been dropped and will be recreated.");

        // Recreate tables
        this.onCreate(db);
    }

    // USER CRUD

    /**
     * This method creates and stores a new user in the database. The data is
     * taken from the {@link User} object that is passed to the method.
     * 
     * @param user
     *            the {@link User} object from which the data will be taken.
     */
    public void createUser(User user) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_TOKEN, user.getOAuthToken());
        values.put(KEY_TOKENSECRET, user.getOauthTokenSecret());

        final long rowID = db.insert(TABLE_USER, null, values);
        Log.i(TAG, "User " + rowID + " has been added.");
    }

    /**
     * This method deletes a specific user from the database.
     * 
     * @param user
     *            the {@link User} object whose data should be deleted.
     */
    public void deleteUser(User user) {
        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_USER, KEY_USERNAME + "='" + user.getUsername() + "'",
                null);
    }

    /**
     * This method returns a list of all users stored in the database and
     * creates corresponding {@link User} objects.
     * 
     * @return a list of users.
     */
    public List<User> getAllUser() {
        final List<User> users = new ArrayList<User>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery(SELECT_ALL + TABLE_USER, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                final User user =
                        new User(cursor.getString(0), cursor.getString(1),
                                cursor.getString(1 + 1));
                users.add(user);
            }
        }
        Log.i(TAG, users.size() + " users were retrieved from the database.");
        return users;
    }

    // -------------------------------------------------------------------------
    // DATA ELEMENT CRUD

    /**
     * This method creates and stores a new data element in the database. The
     * data is taken from the {@link AbstractDataElement} object that is passed
     * to the method.
     * 
     * @author tbrose
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object from which the data
     *            will be taken.
     */
    public void createDataElement(AbstractDataElement dataElement) {
        final SQLiteDatabase db = getWritableDatabase();
        long nextId = this.getNextId(TABLE_DATAELEMENT, KEY_ID);

        if (dataElement instanceof Node) {
            final Node node = (Node) dataElement;

            // Add the Node
            final ContentValues nodeValues = new ContentValues();
            nodeValues.put(KEY_ID, nextId);
            nodeValues.put(KEY_ELEMENT, nextId);
            nodeValues.put(KEY_LAT, node.getLat());
            nodeValues.put(KEY_LON, node.getLon());

            db.insert(TABLE_NODE, null, nodeValues);
        } else if (dataElement instanceof PolyElement) {
            final PolyElement poly = (PolyElement) dataElement;

            // Add the Nodes of the PolyElement
            final List<Node> polyNodes = poly.getNodes();
            final long finalId = nextId + polyNodes.size();
            insertPolyNodes(db, polyNodes, nextId, finalId);
            nextId = finalId;
            // Add the PolyElement
            final ContentValues polyValues = new ContentValues();
            polyValues.put(KEY_ID, nextId);
            polyValues.put(KEY_TYPE, poly.getType().getId());

            db.insert(TABLE_POLYELEMENT, null, polyValues);
        } else {
            throw new IllegalArgumentException("Unknown subtype of "
                    + AbstractDataElement.class.getSimpleName() + ": "
                    + dataElement.getClass().getName());
        }

        // Add the DataElement
        dataElement.setOsmId(nextId);

        final ContentValues elementValues = new ContentValues();
        elementValues.put(KEY_ID, nextId);
        elementValues
                .put(KEY_TYPE, DataElementType.fromElement(dataElement).id);
        db.insert(TABLE_DATAELEMENT, null, elementValues);

        // Add the Tags
        final ContentValues tagInitial = new ContentValues();
        tagInitial.put(KEY_ELEMENT, nextId);
        this.putTags(TABLE_TAGMAP, tagInitial, dataElement.getTags());
    }

    /**
     * Inserts all the given Nodes of the PolyElement with the id
     * {@code finalId} in the given database. The first Node was inserted with
     * the id {@code startId} and then the id is incrementing till
     * {@code finalId - 1}.
     * 
     * @author tbrose
     * 
     * @param db
     *            The database to insert to
     * @param polyNodes
     *            A list of all Nodes to insert
     * @param nextId
     *            The first id for the nodes
     * @param finalId
     *            The id of the PolyElement
     */
    private static void insertPolyNodes(final SQLiteDatabase db,
            final List<Node> polyNodes, long startId, final long finalId) {
        long nextId = startId;
        for (Node node : polyNodes) {
            node.setOsmId(nextId);

            final ContentValues nodeValues = new ContentValues();
            nodeValues.put(KEY_ID, nextId);
            nodeValues.put(KEY_ELEMENT, finalId);
            nodeValues.put(KEY_LAT, node.getLat());
            nodeValues.put(KEY_LON, node.getLon());

            db.insert(TABLE_NODE, null, nodeValues);
            nextId++;
        }
    }

    /**
     * This method deletes a specific data element from the database.
     * 
     * @author tbrose
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object whose data should be
     *            deleted.
     */
    public void deleteDataElement(AbstractDataElement dataElement) {
        final SQLiteDatabase db = getWritableDatabase();

        final String isId = "=" + dataElement.getOsmId();
        db.delete(TABLE_TAGMAP, KEY_ELEMENT + isId, null);
        db.delete(TABLE_NODE, KEY_ELEMENT + isId, null);
        db.delete(TABLE_POLYELEMENT, KEY_ID + isId, null);
        db.delete(TABLE_DATAELEMENT, KEY_ID + isId, null);
    }

    /**
     * This method returns the number of data elements currently stored in the
     * database.
     * 
     * @author tbrose
     * 
     * @return the number of data elements.
     */
    public int getDataElementCount() {
        final Cursor cursor =
                getReadableDatabase().rawQuery(
                        "SELECT COUNT(1) FROM " + TABLE_DATAELEMENT, null);
        cursor.moveToNext();
        final int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * This method updates the data for a specific data element stored in the
     * database.
     * 
     * @author tbrose
     * 
     * @param dataElement
     *            the {@link AbstractDataElement} object for which the data
     *            should be updated.
     * @return the number of rows that have been updated.
     */
    public void updateDataElement(AbstractDataElement dataElement) {
        // Maybe there is an intelligent way to do this ...
        this.deleteDataElement(dataElement);
        this.createDataElement(dataElement);
    }

    /**
     * This method returns a list of all data elements stored in the database
     * and creates corresponding {@link AbstractDataElement} objects.
     * 
     * @author tbrose
     * 
     * @return a list of data elements.
     */
    public List<AbstractDataElement> getAllDataElements() {
        final List<AbstractDataElement> elements =
                new ArrayList<AbstractDataElement>();

        final SQLiteDatabase db = getReadableDatabase();
        final Cursor elementCursor =
                db.rawQuery(SELECT_ALL + TABLE_DATAELEMENT, null);

        // Read all DataElements
        while (elementCursor.moveToNext()) {
            AbstractDataElement element = null;
            final int elementId = elementCursor.getInt(0);
            final DataElementType elementClass =
                    DataElementType.fromId(elementCursor.getInt(1));

            if (elementClass == DataElementType.NODE) {
                final Cursor nodeCursor =
                        db.rawQuery(SELECT + KEY_LAT + "," + KEY_LON + FROM
                                + TABLE_NODE + WHERE + KEY_ELEMENT + "="
                                + elementId, null);
                if (nodeCursor.moveToNext()) {
                    element =
                            new Node(elementId, nodeCursor.getDouble(0),
                                    nodeCursor.getDouble(1));
                }
                nodeCursor.close();
            } else if (elementClass == DataElementType.POLYELEMENT) {
                final Cursor polyCursor =
                        db.rawQuery(SELECT + KEY_TYPE + FROM
                                + TABLE_POLYELEMENT + WHERE + KEY_ID + "="
                                + elementId, null);
                if (polyCursor.moveToNext()) {
                    final PolyElementType type =
                            PolyElementType.fromId(polyCursor.getInt(0));
                    final PolyElement polyElement =
                            new PolyElement(elementId, type);

                    addPolyElementNodes(db, elementId, polyElement);
                    element = polyElement;
                }
                polyCursor.close();

            } else {
                throw new IllegalStateException("Unknown subtype of "
                        + AbstractDataElement.class.getSimpleName() + ": id="
                        + elementCursor.getInt(1));
            }

            if (element == null) {
                throw new IllegalStateException(
                        AbstractDataElement.class.getSimpleName()
                                + " with the id " + elementId
                                + " cannot be read");
            } else {
                final Map<Tag, String> tags =
                        this.buildTags(SELECT + KEY_TAGID + "," + KEY_VALUE
                                + FROM + TABLE_TAGMAP + WHERE + KEY_ELEMENT
                                + "=" + elementId);
                element.setTags(tags);
                elements.add(element);
            }
        }

        elementCursor.close();
        return elements;
    }

    /**
     * Adds all saved nodes for the given id to given PolyElement.
     * 
     * @author tbrose
     * 
     * @param db
     *            The database to read from
     * @param elementId
     *            The osmId of the element
     * @param polyElement
     *            The element where the nodes will be added to
     */
    private static void addPolyElementNodes(final SQLiteDatabase db,
            final int elementId, final PolyElement polyElement) {
        final Cursor nodeCursor =
                db.rawQuery(SELECT + KEY_ID + "," + KEY_LAT + "," + KEY_LON
                        + FROM + TABLE_NODE + WHERE + KEY_ELEMENT + "="
                        + elementId, null);
        while (nodeCursor.moveToNext()) {
            polyElement.addNode(new Node(nodeCursor.getLong(0), nodeCursor
                    .getDouble(1), nodeCursor.getDouble(1 + 1)));
        }
        nodeCursor.close();
    }

    /**
     * This method deletes all entries of the {@link AbstractDataElement} table.
     * 
     * @author tbrose
     */
    public void deleteAllDataElements() {
        final SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_TAGMAP, null, null);
        db.delete(TABLE_NODE, null, null);
        db.delete(TABLE_POLYELEMENT, null, null);
        db.delete(TABLE_DATAELEMENT, null, null);
    }

    // -------------------------------------------------------------------------
    // lastChoice

    /**
     * Returns the saved tagMap for the given category.
     * 
     * @author tbrose
     * 
     * @param category
     *            The category of the last choice
     * @return The last tags for this category
     */
    public Map<Tag, String> getLastChoice(int category) {
        return this.buildTags(SELECT + KEY_TAGID + "," + KEY_VALUE + " FROM "
                + TABLE_LASTCHOICE + WHERE + KEY_TYPE + "=" + category);
    }

    /**
     * Saves the given tagMap for the given category.
     * 
     * @author tbrose
     * 
     * @param category
     *            The category of the last choice
     * @param tags
     *            The last tags for this category
     */
    public void setLastChoice(int category, Map<Tag, String> tags) {
        final ContentValues categoryValue = new ContentValues();
        categoryValue.put(KEY_TYPE, category);
        this.putTags(TABLE_LASTCHOICE, categoryValue, tags);
    }

    /**
     * Executes the query and builds a tagMap from the result of the query.
     * 
     * In the result the first column needs to be the tagId and the second
     * column needs to be the value.
     * 
     * @author tbrose
     * 
     * @param query
     *            Well formed SQL query
     * @return The received tagMap
     */
    private Map<Tag, String> buildTags(String query) {
        final Map<Tag, String> tagMap = new LinkedHashMap<Tag, String>();
        final Cursor cursor = getReadableDatabase().rawQuery(query, null);

        while (cursor.moveToNext()) {
            tagMap.put(Tags.getTagWithId(cursor.getInt(0)), cursor.getString(1));
        }

        cursor.close();
        Log.i(TAG, tagMap.size() + " tags were retrieved from the database.");
        return tagMap;
    }

    /**
     * Executes an insert for each tag-pair of the given map.
     * 
     * @author tbrose
     * 
     * @param table
     *            The table to insert to
     * @param initialValues
     *            The initial content values for each insertion
     * @param tagMap
     *            The tags to be saved
     */
    private void putTags(String table, ContentValues initialValues,
            Map<Tag, String> tagMap) {
        final SQLiteDatabase db = getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.putAll(initialValues);

        for (Map.Entry<Tag, String> tag : tagMap.entrySet()) {
            values.put(KEY_TAGID, tag.getKey().getId());
            values.put(KEY_VALUE, tag.getValue());

            db.insert(table, null, values);
        }
        Log.i(TAG, "Tags have been added.");
    }

    // -------------------------------------------------------------------------
    // GPS-TRACK CRUD

    /**
     * This method creates and stores a new GPS track in the database. The data
     * is taken from the {@link Track} object that is passed to the method.
     * 
     * @author tbrose
     * 
     * @param track
     *            the {@link Track} object from which the data will be taken.
     */
    public void createGPSTrack(Track track) {
        final SQLiteDatabase db = getWritableDatabase();

        final long nextTrackId = this.getNextId(TABLE_GPSTRACK, KEY_ID);
        track.setID(nextTrackId);
        final ContentValues trackValues = valuesForTrack(track);
        db.insert(TABLE_GPSTRACK, null, trackValues);

        long nextPointId = this.getNextId(TABLE_TRACKPOINT, KEY_ID);
        for (TrackPoint tp : track.getTrackPoints()) {
            tp.setID(nextPointId);
            db.insert(TABLE_TRACKPOINT, null, valuesForPoint(nextTrackId, tp));
            nextPointId++;
        }
    }

    /**
     * This method returns the data for a specific GPS track stored in the
     * database and creates the corresponding {@link Track} object.
     * 
     * @author tbrose
     * 
     * @param id
     *            the id of the desired GPS track.
     * @return a {@link Track} object for the desired GPS track.
     */
    public Track getGPSTrack(long id) {
        final SQLiteDatabase db = getReadableDatabase();
        Track track = null;

        final String select = SELECT_ALL + TABLE_GPSTRACK + WHERE + KEY_ID;
        final Cursor cursor = db.rawQuery(select + "=" + id, null);

        if (cursor.moveToNext()) {
            // KEY_ID KEY_TRACKNAME KEY_DESCRIPTION KEY_TAGS FLAG_FINISHED
            track = new Track();
            track.setID(cursor.getLong(0));
            track.setTrackName(cursor.getString(1));
            track.setDescription(cursor.getString(2));
            track.setTags(cursor.getString(3));
            track.setStatus(cursor.getInt(4) != 0);

            final Cursor pointCursor =
                    db.rawQuery(SELECT_ALL + TABLE_TRACKPOINT + WHERE
                            + KEY_ELEMENT + "=" + id + " order by " + KEY_TIME
                            + " ASC", null);
            final List<TrackPoint> trackPoints =
                    new ArrayList<TrackPoint>(pointCursor.getCount());
            while (pointCursor.moveToNext()) {
                final TrackPoint point =
                        new TrackPoint(pointCursor.getDouble(2),
                                pointCursor.getDouble(3),
                                pointCursor.getDouble(4),
                                pointCursor.getLong(5));
                point.setID(pointCursor.getLong(0));
                trackPoints.add(point);
            }
            pointCursor.close();
            track.setTrackPoints(trackPoints);
        }
        cursor.close();
        return track;
    }

    /**
     * This method deletes a specific GPS track from the database.
     * 
     * @author tbrose
     * 
     * @param track
     *            the {@link Track} object whose data should be deleted.
     */
    public void deleteGPSTrack(Track track) {
        final SQLiteDatabase db = getWritableDatabase();
        final String isId = "=" + track.getID();

        db.delete(TABLE_GPSTRACK, KEY_ID + isId, null);
        db.delete(TABLE_TRACKPOINT, KEY_ELEMENT + isId, null);
    }

    /**
     * This method returns the number of GPS tracks currently stored in the
     * database.
     * 
     * @author tbrose
     * 
     * @return the number of GPS tracks.
     */
    public int getGPSTrackCount() {
        final Cursor cursor =
                getReadableDatabase().rawQuery(
                        "SELECT COUNT(1) FROM " + TABLE_GPSTRACK, null);
        cursor.moveToNext();
        final int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * This method updates the data for a specific GPS track stored in the
     * database.
     * 
     * @author tbrose
     * 
     * @param track
     *            the {@link Track} object for which the data should be updated.
     */
    public void updateGPSTrack(Track track) {
        final SQLiteDatabase db = getWritableDatabase();
        final Cursor exists =
                db.rawQuery("SELECT 1 FROM " + TABLE_GPSTRACK + WHERE + KEY_ID
                        + "=" + track.getID(), null);

        if (exists.moveToNext()) {

            db.update(TABLE_GPSTRACK, valuesForTrack(track), KEY_ID + "="
                    + track.getID(), null);

            long nextPointId = this.getNextId(TABLE_TRACKPOINT, KEY_ID);
            for (TrackPoint tp : track.getTrackPoints()) {
                if (tp.getID() == TrackPoint.NO_ID) {
                    tp.setID(nextPointId);
                    db.insert(TABLE_TRACKPOINT, null,
                            valuesForPoint(track.getID(), tp));
                    nextPointId++;
                }
            }
        } else {
            Log.i(TAG, "Attemped to update a track that does not exists");
        }
        exists.close();
    }

    /**
     * This method returns a list of all GPS tracks stored in the database and
     * creates corresponding {@link Track} objects.
     * 
     * @author tbrose
     * 
     * @return a list of GPS tracks.
     */
    public List<Track> getAllGPSTracks() {
        final String query = "SELECT " + KEY_ID + " FROM " + TABLE_GPSTRACK;
        final Cursor cursor = getReadableDatabase().rawQuery(query, null);
        final List<Track> tracks = new ArrayList<Track>(cursor.getCount());

        while (cursor.moveToNext()) {
            tracks.add(this.getGPSTrack(cursor.getInt(0)));
        }
        cursor.close();
        return tracks;
    }

    /**
     * This method deletes all entries of the {@link Track} table.
     * 
     * @author tbrose
     */
    public void deleteAllGPSTracks() {
        final SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_GPSTRACK, null, null);
        db.delete(TABLE_TRACKPOINT, null, null);
    }

    /**
     * Creates the ContentValues for the given Track.
     * 
     * @author tbrose
     * 
     * @param track
     *            The Track to read the values from
     * @return A ContentValues object for inserting into the database holding
     *         the given data
     */
    private static ContentValues valuesForTrack(Track track) {
        final ContentValues values = new ContentValues();
        values.put(KEY_ID, track.getID());
        values.put(KEY_TRACKNAME, track.getTrackName());
        values.put(KEY_DESCRIPTION, track.getDescription());
        values.put(KEY_TAGS, track.getTags());
        values.put(KEY_FINISHED, track.isFinished());
        return values;
    }

    /**
     * Creates the ContentValues for the given TrackPoint with the elementId set
     * to {@code trackId}.
     * 
     * @author tbrose
     * 
     * @param trackId
     *            The id of the track of the given TrackPoint
     * @param tp
     *            The TrackPoint to read the values from
     * @return A ContentValues object for inserting into the database holding
     *         the given data
     */
    private static ContentValues valuesForPoint(long trackId, TrackPoint tp) {
        final ContentValues pointValues = new ContentValues();
        pointValues.put(KEY_ID, tp.getID());
        pointValues.put(KEY_ELEMENT, trackId);
        pointValues.put(KEY_LAT, tp.getLat());
        pointValues.put(KEY_LON, tp.getLon());
        pointValues.put(KEY_ALT, tp.getAlt());
        pointValues.put(KEY_TIME, tp.getTime());
        return pointValues;
    }

    /**
     * Returns the last ID used for DataElements plus one.
     * 
     * @author tbrose
     * 
     * @param table
     *            The table to receive the next id from
     * @param key
     *            The name of the id column
     * 
     * @return the last used ID plus one
     */
    private long getNextId(String table, String key) {
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor =
                db.rawQuery(SELECT + key + FROM + table + " order by " + key
                        + " DESC limit 1", null);
        long lastId = 0;
        if (cursor.moveToNext()) {
            lastId = cursor.getLong(0);
            Log.d(TAG, "LAST ID: " + lastId);
        }
        cursor.close();
        return lastId + 1;
    }
}