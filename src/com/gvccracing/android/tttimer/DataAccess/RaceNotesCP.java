package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;

public class RaceNotesCP {

    // BaseColumn contains _id.
    public static final class RaceNotes implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceNotes.class.getSimpleName() + "~");

        // Table column
        public static final String Race_ID = "Race_ID";
        public static final String WeatherNotes = "WeatherNotes";
        public static final String Temperature = "Temperature";
        public static final String WindSpeed = "WindSpeed";
        public static final String WindDirection = "WindDirection";
        public static final String Humidity = "Humidity";
        public static final String OtherNotes = "OtherNotes";
        
        public static String getTableName(){
        	return RaceNotes.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceNotes.getTableName()
                    + " (" + _ID + " integer primary key autoincrement, "
                    + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") null," 
                    + WeatherNotes + " text null," 
                    + Temperature + " text null,"
                    + WindSpeed + " text null,"
                    + WindDirection + " text null,"
                    + Humidity + " text null,"
                    + OtherNotes + " text null"
                    + ");";
        }
        
        private static Uri Create(Context context, ContentValues content) {
	     	return context.getContentResolver().insert(RaceNotes.CONTENT_URI, content);
		}

		public static int Update(Context context, Long race_ID,
				String weatherNotes, Integer temperature, Integer windSpeed,
				String windDirection, Integer humidity, String otherNotes,
				boolean addIfNotExist) {
			ContentValues content = new ContentValues();
	        if(weatherNotes != null)
	        {
	        	content.put(RaceNotes.WeatherNotes, weatherNotes);
	        }
	        if(temperature != null)
	        {
	        	content.put(RaceNotes.Temperature, temperature);
	        }
	        if(windSpeed != null)
	        {
	        	content.put(RaceNotes.WindSpeed, windSpeed);
	        }
	        if(windDirection != null)
	        {
	        	content.put(RaceNotes.WindDirection, windDirection);
	        }
	        if(humidity != null)
	        {
	        	content.put(RaceNotes.Humidity, humidity);
	        }
	        if(otherNotes != null)
	        {
	        	content.put(RaceNotes.OtherNotes, otherNotes);
	        }
	        int numChanged =  context.getContentResolver().update(RaceNotes.CONTENT_URI, content, RaceNotes.Race_ID + "=?", new String[]{race_ID.toString()});
			if(addIfNotExist && numChanged < 1){
				content.put(RaceNotes.Race_ID, race_ID);
				RaceNotes.Create(context, content);
				numChanged = 1;
			}
			
			return numChanged;
		}

		public static Uri[] getAllUrisToNotifyOnChange() {
			return new Uri[]{RaceNotes.CONTENT_URI};
		}
    }
}
