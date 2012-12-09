package com.xcracetiming.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceSeries extends ContentProviderTable implements BaseColumns {
    
    private static final RaceSeries instance = new RaceSeries();
    
    public RaceSeries() {}
 
    public static RaceSeries Instance() {
        return instance;
    }

    // Table column
    public static final String SeriesName = "SeriesName";
    public static final String SeriesStartDate = "SeriesStartDate";
    public static final String SeriesEndDate = "SeriesEndDate";
    public static final String SeriesScoringType = "SeriesScoringType";
    
    public String getCreate(){
    	return "create table " + getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + SeriesName + " text not null, " 
    	        + SeriesStartDate + " integer not null,"
    	        + SeriesEndDate + " integer not null,"
    	        + SeriesScoringType + " integer not null);";
    }

    /**
     * 
     * @param context
     * @param raceSeriesName
     * @param seriesStartDate
     * @param seriesEndDate
     * @param seriesScoringType
     * @return
     */
	public Uri Create(Context context, String raceSeriesName, Date seriesStartDate, Date seriesEndDate, String seriesScoringType) {
		ContentValues content = new ContentValues();
		content.put(RaceSeries.SeriesName, raceSeriesName);
		content.put(RaceSeries.SeriesStartDate, seriesStartDate.getTime());
		content.put(RaceSeries.SeriesEndDate, seriesEndDate.getTime());
		content.put(RaceSeries.SeriesScoringType, seriesScoringType);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}

	public int Update(Context context, String where, String[] selectionArgs, String raceSeriesName, Date seriesStartDate, Date seriesEndDate, String seriesScoringType) {
		ContentValues content = new ContentValues();
		if(raceSeriesName != null)
        {
			content.put(RaceSeries.SeriesName, raceSeriesName);
        }
        if(seriesStartDate != null)
        {
        	content.put(RaceSeries.SeriesStartDate, seriesStartDate.getTime());
        }
        if(seriesStartDate != null)
        {
        	content.put(RaceSeries.SeriesEndDate, seriesEndDate.getTime());
        }
        if(seriesScoringType != null)
        {
        	content.put(RaceSeries.SeriesScoringType, seriesScoringType);
        }
		return context.getContentResolver().update(CONTENT_URI, content, where, selectionArgs);
	}
}