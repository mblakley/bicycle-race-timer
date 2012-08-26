package com.gvccracing.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerGroupCP.RacerGroup;

public class RacerFinishGroupCP {

    // BaseColumn contains _id.
    public static final class RacerFinishGroup implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerFinishGroup.class.getSimpleName() + "~");

        // Table column
        public static final String RacerGroup_ID = "RacerGroup_ID";
        public static final String Race_ID = "Race_ID";
        public static final String RacerClubInfo_ID = "RacerClubInfo_ID";
        public static final String FinishDateTime = "FinishDateTime";
        
        public static String getTableName(){
        	return RacerFinishGroup.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RacerFinishGroup.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RacerGroup_ID + " integer references " + RacerGroup.getTableName() + "(" + RacerGroup._ID + ") not null,"
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null,"
        	        + RacerClubInfo_ID + " integer references " + RacerClubInfo.getTableName() + "(" + RacerClubInfo._ID + ") not null,"
        	        + FinishDateTime + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerFinishGroup.CONTENT_URI};
        }

		public static Uri Create(Context context, long racerGroup_ID, long race_ID, long racerClubInfo_ID, Date finishDateTime) {
			ContentValues content = new ContentValues();
			content.put(RacerFinishGroup.RacerGroup_ID, racerGroup_ID);
			content.put(RacerFinishGroup.Race_ID, race_ID);
			content.put(RacerFinishGroup.RacerClubInfo_ID, racerClubInfo_ID);
			content.put(RacerFinishGroup.FinishDateTime, finishDateTime.getTime());

	     	return context.getContentResolver().insert(RacerFinishGroup.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long racerGroup_ID, Long race_ID, Long racerClubInfo_ID, Date finishDateTime) {
			ContentValues content = new ContentValues();
			if(racerGroup_ID != null)
	        {
				content.put(RacerFinishGroup.RacerGroup_ID, racerGroup_ID);
	        }
	        if(race_ID != null)
	        {
	        	content.put(RacerFinishGroup.Race_ID, race_ID);
	        }
	        if(racerClubInfo_ID != null)
	        {
	        	content.put(RacerFinishGroup.RacerClubInfo_ID, racerClubInfo_ID);
	        }
	        if(finishDateTime != null)
	        {
	        	content.put(RacerFinishGroup.FinishDateTime, finishDateTime.getTime());
	        }
			return context.getContentResolver().update(RacerFinishGroup.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RacerFinishGroup.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}