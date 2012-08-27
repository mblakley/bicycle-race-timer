package com.gvccracing.android.tttimer.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerRegistrationCP.RacerRegistration;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfoCP.RacerUSACInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class RaceResultsTeamOrRacerViewCP {
	// BaseColumn contains _id.
    public static final class RaceResultsTeamOrRacerView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceResultsTeamOrRacerView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceResults.getTableName() + 
        			" LEFT OUTER JOIN " + RacerRegistration.getTableName() + " ON (" + RaceResults.getTableName() + "." + RaceResults.RacerRegistration_ID + " = " + RacerRegistration.getTableName() + "._ID)" +
        			" LEFT OUTER JOIN " + RacerUSACInfo.getTableName() + " ON (" + RacerRegistration.getTableName() + "." + RacerRegistration.RacerUSACInfo_ID + " = " + RacerUSACInfo.getTableName() + "._ID)" +
        			" LEFT OUTER JOIN " + RacerClubInfo.getTableName() + " ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.RacerUSACInfo_ID + " = " + RacerRegistration.getTableName() + "." + RacerRegistration.RacerUSACInfo_ID + ")" +
        			" LEFT OUTER JOIN " + TeamInfo.getTableName() + " ON (" + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.getTableName() + "._ID)";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceResultsTeamOrRacerView.CONTENT_URI, RaceResults.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceResultsTeamOrRacerView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
