package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;


public class RacerCP {

    // BaseColumn contains _id.
    public static final class Racer implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, Racer.class.getSimpleName() + "~");

        // Table column
        public static final String FirstName = "FirstName";
        public static final String LastName = "LastName";
        public static final String USACNumber = "USACNumber";
        public static final String BirthDate = "BirthDate";
        public static final String PhoneNumber = "PhoneNumber";
        public static final String EmergencyContactName = "EmergencyContactName";
        public static final String EmergencyContactPhoneNumber = "EmergencyContactPhoneNumber";
        
        public static String getTableName(){
        	return Racer.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + getTableName() 
            + " (" + _ID + " integer primary key autoincrement, "
            + FirstName + " text not null, " 
            + LastName + " text not null,"
            + USACNumber + " integer not null,"
            + BirthDate + " integer null,"
            + PhoneNumber + " integer null,"
            + EmergencyContactName + " text null,"
            + EmergencyContactPhoneNumber + " integer null"
            + ");";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{Racer.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI};
        }

		public static Uri Create(Context context, String firstName, String lastName, int usacNumber, 
								long birthDate, int phoneNumber, String emergencyContactName, int emergencyContactPhoneNumber) {
			ContentValues content = new ContentValues();
	     	content.put(Racer.FirstName, firstName);
	     	content.put(Racer.LastName, lastName);
	     	content.put(Racer.USACNumber, usacNumber);
	     	content.put(Racer.BirthDate, birthDate);
	     	content.put(Racer.PhoneNumber, phoneNumber);
	     	content.put(Racer.EmergencyContactName, emergencyContactName);
	     	content.put(Racer.EmergencyContactPhoneNumber, emergencyContactPhoneNumber);
			return context.getContentResolver().insert(Racer.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(Racer.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
		
		public static int Update(Context context, long racer_ID, String firstName, String lastName, Integer usacNumber, Long birthDate, 
								 String phoneNumber, String emergencyContactName, String emergencyContactPhoneNumber) {
			ContentValues content = new ContentValues();
	        if(firstName != null)
	        {
	        	content.put(Racer.FirstName, firstName);
	        }
	        if(lastName != null)
	        {
	        	content.put(Racer.LastName, lastName);
	        }
	        if(usacNumber != null)
	        {
	        	content.put(Racer.USACNumber, usacNumber);
	        }
	        if(birthDate != null)
	        {
	        	content.put(Racer.BirthDate, birthDate);
	        }
	        if(phoneNumber != null)
	        {
	        	content.put(Racer.PhoneNumber, phoneNumber);
	        }
	        if(emergencyContactName != null)
	        {
	        	content.put(Racer.EmergencyContactName, emergencyContactName);
	        }
	        if(emergencyContactPhoneNumber != null)
	        {
	        	content.put(Racer.EmergencyContactPhoneNumber, emergencyContactPhoneNumber);
	        }
			int numChanged = context.getContentResolver().update(Racer.CONTENT_URI, content, Racer._ID + "=?", new String[]{Long.toString(racer_ID)});
			
			return numChanged;
		}
    }
}
