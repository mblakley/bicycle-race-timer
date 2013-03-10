package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

public class RaceSeriesRaces extends ContentProviderTable implements BaseColumns {
	private static final RaceSeriesRaces instance = new RaceSeriesRaces();
    
    public RaceSeriesRaces() {}
 
    public static RaceSeriesRaces Instance() {
        return instance;
    }

    // Table column
    public static final String RaceSeries_ID = "RaceSeries_ID";
    public static final String Race_ID = "Race_ID";      
    
    public String getCreate(){
    	return "create table " + RaceSeriesRaces.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceSeries_ID + " integer references " + RaceSeries.Instance().getTableName() + "(" + RaceSeries._ID + ") not null, "
    	        + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null"
    	        + ");";
    }

    /**
     * Create a new RaceSeriesRaces record
     * @param context - The Context used to get the contentResolver that we call insert on
     * @param raceSeries_ID - The raceSeries_ID that this category is a part of
     * @param race_ID - The race foreign key
     * @return The URI containing the id of the newly added record
     */
	public Uri Create(Context context, long raceSeries_ID, long race_ID) {
		ContentValues content = new ContentValues();
		content.put(RaceSeriesRaces.RaceSeries_ID, raceSeries_ID);
		content.put(RaceSeriesRaces.Race_ID, race_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}
