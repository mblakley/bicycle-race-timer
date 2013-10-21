package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class TeamMembers extends ContentProviderTable implements BaseColumns {

    private static final TeamMembers instance = new TeamMembers();

    public TeamMembers() {}

    public static TeamMembers Instance() {
        return instance;
    }

    // Table column
    public static final String TeamInfo_ID = "TeamInfo_ID";
    public static final String RacerClubInfo_ID = "RacerClubInfo_ID";
    public static final String TeamRacerNumber = "TeamRacerNumber";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") not null, "
                + RacerClubInfo_ID + " integer references " + RacerClubInfo.Instance().getTableName() + "(" + RacerClubInfo._ID + ") not null,"
                + TeamRacerNumber + " integer not null);";
    }

    public int Update(Context context, long teamInfo_ID, long racerClubInfo_ID, long teamRacerNumber, boolean addIfNotExist) {
        ContentValues content = new ContentValues();
        content.put(TeamMembers.RacerClubInfo_ID, racerClubInfo_ID);
        int numChanged = context.getContentResolver().update(TeamMembers.Instance().CONTENT_URI, content, TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?", new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)});
        if(addIfNotExist && numChanged < 1){
            content.put(TeamMembers.TeamInfo_ID, teamInfo_ID);
            content.put(TeamMembers.TeamRacerNumber, teamRacerNumber);
            TeamMembers.Instance().Create(context, content);
            numChanged = 1;
        }

        return numChanged;
    }

    public int Delete(Context context, long teamInfo_ID, long teamRacerNumber) {
        String where = TeamMembers.TeamInfo_ID + "=? AND " + TeamMembers.TeamRacerNumber + "=?" ;
        String[] selectionArgs = new String[]{Long.toString(teamInfo_ID), Long.toString(teamRacerNumber)};

        return context.getContentResolver().delete(TeamMembers.Instance().CONTENT_URI, where, selectionArgs);
    }
}
