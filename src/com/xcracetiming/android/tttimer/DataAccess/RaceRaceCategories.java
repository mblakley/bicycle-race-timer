package com.xcracetiming.android.tttimer.DataAccess;

import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceRaceCategories extends ContentProviderTable implements BaseColumns {
    
    private static final RaceRaceCategories instance = new RaceRaceCategories();
    
    public RaceRaceCategories() {}
 
    public static RaceRaceCategories Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";       
    
    public String getCreate(){
    	return "create table " + RaceCategory.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null,"
    	        + RaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null"
    	        + ");";
    }
}
