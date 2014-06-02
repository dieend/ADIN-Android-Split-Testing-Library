package com.dieend.adin.hermes;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MetricsDatabaseHelper extends SQLiteOpenHelper{
	 // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "MetricsManager";
    
    // Contacts table name
    private static final String TABLE_METRICS = "metrics";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EVENT = "event";
    private static final String KEY_PARAM = "parameters";
    private static final String KEY_TIME_START = "time_start";
    private static final String KEY_TIME_END = "time_end";
    
    public MetricsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_METRICS_TABLE = "CREATE TABLE " + TABLE_METRICS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT + " TEXT,"
                + KEY_PARAM + " TEXT," + KEY_TIME_START + " INTEGER," + KEY_TIME_END +" INTEGER )";
        db.execSQL(CREATE_METRICS_TABLE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_METRICS);
		 
        // Create tables again
        onCreate(db);
	}
	public long add(String key, JSONObject params, long timeStart, long timeEnd) {
		SQLiteDatabase db = getWritableDatabase();
	    ContentValues values = new ContentValues()
	    ;
	    values.put(KEY_EVENT, key);
	    values.put(KEY_PARAM, params.toString());
	    values.put(KEY_TIME_START, timeStart);
	    values.put(KEY_TIME_END, timeEnd);
	    long ret = db.insert(TABLE_METRICS, null, values);
	    return ret;
	}
	public void update(long id, String event, long timeEnd) {
		SQLiteDatabase db = getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put(KEY_TIME_END, timeEnd);
	    long affected = db.update(TABLE_METRICS, values , KEY_ID + "=" + id, null);
	    assert affected > 0;
	}

}
