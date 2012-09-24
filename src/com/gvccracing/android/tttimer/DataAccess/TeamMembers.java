package com.gvccracing.android.tttimer.DataAccess;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewInclusive;

// BaseColumn contains _id.
public final class TeamMembers extends ContentProviderTable implements BaseColumns {
    
    private static final TeamMembers instance = new TeamMembers();
    
    public TeamMembers() {}
 
    public static TeamMembers Instance() {
        return instance;
    }

    // Table column
    public static final String TeamInfo_ID = "TeamInfo_ID";
    public static final String RacerSeriesInfo_ID = "RacerClubInfo_ID";
    public static final String TeamRacerNumber = "TeamRacerNumber";
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") not null, " 
    	        + RacerSeriesInfo_ID + " integer references " + RacerSeriesInfo.Instance().getTableName() + "(" + RacerSeriesInfo._ID + ") not null,"
    	        + TeamRacerNumber + " integer not null);";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }

	public int Update(Context context, long teamInfo_ID, long racerClubInfo_ID, long teamRacerNumber, boolean addIfNotExist) {
		ContentValues content = new ContentValues();
     	content.put(TeamMembers.RacerSeriesInfo_ID, racerClubInfo_ID);
     	int numChanged = context.getContentResolver().update(CONTENT_URI, content, TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?", new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)});
		if(addIfNotExist && numChanged < 1){
	     	content.put(TeamMembers.TeamInfo_ID, teamInfo_ID);
	     	content.put(TeamMembers.TeamRacerNumber, teamRacerNumber);
			this.Create(context, content);
			numChanged = 1;
		}

     	return numChanged;
	}
	
	public int Delete(Context context, long teamInfo_ID, long teamRacerNumber) {
		String where = TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?" ;
		String[] selectionArgs = new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)};
		
		return context.getContentResolver().delete(CONTENT_URI, where, selectionArgs);
	}
}
