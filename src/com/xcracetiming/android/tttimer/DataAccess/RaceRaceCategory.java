package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceRaceCategory extends ContentProviderTable implements BaseColumns {
    
    private static final RaceRaceCategory instance = new RaceRaceCategory();
    
    public RaceRaceCategory() {}
 
    public static RaceRaceCategory Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";      
    
    public String getCreate(){
    	return "create table " + RaceRaceCategory.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null, "
    	        + RaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null"
    	        + ");";
    }

    /**
     * Create a new RaceRaceCategory record
     * @param context - The Context used to get the contentResolver that we call insert on
     * @param race_ID - The race_ID that this category is a part of
     * @param raceCategory_ID - The race category foreign key
     * @return The URI containing the id of the newly added record
     */
	public Uri Create(Context context, long race_ID, long raceCategory_ID) {
		ContentValues content = new ContentValues();
		content.put(RaceRaceCategory.Race_ID, race_ID);
		content.put(RaceRaceCategory.RaceCategory_ID, raceCategory_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}
