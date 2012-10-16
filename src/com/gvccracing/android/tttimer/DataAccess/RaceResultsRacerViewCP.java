package com.gvccracing.android.tttimer.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceResultsLapsView;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class RaceResultsRacerViewCP {
	// BaseColumn contains _id.
    public static final class RaceResultsRacerView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceResultsRacerView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceResults.getTableName() +
    				" JOIN " + RacerClubInfo.getTableName() + 
    				" ON (" + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + ")" +
    				" JOIN " + Racer.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")";        	
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceResultsRacerView.CONTENT_URI, RaceResultsLapsView.CONTENT_URI};
        }

        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceResultsRacerView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}        
    }
}
