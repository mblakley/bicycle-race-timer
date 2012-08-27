package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceCategoryCP.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfoCP.RacerUSACInfo;

public class RacerRegistrationCP {
	// BaseColumn contains _id.
    public static final class RacerRegistration implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerRegistration.class.getSimpleName() + "~");

        // Table column
        public static final String RacerUSACInfo_ID = "RacerUSACInfo_ID";
        public static final String Race_ID = "Race_ID";
        public static final String RaceCategory_ID = "RaceCategory_ID";
        public static final String BibNumber = "BibNumber";
        
        public static String getTableName(){
        	return RacerRegistration.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RacerRegistration.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RacerUSACInfo_ID + " integer references " + RacerUSACInfo.getTableName() + "(" + RacerUSACInfo._ID + ") not null,"
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null,"
        	        + RaceCategory_ID + " integer references " + RaceCategory.getTableName() + "(" + RaceCategory._ID + ") not null,"
   	        		+ BibNumber + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerRegistration.CONTENT_URI};
        }

		public static Uri Create(Context context, long racerUSACInfo_ID, long race_ID, long raceCategory_ID, long bibNumber) {
			ContentValues content = new ContentValues();
			content.put(RacerRegistration.RacerUSACInfo_ID, racerUSACInfo_ID);
			content.put(RacerRegistration.Race_ID, race_ID);
			content.put(RacerRegistration.RaceCategory_ID, raceCategory_ID);
			content.put(RacerRegistration.BibNumber, bibNumber);

	     	return context.getContentResolver().insert(RacerRegistration.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long racerUSACInfo_ID, Long race_ID, Long raceCategory_ID, Long bibNumber) {
			ContentValues content = new ContentValues();
			if(racerUSACInfo_ID != null)
	        {
				content.put(RacerRegistration.RacerUSACInfo_ID, racerUSACInfo_ID);
	        }
			if(race_ID != null)
	        {
	        	content.put(RacerRegistration.Race_ID, race_ID);
	        }
	        if(raceCategory_ID != null)
	        {
	        	content.put(RacerRegistration.RaceCategory_ID, raceCategory_ID);
	        }
	        if(bibNumber != null)
	        {
	        	content.put(RacerRegistration.BibNumber, bibNumber);
	        }
			return context.getContentResolver().update(RacerRegistration.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RacerRegistration.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
