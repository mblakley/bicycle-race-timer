package com.gvccracing.android.tttimer.DataAccess;

import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class TeamRaces extends ContentProviderTable implements BaseColumns {

    private static final TeamRaces instance = new TeamRaces();

    public TeamRaces() {}

    public static TeamRaces Instance() {
        return instance;
    }

    // Table column
    public static final String TeamInfo_ID = "TeamInfo_ID";
    public static final String Race_ID = "Race_ID";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + TeamInfo_ID + " integer references " + TeamInfo.Instance().getTableName() + "(" + TeamInfo._ID + ") not null, "
                + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null);";
    }
}
