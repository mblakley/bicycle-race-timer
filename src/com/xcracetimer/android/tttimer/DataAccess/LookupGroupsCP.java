package com.xcracetimer.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class LookupGroupsCP {
	
    // BaseColumn contains _id.
    public static final class LookupGroups implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, LookupGroups.class.getSimpleName());

        // Table column
        public static final String LookupGroup = "LookupGroup";
        public static final String LookupValue = "LookupValue";
        
        // Groups
        public static final String Lookup_Group_Category = "Category";
        public static final String Lookup_Group_Humidity = "Humidity";
        
        public static String getTableName(){
        	return LookupGroups.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + LookupGroups.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + LookupGroup + " text not null, "
        	        + LookupValue + " text not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{LookupGroups.CONTENT_URI};
        }

		public static int Update(Context context, Long Lookup_ID, String LookupGroup, String LookupValue, boolean addIfNotExist) {
			ContentValues content = new ContentValues();
			if(LookupGroup != null)
	        {
	        	content.put(LookupGroups.LookupGroup, LookupGroup);
	        }
	        if(LookupValue != null)
	        {
	        	content.put(LookupGroups.LookupValue, LookupValue);
	        }
			int numChanged = context.getContentResolver().update(LookupGroups.CONTENT_URI, content, LookupGroups._ID + "=?", new String[]{Long.toString(Lookup_ID)});
			if(addIfNotExist && numChanged < 1){
				LookupGroups.Create(context, content);
				numChanged = 1;
			}
			
			return numChanged;
		}


		private static Uri Create(Context context, ContentValues content) {
	     	return context.getContentResolver().insert(LookupGroups.CONTENT_URI, content);
		}

		public static Cursor Read(Context context, String LookupGroupToRetrieve) {
			return context.getContentResolver().query(LookupGroups.CONTENT_URI, new String[]{LookupGroups._ID, LookupGroups.LookupGroup, LookupGroups.LookupValue}, LookupGroups.LookupGroup + "=?", new String[]{LookupGroupToRetrieve}, null); 
		}
		
		public static List<String> ReadValue(Context context, String LookupGroupToRetrieve) {
			Cursor temp = LookupGroups.Read(context, LookupGroupToRetrieve);

			List<String> vals = new ArrayList<String>();
			if(temp != null){
				temp.moveToFirst();
				while(!temp.isAfterLast()){
					vals.add(temp.getString(temp.getColumnIndex(LookupGroups.LookupValue)));
				}
			}
			temp.close();
			temp = null;
			return vals;
		}
    }
}
