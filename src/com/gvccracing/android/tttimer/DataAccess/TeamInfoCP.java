package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

public class TeamInfoCP {

    // BaseColumn contains _id.
    public static final class TeamInfo implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamInfo.class.getSimpleName() + "~");

        // Table column
        public static final String TeamName = "TeamName";
        public static final String TeamCategory = "TeamCategory";
        public static final String Year = "Year";
        
        public static String getTableName(){
        	return TeamInfo.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + TeamInfo.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + TeamName + " text not null, " 
        	        + TeamCategory + " text not null, " 
        	        + Year + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamInfo.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI};
        }

		public static Uri Create(Context context, String teamName, String teamCategory) {
			ContentValues content = new ContentValues();
	     	content.put(TeamInfo.TeamName, teamName);
	     	content.put(TeamInfo.TeamCategory, teamCategory);

	     	return context.getContentResolver().insert(TeamInfo.CONTENT_URI, content);
		}
    }
}