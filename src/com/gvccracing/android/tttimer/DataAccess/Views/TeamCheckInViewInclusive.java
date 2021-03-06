package com.gvccracing.android.tttimer.DataAccess.Views;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembers;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class TeamCheckInViewInclusive extends ContentProviderTable implements BaseColumns {

    private static final TeamCheckInViewInclusive instance = new TeamCheckInViewInclusive();

    public TeamCheckInViewInclusive() {}

    public static TeamCheckInViewInclusive Instance() {
        return instance;
    }

    public String getTableName(){
        return TeamInfo.Instance().getTableName()
                + " JOIN " + TeamMembers.Instance().getTableName() +
                " ON (" + TeamInfo.Instance().getTableName() + "." + TeamInfo._ID + " = " + TeamMembers.Instance().getTableName() + "." + TeamMembers.TeamInfo_ID + ")"
                + " JOIN " + RacerClubInfo.Instance().getTableName() +
                " ON (" + TeamMembers.Instance().getTableName() + "." + TeamMembers.RacerClubInfo_ID + " = " + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo._ID + ")"
                + " JOIN " + Racer.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")";
    }

    public String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
        urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
        Cursor checkIns = context.getContentResolver().query(TeamCheckInViewInclusive.Instance().CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
        int numCheckIns = checkIns.getCount();
        if(checkIns != null){
            checkIns.close();
            checkIns = null;
        }
        return numCheckIns;
    }
}
