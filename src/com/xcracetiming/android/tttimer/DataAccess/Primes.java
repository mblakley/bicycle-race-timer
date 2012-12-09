package com.xcracetiming.android.tttimer.DataAccess;

import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class Primes extends ContentProviderTable implements BaseColumns {
    
    private static final Primes instance = new Primes();
    
    public Primes() {}
 
    public static Primes Instance() {
        return instance;
    }

    // Table column
    public static final String DATABASE_COLUMN_RacerClubInfo_ID = "RacerClubInfo_ID";
    public static final String DATABASE_COLUMN_Race_ID = "Race_ID";
    public static final String DATABASE_COLUMN_PrimeNumber = "PrimeNumber";
    public static final String DATABASE_COLUMN_Placing = "Placing";
    public static final String DATABASE_COLUMN_Points = "Points";
    
    public String getCreate(){
    	return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + DATABASE_COLUMN_RacerClubInfo_ID + " integer references " + RacerSeriesInfo.Instance().getTableName() + "(" + RacerSeriesInfo._ID + ") not null, " 
                + DATABASE_COLUMN_Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null," 
                + DATABASE_COLUMN_PrimeNumber + " integer not null,"
                + DATABASE_COLUMN_Placing + " integer not null,"
                + DATABASE_COLUMN_Points + " integer not null"
                + ");";
    }
}
