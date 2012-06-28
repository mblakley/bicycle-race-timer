package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class UnassignedTimesCP {

    // BaseColumn contains _id.
    public static final class UnassignedTimes implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, UnassignedTimes.class.getSimpleName() + "~");

        // Table column
        public static final String Race_ID = "Race_ID";
        public static final String FinishTime = "FinishTime";
        public static final String RaceResult_ID = "RaceResult_ID";
        
        public static String getTableName(){
        	return UnassignedTimes.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + UnassignedTimes.getTableName()
                    + " (" + _ID + " integer primary key autoincrement, "
                    + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null," 
                    + FinishTime + " integer not null"
                    + RaceResult_ID + " integer references " + RaceResults.getTableName() + "(" + RaceResults._ID + ") null"
                    + ");";
        }

		public static Uri[] getAllUrisToNotifyOnChange() {
			return new Uri[]{UnassignedTimes.CONTENT_URI};
		}
		
		public static Uri Create(Context context, long race_ID, long finishTime, Long raceResult_ID) {
			ContentValues content = new ContentValues();
			content.put(UnassignedTimes.Race_ID, race_ID);
			content.put(UnassignedTimes.FinishTime, finishTime);
			content.put(UnassignedTimes.RaceResult_ID, raceResult_ID);

	     	return context.getContentResolver().insert(UnassignedTimes.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(UnassignedTimes.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}		
		
		public static int Update(Context context, long unassignedTime_ID, Long race_ID, Long finishTime, Long raceResult_ID) {
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
			
			return UnassignedTimes.Update(context, content, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});
		}

		public static int Update(Context context, ContentValues content, String where, String[] selectionArgs) {
			return context.getContentResolver().update(UnassignedTimes.CONTENT_URI, content, where, selectionArgs);
		}

		public static int Delete(Context context, String selection, String[] selectionArgs) {
			return context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, selection, selectionArgs);
		}
    }
}
