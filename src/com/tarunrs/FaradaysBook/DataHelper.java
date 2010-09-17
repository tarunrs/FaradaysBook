package com.tarunrs.FaradaysBook;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {
	
	   private static final String DATABASE_NAME = "notes.db";
	   private static final int DATABASE_VERSION = 2;
	   private static final String TABLE_NAME = "notes";
	   private static final String TABLE_NAME_PENDING = "notes_pending";
	   private Context context;
	   private SQLiteDatabase db;
	   private SQLiteStatement insertStmt;
	   private SQLiteStatement insertPendingStmt;
	   private static final String INSERT = "insert into " + TABLE_NAME + "(user_id, body, modified_at, privacy, id) values (?, ?, ? , ? , ?)";
	   private static final String INSERT_PENDING = "insert into " + TABLE_NAME_PENDING + "( user_id, body, modified_at, privacy) values (?, ?, ? , ?)";
//	   private static final String INSERT = "insert into " + TABLE_NAME + "(body, privacy, modified_at) values (?, ?, ?)";
	   
	   public DataHelper(Context context) {
	      this.context = context;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	      this.insertPendingStmt = this.db.compileStatement(INSERT_PENDING);
	   }
	   
	   public long insertToCache(Note note){
		   Log.d("insertToCache", note.body);
		   return insert(note, true);
	   }
	   
	   public long insertToAuth(Note note){
		   return insert(note, false);
	   }
	   
	   private long insert(Note note, boolean toCache) {
		  SQLiteStatement tmpStmt = null; 
		  Log.d("insert", note.body);
		  if(toCache == true)
			  tmpStmt =  this.insertPendingStmt;
		  else
			  tmpStmt = this.insertStmt;
			
		  tmpStmt.bindLong(1, note.user_id);
		  tmpStmt.bindString(2, note.body);
		  tmpStmt.bindString(3, note.modified_at);
	      if(note.privacy != null)
	    	  tmpStmt.bindString(4, note.privacy);
	      else
	    	  tmpStmt.bindString(4, "private");
	      
	      if(toCache == false){
			  tmpStmt.bindLong(5, note.id);  
		  }
    
	      try{
	    	  Log.d("insert", "trying");
	    	  return tmpStmt.executeInsert();
	      }
	      catch(Exception e){
	    	  Log.d("insert", "error");  
	    	  return -1;
	      }
	   }
	   
	   public void deleteAllCache() {
		   this.db.delete(TABLE_NAME_PENDING, null, null);
	   }
	   
	   public void deleteAllAuth() {
		   this.db.delete(TABLE_NAME, null, null);
	   }
	   
	   public void deleteAll() {
	      deleteAllAuth();
	      deleteAllCache();
	   }
	   
	   public List<Note> selectAllAuth() {
		   return selectAll(false);		   
	   }
	   
	   public List<Note> selectAllCache() {
		   return selectAll(true);		   
	   }
	   
	   private List<Note> selectAll(boolean fromCache ) {
	      List<Note> list = new ArrayList<Note>();
	      Note temp = null;
	      Cursor cursor = null;
	      if(fromCache == false)
	    	  cursor = this.db.query(TABLE_NAME, new String[] { "body", "modified_at" },null, null, null, null, "modified_at");
	      else
	    	  cursor = this.db.query(TABLE_NAME_PENDING, new String[] { "body" },null, null, null, null, null);
	      Log.d("SelectAll", "Selecting");
	      Log.d("Cursor", cursor.toString());
	      Log.d("Cursor", String.valueOf(cursor.moveToFirst()));
	      if (cursor.moveToFirst()) {
	         do {
	        	 if(fromCache == false)
	        		 temp = new Note(cursor.getString(0), cursor.getString(1));
	        	 else
	        	 {
	        		 temp = new Note();
	        		 temp.body = cursor.getString(0);
	        	 }
	        	Log.d("SelectAuth", temp.body);
	            list.add(temp);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      Log.d("SelectAll", "Selected");
	      return list;
	   }
	   
	   private static class OpenHelper extends SQLiteOpenHelper {
	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
	      @Override
	      public void onCreate(SQLiteDatabase db) {
	    	 Log.e("db","onCreate");
	         db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, user_id INTEGER, body TEXT, modified_at TEXT, privacy TEXT)");
	         db.execSQL("CREATE TABLE " + TABLE_NAME_PENDING + "(user_id INTEGER, body TEXT, modified_at TEXT, privacy TEXT)");
	         Log.e("db","onCreate finish");
	      }
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PENDING);
	         onCreate(db);
	      }
	   }
	   
	}