package com.xcracetiming.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceNotes extends ContentProviderTable implements BaseColumns {
    
    private static final RaceNotes instance = new RaceNotes();
    
    public RaceNotes() {}
 
    public static RaceNotes Instance() {
        return instance;
    }

    // Table column
    public static final String Race_ID = "Race_ID";
    public static final String WeatherNotes = "WeatherNotes";
    public static final String Temperature = "Temperature";
    public static final String WindSpeed = "WindSpeed";
    public static final String WindDirection = "WindDirection";
    public static final String Humidity = "Humidity";
    public static final String OtherNotes = "OtherNotes";        
    
    public String getCreate(){
    	return "create table " + RaceNotes.Instance().getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + Race_ID + " integer references " + Race.Instance().getTableName() + "(" + Race._ID + ") not null," 
                + WeatherNotes + " text null," 
                + Temperature + " text null,"
                + WindSpeed + " text null,"
                + WindDirection + " text null,"
                + Humidity + " integer null,"
                + OtherNotes + " text null"
                + ");";
    }        

	public int Update(Context context, Long race_ID,
			String weatherNotes, Integer temperature, Integer windSpeed,
			String windDirection, Long humidity, String otherNotes,
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
        int numChanged =  context.getContentResolver().update(RaceNotes.Instance().CONTENT_URI, content, RaceNotes.Race_ID + "=?", new String[]{race_ID.toString()});
		if(addIfNotExist && numChanged < 1){
			content.put(RaceNotes.Race_ID, race_ID);
			RaceNotes.Instance().Create(context, content);
			numChanged = 1;
		}
		
		return numChanged;
	}
}