package com.gvccracing.android.tttimer.DataAccess;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class RaceSeriesCP {

    // BaseColumn contains _id.
    public static final class RaceSeries implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceSeries.class.getSimpleName() + "~");

        // Table column
        public static final String SeriesName = "SeriesName";
        public static final String SeriesStartDate = "SeriesStartDate";
        public static final String SeriesEndDate = "SeriesEndDate";
        public static final String SeriesScoringType = "SeriesScoringType";
        
        public static String getTableName(){
        	return RaceSeries.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceSeries.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + SeriesName + " text not null, " 
        	        + SeriesStartDate + " integer not null,"
        	        + SeriesEndDate + " integer not null,"
        	        + SeriesScoringType + " integer not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceSeries.CONTENT_URI};
        }

		public static Uri Create(Context context, String raceSeriesName, Date seriesStartDate, Date seriesEndDate, String seriesScoringType) {
			ContentValues content = new ContentValues();
			content.put(RaceSeries.SeriesName, raceSeriesName);
			content.put(RaceSeries.SeriesStartDate, seriesStartDate.getTime());
			content.put(RaceSeries.SeriesEndDate, seriesEndDate.getTime());
			content.put(RaceSeries.SeriesScoringType, seriesScoringType);

	     	return context.getContentResolver().insert(RaceSeries.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, String raceSeriesName, Date seriesStartDate, Date seriesEndDate, String seriesScoringType) {
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
			return context.getContentResolver().update(RaceSeries.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceSeries.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}