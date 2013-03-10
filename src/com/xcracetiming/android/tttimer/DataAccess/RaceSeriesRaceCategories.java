package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

public class RaceSeriesRaceCategories extends ContentProviderTable implements BaseColumns{
	private static final RaceSeriesRaceCategories instance = new RaceSeriesRaceCategories();
    
    public RaceSeriesRaceCategories() {}
 
    public static RaceSeriesRaceCategories Instance() {
        return instance;
    }

    // Table column
    public static final String RaceSeries_ID = "RaceSeries_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";      
    
    public String getCreate(){
    	return "create table " + RaceRaceCategory.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceSeries_ID + " integer references " + RaceSeries.Instance().getTableName() + "(" + RaceSeries._ID + ") not null, "
    	        + RaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null"
    	        + ");";
    }

    /**
     * Create a new RaceSeriesRaceCategories record
     * @param context - The Context used to get the contentResolver that we call insert on
     * @param raceSeries_ID - The raceSeries_ID that this category is a part of
     * @param raceCategory_ID - The race category foreign key
     * @return The URI containing the id of the newly added record
     */
	public Uri Create(Context context, long raceSeries_ID, long raceCategory_ID) {
		ContentValues content = new ContentValues();
		content.put(RaceSeriesRaceCategories.RaceSeries_ID, raceSeries_ID);
		content.put(RaceSeriesRaceCategories.RaceCategory_ID, raceCategory_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}
