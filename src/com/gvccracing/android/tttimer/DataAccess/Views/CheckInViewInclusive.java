package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class CheckInViewInclusive extends ContentProviderTable implements BaseColumns {

    private static final CheckInViewInclusive instance = new CheckInViewInclusive();

    public CheckInViewInclusive() {}

    public static CheckInViewInclusive Instance() {
        return instance;
    }

    public String getTableName(){
        return RacerClubInfo.Instance().getTableName() +
                " JOIN " + Racer.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")";// +
                //" LEFT OUTER JOIN " + RaceResults.Instance().getTableName() +
                //" ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo._ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults.RacerClubInfo_ID + ")";
    }

    public static String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
