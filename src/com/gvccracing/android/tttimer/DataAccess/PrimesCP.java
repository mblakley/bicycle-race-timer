package com.gvccracing.android.tttimer.DataAccess;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class PrimesCP {

    // BaseColumn contains _id.
    public static final class Primes implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, Primes.class.getSimpleName());

        // Table column
        public static final String DATABASE_COLUMN_RacerClubInfo_ID = "RacerClubInfo_ID";
        public static final String DATABASE_COLUMN_Race_ID = "Race_ID";
        public static final String DATABASE_COLUMN_PrimeNumber = "PrimeNumber";
        public static final String DATABASE_COLUMN_Placing = "Placing";
        public static final String DATABASE_COLUMN_Points = "Points";
        
        public static String getTableName(){
        	return Primes.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + Primes.getTableName()
                    + " (" + _ID + " integer primary key autoincrement, "
                    + DATABASE_COLUMN_RacerClubInfo_ID + " integer references " + RacerClubInfo.getTableName() + "(" + RacerClubInfo._ID + ") not null, " 
                    + DATABASE_COLUMN_Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null," 
                    + DATABASE_COLUMN_PrimeNumber + " integer not null,"
                    + DATABASE_COLUMN_Placing + " integer not null,"
                    + DATABASE_COLUMN_Points + " integer not null"
                    + ");";
        }
    }
}
