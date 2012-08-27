package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerRegistrationCP.RacerRegistration;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfoCP.RacerUSACInfo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class CheckInViewCP {

    // BaseColumn contains _id.
    public static final class CheckInViewInclusive implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, CheckInViewInclusive.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RacerClubInfo.getTableName() + 
        			" JOIN " + RacerUSACInfo.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.RacerUSACInfo_ID + " = " + RacerUSACInfo.getTableName() + "." + RacerUSACInfo._ID + ")" +
        			" JOIN " + Racer.getTableName() + 
    				" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")";// +
        			//" LEFT OUTER JOIN " + RaceResults.getTableName() + 
    				//" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + " = " + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI};
        }
    }
    
    // BaseColumn contains _id.
    public static final class CheckInViewExclusive implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, CheckInViewExclusive.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RacerClubInfo.getTableName() + 
        			" JOIN " + RacerUSACInfo.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.RacerUSACInfo_ID + " = " + RacerUSACInfo.getTableName() + "." + RacerUSACInfo._ID + ")" +
    				" JOIN " + Racer.getTableName() + 
    				" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")" +
    				" JOIN " + RacerRegistration.getTableName() + 
    				" ON (" + RacerRegistration.getTableName() + "." + RacerRegistration.RacerUSACInfo_ID + " = " + RacerUSACInfo.getTableName() + "." + Racer._ID + ")" +
        			" JOIN " + RaceResults.getTableName() + 
    				" ON (" + RaceResults.getTableName() + "." + RaceResults.RacerRegistration_ID + " = " + RacerRegistration.getTableName() + "." + RacerRegistration._ID + ")" +
    				" JOIN " + Race.getTableName() + 
    				" ON (" + Race.getTableName() + "." + Race._ID + " = " + RaceResults.getTableName() + "." + RaceResults.Race_ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{CheckInViewExclusive.CONTENT_URI, CheckInViewInclusive.CONTENT_URI};
        }

        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(CheckInViewExclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
        
		public static int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			Cursor checkIns = context.getContentResolver().query(CheckInViewExclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
			int numCheckIns = checkIns.getCount();
			if(checkIns != null){
				checkIns.close();
				checkIns = null;
			}
			return numCheckIns;
		}
    }
}