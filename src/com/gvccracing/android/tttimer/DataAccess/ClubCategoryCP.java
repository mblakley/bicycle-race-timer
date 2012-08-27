package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.RaceCategoryCP.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RaceSeriesCP.RaceSeries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ClubCategoryCP {
	// BaseColumn contains _id.
    public static final class ClubCategory implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, ClubCategory.class.getSimpleName() + "~");

        // Table column
        public static final String RaceSeries_ID = "RaceSeries_ID";
        public static final String RaceCategory_ID = "RaceCategory_ID";
        
        public static String getTableName(){
        	return ClubCategory.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + ClubCategory.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + RaceSeries_ID + " integer references " + RaceSeries.getTableName() + "(" + RaceSeries._ID + ") not null,"
        	        + RaceCategory_ID + " integer references " + RaceCategory.getTableName() + "(" + RaceCategory._ID + ") not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{ClubCategory.CONTENT_URI};
        }

		public static Uri Create(Context context, long raceSeries_ID, String raceCategory_ID) {
			ContentValues content = new ContentValues();
			content.put(ClubCategory.RaceSeries_ID, raceSeries_ID);
			content.put(ClubCategory.RaceCategory_ID, raceCategory_ID);

	     	return context.getContentResolver().insert(ClubCategory.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long raceSeries_ID, Long raceCategory_ID) {
			ContentValues content = new ContentValues();
			if(raceSeries_ID != null)
	        {
				content.put(ClubCategory.RaceSeries_ID, raceSeries_ID);
	        }
	        if(raceCategory_ID != null)
	        {
	        	content.put(ClubCategory.RaceCategory_ID, raceCategory_ID);
	        }
			return context.getContentResolver().update(ClubCategory.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(ClubCategory.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
