package com.gvccracing.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceLapsInfoView;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamLaps;

// BaseColumn contains _id.
public final class RaceLaps extends ContentProviderTable implements BaseColumns {

    private static final RaceLaps instance = new RaceLaps();

    public RaceLaps() {}

    public static RaceLaps Instance() {
        return instance;
    }

    // Table column
    public static final String RaceResult_ID = "RaceResult_ID";
    public static final String LapNumber = "LapNumber";
    public static final String StartTime = "StartTime";
    public static final String FinishTime = "FinishTime";
    public static final String ElapsedTime = "ElapsedTime";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + RaceResult_ID + " integer references " + RaceResults.Instance().getTableName() + "(" + RaceResults._ID + ") not null, "
                + LapNumber + " integer not null,"
                + StartTime + " integer not null,"
                + FinishTime + " integer null,"
                + ElapsedTime + " integer null);";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(RaceLaps.Instance().CONTENT_URI);
        urisToNotify.add(RaceLapsInfoView.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(RaceInfoView.Instance().CONTENT_URI);
        urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
        urisToNotify.add(TeamLaps.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Uri Create(Context context, long raceResult_ID, long lapNumber, long raceStartTime, long raceFinishTime, long elapsedTime) {
        ContentValues content = new ContentValues();
        content.put(RaceLaps.RaceResult_ID, raceResult_ID);
        content.put(RaceLaps.LapNumber, lapNumber);
        content.put(RaceLaps.StartTime, raceStartTime);
        content.put(RaceLaps.FinishTime, raceFinishTime);
        content.put(RaceLaps.ElapsedTime, elapsedTime);

        return context.getContentResolver().insert(RaceLaps.Instance().CONTENT_URI, content);
    }

    public int Update(Context context, String where, String[] selectionArgs, Long raceResult_ID, Long lapNumber, Long raceStartTime, Long raceFinishTime, Long raceElapsedTime) {
        ContentValues content = new ContentValues();
        if(raceResult_ID != null)
        {
            content.put(RaceLaps.RaceResult_ID, raceResult_ID);
        }
        if(lapNumber != null)
        {
            content.put(RaceLaps.LapNumber, lapNumber);
        }
        if(raceStartTime != null)
        {
            content.put(RaceLaps.StartTime, raceStartTime);
        }
        if(raceFinishTime != null)
        {
            content.put(RaceLaps.FinishTime, raceFinishTime);
        }
        if(raceElapsedTime != null)
        {
            content.put(RaceLaps.ElapsedTime, raceElapsedTime);
        }
        return context.getContentResolver().update(RaceLaps.Instance().CONTENT_URI, content, where, selectionArgs);
    }

    public Hashtable<String, Long> getValues(Context context, Long race_ID) {
        Hashtable<String, Long> raceValues = new Hashtable<String, Long>();

        Cursor raceCursor = RaceLaps.Instance().Read(context, null, RaceLaps._ID + "=?", new String[]{Long.toString(race_ID)}, null);
        if(raceCursor != null && raceCursor.getCount() > 0){
            raceCursor.moveToFirst();
            raceValues.put(RaceLaps._ID, race_ID);
            raceValues.put(RaceLaps.RaceResult_ID, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.RaceResult_ID)));
            raceValues.put(RaceLaps.LapNumber, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.LapNumber)));
            raceValues.put(RaceLaps.StartTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.StartTime)));
            raceValues.put(RaceLaps.FinishTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.FinishTime)));
            raceValues.put(RaceLaps.ElapsedTime, raceCursor.getLong(raceCursor.getColumnIndex(RaceLaps.ElapsedTime)));
        }
        if( raceCursor != null){
            raceCursor.close();
            raceCursor = null;
        }

        return raceValues;
    }
}