package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class TeamMembersCP {

    // BaseColumn contains _id.
    public static final class TeamMembers implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamMembers.class.getSimpleName() + "~");

        // Table column
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String RacerClubInfo_ID = "RacerClubInfo_ID";
        public static final String TeamRacerNumber = "TeamRacerNumber";
        
        public static String getTableName(){
        	return TeamMembers.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + TeamMembers.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, " 
        	        + RacerClubInfo_ID + " integer references " + RacerClubInfo.getTableName() + "(" + RacerClubInfo._ID + ") not null,"
        	        + TeamRacerNumber + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamMembers.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI};
        }

		public static int Update(Context context, long teamInfo_ID, long racerClubInfo_ID, long teamRacerNumber, boolean addIfNotExist) {
			ContentValues content = new ContentValues();
	     	content.put(TeamMembers.RacerClubInfo_ID, racerClubInfo_ID);
	     	int numChanged = context.getContentResolver().update(TeamMembers.CONTENT_URI, content, TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?", new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)});
			if(addIfNotExist && numChanged < 1){
		     	content.put(TeamMembers.TeamInfo_ID, teamInfo_ID);
		     	content.put(TeamMembers.TeamRacerNumber, teamRacerNumber);
				TeamMembers.Create(context, content);
				numChanged = 1;
			}

	     	return numChanged;
		}

		public static Uri Create(Context context, ContentValues content) {
			return context.getContentResolver().insert(TeamMembers.CONTENT_URI, content);
		}
		
		public static int Delete(Context context, long teamInfo_ID, long teamRacerNumber) {
			String where = TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?" ;
			String[] selectionArgs = new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)};
			
			return context.getContentResolver().delete(TeamMembers.CONTENT_URI, where, selectionArgs);
		}
    }
}
