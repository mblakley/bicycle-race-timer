package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class LocationImages extends ContentProviderTable implements BaseColumns {
    
    private static final LocationImages instance = new LocationImages();
    
    public LocationImages() {}
 
    public static LocationImages Instance() {
        return instance;
    }

    // Table column
    public static final String Image = "Image";
    public static final String Notes = "Notes";
    public static final String RaceLocation_ID = "RaceLocation_ID";
    public static final String DropboxFilename = "DropboxFilename";
    public static final String DropboxRevision = "DropboxRevision";    
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Image + " blob not null, " 
    	        + Notes + " text not null, "
    	        + RaceLocation_ID + " integer not null,"
    	        + DropboxFilename + " text null,"
    	        + DropboxRevision + " text null"
    	        + ");";
    }

	public Uri Create(Context context, byte[] image, String notes, long raceLocation_ID, String dropboxFilename, String dropboxRevision) {
		ContentValues content = new ContentValues();
     	content.put(LocationImages.Image, image);
     	content.put(LocationImages.Notes, notes);
     	content.put(LocationImages.RaceLocation_ID, raceLocation_ID);
     	content.put(LocationImages.DropboxFilename, dropboxFilename);
     	content.put(LocationImages.DropboxRevision, dropboxRevision);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
	
	public int Update(Context context, long LocationImages_ID, byte[] image, String notes, Long raceLocation_ID, String dropboxFilename, String dropboxRevision) {
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
		return context.getContentResolver().update(CONTENT_URI, content, LocationImages._ID + "=?", new String[]{Long.toString(LocationImages_ID)});
	}
}