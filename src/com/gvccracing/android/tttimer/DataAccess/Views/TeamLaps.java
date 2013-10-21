package com.gvccracing.android.tttimer.DataAccess.Views;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembers;

import java.util.ArrayList;

/**
 * Created by mab0270 on 10/18/13.
 */
// BaseColumn contains _id.
public final class TeamLaps extends ContentProviderTable implements BaseColumns {

    private static final TeamLaps instance = new TeamLaps();

    public TeamLaps() {}

    public static TeamLaps Instance() {
        return instance;
    }

    public String getTableName(){
        String tableName = TeamInfo.Instance().getTableName()
                + " JOIN " + RaceResults.Instance().getTableName() +
                " ON (" + RaceResults.Instance().getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.Instance().getTableName() + "." + TeamInfo._ID + ")"
                + " LEFT OUTER JOIN " + RaceLaps.Instance().getTableName() +
                " ON (" + RaceLaps.Instance().getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.Instance().getTableName() + "." + RaceResults._ID + ")";

        return tableName;
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
        urisToNotify.add(TeamMembers.Instance().CONTENT_URI);

        return urisToNotify;
    }
}
