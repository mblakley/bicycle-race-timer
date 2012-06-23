package com.gvccracing.android.tttimer.DataAccess;

import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;

public class RaceCP {

    // BaseColumn contains _id.
    public static final class Race implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, Race.class.getSimpleName() + "~");

        // Table column
        public static final String RaceDate = "RaceDate";
        public static final String RaceLocation_ID = "RaceLocation_ID";
        public static final String RaceType = "RaceType";
        public static final String RaceStartTime = "RaceStartTime";
        public static final String StartInterval = "StartInterval";
        public static final String NumLaps = "NumLaps";
        
        public static String getTableName(){
        	return Race.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + Race.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RaceDate + " integer not null, " 
        	        + RaceLocation_ID + " integer references " + RaceLocation.getTableName() + "(" + RaceLocation._ID + ") not null,"
        	        + RaceType + " integer not null,"
        	        + RaceStartTime + " integer null,"
        	        + NumLaps + " integer null,"
        	        + StartInterval + " integer null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{Race.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceInfoView.CONTENT_URI, RaceLocation.CONTENT_URI};
        }

		public static Uri Create(Context context, long raceLocation, Date raceDate, Long raceStartTime, long raceTypeID, Long startTimeOffset, long numLaps) {
			ContentValues content = new ContentValues();
			content.put(Race.RaceLocation_ID, raceLocation);
			content.put(Race.RaceDate, raceDate.getTime());
			content.put(Race.RaceStartTime, raceStartTime);
			content.put(Race.RaceType, raceTypeID);
			content.put(Race.NumLaps, numLaps);
			content.put(Race.StartInterval, startTimeOffset);

	     	return context.getContentResolver().insert(Race.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long race_ID, Long raceLocation_ID, Date raceDate, Long raceStartTime, Long raceTypeID, Long startTimeOffset, Long numLaps) {
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
	        if(raceTypeID != null)
	        {
	        	content.put(Race.RaceType, raceTypeID);
	        }
	        if(startTimeOffset != null)
	        {
	        	content.put(Race.StartInterval, startTimeOffset);
	        }
	        if(numLaps != null)
	        {
	        	content.put(Race.NumLaps, numLaps);
	        }
			return context.getContentResolver().update(Race.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(Race.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static Hashtable<String, Long> getValues(Context context, Long race_ID) {
			Hashtable<String, Long> raceValues = new Hashtable<String, Long>();
			
			Cursor raceCursor = Race.Read(context, null, Race._ID + "=?", new String[]{Long.toString(race_ID)}, null);
			if(raceCursor != null && raceCursor.getCount() > 0){
				raceCursor.moveToFirst();
				raceValues.put(Race._ID, race_ID);
				raceValues.put(Race.RaceDate, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceDate)));
				raceValues.put(Race.RaceLocation_ID, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceLocation_ID)));
				raceValues.put(Race.RaceType, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceType)));
				raceValues.put(Race.RaceStartTime, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceStartTime)));
				raceValues.put(Race.NumLaps, raceCursor.getLong(raceCursor.getColumnIndex(Race.NumLaps)));
				raceValues.put(Race.StartInterval, raceCursor.getLong(raceCursor.getColumnIndex(Race.StartInterval)));
			}
			if( raceCursor != null){
				raceCursor.close();
				raceCursor = null;
			}
			
			return raceValues;
		}
    }
}
