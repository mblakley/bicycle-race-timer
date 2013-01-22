package com.xcracetiming.android.tttimer.DataAccess;

import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceSeriesCategories extends ContentProviderTable implements BaseColumns {
    
    private static final RaceSeriesCategories instance = new RaceSeriesCategories();
    
    public RaceSeriesCategories() {}
 
    public static RaceSeriesCategories Instance() {
        return instance;
    }

    // Table column
    public static final String RaceSeries_ID = "RaceSeries_ID";
    public static final String RaceCategory_ID = "RaceCategory_ID";       
    
    public String getCreate(){
    	return "create table " + RaceCategory.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceSeries_ID + " integer references " + RaceSeries.Instance().getTableName() + "(" + RaceSeries._ID + ") not null,"
    	        + RaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null"
    	        + ");";
    }
}
