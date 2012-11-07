package com.xcracetimer.android.tttimer.DataAccess;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class TeamInfoCP {

    // BaseColumn contains _id.
    public static final class TeamInfo implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamInfo.class.getSimpleName() + "~");

        // Table column
        public static final String TeamName = "TeamName";
        public static final String Year = "Year";
        public static final String Division = "Division";
        public static final String DivisionWins = "DivisionWins";
        public static final String DivisionLosses = "DivisionLosses";
        public static final String OverallWins = "OverallWins";
        public static final String OverallLosses = "OverallLosses";
        
        public static String getTableName(){
        	return TeamInfo.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + TeamInfo.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + TeamName + " text not null, " 
        	        + Year + " integer not null, " 
        	        + Division + " integer not null, "
        	        + DivisionWins + " integer not null, "
        	        + DivisionLosses + " integer not null, "
        	        + OverallWins + " integer not null, "
        	        + OverallLosses + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamInfo.CONTENT_URI};
        }

		public static Uri Create(Context context, String teamName, Long year, Long division, Long divisionWins, Long divisionLosses, Long overallWins, Long overallLosses) {
			ContentValues content = new ContentValues();
	     	content.put(TeamInfo.TeamName, teamName);
	     	content.put(TeamInfo.Year, year);
	     	content.put(TeamInfo.Division, division);
	     	content.put(TeamInfo.DivisionWins, divisionWins);
	     	content.put(TeamInfo.DivisionLosses, divisionLosses);
	     	content.put(TeamInfo.OverallWins, overallWins);
	     	content.put(TeamInfo.OverallLosses, overallLosses);

	     	return context.getContentResolver().insert(TeamInfo.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamInfo.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static Hashtable<String, Object> getValues(Context context, Long teamInfo_ID) {
			Hashtable<String, Object> teamInfoValues = new Hashtable<String, Object>();
			
			Cursor teamCursor = TeamInfo.Read(context, null, TeamInfo._ID + "=?", new String[]{Long.toString(teamInfo_ID)}, null);
			if(teamCursor != null && teamCursor.getCount() > 0){
				teamCursor.moveToFirst();
				teamInfoValues.put(TeamInfo._ID, teamInfo_ID);
				teamInfoValues.put(TeamInfo.TeamName, teamCursor.getString(teamCursor.getColumnIndex(TeamInfo.TeamName)));
				teamInfoValues.put(TeamInfo.Year, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.Year)));
				teamInfoValues.put(TeamInfo.Division, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.Division)));
				teamInfoValues.put(TeamInfo.DivisionWins, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.DivisionWins)));
				teamInfoValues.put(TeamInfo.DivisionLosses, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.DivisionLosses)));
				teamInfoValues.put(TeamInfo.OverallWins, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.OverallWins)));
				teamInfoValues.put(TeamInfo.OverallLosses, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.OverallLosses)));
			}
			if( teamCursor != null){
				teamCursor.close();
				teamCursor = null;
			}
			
			return teamInfoValues;
		}
    }
}