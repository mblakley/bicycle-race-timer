package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.TeamCheckInViewInclusive;

// BaseColumn contains _id.
public final class RaceResults extends ContentProviderTable implements BaseColumns {
    
    private static final RaceResults instance = new RaceResults();
    
    public RaceResults() {}
 
    public static RaceResults Instance() {
        return instance;
    }
    
    // Table column
    public static final String RaceCategory_ID = "RaceCategory_ID";
    public static final String BibNumber = "BibNumber";
    public static final String StartOrder = "StartOrder";
    public static final String StartTimeOffset = "StartTimeOffset";
    public static final String StartTime = "StartTime";
    public static final String EndTime = "EndTime";
    public static final String ElapsedTime = "ElapsedTime";
    public static final String OverallPlacing = "OverallPlacing";
    public static final String CategoryPlacing = "CategoryPlacing";
    public static final String Points = "Points";
    public static final String PrimePoints = "PrimePoints";
    
    public String getTableName(){
    	return RaceResults.class.getSimpleName();
    }
    
    public String getCreate(){
    	return "create table " + getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + RaceCategory_ID + " integer references " + RaceCategory.Instance().getTableName() + "(" + RaceCategory._ID + ") not null, "
    	        + BibNumber + " integer not null,"
    	        + StartOrder + " integer not null,"
    	        + StartTimeOffset + " integer not null,"
    	        + StartTime + " integer null,"
    	        + EndTime + " integer null,"
    	        + ElapsedTime + " integer null,"
    	        + OverallPlacing + " integer null,"
    	        + CategoryPlacing + " integer null,"
    	        + Points + " integer not null,"
    	        + PrimePoints + " integer not null"
    	        + ");";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }

	public Uri Create(Context context,
			long raceCategory_ID, long bibNumber, int startOrder,
			Long startTimeOffset, Long startTime, Long endTime,
			Long elapsedTime, Integer overallPlacing,
			Integer categoryPlacing, Integer points, Integer primePoints) {
		ContentValues content = new ContentValues();
     	content.put(RaceResults.RaceCategory_ID, raceCategory_ID);
     	content.put(RaceResults.BibNumber, bibNumber);
     	content.put(RaceResults.StartOrder, startOrder);
     	content.put(RaceResults.StartTimeOffset, startTimeOffset);
     	content.put(RaceResults.StartTime, startTime);
     	content.put(RaceResults.EndTime, endTime);
     	content.put(RaceResults.ElapsedTime, elapsedTime);
     	content.put(RaceResults.OverallPlacing, overallPlacing);
     	content.put(RaceResults.CategoryPlacing, categoryPlacing);
     	content.put(RaceResults.Points, points);
     	content.put(RaceResults.PrimePoints, primePoints);
     	return context.getContentResolver().insert(CONTENT_URI, content);
	}
}