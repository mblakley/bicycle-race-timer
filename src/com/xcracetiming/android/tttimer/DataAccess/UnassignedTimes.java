package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class UnassignedTimes extends ContentProviderTable implements BaseColumns {
    
    private static final UnassignedTimes instance = new UnassignedTimes();
    
    public UnassignedTimes() {}
 
    public static UnassignedTimes Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String FinishTime = "FinishTime";
    public static final String RaceResult_ID = "RaceResult_ID";
    
    public String getCreate(){
    	return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null," 
                + FinishTime + " integer not null,"
                + RaceResult_ID + " integer references " + RaceResults.Instance().getTableName() + "(" + RaceResults._ID + ") null"
                + ");";
    }
	
	public Uri Create(Context context, long race_ID, long finishTime, Long raceResult_ID) {
		ContentValues content = new ContentValues();
		content.put(UnassignedTimes.Race_ID, race_ID);
		content.put(UnassignedTimes.FinishTime, finishTime);
		content.put(UnassignedTimes.RaceResult_ID, raceResult_ID);

     	return context.getContentResolver().insert(UnassignedTimes.Instance().CONTENT_URI, content);
	}
	
	public int Update(Context context, long unassignedTime_ID, Long race_ID, Long finishTime, Long raceResult_ID) {
		ContentValues content = new ContentValues();
		if(race_ID != null)
        {
        	content.put(UnassignedTimes.Race_ID, race_ID);
        }
        if(finishTime != null)
        {
        	content.put(UnassignedTimes.FinishTime, finishTime);
        }
        if(raceResult_ID != null)
        {
    		content.put(UnassignedTimes.RaceResult_ID, raceResult_ID);
        }
		
		return Update(context, content, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});
	}
}
