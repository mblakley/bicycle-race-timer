package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RaceInfoView extends ContentProviderTable implements BaseColumns {

    private static final RaceInfoView instance = new RaceInfoView();

    public RaceInfoView() {}

    public static RaceInfoView Instance() {
        return instance;
    }

    public String getTableName(){
        return Race.Instance().getTableName() + " JOIN " + RaceLocation.Instance().getTableName() +
                " ON (" + Race.Instance().getTableName() + "." + Race.RaceLocation_ID + " = " + RaceLocation.Instance().getTableName() + "._ID)";
    }

    public static String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(Race.Instance().CONTENT_URI);
        urisToNotify.add(RaceLocation.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
