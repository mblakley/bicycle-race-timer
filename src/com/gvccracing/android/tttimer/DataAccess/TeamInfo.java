package com.gvccracing.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewInclusive;

// BaseColumn contains _id.
public final class TeamInfo extends ContentProviderTable implements BaseColumns {

    private static final TeamInfo instance = new TeamInfo();

    public TeamInfo() {}

    public static TeamInfo Instance() {
        return instance;
    }

    // Table column
    public static final String TeamName = "TeamName";
    public static final String TeamCategory = "TeamCategory";
    public static final String Year = "Year";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + TeamName + " text not null, "
                + TeamCategory + " text not null, "
                + Year + " integer not null);";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
        urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public static Uri Create(Context context, String teamName, String teamCategory) {
        ContentValues content = new ContentValues();
        content.put(TeamInfo.TeamName, teamName);
        content.put(TeamInfo.TeamCategory, teamCategory);
        content.put(TeamInfo.Year, Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));

        return context.getContentResolver().insert(TeamInfo.Instance().CONTENT_URI, content);
    }

    public Hashtable<String, Object> getValues(Context context, Long teamInfo_ID) {
        Hashtable<String, Object> teamInfoValues = new Hashtable<String, Object>();

        Cursor teamCursor = TeamInfo.Instance().Read(context, null, TeamInfo._ID + "=?", new String[]{Long.toString(teamInfo_ID)}, null);
        if(teamCursor != null && teamCursor.getCount() > 0){
            teamCursor.moveToFirst();
            teamInfoValues.put(TeamInfo._ID, teamInfo_ID);
            teamInfoValues.put(TeamInfo.TeamName, teamCursor.getString(teamCursor.getColumnIndex(TeamInfo.TeamName)));
            teamInfoValues.put(TeamInfo.TeamCategory, teamCursor.getString(teamCursor.getColumnIndex(TeamInfo.TeamCategory)));
            teamInfoValues.put(TeamInfo.Year, teamCursor.getLong(teamCursor.getColumnIndex(TeamInfo.Year)));
        }
        if( teamCursor != null){
            teamCursor.close();
            teamCursor = null;
        }

        return teamInfoValues;
    }
}