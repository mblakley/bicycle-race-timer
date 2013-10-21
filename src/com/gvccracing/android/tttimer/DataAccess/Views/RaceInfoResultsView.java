package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RaceInfoResultsView extends ContentProviderTable implements BaseColumns {

    private static final RaceInfoResultsView instance = new RaceInfoResultsView();

    public RaceInfoResultsView() {}

    public static RaceInfoResultsView Instance() {
        return instance;
    }

    public String getTableName(){
        return Race.Instance().getTableName() + " JOIN " + RaceLocation.Instance().getTableName() +
                " ON (" + Race.Instance().getTableName() + "." + Race.RaceLocation_ID + " = " + RaceLocation.Instance().getTableName() + "._ID)"
                + " JOIN " + RaceResults.Instance().getTableName() +
                " ON (" + Race.Instance().getTableName() + "." + Race._ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID + ")";
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(Race.Instance().CONTENT_URI);
        urisToNotify.add(RaceResults.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
