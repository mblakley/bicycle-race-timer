package com.gvccracing.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;

public class RacerUSACInfoCP {
	// BaseColumn contains _id.
    public static final class RacerUSACInfo implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RacerUSACInfo.class.getSimpleName() + "~");

        // Table column
        public static final String Racer_ID = "Racer_ID";
        public static final String TeamName = "TeamName";
        public static final String USACNumber = "USACNumber";
        public static final String USACCategory = "USACCategory";
        public static final String LicenseType = "LicenseType";
        public static final String IsCurrent = "IsCurrent";
        public static final String UpdateDate = "UpdateDate";
        
        public static String getTableName(){
        	return RacerUSACInfo.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RacerUSACInfo.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + Racer_ID + " integer references " + Racer.getTableName() + "(" + Racer._ID + ") not null,"
        	        + TeamName + " text null"
        	        + USACNumber + " text not null"
        	        + USACCategory + " text null"
        	        + LicenseType + " text null"
        	        + IsCurrent + " integer null"
        	        + UpdateDate + " integer null";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RacerUSACInfo.CONTENT_URI};
        }

		public static Uri Create(Context context, long racer_ID, String teamName, String usacNumber, String usacCategory, String licenseType, boolean isCurrent, Date updateDate) {
			ContentValues content = new ContentValues();
			content.put(RacerUSACInfo.Racer_ID, racer_ID);
			content.put(RacerUSACInfo.TeamName, teamName);
			content.put(RacerUSACInfo.USACNumber, usacNumber);
			content.put(RacerUSACInfo.USACCategory, usacCategory);
			content.put(RacerUSACInfo.LicenseType, licenseType);
			content.put(RacerUSACInfo.IsCurrent, isCurrent ? 1l : 0l);
			content.put(RacerUSACInfo.UpdateDate, updateDate.getTime());

	     	return context.getContentResolver().insert(RacerUSACInfo.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, Long racer_ID, String teamName, String usacNumber, String usacCategory, String licenseType, Boolean isCurrent, Date updateDate) {
			ContentValues content = new ContentValues();
			if(racer_ID != null)
	        {
				content.put(RacerUSACInfo.Racer_ID, racer_ID);
	        }
	        if(teamName != null)
	        {
	        	content.put(RacerUSACInfo.TeamName, teamName);
	        }
	        if(usacNumber != null)
	        {
	        	content.put(RacerUSACInfo.USACNumber, usacNumber);
	        }
	        if(usacCategory != null)
	        {
	        	content.put(RacerUSACInfo.USACCategory, usacCategory);
	        }
	        if(licenseType != null)
	        {
	        	content.put(RacerUSACInfo.LicenseType, licenseType);
	        }
	        if(isCurrent != null)
	        {
	        	content.put(RacerUSACInfo.IsCurrent, isCurrent);
	        }
	        if(updateDate != null)
	        {
	        	content.put(RacerUSACInfo.UpdateDate, updateDate.getTime());
	        }
			return context.getContentResolver().update(RacerUSACInfo.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RacerUSACInfo.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
