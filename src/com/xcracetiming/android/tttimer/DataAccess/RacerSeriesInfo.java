package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;

import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RacerSeriesInfo extends ContentProviderTable implements BaseColumns {
    
    private static final RacerSeriesInfo instance = new RacerSeriesInfo();
    
    public RacerSeriesInfo() {}
 
    public static RacerSeriesInfo Instance() {
        return instance;
    }

    // Table column
    public static final String RacerUSACInfo_ID = "RacerUSACInfo_ID";
    public static final String SeriesBibNumber = "SeriesBibNumber";
    public static final String RaceSeries_ID = "RaceSeries_ID";
    public static final String CurrentRaceCategory_ID = "CurrentRaceCategory_ID";
    public static final String TTPoints = "TTPoints";
    public static final String RRPoints = "RRPoints";
    public static final String PrimePoints = "PrimePoints";
    public static final String BARPoints = "BARPoints";
    public static final String Upgraded = "Upgraded";
    public static final String OnlineRecordID = "OnlineRecordID";
    
    public String getCreate(){
    	return "create table " + getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RacerUSACInfo_ID + " integer references " + RacerUSACInfo.Instance().getTableName() + "(" + RacerUSACInfo._ID + ") not null, " 
    	        + SeriesBibNumber + " integer not null, " 
    	        + RaceSeries_ID + " integer references " + RaceSeries.Instance().getTableName() + "(" + RaceSeries._ID + ") not null, "
    	        + CurrentRaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null, " 
    	        + TTPoints + " integer not null," 
    	        + RRPoints + " integer not null,"
    	        + PrimePoints + " integer not null," 
    	        + BARPoints + " integer not null," 
    	        + Upgraded + " integer not null," 
    	        + OnlineRecordID + " text null"
    			+ ");";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }

	public Uri Create(Context context, long racerUSACInfo_ID, String seriesBibNumber, long raceSeries_ID, long currentRaceCategory_ID, long ttPoints, long rrPoints, long primePoints, Long onlineRecordID) {
		ContentValues content = new ContentValues();
		content.put(RacerSeriesInfo.RacerUSACInfo_ID, racerUSACInfo_ID);
     	content.put(RacerSeriesInfo.SeriesBibNumber, seriesBibNumber);
     	content.put(RacerSeriesInfo.RaceSeries_ID, raceSeries_ID);
     	content.put(RacerSeriesInfo.CurrentRaceCategory_ID, currentRaceCategory_ID);
     	content.put(RacerSeriesInfo.TTPoints, 0);
     	content.put(RacerSeriesInfo.RRPoints, 0);
     	content.put(RacerSeriesInfo.PrimePoints, 0);
     	content.put(RacerSeriesInfo.Upgraded, 0);
     	content.put(RacerSeriesInfo.OnlineRecordID, onlineRecordID);
		return context.getContentResolver().insert(CONTENT_URI, content);
	}

	public Cursor Read(Context context, String barcode, int year) {
		return context.getContentResolver().query(CONTENT_URI, new String[]{RacerSeriesInfo._ID, RacerSeriesInfo.RacerUSACInfo_ID}, RacerSeriesInfo.SeriesBibNumber + "=? AND " + RacerSeriesInfo.RaceSeries_ID + "=?", new String[]{barcode, Integer.toString(year)}, null);
	}
	
	public int Update(Context context, long racerSeriesInfo_ID, Long racerUSACInfo_ID, String seriesBibNumber, Long raceSeries_ID, 
			 Long currentRaceCategory_ID, Long ttPoints, Long rrPoints, Long primePoints, Boolean upgraded, String onlineRecordID) {
		ContentValues content = new ContentValues();
		if(racerUSACInfo_ID != null)
		{
			content.put(RacerSeriesInfo.RacerUSACInfo_ID, racerUSACInfo_ID);
		}
		if(seriesBibNumber != null)
		{
			content.put(RacerSeriesInfo.SeriesBibNumber, seriesBibNumber);
		}
		if(raceSeries_ID != null)
		{
			content.put(RacerSeriesInfo.RaceSeries_ID, raceSeries_ID);
		}
		if(currentRaceCategory_ID != null)
		{
			content.put(RacerSeriesInfo.CurrentRaceCategory_ID, currentRaceCategory_ID);
		}
		if(ttPoints != null)
		{
			content.put(RacerSeriesInfo.TTPoints, ttPoints);
		}
		if(rrPoints != null)
		{
			content.put(RacerSeriesInfo.RRPoints, rrPoints);
		}
		if(primePoints != null)
		{
			content.put(RacerSeriesInfo.PrimePoints, primePoints);
		}
		if(upgraded != null)
		{
			content.put(RacerSeriesInfo.Upgraded, upgraded ? 1 : 0);
		}
		if(onlineRecordID != null)
		{
			content.put(RacerSeriesInfo.OnlineRecordID, onlineRecordID);
		}
		int numChanged = context.getContentResolver().update(CONTENT_URI, content, RacerSeriesInfo._ID + "=?", new String[]{Long.toString(racerSeriesInfo_ID)});
		
		return numChanged;
	}
}