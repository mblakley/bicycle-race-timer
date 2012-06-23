package com.gvccracing.android.tttimer.DataAccess;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class TeamRacesCP {

    // BaseColumn contains _id.
    public static final class TeamRaces implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamRaces.class.getSimpleName() + "~");

        // Table column
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String Race_ID = "Race_ID";
        
        public static String getTableName(){
        	return TeamRaces.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + TeamRaces.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, " 
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null);";
        }
    }
}
