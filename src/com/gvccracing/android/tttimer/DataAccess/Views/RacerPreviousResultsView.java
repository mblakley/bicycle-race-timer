package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RacerPreviousResultsView extends ContentProviderTable implements BaseColumns {

    private static final RacerPreviousResultsView instance = new RacerPreviousResultsView();

    public RacerPreviousResultsView() {}

    public static RacerPreviousResultsView Instance() {
        return instance;
    }

    public String getTableName(){
        return RacerClubInfo.Instance().getTableName() +
                " JOIN " + Racer.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")" +
                " JOIN " + RaceResults.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo._ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults.RacerClubInfo_ID + ")" +
                " JOIN " + Race.Instance().getTableName() +
                " ON (" + RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID + " = " + Race.Instance().getTableName() + "." + Race._ID + ")" +
                " JOIN " + RaceLocation.Instance().getTableName() +
                " ON (" + RaceLocation.Instance().getTableName() + "." + RaceLocation._ID + " = " + Race.Instance().getTableName() + "." + Race.RaceLocation_ID + ")";
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(RacerClubInfo.Instance().CONTENT_URI);
        urisToNotify.add(Racer.Instance().CONTENT_URI);
        urisToNotify.add(RaceResults.Instance().CONTENT_URI);
        urisToNotify.add(Race.Instance().CONTENT_URI);
        urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
