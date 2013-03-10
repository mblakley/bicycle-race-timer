package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class SeriesRaceIndividualResults extends ContentProviderTable implements BaseColumns {
    
    private static final SeriesRaceIndividualResults instance = new SeriesRaceIndividualResults();
    
    public SeriesRaceIndividualResults() {}
 
    public static SeriesRaceIndividualResults Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String RacerSeriesInfo_ID = "RacerSeriesInfo_ID";
    public static final String RaceResult_ID = "RaceResult_ID";
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null, "         	        
    	        + RacerSeriesInfo_ID + " integer references " + RacerSeriesInfo.Instance().getTableName() + "(" + RacerSeriesInfo._ID + ") not null, "
    	        + RaceResult_ID + " integer references " + RaceResults.Instance().getTableName() + "(" + RaceResults._ID + ") not null"
    	        + ");";
    }

	public Uri Create(Context context, long race_ID, long racerSeriesInfo_ID, long raceCategory_ID, Long raceResult_ID) {
		ContentValues content = new ContentValues();
     	content.put(SeriesRaceIndividualResults.Race_ID, race_ID);
     	content.put(SeriesRaceIndividualResults.RacerSeriesInfo_ID, racerSeriesInfo_ID);
     	content.put(SeriesRaceIndividualResults.RaceResult_ID, raceResult_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}