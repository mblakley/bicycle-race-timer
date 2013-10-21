package com.gvccracing.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceInfoView;

// BaseColumn contains _id.
public final class Race extends ContentProviderTable implements BaseColumns {

    private static final Race instance = new Race();

    public Race() {}

    public static Race Instance() {
        return instance;
    }

    // Table column
    public static final String RaceDate = "RaceDate";
    public static final String RaceLocation_ID = "RaceLocation_ID";
    public static final String RaceType = "RaceType";
    public static final String RaceStartTime = "RaceStartTime";
    public static final String StartInterval = "StartInterval";
    public static final String NumLaps = "NumLaps";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + RaceDate + " integer not null, "
                + RaceLocation_ID + " integer references " + RaceLocation.Instance().getTableName() + "(" + RaceLocation._ID + ") not null,"
                + RaceType + " integer not null,"
                + RaceStartTime + " integer null,"
                + NumLaps + " integer null,"
                + StartInterval + " integer null);";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(Race.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(RaceInfoView.Instance().CONTENT_URI);
        urisToNotify.add(RaceLocation.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Uri Create(Context context, long raceLocation, Date raceDate, Long raceStartTime, long raceTypeID, Long startTimeOffset, long numLaps) {
        ContentValues content = new ContentValues();
        content.put(Race.RaceLocation_ID, raceLocation);
        content.put(Race.RaceDate, raceDate.getTime());
        content.put(Race.RaceStartTime, raceStartTime);
        content.put(Race.RaceType, raceTypeID);
        content.put(Race.NumLaps, numLaps);
        content.put(Race.StartInterval, startTimeOffset);

        return context.getContentResolver().insert(Race.Instance().CONTENT_URI, content);
    }

    public int Update(Context context, String where, String[] selectionArgs, Long race_ID, Long raceLocation_ID, Date raceDate, Long raceStartTime, Long raceTypeID, Long startTimeOffset, Long numLaps) {
        ContentValues content = new ContentValues();
        if(raceLocation_ID != null)
        {
            content.put(Race.RaceLocation_ID, raceLocation_ID);
        }
        if(raceDate != null)
        {
            content.put(Race.RaceDate, raceDate.getTime());
        }
        if(raceStartTime != null)
        {
            content.put(Race.RaceStartTime, raceStartTime);
        }
        if(raceTypeID != null)
        {
            content.put(Race.RaceType, raceTypeID);
        }
        if(startTimeOffset != null)
        {
            content.put(Race.StartInterval, startTimeOffset);
        }
        if(numLaps != null)
        {
            content.put(Race.NumLaps, numLaps);
        }
        return context.getContentResolver().update(Race.Instance().CONTENT_URI, content, where, selectionArgs);
    }

    public Hashtable<String, Long> getValues(Context context, Long race_ID) {
        Hashtable<String, Long> raceValues = new Hashtable<String, Long>();

        Cursor raceCursor = Race.Instance().Read(context, null, Race._ID + "=?", new String[]{Long.toString(race_ID)}, null);
        if(raceCursor != null && raceCursor.getCount() > 0){
            raceCursor.moveToFirst();
            raceValues.put(Race._ID, race_ID);
            raceValues.put(Race.RaceDate, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceDate)));
            raceValues.put(Race.RaceLocation_ID, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceLocation_ID)));
            raceValues.put(Race.RaceType, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceType)));
            raceValues.put(Race.RaceStartTime, raceCursor.getLong(raceCursor.getColumnIndex(Race.RaceStartTime)));
            raceValues.put(Race.NumLaps, raceCursor.getLong(raceCursor.getColumnIndex(Race.NumLaps)));
            raceValues.put(Race.StartInterval, raceCursor.getLong(raceCursor.getColumnIndex(Race.StartInterval)));
        }
        if( raceCursor != null){
            raceCursor.close();
            raceCursor = null;
        }

        return raceValues;
    }
}
