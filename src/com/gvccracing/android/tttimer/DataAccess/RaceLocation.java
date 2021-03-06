package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceLocation extends ContentProviderTable implements BaseColumns {

    private static final RaceLocation instance = new RaceLocation();

    public RaceLocation() {}

    public static RaceLocation Instance() {
        return instance;
    }

    // Table column
    public static final String CourseName = "CourseName";
    public static final String Distance = "Distance";
    public static final String TurnAroundInfo = "TurnAroundInfo";
    public static final String TurnAroundPic = "TurnAroundPic";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + CourseName + " text not null, "
                + Distance + " real not null, "
                + TurnAroundInfo + " text null,"
                + TurnAroundPic + " blob null"
                + ");";
    }

    public Uri Create(Context context, String courseName2,
            String distance2) {
        ContentValues content = new ContentValues();
        content.put(RaceLocation.CourseName, courseName2);
        content.put(RaceLocation.Distance, distance2);

        return context.getContentResolver().insert(RaceLocation.Instance().CONTENT_URI, content);
    }

    public int Update(Context context, long raceLocation_ID, String courseName, String distance) {
        ContentValues content = new ContentValues();
        if(courseName != null)
        {
            content.put(RaceLocation.CourseName, courseName);
        }
        if(distance != null)
        {
            content.put(RaceLocation.Distance, distance);
        }
        return context.getContentResolver().update(RaceLocation.Instance().CONTENT_URI, content, RaceLocation._ID + "=?", new String[]{Long.toString(raceLocation_ID)});
    }
}
