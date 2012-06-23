package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;


public class UnassignedTimesCP {

    // BaseColumn contains _id.
    public static final class UnassignedTimes implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, UnassignedTimes.class.getSimpleName() + "~");

        // Table column
        public static final String Race_ID = "Race_ID";
        public static final String FinishTime = "FinishTime";
        
        public static String getTableName(){
        	return UnassignedTimes.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + UnassignedTimes.getTableName()
                    + " (" + _ID + " integer primary key autoincrement, "
                    + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null," 
                    + FinishTime + " integer not null"
                    + ");";
        }

		public static Uri[] getAllUrisToNotifyOnChange() {
			return new Uri[]{UnassignedTimes.CONTENT_URI};
		}

		public static int Delete(Context context, String selection, String[] selectionArgs) {
			return context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, selection, selectionArgs);
		}
    }
}
