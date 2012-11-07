package com.xcracetimer.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetimer.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class RaceMeetTeamsCP {

    // BaseColumn contains _id.
    public static final class RaceMeetTeams implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceMeetTeams.class.getSimpleName() + "~");

        // Table column
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String RaceMeet_ID = "RaceMeet_ID";
        public static final String TeamRacerNumber = "TeamRacerNumber";
        
        public static String getTableName(){
        	return RaceMeetTeams.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceMeetTeams.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, " 
        	        + RaceMeet_ID + " integer references " + RaceMeet.getTableName() + "(" + RaceMeet._ID + ") not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceMeetTeams.CONTENT_URI};
        }

		public static int Update(Context context, long teamInfo_ID, long raceMeet_ID, boolean addIfNotExist) {
			ContentValues content = new ContentValues();
	     	content.put(RaceMeetTeams.RaceMeet_ID, raceMeet_ID);
	     	int numChanged = context.getContentResolver().update(RaceMeetTeams.CONTENT_URI, content, RaceMeetTeams.TeamInfo_ID + "=?", new String[]{Long.toString(teamInfo_ID)});
			if(addIfNotExist && numChanged < 1){
		     	content.put(RaceMeetTeams.TeamInfo_ID, teamInfo_ID);
				RaceMeetTeams.Create(context, content);
				numChanged = 1;
			}

	     	return numChanged;
		}

		public static Uri Create(Context context, ContentValues content) {
			return context.getContentResolver().insert(RaceMeetTeams.CONTENT_URI, content);
		}
		
		public static int Delete(Context context, long teamInfo_ID, long raceMeet_ID) {
			String where = RaceMeetTeams.TeamInfo_ID + "=? AND " + RaceMeetTeams.RaceMeet_ID + "=?" ;
			String[] selectionArgs = new String[]{Long.toString(teamInfo_ID), Long.toString(raceMeet_ID)};
			
			return context.getContentResolver().delete(RaceMeetTeams.CONTENT_URI, where, selectionArgs);
		}
    }
}
