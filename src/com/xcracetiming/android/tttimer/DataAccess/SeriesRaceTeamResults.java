package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class SeriesRaceTeamResults extends ContentProviderTable implements BaseColumns {
    
    private static final SeriesRaceTeamResults instance = new SeriesRaceTeamResults();
    
    public SeriesRaceTeamResults() {}
 
    public static SeriesRaceTeamResults Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String TeamInfo_ID = "TeamInfo_ID";
    public static final String RaceResult_ID = "RaceResult_ID";
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null, " 
    	        + TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") not null, " 
    	        + RaceResult_ID + " integer references " + RaceResults.Instance().getTableName() + "(" + RaceResults._ID + ") not null"
    	        + ");";
    }

	public Uri Create(Context context, long race_ID, long teamInfo_ID, Long raceResult_ID) {
		ContentValues content = new ContentValues();
     	content.put(SeriesRaceTeamResults.Race_ID, race_ID);
     	content.put(SeriesRaceTeamResults.TeamInfo_ID, teamInfo_ID);
     	content.put(SeriesRaceTeamResults.RaceResult_ID, raceResult_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}