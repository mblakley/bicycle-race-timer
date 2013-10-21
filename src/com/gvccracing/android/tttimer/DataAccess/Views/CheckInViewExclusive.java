package com.gvccracing.android.tttimer.DataAccess.Views;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class CheckInViewExclusive extends ContentProviderTable implements BaseColumns {

    private static final CheckInViewExclusive instance = new CheckInViewExclusive();

    public CheckInViewExclusive() {}

    public static CheckInViewExclusive Instance() {
        return instance;
    }

    public String getTableName(){
        return RacerClubInfo.Instance().getTableName() +
                " JOIN " + Racer.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")" +
                " JOIN " + RaceResults.Instance().getTableName() +
                " ON (" + RaceResults.Instance().getTableName() + "." + RaceResults.RacerClubInfo_ID + " = " + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo._ID + ")" +
                " JOIN " + Race.Instance().getTableName() +
                " ON (" + Race.Instance().getTableName() + "." + Race._ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID + ")";
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
        Cursor checkIns = context.getContentResolver().query(CheckInViewExclusive.Instance().CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
        int numCheckIns = checkIns.getCount();
        if(checkIns != null){
            checkIns.close();
            checkIns = null;
        }
        return numCheckIns;
    }
}