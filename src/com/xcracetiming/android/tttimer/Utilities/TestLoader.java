package com.xcracetiming.android.tttimer.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Testing a different way to do the loaders instead of having the activities/fragments implement this interface
 * @author mab
 *
 */
public class TestLoader implements LoaderManager.LoaderCallbacks<Cursor> {

	FragmentActivity fragActivity;
	View v;
	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();
	
	public TestLoader(FragmentActivity activity, View view){
		fragActivity = activity;
		v = view;
	}

	private String LOG_TAG(){
		
		return "testLoader";
	}
	
	private FragmentActivity getActivity(){
		return fragActivity;
	}
	
	public TextView getTextView(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) v.findViewById(id));
		}
		return (TextView)viewList.get(id);
	}
	
	public LinearLayout getLinearLayout(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (LinearLayout) v.findViewById(id));
		}
		return (LinearLayout)viewList.get(id);
	}
	
	/**
	 * Create the loader with the given id.
	 * @param id - The id of the loader to create.
	 * @param args - A list that can be filled with parameters to be used in the loader's query
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;	
		switch(id){
			case Loaders.RACE_INFO_LOADER:					
				//loader = Loaders.GetRaceInfo(getActivity());
				break;
			case Loaders.APP_SETTINGS_LOADER_RACEINFO:				
				//loader = Loaders.GetDistanceUnits(getActivity());
				break;
			case Loaders.COURSE_RECORD_LOADER:
				//loader = Loaders.GetCourseRecord(getActivity(), args);
				break;
		}
		Log.v(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	/**
	 * The cursor loader is finished, so get the results and do something with them.
	 * @param loader - The loader that finished loading
	 * @param cursor - The cursor that is filled the result of the loader's query
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			if(cursor != null && cursor.getCount() > 0){
				cursor.moveToFirst();			
				switch(loader.getId()){
					case Loaders.RACE_INFO_LOADER:
						// Get all of the values out of the cursors, and set the textboxes to their values
						String raceSeries = cursor.getString(cursor.getColumnIndex(RaceSeries.SeriesName));						
						Long raceDateMS = cursor.getLong(cursor.getColumnIndex(Race.RaceDate));
						String courseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
						long raceLocation_ID = cursor.getLong(cursor.getColumnIndex(Race.RaceLocation_ID));
						String raceTypeName = cursor.getString(cursor.getColumnIndex(RaceType.RaceTypeDescription));
						long startInterval = cursor.getLong(cursor.getColumnIndex(Race.StartInterval));
						String startIntervalText = Long.toString(startInterval);
						boolean hasMultipleLaps = cursor.getInt(cursor.getColumnIndex(RaceType.HasMultipleLaps)) > 0;
						//distanceUnit = cursor.getString(cursor.getColumnIndex(RaceLocation.DistanceUnit));	
						
						// Show the race laps if there's more than 1 total lap, or if the race can have multiple laps
						long numRaceLaps = 1;
						if(hasMultipleLaps){		
							getLinearLayout(R.id.llRaceLaps).setVisibility(View.GONE);
						}else{
							numRaceLaps = cursor.getLong(cursor.getColumnIndex(RaceWave.NumLaps));
	
							// You can't do 0 laps, or there wouldn't be a race!  Default it to 1.
							if(numRaceLaps <= 0){
								numRaceLaps = 1;
							}	
							
							getLinearLayout(R.id.llRaceLaps).setVisibility(View.VISIBLE);							
						}			
						
						// Get the distance for a single lap of the course
						//distance = cursor.getFloat(cursor.getColumnIndex(RaceLocation.Distance)) * (float)numRaceLaps;	
						
						// Set the race date text in the format M/d/yy
						Date raceDateTemp = new Date(raceDateMS);
						SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy", Locale.US);						
						getTextView(R.id.raceDate).setText(formatter.format(raceDateTemp).toString());
						// Set the course name
						getTextView(R.id.raceCourseName).setText(courseName);
						// Set the race type
						getTextView(R.id.raceType).setText(raceTypeName);
						// Set the race start interval, only if there's a real start interval
						if(startInterval > 0) {
							getTextView(R.id.raceStartInterval).setText(startIntervalText);
							getLinearLayout(R.id.llStartInterval).setVisibility(View.VISIBLE);
						} else { 
							getLinearLayout(R.id.llStartInterval).setVisibility(View.GONE);
						}
						// Set the text of the race series
						getTextView(R.id.raceSeriesName).setText(raceSeries);	
						// Set the text of the number of race laps
						getTextView(R.id.raceLaps).setText(Long.toString(numRaceLaps));
					
						getActivity().getSupportLoaderManager().restartLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO, null, this); 
						Bundle b = new Bundle();
						b.putLong(Race.RaceLocation_ID, raceLocation_ID);
					    getActivity().getSupportLoaderManager().restartLoader(Loaders.COURSE_RECORD_LOADER, b, this);
						break;
					case Loaders.APP_SETTINGS_LOADER_RACEINFO:					
						//distanceUnitSetting = cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue));					
						//SetDistance(distance, distanceUnit, distanceUnitSetting);
						break;	
					case Loaders.COURSE_RECORD_LOADER:
						long elapsedTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
			        	getTextView(R.id.courseRecord).setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
						break;
				}
			}
			Log.v(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}

	/**
	 * The loader has been reset.  Really this should be used for cleaning up binding.
	 * @param loader - The loader that was reset.
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case Loaders.RACE_INFO_LOADER:
					break;
				case Loaders.APP_SETTINGS_LOADER_RACEINFO:
					break;
				case Loaders.COURSE_RECORD_LOADER:
					break;
			}
			Log.v(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}

}
