/**
 * 
 */
package io.github.data4all.model;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.data4all.model.data.BoundingBox;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.User;
import io.github.data4all.model.data.Way;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Richard Rohde, Kristin Dahnken
 *
 */
public class DataBaseHandler extends SQLiteOpenHelper {

	

	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Data4AllDB";
 
    // Table Names
    private static final String TABLE_BOUNDINGBOX = "boundingboxes";
    private static final String TABLE_NODE = "nodes";
    private static final String TABLE_OSMELEMENT = "osmelements";
    private static final String TABLE_OSMPARENTLIST = "osmparentlist";
    private static final String TABLE_OSMTAGMAP = "osmtagmap";
    private static final String TABLE_RELATION = "relations";
    private static final String TABLE_RELATIONMEMBER = "relationmembers";
    private static final String TABLE_USER = "users";
    private static final String TABLE_WAY = "ways";
    
 
    // Columns Names
    private static final String KEY_OSMID = "osmid";    
    
    // BoundingBox Column Names
    private static final String KEY_MINLAT = "minlat";
    private static final String KEY_MINLON = "minlon";
    private static final String KEY_MAXLAT = "maxlat";
    private static final String KEY_MAXLON = "maxlon";
    private static final String KEY_ID = "id";
    
    // Node Column Names    
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    
    // OSMElement Column Names    
    private static final String KEY_OSMVERSION = "osmversion";
    
    // OSMParentList Column Names    
    private static final String KEY_RELATIONID = "relationid";
    
    // OSMTagMap Column Names    
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";
    
    // Relation Column Names    
    private static final String KEY_RELATIONMEMBER = "relationmembers";
    
    // RelationMember Column Names    
    private static final String KEY_TYPE = "type";
    private static final String KEY_REF = "ref";  
    private static final String KEY_ROLE = "role";
    
    // User Column Names    
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGINSTATUS = "loginstatus";
   
    // Way Column Names    
    private static final String KEY_NODEID = "nodeid";
 
    
    /**
     * Default constructor for the database handler.
     * 
     * @param context the application
     */
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OSMELEMENTS_TABLE = "CREATE TABLE " + TABLE_OSMELEMENT + " ("
                + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_OSMVERSION + " INTEGER" 
        		+")";
    	String CREATE_NODES_TABLE = "CREATE TABLE " + TABLE_NODE + " ("
                + KEY_OSMID + " INTEGER PRIMARY KEY," + KEY_LAT + " REAL,"
                + KEY_LON + " REAL" +")";
        String CREATE_WAYS_TABLE = "CREATE TABLE " + TABLE_WAY + " ("
                + KEY_OSMID + " INTEGER NOT NULL," 
        		+ KEY_NODEID + " INTEGER NOT NULL,"
                + " PRIMARY KEY ("+ KEY_OSMID + ", " + KEY_NODEID + "))";
        String CREATE_RELATIONMEMBERS_TABLE = "CREATE TABLE " + TABLE_RELATIONMEMBER 
        		+ " ("
                + KEY_REF + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT,"
                + KEY_ROLE + " TEXT" +")";
        String CREATE_RELATIONS_TABLE = "CREATE TABLE " + TABLE_RELATION + " ("
                + KEY_OSMID + " INTEGER NOT NULL," 
        		+ KEY_RELATIONMEMBER + " INTEGER NOT NULL,"
        		+ " PRIMARY KEY ("+ KEY_OSMID + ", " + KEY_RELATIONMEMBER + "))";
        String CREATE_OSMTAGMAPS_TABLE = "CREATE TABLE " + TABLE_OSMTAGMAP + " ("
                + KEY_OSMID + " INTEGER NOT NULL," + KEY_KEY + " TEXT NOT NULL,"
                + KEY_VALUE + " TEXT," 
                + " PRIMARY KEY ("+ KEY_OSMID + ", " + KEY_KEY + "))";
        String CREATE_OSMPARENTLISTS_TABLE = "CREATE TABLE " + TABLE_OSMPARENTLIST 
        		+ " ("
                + KEY_OSMID + " INTEGER NOT NULL," 
        		+ KEY_RELATIONID + " INTEGER NOT NULL,"
                + " PRIMARY KEY ("+ KEY_OSMID +", "+ KEY_RELATIONID + "))";
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_USERNAME + " TEXT PRIMARY KEY," + KEY_TOKEN + " TEXT,"
                + KEY_LOGINSTATUS + " INTEGER" +")";
        String CREATE_BOUNDINGBOXS_TABLE = "CREATE TABLE " + TABLE_BOUNDINGBOX + " ("
                + KEY_MINLAT + " REAL NOT NULL," + KEY_MINLON + " REAL NOT NULL," 
        		+ KEY_MAXLAT + " REAL NOT NULL," + KEY_MAXLON + " REAL NOT NULL," 
        		+ " PRIMARY KEY ("+ KEY_MINLAT + ", " + KEY_MINLON 
        		+", "+ KEY_MAXLAT +", "+ KEY_MAXLON + "))";
        
        db.execSQL(CREATE_OSMELEMENTS_TABLE);
        db.execSQL(CREATE_NODES_TABLE);
        db.execSQL(CREATE_WAYS_TABLE);
        db.execSQL(CREATE_RELATIONMEMBERS_TABLE);
        db.execSQL(CREATE_RELATIONS_TABLE);
        db.execSQL(CREATE_OSMTAGMAPS_TABLE);
        db.execSQL(CREATE_OSMPARENTLISTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_BOUNDINGBOXS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOUNDINGBOX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSMPARENTLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSMTAGMAP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELATIONMEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSMELEMENT);
        
        // Create tables again
        onCreate(db);
    }
    
  //-------------------------------------------------------------------------
  // BoundingBox CRUD
    
//    public void createBoundingBox(BoundingBox boundingBox){
//    	SQLiteDatabase db = getWritableDatabase();
//    	
//    	ContentValues values = new ContentValues();
//    	
//    	values.put(KEY_MINLAT, boundingBox.getMinlat());
//    	values.put(KEY_MINLON, boundingBox.getMinlon());
//    	values.put(KEY_MAXLAT, boundingBox.getMaxlat());
//    	values.put(KEY_MAXLON, boundingBox.getMaxlon());
//    	
//    	db.insert(TABLE_BOUNDINGBOX, null, values);
//    	db.close();
//    }
//    public BoundingBox getBoundingBox (int id){ //evtl. für BoundingBox noch ne id hinzufügen
//    	SQLiteDatabase db = getReadableDatabase();
//    	
//    	//TODO: use ID??
//    	Cursor cursor = db.query(TABLE_BOUNDINGBOX, new String[]{KEY_MINLAT, KEY_MINLON, KEY_MAXLAT, KEY_MAXLON}, 
//    			KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
//    	
//    	if(cursor != null)
//    		cursor.moveToFirst();
//    	
//    	BoundingBox boundingBox = new BoundingBox(Double.parseDouble(cursor.getString(0)), 
//    			Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)), 
//    			Double.parseDouble(cursor.getString(3)));
//    	
//    	cursor.close();
//    	db.close();
//    	return boundingBox;
//    }
//    
//    public void deleteBoundingBox(BoundingBox boundingBox){
//    	SQLiteDatabase db = getWritableDatabase();
//    	
//    	
////    	db.delete(TABLE_BOUNDINGBOX, KEY_ID + "=?", new String[]{String.valueOf(boundingBox.getID())});
//    	
//    	db.close();
//    }
//    
//    public int getBoundingBoxCount(){
//    	SQLiteDatabase db = getReadableDatabase();
//    	
//    	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOUNDINGBOX, null);
//      int count = cursor.getCount();
//    	cursor.close();
//    	db.close();
//    	
//    	return count;
//    }
//    
//    public int updateBoundingBox(BoundingBox boundingBox){
//    	SQLiteDatabase db = getWritableDatabase();
//    	
//    	ContentValues values = new ContentValues();
//    	
//    	values.put(KEY_MINLAT, boundingBox.getMinlat());
//    	values.put(KEY_MINLON, boundingBox.getMinlon());
//    	values.put(KEY_MAXLAT, boundingBox.getMaxlat());
//    	values.put(KEY_MAXLON, boundingBox.getMaxlon());
//    	
////    	db.close();
////    	return db.update(TABLE_BOUNDINGBOX, values, KEY_ID + "=?", new String[]{String.valueOf(boundingBox.getID())});
//    return 0;
//    }
//    
//    public List<BoundingBox> getAllBoundingBox(){
//    	List<BoundingBox> boundingBoxes = new ArrayList<BoundingBox>();
//    	
//    	SQLiteDatabase db = getReadableDatabase();
//    	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOUNDINGBOX, null);
//    	
//    	if(cursor.moveToFirst()){
//    		do{
//    			BoundingBox boundingBox = new BoundingBox(Double.parseDouble(cursor.getString(0)), 
//    					Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)), 
//    					Double.parseDouble(cursor.getString(3)));
//    			boundingBoxes.add(boundingBox);
//    		}
//    		while (cursor.moveToNext());
//    	}
//    	
//    	db.close();
//    	return boundingBoxes;
//    }
    
    //-------------------------------------------------------------------------
    // USER CRUD
      
    /**
     * This method creates and stores a new user in the database. The data is taken from the {@link User} object 
     * that is passed to the method.
     * 
     * @param user the {@link User} object from which the data will be taken
     */
      public void createUser(User user){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	ContentValues values = new ContentValues();
    	
    	values.put(KEY_USERNAME, user.getUsername());
    	values.put(KEY_TOKEN, user.getLoginToken());
    	values.put(KEY_LOGINSTATUS, user.isLoggedIn());
    	
    	db.insert(TABLE_USER, null, values);
      	
      	db.close();
      }
      
      /**
       * This method returns the data for a specific user stored in the database and creates the corresponding 
       * {@link User} object.
       * 
       * @param username the name of the desired user
       * @return a {@link User} object for the desired user
       */
      public User getUser (String username){
      	SQLiteDatabase db = getReadableDatabase();
    	
      	Cursor cursor = db.query(TABLE_USER, new String[]{KEY_USERNAME, KEY_TOKEN, KEY_LOGINSTATUS}, 
      			KEY_USERNAME + "=?", new String[]{username}, null, null, null, null);
      	
      	if(cursor != null)
      		cursor.moveToFirst();
      	
      	User user  = new User(cursor.getString(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)));
      	
      	cursor.close();
      	db.close();
      	return user;
      }
      
      /**
       * This method deletes a specific user from the database.
       * 
       * @param user the {@link User} object whose data should be deleted
       */
      public void deleteUser(User user){
    	  SQLiteDatabase db = getWritableDatabase();
      	
      	
      	db.delete(TABLE_USER, KEY_USERNAME + "=?", new String[]{user.getUsername()});
      	
      	db.close();
      }
      
      /**
       * This method returns the number of users currently stored in the database.
       * 
       * @return the number of users
       */
      public int getUserCount(){
    	SQLiteDatabase db = getReadableDatabase();
      	
      	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
      	int count = cursor.getCount();
      	cursor.close();
      	db.close();
      	
      	return count;
      }
      
      /**
       * This method updates the data for a specific user stored in the database.
       * 
       * @param user the {@link User} object for which the data should be updated
       * @return the number of rows that have been updated
       */
      public int updateUser(User user){
      	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
    	values.put(KEY_USERNAME, user.getUsername());
    	values.put(KEY_TOKEN, user.getLoginToken());
    	values.put(KEY_LOGINSTATUS, user.isLoggedIn());
    	
    	int count = db.update(TABLE_USER, values, KEY_USERNAME + "=?", new String[]{user.getUsername()});
    	db.close();
    	return count;
      }
      
      /**
       * This method returns a list of all users stored in the database and creates corresponding {@link User} 
       * objects.
       * 
       * @return a list of users
       */
      public List<User> getAllUser(){
    	List<User> users = new ArrayList<User>();
      	
      	SQLiteDatabase db = getReadableDatabase();
      	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
      	
      	if(cursor.moveToFirst()){
      		do{
      			User user = new User(cursor.getString(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)));
      			users.add(user);
      		}
      		while (cursor.moveToNext());
      	}
      	
      	db.close();
      	return users;
      }
      
      //-------------------------------------------------------------------------
    // NODE CRUD
      
      /**
       * This method creates and stores a new node in the database. The data is taken from the {@link Node} object 
       * that is passed to the method.
       * 
       * @param node the {@link Node} object from which the data will be taken
       */
      public void createNode(Node node){
    	SQLiteDatabase db = getWritableDatabase();
        	
        ContentValues values = new ContentValues();
      	
      	values.put(KEY_OSMID, node.getOsmId());
      	values.put(KEY_LAT, node.getLat());
      	values.put(KEY_LON, node.getLon());
      	
      	db.insert(TABLE_NODE, null, values);
      	
      		
        db.close();
        
        createOsmElement(node.getOsmId(),node.getOsmVersion());
      	createParentRelation(node.getOsmId(),node.getParentRelations());
      	createTagSortedMap(node.getOsmId(), node.getTags());
      }
      
      /**
       * This method returns the data for a specific node stored in the database and creates the corresponding 
       * {@link Node} object.
       * 
       * @param id the id of the desired node
       * @return a {@link Node} object for the desired node
       */
      public Node getNode (long id){
    	  SQLiteDatabase db = getReadableDatabase();
      	
        	Cursor cursor = db.query(TABLE_NODE, new String[]{KEY_OSMID, KEY_LAT, KEY_LON}, 
        			KEY_OSMID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        	
        	if(cursor != null)
        		cursor.moveToFirst();
        	
        	Node node  = new Node(Long.parseLong(cursor.getString(0)), getOsmElementOsmVersion (id), 
        			Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
        	
        	cursor.close();
        	db.close();
        	
        	node.setTags(getTagSortedMap(id));
        	node.addParentRelations(getParentRelationList(id));
        	
        	return node;
      }
      
      /**
       * This method deletes a specific node from the database.
       * 
       * @param node the {@link Node} object whose data should be deleted
       */
      public void deleteNode(Node node){
    	  SQLiteDatabase db = getWritableDatabase();
        	
        	
        	db.delete(TABLE_NODE, KEY_OSMID + "=?", new String[]{String.valueOf(node.getOsmId())});
        	
        	db.close();
        	
        	deleteOsmElement(node.getOsmId());
        	deleteParentRelationList(node.getOsmId());
        	deleteTagSortedMap(node.getOsmId());
        	deleteNodeInWay(node.getOsmId());
        	deleteRelationMemberByRefId(node.getOsmId());
      }
      
      /**
       * This method returns the number of nodes currently stored in the database.
       * 
       * @return the number of nodes
       */
      public int getNodeCount(){
    	  SQLiteDatabase db = getReadableDatabase();
        	
        	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);
        	int count = cursor.getCount();
        	cursor.close();
        	db.close();
        	
        	return count;
      }
      
      /**
       * This method updates the data for a specific node stored in the database.
       * 
       * @param node the {@link Node} object for which the data should be updated
       * @return the number of rows that have been updated
       */
      public int updateNode(Node node){
    	SQLiteDatabase db = getWritableDatabase();
      	
      	ContentValues values = new ContentValues();
      	
      	values.put(KEY_OSMID, node.getOsmId());
      	values.put(KEY_LAT, node.getLat());
      	values.put(KEY_LON, node.getLon());
     	int count = db.update(TABLE_NODE, values, KEY_OSMID + "=?", new String[]{String.valueOf(node.getOsmId())});
      	db.close();
     	
      	count += updateOsmElement(node.getOsmId(),node.getOsmVersion());
      	count += updateParentRelation(node.getOsmId(),node.getParentRelations());
      	count += updateTagSortedMap(node.getOsmId(), node.getTags());
     	
      	return count;
      }
      
      /**
       * This method returns a list of all nodes stored in the database and creates corresponding {@link Node} 
       * objects.
       * 
       * @return a list of nodes
       */
      public List<Node> getAllNode(){
    	  List<Node> nodes = new ArrayList<Node>();
        	
        	SQLiteDatabase db = getReadableDatabase();
        	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);
        	
        	if(cursor.moveToFirst()){
        		do{
        			Node node = new Node(Long.parseLong(cursor.getString(0)), getOsmElementOsmVersion (Long.parseLong(cursor.getString(0))), 
                			Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
        			node.setTags(getTagSortedMap(node.getOsmId()));
                	node.addParentRelations(getParentRelationList(node.getOsmId()));
        			nodes.add(node);
        		}
        		while (cursor.moveToNext());
        	}
        	
        	db.close();
        	return nodes;
      }
      
      //-------------------------------------------------------------------------
    // WAY CRUD
      
      /**
       * This method creates and stores a new way in the database. The data is taken from the {@link Way} object 
       * that is passed to the method.
       * 
       * @param way the {@link Way} object from which the data will be taken
       */
      public void createWay(Way way){
    	SQLiteDatabase db = getWritableDatabase();
      	
        ContentValues values = new ContentValues();
        	
        values.put(KEY_OSMID, way.getOsmId());
        
        for(Node n : way.getNodes()){
        	values.put(KEY_NODEID, n.getOsmId());
        	db.insert(TABLE_WAY, null, values);       	
        }         	
        db.close();
        
        createOsmElement(way.getOsmId(),way.getOsmVersion());
      	createParentRelation(way.getOsmId(),way.getParentRelations());
      	createTagSortedMap(way.getOsmId(), way.getTags());
      }
      
      /**
       * This method returns the data for a specific way stored in the database and creates the corresponding 
       * {@link Way} object.
       * 
       * @param id the id of the desired way
       * @return a {@link Way} object for the desired way
       */
      public Way getWay (long id){
    	SQLiteDatabase db = getReadableDatabase();
        	
      	Cursor cursor = db.query(TABLE_WAY, new String[]{KEY_OSMID, KEY_NODEID}, 
      			KEY_OSMID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
      	
      	if(cursor != null)
      		cursor.moveToFirst();
      	
      	List<Node> allNodes = getAllNode();
      	List<Node> wayNodes = new ArrayList<Node>();
      	
      	for(Node n : allNodes){
      		
      		if(!cursor.isFirst()){
      			cursor.moveToNext();
      		}
          		if(Long.parseLong(cursor.getString(1)) == n.getOsmId()){
          			wayNodes.add(n);
          		} 
      				
      	}
      	
      	Way way  = new Way(Long.parseLong(cursor.getString(0)), getOsmElementOsmVersion(id)); 
      	way.addNodes(wayNodes, false);
      	
      	cursor.close();
      	db.close();
      	
      	way.setTags(getTagSortedMap(id));
    	way.addParentRelations(getParentRelationList(id));
      	
      	return way;
      }
      
      /**
       * This method deletes a specific way from the database.
       * 
       * @param way the {@link Way} object whose data should be deleted
       */
      public void deleteWay(Way way){
    	SQLiteDatabase db = getWritableDatabase();   	
      	
      	db.delete(TABLE_WAY, KEY_OSMID + "=?", new String[]{String.valueOf(way.getOsmId())});
      	
      	db.close();
      	
      	deleteOsmElement(way.getOsmId());
    	deleteParentRelationList(way.getOsmId());
    	deleteTagSortedMap(way.getOsmId());
    	deleteRelationMemberByRefId(way.getOsmId()); 
      }
      
      /**
       * This method returns the number of ways currently stored in the database.
       * 
       * @return the number of ways
       */
      public int getWayCount(){
    	SQLiteDatabase db = getReadableDatabase();
      	
      	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WAY, null);
      	int count = cursor.getCount();
      	cursor.close();
      	db.close();
      	
      	return count;
      }
      
      /**
       * This method updates the data for a specific way stored in the database.
       * 
       * @param way the {@link Way} object for which the data should be updated
       * @return the number of rows that have been updated
       */
      public int updateWay(Way way){
    	  SQLiteDatabase db = getWritableDatabase();
      	
          ContentValues values = new ContentValues();
          
          int count = 0;
          	
          values.put(KEY_OSMID, way.getOsmId());

          for(Node n : way.getNodes()){
        	 values.put(KEY_NODEID, n.getOsmId());
        	 count += db.update(TABLE_WAY, values, KEY_OSMID + "=?", new String[]{String.valueOf(way.getOsmId())});
          }       
          
          db.close();
          
          count += updateOsmElement(way.getOsmId(),way.getOsmVersion());
          count += updateParentRelation(way.getOsmId(),way.getParentRelations());
          count += updateTagSortedMap(way.getOsmId(), way.getTags());
          
          return count;
      }
      
      /**
       * This method returns a list of all ways stored in the database and creates corresponding {@link Way} 
       * objects.
       * 
       * @return a list of ways
       */
      public List<Way> getAllWay(){
    	List<Way> ways = new ArrayList<Way>();
        	
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WAY, null);
      	
      	List<Node> allNodes = getAllNode();
      	List<Node> wayNodes = new ArrayList<Node>();
        	
        	if(cursor.moveToFirst()){
        		do{
        			for(Node n : allNodes){ 	      		
                  		if(Long.parseLong(cursor.getString(1)) == n.getOsmId()){
                  			wayNodes.add(n);
                  		}      				
              	}
        			Way way = new Way(Long.parseLong(cursor.getString(0)), getOsmElementOsmVersion(Long.parseLong(cursor.getString(0)))); 
        			way.addNodes(wayNodes, false);
        			way.setTags(getTagSortedMap(way.getOsmId()));
        	    	way.addParentRelations(getParentRelationList(way.getOsmId()));
        			ways.add(way);
        		}
        		while (cursor.moveToNext());
        	}
        	
        	db.close();
        	return ways;
      }
      
      //-------------------------------------------------------------------------
    // RELATION CRUD
      
      /**
       * This method creates and stores a new relation in the database. The data is taken from the {@link Relation} 
       * object that is passed to the method.
       * 
       * @param relation the {@link Relation} object from which the data will be taken
       */
      public void createRelation(Relation relation){
    	SQLiteDatabase db = getWritableDatabase();
      	
        ContentValues values = new ContentValues();
          	        
        values.put(KEY_OSMID, relation.getOsmId());
        
        for(RelationMember rm : relation.getMembers()){
        	values.put(KEY_RELATIONMEMBER, rm.getRef());
        	db.insert(TABLE_RELATION, null, values);       	
        }         	
        db.close();
        
        createOsmElement(relation.getOsmId(),relation.getOsmVersion());
      	createParentRelation(relation.getOsmId(),relation.getParentRelations());
      	createTagSortedMap(relation.getOsmId(), relation.getTags());
      }
      
      /**
       * This method returns the data for a specific relation stored in the database and creates the corresponding 
       * {@link Relation} object.
       * 
       * @param id the id of the desired relation
       * @return a {@link Relation} object for the desired relation
       */
      public Relation getRelation (long id){
    	SQLiteDatabase db = getReadableDatabase();
        	
      	Cursor cursor = db.query(TABLE_RELATION, new String[]{KEY_OSMID, KEY_RELATIONMEMBER}, 
      			KEY_OSMID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
      	
      	if(cursor != null)
      		cursor.moveToFirst();
      	
      	List<RelationMember> allMembers = getAllRelationMember();
      	List<RelationMember> theseMembers = new ArrayList<RelationMember>();
      	
      	for(RelationMember rm : allMembers){
      		
      		if(!cursor.isFirst()){
      			cursor.moveToNext();
      		}
          		if(Long.parseLong(cursor.getString(1)) == rm.getRef()){
          			theseMembers.add(rm);
          		} 
      				
      	}
      	Relation relation = new Relation(id, getOsmElementOsmVersion(id)); 
      	for(RelationMember rm : theseMembers){
      		relation.addMember(rm);
      	}
      	
      	cursor.close();
      	db.close();
      	
      	relation.setTags(getTagSortedMap(id));
    	relation.addParentRelations(getParentRelationList(id));
      	return relation;
      }
      
      /**
       * This method deletes a specific relation from the database.
       * 
       * @param relation the {@link Relation} object whose data should be deleted
       */
      public void deleteRelation(Relation relation){
    	SQLiteDatabase db = getWritableDatabase();
      	      	
      	db.delete(TABLE_RELATION, KEY_OSMID + "=?", new String[]{String.valueOf(relation.getOsmId())});
      	
      	db.close();
      	
      	deleteOsmElement(relation.getOsmId());
    	deleteParentRelationList(relation.getOsmId());
    	deleteTagSortedMap(relation.getOsmId());
    	deleteRelationMemberByRefId(relation.getOsmId());
      }
      
      /**
       * This method returns the number of relations currently stored in the database.
       * 
       * @return the number of relations
       */
      public int getRelationCount(){
    	SQLiteDatabase db = getReadableDatabase();
      	
      	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATION, null);
      	int count = cursor.getCount();
      	cursor.close();
      	db.close();
      	
      	return count;
      }
      
      /**
       * This method updates the data for a specific relation stored in the database.
       * 
       * @param relation the {@link Relation} object for which the data should be updated
       * @return the number of rows that have been updated
       */
      public int updateRelation(Relation relation){
    	SQLiteDatabase db = getWritableDatabase();
        	
        ContentValues values = new ContentValues();
        
        int count = 0;
      	
        values.put(KEY_OSMID, relation.getOsmId());

        for(RelationMember rm : relation.getMembers()){
      	 values.put(KEY_RELATIONMEMBER, rm.getRef());
      	 count += db.update(TABLE_RELATION, values, KEY_OSMID + "=?", new String[]{String.valueOf(relation.getOsmId())});
        }        
        
        db.close();
        
        count += updateOsmElement(relation.getOsmId(),relation.getOsmVersion());
      	count += updateParentRelation(relation.getOsmId(),relation.getParentRelations());
      	count += updateTagSortedMap(relation.getOsmId(), relation.getTags());
        
        return count;
      }
      
      /**
       * This method returns a list of all relations stored in the database and creates corresponding 
       * {@link Relation} objects.
       * 
       * @return a list of relations
       */
      public List<Relation> getAllRelation(){
    	List<Relation> relations = new ArrayList<Relation>();
      	
      	SQLiteDatabase db = getReadableDatabase();
      	Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATION, null);
      	
      	List<RelationMember> allMembers = getAllRelationMember();
      	List<RelationMember> theseMembers = new ArrayList<RelationMember>();
        	
        	if(cursor.moveToFirst()){
        		do{
        			for(RelationMember rm : allMembers){ 	      		
                  		if(Long.parseLong(cursor.getString(1)) == rm.getRef()){
                  			theseMembers.add(rm);
                  		}      				
              	}
        			Relation relation = new Relation(Long.parseLong(cursor.getString(0)), getOsmElementOsmVersion(Long.parseLong(cursor.getString(0)))); 
        			relation.addMembers(theseMembers, false);
        			relation.setTags(getTagSortedMap(relation.getOsmId()));
        	    	relation.addParentRelations(getParentRelationList(relation.getOsmId()));
        			relations.add(relation);
        		}
        		while (cursor.moveToNext());
        	}
        	
        	db.close();
        	return relations;
      }
      
      //-------------------------------------------------------------------------
      
    // RELATIONMEMBER CRUD
      
      /**
       * This method creates and stores a new relation member in the database. The data is taken from the 
       * {@link RelationMember} object that is passed to the method.
       * 
       * @param relationMember the {@link RelationMember} object from which the data will be taken
       */
      public void createRelationMember(RelationMember relationMember){ 
    	  SQLiteDatabase db = getWritableDatabase();
        	
          ContentValues values = new ContentValues();
          	
          values.put(KEY_REF, relationMember.getRef());
          values.put(KEY_TYPE, relationMember.getType());
          values.put(KEY_ROLE, relationMember.getRole());
          	
          db.insert(TABLE_RELATIONMEMBER, null, values);
            	
          db.close();
      }
      
      /**
       * This method returns the data for a specific relation member stored in the database and creates the 
       * corresponding {@link RelationMember} object.
       * 
       * @param ref the reference to the desired relation member
       * @return a {@link RelationMember} object for the desired relation member
       */
      public RelationMember getRelationMember (long ref){ 
    	SQLiteDatabase db = getReadableDatabase();
      	
        Cursor cursor = db.query(TABLE_RELATIONMEMBER, new String[]{KEY_REF, KEY_TYPE, KEY_ROLE}, 
        			KEY_REF + "=?", new String[]{String.valueOf(ref)}, null, null, null, null);
        	
        if(cursor != null)
         cursor.moveToFirst();
        	
        	RelationMember relationMember  = new RelationMember(cursor.getString(1), 
        			Long.parseLong(cursor.getString(0)), cursor.getString(2));
        	
        	cursor.close();
        	db.close();
        return relationMember;
      }
      
      /**
       * This method deletes a specific relation member from the database.
       * 
       * @param relationMember the {@link RelationMember} object whose data should be deleted
       */
      public void deleteRelationMember(RelationMember relationMember){
    	SQLiteDatabase db = getWritableDatabase();
	      	
        db.delete(TABLE_RELATIONMEMBER, KEY_REF + "=?", new String[]{String.valueOf(relationMember.getRef())});
        	
        db.close();
      }
      
      /**
       * This method deletes a specific relation member from the database by the refId.
       * 
       * @param refId the ref of an RelationMember {@link RelationMember} object whose data should be deleted
       */
      
      private void deleteRelationMemberByRefId(long refId){
      	SQLiteDatabase db = getWritableDatabase();
  	      	
          db.delete(TABLE_RELATIONMEMBER, KEY_REF + "=?", new String[]{String.valueOf(refId)});
          	
          db.close();
        }
      
      /**
       * This method returns the number of relation members currently stored in the database.
       * 
       * @return the number of relation members
       */
      public int getRelationMemberCount(){
    	SQLiteDatabase db = getReadableDatabase();
        	
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATIONMEMBER, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        	
        return count;
      }
      
      /**
       * This method updates the data for a specific relation member stored in the database.
       * 
       * @param relationMember the {@link RelationMember} object for which the data should be updated
       * @return the number of rows that have been updated
       */
      public int updateRelationMember(RelationMember relationMember){ 
    	  SQLiteDatabase db = getWritableDatabase();
      	
          ContentValues values = new ContentValues();
          	
          values.put(KEY_REF, relationMember.getRef());
          values.put(KEY_TYPE, relationMember.getType());
          values.put(KEY_ROLE, relationMember.getRole());
         	
//        db.close();
          return db.update(TABLE_RELATIONMEMBER, values, KEY_REF + "=?", 
        		  new String[]{String.valueOf(relationMember.getRef())});
      }
      
      /**
       * This method returns a list of all relation members stored in the database and creates corresponding 
       * {@link RelationMember} objects.
       * 
       * @return a list of relation members
       */
      public List<RelationMember> getAllRelationMember(){
    	List<RelationMember> relationMembers = new ArrayList<RelationMember>();
        	
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATIONMEMBER, null);
        	
        if(cursor.moveToFirst()){
        	do{
        		RelationMember relationMember = new RelationMember(cursor.getString(1), 
            			Long.parseLong(cursor.getString(0)), cursor.getString(2));
        		relationMembers.add(relationMember);
        	}
        	while (cursor.moveToNext());
        }
        	
        db.close();
        return relationMembers;
      }
      
      //---Hilfsfunktionen----------------------------------------------------------------
      /**
       * This method creates and stores a new OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param osmVersion the version of the OsmElement object
       * 
       */      
      private void createOsmElement (long osmId, long osmVersion){
    	SQLiteDatabase db = getWritableDatabase();
        	
        ContentValues values = new ContentValues();
      	
      	values.put(KEY_OSMID, osmId);
      	values.put(KEY_OSMVERSION, osmVersion);
      	
      	db.insert(TABLE_OSMELEMENT, null, values);
        	
        db.close();
      }
      
      /**
       * This method creates and stores the ParentRelationList of an OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param parentRelation the List of Parent Relations of the OsmElement object
       * 
       */       
      private void createParentRelation (long osmId, List<Relation> parentRelations){
      	SQLiteDatabase db = getWritableDatabase();
          	
          ContentValues values = new ContentValues();
        	
        	values.put(KEY_OSMID, osmId);
        	
        	for(Relation r : parentRelations){
        		values.put(KEY_RELATIONID, r.getOsmId());
        		db.insert(TABLE_OSMPARENTLIST, null, values);
        	}
          db.close();
        }
      
      /**
       * This method creates and stores the SortedMap of Tags of an OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param tags the SortedMap of tags of the OsmElement object
       * 
       */  
      private void createTagSortedMap (long osmId, SortedMap<String, String> tags){
      	SQLiteDatabase db = getWritableDatabase();
          	
          ContentValues values = new ContentValues();
        	
        	values.put(KEY_OSMID, osmId);
        	
        	for(String key : tags.keySet()){
        		values.put(KEY_KEY, key);
        		values.put(KEY_VALUE, tags.get(key));
        		db.insert(TABLE_OSMTAGMAP, null, values);
        	}
          	
          db.close();
        }
      
      /**
       * This method updates and stores a new OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param osmVersion the version of the OsmElement object
       * @return the number of rows that have been updated
       */ 
      private int updateOsmElement (long osmId, long osmVersion){
    	SQLiteDatabase db = getWritableDatabase();
        	
        ContentValues values = new ContentValues();
      	
      	values.put(KEY_OSMID, osmId);
      	values.put(KEY_OSMVERSION, osmVersion);
      	
      	int count = db.update(TABLE_OSMELEMENT, values, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
        	
        db.close();
        return count;
      }
      
      /**
       * This method updates and stores the ParentRelationList of an OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param parentRelation the List of Parent Relations of the OsmElement object
       * @return the number of rows that have been updated
       */
      private int updateParentRelation (long osmId, List<Relation> parentRelations){
      	SQLiteDatabase db = getWritableDatabase();
          	
          ContentValues values = new ContentValues();
        	
        	values.put(KEY_OSMID, osmId);
        	int count = 0;
        	for(Relation r : parentRelations){
        		values.put(KEY_RELATIONID, r.getOsmId());
        		count += db.update(TABLE_OSMPARENTLIST, values, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
        	}
          db.close();
          return count;
        }
      
      /**
       * This method updates and stores the SortedMap of Tags of an OsmElement in the database. 
       * 
       * @param osmId the id of the OsmElement object
       * @param tags the SortedMap of tags of the OsmElement object
       * @return the number of rows that have been updated
       */
      private int updateTagSortedMap (long osmId, SortedMap<String, String> tags){
      	SQLiteDatabase db = getWritableDatabase();
          	
          ContentValues values = new ContentValues();
        	
        	values.put(KEY_OSMID, osmId);
        	int count = 0;
        	for(String key : tags.keySet()){
        		values.put(KEY_KEY, key);
        		values.put(KEY_VALUE, tags.get(key));
        		count += db.update(TABLE_OSMTAGMAP, values, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
        	}
          	
          db.close();
          
          return count;
        }
      
      /**
       * This method deletes a specific OsmElement from the database.
       * 
       * @param osmId the id of an OsmElement object whose data should be deleted
       */
      private void deleteOsmElement (long osmId){
      	SQLiteDatabase db = getWritableDatabase();
  	      	
          db.delete(TABLE_OSMELEMENT, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
          	
          db.close();
        }
      
      /**
       * This method deletes a specific ParentRelationList of an OsmElement from the database.
       * 
       * @param osmId the id of the OsmElement object whose List data should be deleted
       */
      private void deleteParentRelationList (long osmId){
        	SQLiteDatabase db = getWritableDatabase();
    	      	
            db.delete(TABLE_OSMPARENTLIST, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
            	
            db.close();
          }
      
      /**
       * This method deletes a specific TagSortedMap of an OsmElement from the database.
       * 
       * @param osmId the id of the OsmElement object whose Map data should be deleted
       */
      private void deleteTagSortedMap (long osmId){
        	SQLiteDatabase db = getWritableDatabase();
    	      	
            db.delete(TABLE_OSMTAGMAP, KEY_OSMID + "=?", new String[]{String.valueOf(osmId)});
            	
            db.close();
          }
      
      /**
       * This method deletes a specific Node in an Way from the database.
       * 
       * @param nodeId the id of Node object in an Way whose data should be deleted
       */
      private void deleteNodeInWay (long nodeId){
        	SQLiteDatabase db = getWritableDatabase();
    	      	
            db.delete(TABLE_WAY, KEY_NODEID + "=?", new String[]{String.valueOf(nodeId)});
            	
            db.close();
          }      
      
      /**
       * This method returns the data for a specific way stored in the database and creates the corresponding 
       * List object.
       * 
       * @param osmId the id of the OsmElement
       * @return a List object for the OsmElement
       */
      private List<Relation> getParentRelationList (long osmId){
    	  List<Relation> parentRelation= new ArrayList<Relation> ();
    	  
    	  SQLiteDatabase db = getReadableDatabase();
          Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_OSMPARENTLIST, null);
          db.query(TABLE_OSMPARENTLIST, new String[]{KEY_OSMID, KEY_RELATIONID}, 
      			KEY_OSMID + "=?", new String[]{String.valueOf(osmId)}, null, null, null, null);
          	
          if(cursor.moveToFirst()){
          	do{
          		Relation relation = getRelation(Long.parseLong(cursor.getString(1)));
          		parentRelation.add(relation);
          	}
          	while (cursor.moveToNext());
          }
          
          cursor.close();	
          db.close();
    	  
    	  return parentRelation;
      }
      
      /**
       * This method returns the data for a specific way stored in the database and creates the corresponding 
       * Map object.
       * 
       * @param osmId the id of the OsmElement
       * @return a Map object for the OsmElement
       */
      private SortedMap<String, String> getTagSortedMap (long osmId){
    	  SortedMap<String, String> tagMap= new TreeMap<String, String> ();
    	  
    	  SQLiteDatabase db = getReadableDatabase();
          Cursor cursor = db.query(TABLE_OSMTAGMAP, new String[]{KEY_OSMID, KEY_KEY, KEY_VALUE}, 
        			KEY_OSMID + "=?", new String[]{String.valueOf(osmId)}, null, null, null, null);
          	
          if(cursor.moveToFirst()){
          	do{
          		tagMap.put(cursor.getString(1),cursor.getString(2));
          	}
          	while (cursor.moveToNext());
          }
          
          cursor.close();
          db.close();
    	  return tagMap;
      }
       	  
      /**
       * This method returns the data for a specific way stored in the database and returns the version of an OsmElement.
       * 
       * @param osmId the id of the OsmElement
       * @return the version of the OsmElement
       */
      private long getOsmElementOsmVersion (long osmId){
    	  SQLiteDatabase db = getReadableDatabase();
        	
      	Cursor cursor = db.query(TABLE_OSMELEMENT, new String[]{KEY_OSMID, KEY_OSMVERSION}, 
      			KEY_OSMID + "=?", new String[]{String.valueOf(osmId)}, null, null, null, null);
      	
      	if(cursor != null)
      		cursor.moveToFirst();
      	
      	Long version = Long.parseLong(cursor.getString(1));
      	
      	cursor.close();
      	db.close();
      	return version;
      }
}