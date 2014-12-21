/**
 * 
 */
package io.github.data4all.model;


import java.util.List;

import io.github.data4all.model.data.BoundingBox;
import io.github.data4all.model.data.Node;
import io.github.data4all.model.data.Relation;
import io.github.data4all.model.data.RelationMember;
import io.github.data4all.model.data.User;
import io.github.data4all.model.data.Way;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Richard Rohde
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
    
    public void createBoundingBox(BoundingBox boundingBox){
    	SQLiteDatabase db = getWritableDatabase();
    	
    	//TODO:
    	
    	db.close();
    }
    public BoundingBox getBoundingBox (int value){ //evtl. für BoundingBox noch ne id hinzufügen
    	SQLiteDatabase db = getReadableDatabase();
    	
    	//TODO:
    	
    	db.close();
    	return null;
    }
    
    public void deleteBoundingBox(BoundingBox boundingBox){
    	SQLiteDatabase db = getWritableDatabase();
    	
    	//TODO:
    	
    	db.close();
    }
    
    public int getBoundingBoxCount(){
    	SQLiteDatabase db = getReadableDatabase();
    	
    	//TODO:
    	
    	db.close();
    	return 0;
    }
    
    public int updateBoundingBox(BoundingBox boundingBox){
    	SQLiteDatabase db = getWritableDatabase();
    	
    	//TODO:
    	
    	db.close();
    	return 0;
    }
    
    public List<BoundingBox> getAllBoundingBox(){
    	SQLiteDatabase db = getReadableDatabase();
    	
    	//TODO:
    	
    	db.close();
    	return null;
    }
    
    //-------------------------------------------------------------------------
    // USER CRUD
      
      public void createUser(User user){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      public User getUser (String Username){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      public void deleteUser(User user){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      
      public int getUserCount(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public int updateUser(User user){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public List<User> getAllUser(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      //-------------------------------------------------------------------------
    // NODE CRUD
      
      public void createNode(Node node){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      public Node getNode (int id){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      public void deleteNode(Node node){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      
      public int getNodeCount(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public int updateNode(Node node){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public List<Node> getAllNode(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      //-------------------------------------------------------------------------
    // WAY CRUD
      
      public void createWay(Way way){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      public Way getWay (int id){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      public void deleteWay(Way way){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      
      public int getWayCount(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public int updateWay(Way way){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public List<Way> getAllWay(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      //-------------------------------------------------------------------------
    // RELATION CRUD
      
      public void createRelation(Relation relation){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      public Relation getRelation (int id){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      public void deleteRelation(Relation relation){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      
      public int getRelationCount(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public int updateRelation(Relation relation){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public List<Relation> getAllRelation(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      //-------------------------------------------------------------------------
      
    // RELATIONMEMBER CRUD
      
      public void createRelationMember(RelationMember relationMember){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      public Relation getRelationMember (int ref){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      public void deleteRelationMember(RelationMember relationMember){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      }
      
      public int getRelationMemberCount(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public int updateRelationMember(RelationMember relationMember){
      	SQLiteDatabase db = getWritableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return 0;
      }
      
      public List<Relation> getAllRelationMember(){
      	SQLiteDatabase db = getReadableDatabase();
      	
      	//TODO:
      	
      	db.close();
      	return null;
      }
      
      //-------------------------------------------------------------------------
}




