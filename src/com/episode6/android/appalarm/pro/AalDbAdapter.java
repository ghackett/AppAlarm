package com.episode6.android.appalarm.pro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class AalDbAdapter {
	public static final String TABLE_NAME_ALARMS = "alarms";
	
	public static final String DATABASE_NAME = "dbE6AlarmAppChooser";
	public static final int DATABASE_VERSION = 12;
	public static final String[] DATABASE_DROP = {
		"DROP TABLE IF EXISTS " + TABLE_NAME_ALARMS
	};
	
	
	private final Context mCtx;
	
	private static final String TAG = "AalDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(SimplePropertyCollection.getCreateTableStatement(AlarmItem.ALARM_DEFAULTS_ALL, TABLE_NAME_ALARMS));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            for (int i = 0; i < DATABASE_DROP.length; i++) {
        		db.execSQL(DATABASE_DROP[i]);
        	}
            onCreate(db);
        }
    }

    public AalDbAdapter(Context ctx) {
    	this.mCtx = ctx;
    }
    
    public AalDbAdapter open() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public Cursor fetchAllAlarms() {
    	return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_LIST), null, null, null, null, AlarmItem.KEY_ROWID);
    }
    
    public Cursor fetchAllAlarmsForBackup() {
    	return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_ALL), null, null, null, null, AlarmItem.KEY_ROWID);
    }

    public Cursor fetchEnabledAlarms() {
    	return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_LIST), AlarmItem.KEY_ENABLED+"=1", null, null, null, AlarmItem.KEY_ROWID);
    }
    
    public AlarmItem getAlarmById(long id) {
    	if (id == 0) {
    		return new AlarmItem();
    	} 
    	
    	Cursor cur = mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_ALL), AlarmItem.KEY_ROWID+"="+id, null, null, null, AlarmItem.KEY_ROWID);
    	
    	if (cur == null) {
    		return new AlarmItem();
    	}
    	
		cur.moveToFirst();
		if (cur.isAfterLast()) {
			cur.close();
			return new AlarmItem();
		} else {
			AlarmItem ai = new AlarmItem(cur);
			cur.close();
			return ai;
		}
    }
    
    public AlarmItem getNewAlarm() {
    	return new AlarmItem();
    }
    
    public void saveAlarm(AlarmItem ai) {
    	ai.saveItem(mDb, TABLE_NAME_ALARMS, AlarmItem.KEY_ROWID);
    }
    
    public void setAlarmEnabled(long alarmId, boolean enabled) {
    	ContentValues cv = new ContentValues();
    	cv.put(AlarmItem.KEY_ENABLED, enabled);
    	mDb.update(TABLE_NAME_ALARMS, cv, AlarmItem.KEY_ROWID+"="+alarmId, null);
    }
    
    public void deleteAlarm(long alarm_id) {
    	mDb.delete(TABLE_NAME_ALARMS, AlarmItem.KEY_ROWID+"="+alarm_id, null);
    }   
    public void deleteAllAlarms() {
    	mDb.delete(TABLE_NAME_ALARMS, null, null);
    }
}
