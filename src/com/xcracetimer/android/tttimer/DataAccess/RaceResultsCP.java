package com.xcracetimer.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetimer.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.xcracetimer.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsTeamOrRacerViewCP.RaceResultsTeamOrRacerView;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class RaceResultsCP {

    // BaseColumn contains _id.
    public static final class RaceResults implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceResults.class.getSimpleName() + "~");
        
        // Table column
        public static final String RacerClubInfo_ID = "RacerClubInfo_ID";
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String Race_ID = "Race_ID";
        public static final String StartTime = "StartTime";
        public static final String EndTime = "EndTime";
        public static final String ElapsedTime = "ElapsedTime";
        public static final String OverallPlacing = "OverallPlacing";
        public static final String Removed = "Removed";
        
        public static String getTableName(){
        	return RaceResults.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceResults.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RacerClubInfo_ID + " integer references " + RacerClubInfo.getTableName() + "(" + RacerClubInfo._ID + ") null, "
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") null, "
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null, "
        	        + StartTime + " integer null,"
        	        + EndTime + " integer null,"
        	        + ElapsedTime + " integer null,"
        	        + OverallPlacing + " integer null,"
        	        + Removed + " text not null"
        	        + ");";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceResults.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceResultsTeamOrRacerView.CONTENT_URI};
        }

		public static Uri Create(Context context,
				Long racerInfo_ID, long race_ID,
				Long startTime, Long endTime,
				Long elapsedTime, Integer overallPlacing,
				Long teamInfo_ID, boolean removed) {
			ContentValues content = new ContentValues();
	     	content.put(RaceResults.RacerClubInfo_ID, racerInfo_ID);
	     	content.put(RaceResults.Race_ID, race_ID);
	     	content.put(RaceResults.StartTime, startTime);
	     	content.put(RaceResults.EndTime, endTime);
	     	content.put(RaceResults.ElapsedTime, elapsedTime);
	     	content.put(RaceResults.OverallPlacing, overallPlacing);
	     	content.put(RaceResults.TeamInfo_ID, teamInfo_ID);
	     	content.put(RaceResults.Removed, Boolean.toString(removed));
	     	return context.getContentResolver().insert(RaceResults.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceResults.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static int Update(Context context, ContentValues content, String selection, String[] selectionArgs) {
			return context.getContentResolver().update(RaceResults.CONTENT_URI, content, selection, selectionArgs);
		}
		
		public static int Delete(Context context, String selection, String[] selectionArgs) {
			return context.getContentResolver().delete(RaceResults.CONTENT_URI, selection, selectionArgs);
		}
    }
}
