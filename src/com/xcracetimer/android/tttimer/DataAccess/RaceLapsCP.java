package com.xcracetimer.android.tttimer.DataAccess;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetimer.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.xcracetimer.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.xcracetimer.android.tttimer.DataAccess.RaceInfoViewCP.RaceLapsInfoView;
import com.xcracetimer.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsRacerViewCP.RaceResultsRacerView;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class RaceLapsCP {

    // BaseColumn contains _id.
    public static final class RaceLaps implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceLaps.class.getSimpleName() + "~");

        // Table column
        public static final String RaceResult_ID = "RaceResult_ID";
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String Race_ID = "Race_ID";
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
        	        + RaceResult_ID + " integer references " + RaceResults.getTableName() + "(" + RaceResults._ID + ") null, " 
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") null, "
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") null, "
        	        + LapNumber + " integer not null,"
        	        + StartTime + " integer not null,"
        	        + FinishTime + " integer null,"
        	        + ElapsedTime + " integer null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceLaps.CONTENT_URI, RaceResultsRacerView.CONTENT_URI, RaceResultsLapsView.CONTENT_URI, RaceLapsInfoView.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceInfoView.CONTENT_URI, RaceLocation.CONTENT_URI, TeamLaps.CONTENT_URI};
        }

		public static Uri Create(Context context, Long raceResult_ID, Long teamInfo_ID, long lapNumber, long raceStartTime, long raceFinishTime, long elapsedTime, long race_ID) {
			ContentValues content = new ContentValues();
			content.put(RaceLaps.RaceResult_ID, raceResult_ID);
			content.put(RaceLaps.TeamInfo_ID, teamInfo_ID);
			content.put(RaceLaps.LapNumber, lapNumber);
			content.put(RaceLaps.StartTime, raceStartTime);
			content.put(RaceLaps.FinishTime, raceFinishTime);
			content.put(RaceLaps.ElapsedTime, elapsedTime);
			content.put(RaceLaps.Race_ID, race_ID);

	     	return context.getContentResolver().insert(RaceLaps.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long raceResult_ID, Long teamInfo_ID, Long lapNumber, Long raceStartTime, Long raceFinishTime, Long raceElapsedTime, Long race_ID) {
			ContentValues content = new ContentValues();
			if(raceResult_ID != null)
	        {
				content.put(RaceLaps.RaceResult_ID, raceResult_ID);
	        }
			if(teamInfo_ID != null)
	        {
				content.put(RaceLaps.TeamInfo_ID, teamInfo_ID);
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
	        if(race_ID != null)
	        {
	        	content.put(RaceLaps.Race_ID, race_ID);
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
				raceValues.put(RaceLaps.TeamInfo_ID, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.TeamInfo_ID)));
				raceValues.put(RaceLaps.LapNumber, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.LapNumber)));
				raceValues.put(RaceLaps.StartTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.StartTime)));
				raceValues.put(RaceLaps.FinishTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.FinishTime)));
				raceValues.put(RaceLaps.ElapsedTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.ElapsedTime)));
				raceValues.put(RaceLaps.Race_ID, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.Race_ID)));
			}
			if( raceCursor != null){
				raceCursor.close();
				raceCursor = null;
			}
			
			return raceValues;
		}
    }
    
    // BaseColumn contains _id.
    public static final class RaceResultsLapsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceResultsLapsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceLaps.getTableName() +
        			" JOIN " + Race.getTableName() + 
    				" ON (" + RaceLaps.getTableName() + "." + RaceLaps.Race_ID + " = " + Race.getTableName() + "." + Race._ID + ")" +
        			" LEFT OUTER JOIN " + TeamInfo.getTableName() + 
    				" ON (" + RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID + " = " + TeamInfo.getTableName() + "." + TeamInfo._ID + ")" +
    				" LEFT OUTER JOIN " + RaceResults.getTableName() + 
    				" ON (" + RaceLaps.getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.getTableName() + "." + RaceResults._ID + ")" +
    				" LEFT OUTER JOIN " + RacerClubInfo.getTableName() + 
    				" ON (" + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + ")" +
    				" LEFT OUTER JOIN " + Racer.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")";        	
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceResultsLapsView.CONTENT_URI};
        }

        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
        
		public static int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			Cursor checkIns = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
			int numCheckIns = checkIns.getCount();
			if(checkIns != null){
				checkIns.close();
				checkIns = null;
			}
			return numCheckIns;
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
        	return new Uri[]{TeamLaps.CONTENT_URI, TeamInfo.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamLaps.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
