package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RaceResultsTeamOrRacerView extends ContentProviderTable implements BaseColumns {

    private static final RaceResultsTeamOrRacerView instance = new RaceResultsTeamOrRacerView();

    public RaceResultsTeamOrRacerView() {}

    public static RaceResultsTeamOrRacerView Instance() {
        return instance;
    }

    public String getTableName(){
        return RaceResults.Instance().getTableName() +
                " LEFT OUTER JOIN " + RacerClubInfo.Instance().getTableName() + " ON (" + RaceResults.Instance().getTableName() + "." + RaceResults.RacerClubInfo_ID + " = " + RacerClubInfo.Instance().getTableName() + "._ID)" +
                " LEFT OUTER JOIN " + TeamInfo.Instance().getTableName() + " ON (" + RaceResults.Instance().getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.Instance().getTableName() + "._ID)";
    }

    public static String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(RaceResultsTeamOrRacerView.Instance().CONTENT_URI);
        urisToNotify.add(RaceResults.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }
}