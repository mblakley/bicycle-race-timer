package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceWave extends ContentProviderTable implements BaseColumns {
    
    private static final RaceWave instance = new RaceWave();
    
    public RaceWave() {}
 
    public static RaceWave Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";
    public static final String WaveStartTime = "WaveStartTime";
    public static final String NumLaps = "NumLaps";
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " integer not null, " 
    	        + RaceCategory_ID + " integer not null, " 
    	        + WaveStartTime + " integer not null,"
    	        + NumLaps + " integer not null"
    	        + ");";
    }

	public Uri Create(Context context, long race_ID, long raceCategory_ID, long waveStartTime, long numLaps) {
		ContentValues content = new ContentValues();
     	content.put(RaceWave.Race_ID, race_ID);
     	content.put(RaceWave.RaceCategory_ID, raceCategory_ID);
     	content.put(RaceWave.WaveStartTime, waveStartTime);
     	content.put(RaceWave.NumLaps, numLaps);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}