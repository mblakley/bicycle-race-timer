package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceResultsTeamOrRacerView;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewInclusive;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RaceResults extends ContentProviderTable implements BaseColumns {

    private static final RaceResults instance = new RaceResults();

    public RaceResults() {}

    public static RaceResults Instance() {
        return instance;
    }

    // Table column
    public static final String RacerClubInfo_ID = "RacerClubInfo_ID";
    public static final String TeamInfo_ID = "TeamInfo_ID";
    public static final String Race_ID = "Race_ID";
    public static final String StartOrder = "StartOrder";
    public static final String StartTimeOffset = "StartTimeOffset";
    public static final String StartTime = "StartTime";
    public static final String EndTime = "EndTime";
    public static final String ElapsedTime = "ElapsedTime";
    public static final String OverallPlacing = "OverallPlacing";
    public static final String CategoryPlacing = "CategoryPlacing";
    public static final String Points = "Points";
    public static final String PrimePoints = "PrimePoints";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + RacerClubInfo_ID + " integer references " + RacerClubInfo.Instance().getTableName() + "(" + RacerClubInfo._ID + ") null, "
                + TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") null, "
                + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null, "
                + StartOrder + " integer not null,"
                + StartTimeOffset + " integer not null,"
                + StartTime + " integer null,"
                + EndTime + " integer null,"
                + ElapsedTime + " integer null,"
                + OverallPlacing + " integer null,"
                + CategoryPlacing + " integer null,"
                + Points + " integer not null,"
                + PrimePoints + " integer not null"
                + ");";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(RaceResultsTeamOrRacerView.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Uri Create(Context context,
            Long racerInfo_ID, long race_ID, int startOrder,
            Long startTimeOffset, Long startTime, Long endTime,
            Long elapsedTime, Integer overallPlacing,
            Integer categoryPlacing, Integer points, Integer primePoints, Long teamInfo_ID) {
        ContentValues content = new ContentValues();
        content.put(RaceResults.RacerClubInfo_ID, racerInfo_ID);
        content.put(RaceResults.Race_ID, race_ID);
        content.put(RaceResults.StartOrder, startOrder);
        content.put(RaceResults.StartTimeOffset, startTimeOffset);
        content.put(RaceResults.StartTime, startTime);
        content.put(RaceResults.EndTime, endTime);
        content.put(RaceResults.ElapsedTime, elapsedTime);
        content.put(RaceResults.OverallPlacing, overallPlacing);
        content.put(RaceResults.CategoryPlacing, categoryPlacing);
        content.put(RaceResults.Points, points);
        content.put(RaceResults.PrimePoints, primePoints);
        content.put(RaceResults.TeamInfo_ID, teamInfo_ID);
        return context.getContentResolver().insert(RaceResults.Instance().CONTENT_URI, content);
    }
}