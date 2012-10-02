package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class RacerClubInfoCP {

    // BaseColumn contains _id.
    public static final class RacerClubInfo implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerClubInfo.class.getSimpleName() + "~");

        // Table column
        public static final String Racer_ID = "Racer_ID";
        public static final String TeamInfo_ID = "TeamInfo_ID";
        public static final String Category = "Category";
        public static final String Grade = "Grade";
        public static final String SpeedLevel = "SpeedLevel";
        
        public static String getTableName(){
        	return RacerClubInfo.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RacerClubInfo.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + Racer_ID + " integer references " + Racer.getTableName() + "(" + Racer._ID + ") not null, " 
        	        + TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, " 
        	        + Category + " text not null," 
        	        + Grade + " integer not null," 
        	        + SpeedLevel + " float not null" 
        			+ ");";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerClubInfo.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI};
        }

		public static Uri Create(Context context, long racer_ID, long teamInfo_ID, String category, long grade, float speedLevel) {
			ContentValues content = new ContentValues();
			content.put(RacerClubInfo.Racer_ID, racer_ID);
	     	content.put(RacerClubInfo.TeamInfo_ID, teamInfo_ID);
	     	content.put(RacerClubInfo.Category, category);
	     	content.put(RacerClubInfo.Grade, grade);
	     	content.put(RacerClubInfo.SpeedLevel, speedLevel);
			return context.getContentResolver().insert(RacerClubInfo.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder){
			return context.getContentResolver().query(RacerClubInfo.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
		
		public static int Update(Context context, long racerClubInfo_ID, Long racer_ID, Long teamInfo_ID, String category, Long grade, Float speedLevel) {
			ContentValues content = new ContentValues();
			if(racer_ID != null)
			{
				content.put(RacerClubInfo.Racer_ID, racer_ID);
			}
			if(teamInfo_ID != null)
			{
				content.put(RacerClubInfo.TeamInfo_ID, teamInfo_ID);
			}
			if(category != null)
			{
				content.put(RacerClubInfo.Category, category);
			}
			if(category != null)
			{
				content.put(RacerClubInfo.Category, category);
			}
			if(grade != null)
			{
				content.put(RacerClubInfo.Grade, grade);
			}
			if(speedLevel != null)
			{
				content.put(RacerClubInfo.SpeedLevel, speedLevel);
			}
			int numChanged = context.getContentResolver().update(RacerClubInfo.CONTENT_URI, content, RacerClubInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)});
			
			return numChanged;
		}
    }
}

