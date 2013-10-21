package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RaceLapsInfoView extends ContentProviderTable implements BaseColumns {

    private static final RaceLapsInfoView instance = new RaceLapsInfoView();

    public RaceLapsInfoView() {}

    public static RaceLapsInfoView Instance() {
        return instance;
    }

    public String getTableName(){
        return Race.Instance().getTableName()
                + " JOIN " + RaceResults.Instance().getTableName() +
                " ON (" + Race.Instance().getTableName() + "." + Race._ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID + ")"
                + " JOIN " + RaceLaps.Instance().getTableName() +
                " ON (" + RaceLaps.Instance().getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults._ID + ")";
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(Race.Instance().CONTENT_URI);
        urisToNotify.add(RaceResults.Instance().CONTENT_URI);
        urisToNotify.add(RaceLaps.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
