package com.gvccracing.android.tttimer.DataAccess;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class RacerPreviousResultsViewCP {
	// BaseColumn contains _id.
    public static final class RacerPreviousResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerPreviousResultsView.class.getSimpleName());
        
        public static String getTableName(){
        	return RacerClubInfo.getTableName() + 
        			" JOIN " + Racer.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")" +
        			" JOIN " + RaceResults.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + " = " + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + ")" +
		        	" JOIN " + Race.getTableName() + 
					" ON (" + RaceResults.getTableName() + "." + RaceResults.Race_ID + " = " + Race.getTableName() + "." + Race._ID + ")" + 
					" JOIN " + RaceLocation.getTableName() + 
					" ON (" + RaceLocation.getTableName() + "." + RaceLocation._ID + " = " + Race.getTableName() + "." + Race.RaceLocation_ID + ")";			
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerClubInfo.CONTENT_URI, Racer.CONTENT_URI, RaceResults.CONTENT_URI, Race.CONTENT_URI, RaceLocation.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI};
        }
    }
}
