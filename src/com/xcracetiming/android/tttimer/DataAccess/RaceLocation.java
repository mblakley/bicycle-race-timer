package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceLocation extends ContentProviderTable implements BaseColumns {
    
    private static final RaceLocation instance = new RaceLocation();
    
    public RaceLocation() {}
 
    public static RaceLocation Instance() {
        return instance;
    }

    // Table column
    public static final String CourseName = "CourseName";
    public static final String Distance = "Distance";
    public static final String DistanceUnit = "DistanceUnits";
    
    public String getTableName(){
    	return RaceLocation.class.getSimpleName();
    }
    
    public String getCreate(){
    	return "create table " + RaceLocation.Instance().getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + CourseName + " text not null, " 
    	        + Distance + " real not null,"
    	        + DistanceUnit + " text not null"
    	        + ");";
    }

	public Uri Create(Context context, String courseName, String distance) {
		ContentValues content = new ContentValues();
     	content.put(RaceLocation.CourseName, courseName);
     	content.put(RaceLocation.Distance, distance);

     	return context.getContentResolver().insert(RaceLocation.Instance().CONTENT_URI, content);
	}
	
	public int Update(Context context, long raceLocation_ID, String courseName, String distance) {
		ContentValues content = new ContentValues();
		if(courseName != null)
        {
			content.put(RaceLocation.CourseName, courseName);
        }
		if(distance != null)
        {
			content.put(RaceLocation.Distance, distance);
        }
		return context.getContentResolver().update(RaceLocation.Instance().CONTENT_URI, content, RaceLocation._ID + "=?", new String[]{Long.toString(raceLocation_ID)});
	}
}