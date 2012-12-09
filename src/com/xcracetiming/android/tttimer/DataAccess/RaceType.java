package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceType extends ContentProviderTable implements BaseColumns {
    
    private static final RaceType instance = new RaceType();
    
    public RaceType() {}
 
    public static RaceType Instance() {
        return instance;
    }

    // Table column
    public static final String RaceTypeDescription = "RaceTypeDescription";
    public static final String LicenseType = "LicenseType";
    public static final String IsTeamRace = "IsTeamRace";
    public static final String HasMultipleLaps = "HasMultipleLaps";
    
    public String getCreate(){
    	return "create table " + getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceTypeDescription + " text not null,"
    	        + LicenseType + " text not null," 
    	        + IsTeamRace + " integer not null,"
    	        + HasMultipleLaps + " integer not null"
    	        + ");";
    }        

	public Uri Create(Context context, String raceTypeDescription, String licenseType, boolean isTeamRace) {
		ContentValues content = new ContentValues();
		content.put(RaceType.RaceTypeDescription, raceTypeDescription);
		content.put(RaceType.LicenseType, licenseType);
		content.put(RaceType.IsTeamRace, isTeamRace ? Long.toString(1l) : Long.toString(0l));
		
     	return context.getContentResolver().insert(CONTENT_URI, content);
	}

	public int Update(Context context, String where, String[] selectionArgs, String raceTypeDescription, String licenseType, Boolean isTeamRace) {
		ContentValues content = new ContentValues();
		if(raceTypeDescription != null)
        {
			content.put(RaceType.RaceTypeDescription, raceTypeDescription);
        }
		if(licenseType != null)
        {
			content.put(RaceType.LicenseType, licenseType);
        }
		if(isTeamRace != null)
        {
			content.put(RaceType.LicenseType, isTeamRace ? Long.toString(1l) : Long.toString(0l));
        }
		return context.getContentResolver().update(CONTENT_URI, content, where, selectionArgs);
	}
}
