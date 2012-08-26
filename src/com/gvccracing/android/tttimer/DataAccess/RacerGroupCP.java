package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RacerFinishGroupCP.RacerFinishGroup;

public class RacerGroupCP {

    // BaseColumn contains _id.
    public static final class RacerGroup implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerGroup.class.getSimpleName() + "~");

        // Table column
        public static final String GroupDescription = "GroupDescription";
        
        public static String getTableName(){
        	return RacerGroup.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RacerGroup.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + GroupDescription + " string not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerGroup.CONTENT_URI, RacerFinishGroup.CONTENT_URI};
        }

		public static Uri Create(Context context, String groupDescription) {
			ContentValues content = new ContentValues();
			content.put(RacerGroup.GroupDescription, groupDescription);

	     	return context.getContentResolver().insert(RacerGroup.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, String groupDescription) {
			ContentValues content = new ContentValues();
			if(groupDescription != null)
	        {
				content.put(RacerGroup.GroupDescription, groupDescription);
	        }
			return context.getContentResolver().update(RacerGroup.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RacerGroup.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}