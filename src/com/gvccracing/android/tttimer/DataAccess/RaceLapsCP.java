package com.gvccracing.android.tttimer.DataAccess;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceLapsInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembersCP.TeamMembers;

public class RaceLapsCP {

    // BaseColumn contains _id.
    public static final class RaceLaps implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceLaps.class.getSimpleName() + "~");

        // Table column
        public static final String RaceResult_ID = "RaceResult_ID";
        public static final String LapNumber = "LapNumber";
        public static final String StartTime = "StartTime";
        public static final String FinishTime = "FinishTime";
        public static final String ElapsedTime = "ElapsedTime";
        
        public static String getTableName(){
        	return RaceLaps.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceLaps.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RaceResult_ID + " integer references " + RaceResults.getTableName() + "(" + RaceResults._ID + ") not null, " 
        	        + LapNumber + " integer not null,"
        	        + StartTime + " integer not null,"
        	        + FinishTime + " integer null,"
        	        + ElapsedTime + " integer null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceLaps.CONTENT_URI, RaceLapsInfoView.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceInfoView.CONTENT_URI, RaceLocation.CONTENT_URI, TeamLaps.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI};
        }

		public static Uri Create(Context context, long raceResult_ID, long lapNumber, long raceStartTime, long raceFinishTime, long elapsedTime) {
			ContentValues content = new ContentValues();
			content.put(RaceLaps.RaceResult_ID, raceResult_ID);
			content.put(RaceLaps.LapNumber, lapNumber);
			content.put(RaceLaps.StartTime, raceStartTime);
			content.put(RaceLaps.FinishTime, raceFinishTime);
			content.put(RaceLaps.ElapsedTime, elapsedTime);

	     	return context.getContentResolver().insert(RaceLaps.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long raceResult_ID, Long lapNumber, Long raceStartTime, Long raceFinishTime, Long raceElapsedTime) {
			ContentValues content = new ContentValues();
			if(raceResult_ID != null)
	        {
				content.put(RaceLaps.RaceResult_ID, raceResult_ID);
	        }
	        if(lapNumber != null)
	        {
	        	content.put(RaceLaps.LapNumber, lapNumber);
	        }
	        if(raceStartTime != null)
	        {
	        	content.put(RaceLaps.StartTime, raceStartTime);
	        }
	        if(raceFinishTime != null)
	        {
	        	content.put(RaceLaps.FinishTime, raceFinishTime);
	        }
	        if(raceElapsedTime != null)
	        {
	        	content.put(RaceLaps.ElapsedTime, raceElapsedTime);
	        }
			return context.getContentResolver().update(RaceLaps.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceLaps.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static Hashtable<String, Long> getValues(Context context, Long race_ID) {
			Hashtable<String, Long> raceValues = new Hashtable<String, Long>();
			
			Cursor raceCursor = RaceLaps.Read(context, null, RaceLaps._ID + "=?", new String[]{Long.toString(race_ID)}, null);
			if(raceCursor != null && raceCursor.getCount() > 0){
				raceCursor.moveToFirst();
				raceValues.put(RaceLaps._ID, race_ID);
				raceValues.put(RaceLaps.RaceResult_ID, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.RaceResult_ID)));
				raceValues.put(RaceLaps.LapNumber, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.LapNumber)));
				raceValues.put(RaceLaps.StartTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.StartTime)));
				raceValues.put(RaceLaps.FinishTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.FinishTime)));
				raceValues.put(RaceLaps.ElapsedTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.ElapsedTime)));
			}
			if( raceCursor != null){
				raceCursor.close();
				raceCursor = null;
			}
			
			return raceValues;
		}
    }
    
    // BaseColumn contains _id.
    public static final class TeamLaps implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamLaps.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	String tableName = TeamInfo.getTableName() 
					+ " JOIN " + RaceResults.getTableName() + 
					" ON (" + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.getTableName() + "." + TeamInfo._ID + ")"
					+ " LEFT OUTER JOIN " + RaceLaps.getTableName() + 
					" ON (" + RaceLaps.getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.getTableName() + "." + RaceResults._ID + ")";
        	
        	return tableName;
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamLaps.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamInfo.CONTENT_URI, TeamMembers.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamLaps.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
