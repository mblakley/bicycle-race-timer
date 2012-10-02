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
import com.gvccracing.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;

public class RaceCP {

    // BaseColumn contains _id.
    public static final class Race implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, Race.class.getSimpleName() + "~");

        // Table column
        public static final String RaceMeet_ID = "RaceMeet_ID";
        public static final String Gender = "Gender";
        public static final String Category = "Category";
        public static final String RaceStartTime = "RaceStartTime";
        public static final String NumSplits = "NumSplits";
        public static final String Distance = "Distance";
        
        public static String getTableName(){
        	return Race.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + Race.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RaceMeet_ID + " integer references " + RaceMeet.getTableName() + "(" + RaceMeet._ID + ") not null,"
        	        + Gender + " text not null,"
        	        + Category + " text not null,"
        	        + RaceStartTime + " integer null,"
        	        + Distance + " integer not null,"
        	        + NumSplits + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{Race.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceInfoView.CONTENT_URI, RaceLocation.CONTENT_URI};
        }

		public static Uri Create(Context context, long raceMeet_ID, Date raceStartTime, String gender, String category, float distance, long numSplits) {
			ContentValues content = new ContentValues();
			content.put(Race.RaceMeet_ID, raceMeet_ID);
			content.put(Race.RaceStartTime, raceStartTime.getTime());
			content.put(Race.Gender, gender);
			content.put(Race.NumSplits, numSplits);
			content.put(Race.Distance, distance);

	     	return context.getContentResolver().insert(Race.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long raceMeet_ID, Date raceStartTime, String gender, String category, Float distance, Long numSplits) {
			ContentValues content = new ContentValues();
			if(raceMeet_ID != null)
	        {
				content.put(Race.RaceMeet_ID, raceMeet_ID);
	        }
	        if(raceStartTime != null)
	        {
	        	content.put(Race.RaceStartTime, raceStartTime.getTime());
	        }
	        if(gender != null)
	        {
	        	content.put(Race.Gender, gender);
	        }
	        if(category != null)
	        {
	        	content.put(Race.Category, category);
	        }
	        if(distance != null)
	        {
	        	content.put(Race.Distance, distance);
	        }
	        if(numSplits != null)
	        {
	        	content.put(Race.NumSplits, numSplits);
	        }
			return context.getContentResolver().update(Race.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(Race.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static Hashtable<String, Object> getValues(Context context, Long race_ID) {
			Hashtable<String, Object> raceValues = new Hashtable<String, Object>();
			
			Cursor raceCursor = Race.Read(context, null, Race._ID + "=?", new String[]{Long.toString(race_ID)}, null);
			if(raceCursor != null && raceCursor.getCount() > 0){
				raceCursor.moveToFirst();
				raceValues.put(Race._ID, race_ID);
				raceValues.put(Race.RaceMeet_ID, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceMeet_ID)));
				raceValues.put(Race.Gender, raceCursor.getString(raceCursor.getColumnIndex(Race.Gender)));
				raceValues.put(Race.Category, raceCursor.getString(raceCursor.getColumnIndex(Race.Category)));
				raceValues.put(Race.RaceStartTime, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceStartTime)));
				raceValues.put(Race.Distance, raceCursor.getLong(raceCursor.getColumnIndex(Race.Distance)));
				raceValues.put(Race.NumSplits, raceCursor.getLong(raceCursor.getColumnIndex(Race.NumSplits)));
			}
			if( raceCursor != null){
				raceCursor.close();
				raceCursor = null;
			}
			
			return raceValues;
		}
    }
}
