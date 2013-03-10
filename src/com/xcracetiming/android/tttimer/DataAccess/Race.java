package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoView;

// BaseColumn contains _id.
public final class Race extends ContentProviderTable implements BaseColumns {
    
    private static final Race instance = new Race();
    
    public Race() {}
 
    public static Race Instance() {
        return instance;
    }

    // Table column
    public static final String RaceDate = "RaceDate";
    public static final String RaceLocation_ID = "RaceLocation_ID";
    public static final String RaceType_ID = "RaceType_ID";
    public static final String RaceStartTime = "RaceStartTime";
    public static final String StartInterval = "StartInterval";
    public static final String EventName = "EventName";
    public static final String USACEventID = "USACEventID";
    public static final String ScoringType = "ScoringType";
    
    public String getCreate(){
    	return "create table " + Race.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceDate + " integer not null, " 
    	        + RaceLocation_ID + " integer references " + RaceLocation.Instance().getTableName() + "(" + RaceLocation._ID + ") not null,"
    	        + RaceType_ID + " integer references " + RaceType.Instance().getTableName() + "(" + RaceType._ID + ") not null,"
    	        + RaceStartTime + " integer null,"
    	        + StartInterval + " integer null,"
    	        + EventName + " text not null,"
	        	+ USACEventID + " integer null,"
	        	+ ScoringType + " text not null);";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	urisToNotify.add(RaceInfoView.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }

	public Uri Create(Context context, long raceLocation, Date raceDate, Long raceStartTime, long raceType_ID, Long startTimeOffset, String eventName, Long USACEventID, String scoringType) {
		ContentValues content = new ContentValues();
		content.put(Race.RaceLocation_ID, raceLocation);
		content.put(Race.RaceDate, raceDate.getTime());
		content.put(Race.RaceStartTime, raceStartTime);
		content.put(Race.RaceType_ID, raceType_ID);
		content.put(Race.StartInterval, startTimeOffset);
		content.put(Race.EventName, eventName);
		content.put(Race.USACEventID, USACEventID);
		content.put(Race.ScoringType, scoringType);

     	return context.getContentResolver().insert(Race.Instance().CONTENT_URI, content);
	}

	public int Update(Context context, String where, String[] selectionArgs, Long race_ID, Long raceLocation_ID, Date raceDate, Long raceStartTime, Long raceType_ID, Long startTimeOffset, String eventName, Long USACEventID, String raceDiscipline, String scoringType) {
		ContentValues content = new ContentValues();
		if(raceLocation_ID != null)
        {
			content.put(Race.RaceLocation_ID, raceLocation_ID);
        }
        if(raceDate != null)
        {
        	content.put(Race.RaceDate, raceDate.getTime());
        }
        if(raceStartTime != null)
        {
        	content.put(Race.RaceStartTime, raceStartTime);
        }
        if(raceType_ID != null)
        {
        	content.put(Race.RaceType_ID, raceType_ID);
        }
        if(startTimeOffset != null)
        {
        	content.put(Race.StartInterval, startTimeOffset);
        }
        if(eventName != null)
        {
        	content.put(Race.EventName, eventName);
        }
        if(USACEventID != null)
        {
        	content.put(Race.USACEventID, USACEventID);
        } 
        if(scoringType != null)
        {
        	content.put(Race.ScoringType, scoringType);
        }
		return context.getContentResolver().update(Race.Instance().CONTENT_URI, content, where, selectionArgs);
	}

	public static Hashtable<String, Object> getValues(Context context, Long race_ID) {
		Hashtable<String, Object> raceValues = new Hashtable<String, Object>();
		
		Cursor raceCursor = Race.Instance().Read(context, null, Race._ID + "=?", new String[]{Long.toString(race_ID)}, null);
		if(raceCursor != null && raceCursor.getCount() > 0){
			raceCursor.moveToFirst();
			raceValues.put(Race._ID, race_ID);
			raceValues.put(Race.RaceDate, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceDate)));
			raceValues.put(Race.RaceLocation_ID, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceLocation_ID)));
			raceValues.put(Race.RaceType_ID, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceType_ID)));
			raceValues.put(Race.RaceStartTime, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceStartTime)));
			raceValues.put(Race.StartInterval, raceCursor.getLong(raceCursor.getColumnIndex(Race.StartInterval)));
			raceValues.put(Race.EventName, raceCursor.getString(raceCursor.getColumnIndex(Race.EventName)));
			raceValues.put(Race.USACEventID, raceCursor.getLong(raceCursor.getColumnIndex(Race.USACEventID)));
			raceValues.put(Race.ScoringType, raceCursor.getString(raceCursor.getColumnIndex(Race.ScoringType)));
		}
		if( raceCursor != null){
			raceCursor.close();
			raceCursor = null;
		}
		
		return raceValues;
	}
}
