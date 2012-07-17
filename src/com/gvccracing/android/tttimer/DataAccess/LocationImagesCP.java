package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class LocationImagesCP {

    // BaseColumn contains _id.
    public static final class LocationImages implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, LocationImages.class.getSimpleName() + "~");

        // Table column
        public static final String Image = "Image";
        public static final String Notes = "Notes";
        public static final String RaceLocation_ID = "RaceLocation_ID";
        public static final String DropboxFilename = "DropboxFilename";
        public static final String DropboxRevision = "DropboxRevision";
        
        public static String getTableName(){
        	return LocationImages.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + LocationImages.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + Image + " blob not null, " 
        	        + Notes + " text not null, "
        	        + RaceLocation_ID + " integer not null,"
        	        + DropboxFilename + " text null,"
        	        + DropboxRevision + " text null"
        	        + ");";
        }

		public static Uri Create(Context context, byte[] image, String notes, long raceLocation_ID, String dropboxFilename, String dropboxRevision) {
			ContentValues content = new ContentValues();
	     	content.put(LocationImages.Image, image);
	     	content.put(LocationImages.Notes, notes);
	     	content.put(LocationImages.RaceLocation_ID, raceLocation_ID);
	     	content.put(LocationImages.DropboxFilename, dropboxFilename);
	     	content.put(LocationImages.DropboxRevision, dropboxRevision);

	     	return context.getContentResolver().insert(LocationImages.CONTENT_URI, content);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(LocationImages.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
		
		public static int Update(Context context, long LocationImages_ID, byte[] image, String notes, Long raceLocation_ID, String dropboxFilename, String dropboxRevision) {
			ContentValues content = new ContentValues();
			if(image != null && image.length > 0)
	        {
				content.put(LocationImages.Image, image);
	        }
			if(notes != null)
	        {
				content.put(LocationImages.Notes, notes);
	        }
			if(raceLocation_ID != null)
	        {
				content.put(LocationImages.RaceLocation_ID, raceLocation_ID);
	        }
			if(dropboxFilename != null)
	        {
				content.put(LocationImages.DropboxFilename, dropboxFilename);
	        }
			if(dropboxRevision != null)
	        {
				content.put(LocationImages.DropboxRevision, dropboxRevision);
	        }
			return context.getContentResolver().update(LocationImages.CONTENT_URI, content, LocationImages._ID + "=?", new String[]{Long.toString(LocationImages_ID)});
		}

		public static Uri[] getAllUrisToNotifyOnChange() {
			return new Uri[]{LocationImages.CONTENT_URI};
		}
    }
}
