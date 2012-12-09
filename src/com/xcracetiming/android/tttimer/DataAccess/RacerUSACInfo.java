package com.xcracetiming.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RacerUSACInfo extends ContentProviderTable implements BaseColumns {
    
    private static final RacerUSACInfo instance = new RacerUSACInfo();
    
    public RacerUSACInfo() {}
 
    public static RacerUSACInfo Instance() {
        return instance;
    }

    // Table column
    public static final String Racer_ID = "Racer_ID";
    public static final String USACClubName = "USACClubName";
    public static final String USACTeamName = "USACTeamName";
    public static final String USACNumber = "USACNumber";
    public static final String USACCategory = "USACCategory";
    public static final String LicenseType = "LicenseType";
    public static final String IsCurrent = "IsCurrent";
    public static final String UpdateDate = "UpdateDate";
    
    public String getCreate(){
    	return "create table " + getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + Racer_ID + " integer references " + Racer.Instance().getTableName() + "(" + Racer._ID + ") not null,"
    	        + USACClubName + " text null,"
    	        + USACTeamName + " text null,"
    	        + USACNumber + " text not null,"
    	        + USACCategory + " text null,"
    	        + LicenseType + " text null,"
    	        + IsCurrent + " integer null,"
    	        + UpdateDate + " integer null);";
    }

	public Uri Create(Context context, long racer_ID, String usacTeamName, String usacNumber, String usacCategory, String licenseType, boolean isCurrent, Long updateDateTime) {
		ContentValues content = new ContentValues();
		content.put(RacerUSACInfo.Racer_ID, racer_ID);
		content.put(RacerUSACInfo.USACTeamName, usacTeamName);
		content.put(RacerUSACInfo.USACNumber, usacNumber);
		content.put(RacerUSACInfo.USACCategory, usacCategory);
		content.put(RacerUSACInfo.LicenseType, licenseType);
		content.put(RacerUSACInfo.IsCurrent, isCurrent ? 1l : 0l);
		content.put(RacerUSACInfo.UpdateDate, updateDateTime);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}

	public int Update(Context context, String where, String[] selectionArgs, Long racer_ID, String usacTeamName, String usacNumber, String usacCategory, String licenseType, Boolean isCurrent, Date updateDate) {
		ContentValues content = new ContentValues();
		if(racer_ID != null)
        {
			content.put(RacerUSACInfo.Racer_ID, racer_ID);
        }
        if(usacTeamName != null)
        {
        	content.put(RacerUSACInfo.USACTeamName, usacTeamName);
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
		return context.getContentResolver().update(CONTENT_URI, content, where, selectionArgs);
	}
}