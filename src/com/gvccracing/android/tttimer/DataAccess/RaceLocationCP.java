package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class RaceLocationCP {

    // BaseColumn contains _id.
    public static final class RaceLocation implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceLocation.class.getSimpleName() + "~");

        // Table column
        public static final String CourseName = "CourseName";
        public static final String Distance = "Distance";
        public static final String TurnAroundInfo = "TurnAroundInfo";
        public static final String TurnAroundPic = "TurnAroundPic";
        
        public static String getTableName(){
        	return RaceLocation.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceLocation.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + CourseName + " text not null, " 
        	        + Distance + " real not null, "
        	        + TurnAroundInfo + " text null,"
        	        + TurnAroundPic + " blob null"
        	        + ");";
        }

		public static Uri Create(Context context, String courseName2,
				String distance2) {
			ContentValues content = new ContentValues();
	     	content.put(RaceLocation.CourseName, courseName2);
	     	content.put(RaceLocation.Distance, distance2);

	     	return context.getContentResolver().insert(RaceLocation.CONTENT_URI, content);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceLocation.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static Uri[] getAllUrisToNotifyOnChange() {
			return new Uri[]{RaceLocation.CONTENT_URI};
		}
    }
}
