package com.gvccracing.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;

public class RaceMeetCP {

    // BaseColumn contains _id.
    public static final class RaceMeet implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceMeet.class.getSimpleName() + "~");

        // Table column
        public static final String RaceMeetDate = "RaceMeetDate";
        public static final String RaceLocation_ID = "RaceLocation_ID";
        
        public static String getTableName(){
        	return RaceMeet.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceMeet.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RaceMeetDate + " integer not null, " 
        	        + RaceLocation_ID + " integer references " + RaceLocation.getTableName() + "(" + RaceLocation._ID + ") not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceMeet.CONTENT_URI, MeetTeamsView.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI, RaceInfoView.CONTENT_URI, RaceLocation.CONTENT_URI};
        }

		public static Uri Create(Context context, Date raceMeetDate, Long raceLocation_ID) {
			ContentValues content = new ContentValues();
			content.put(RaceMeet.RaceMeetDate, raceMeetDate.getTime());
			content.put(RaceMeet.RaceLocation_ID, raceLocation_ID);

	     	return context.getContentResolver().insert(RaceMeet.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long raceMeet_ID, Long raceLocation_ID, Date raceMeetDate) {
			ContentValues content = new ContentValues();
			if(raceLocation_ID != null)
	        {
				content.put(RaceMeet.RaceLocation_ID, raceLocation_ID);
	        }
	        if(raceMeetDate != null)
	        {
	        	content.put(RaceMeet.RaceMeetDate, raceMeetDate.getTime());
	        }
	        
			return context.getContentResolver().update(RaceMeet.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceMeet.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
