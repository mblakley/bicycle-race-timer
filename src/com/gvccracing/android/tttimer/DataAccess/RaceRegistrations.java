package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceRegistrations extends ContentProviderTable implements BaseColumns {
    
    private static final RaceRegistrations instance = new RaceRegistrations();
    
    public RaceRegistrations() {}
 
    public static RaceRegistrations Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String RacerUSACInfo_ID = "RacerUSACInfo_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";
    public static final String RaceResult_ID = "RaceResult_ID";
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " text not null, " 
    	        + RacerUSACInfo_ID + " integer not null, "
    	        + RaceCategory_ID + " integer not null, " 
    	        + RaceResult_ID + " integer not null"
    	        + ");";
    }

	public Uri Create(Context context, long race_ID, long racerUSACInfo_ID, long raceCategory_ID, Long raceResult_ID) {
		ContentValues content = new ContentValues();
     	content.put(RaceRegistrations.Race_ID, race_ID);
     	content.put(RaceRegistrations.RacerUSACInfo_ID, racerUSACInfo_ID);
     	content.put(RaceRegistrations.RaceCategory_ID, raceCategory_ID);
     	content.put(RaceRegistrations.RaceResult_ID, raceResult_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}