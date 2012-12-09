package com.xcracetiming.android.tttimer.Tabs;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoView;
import com.xcracetiming.android.tttimer.Dialogs.AdminAuthView;
import com.xcracetiming.android.tttimer.Dialogs.AdminMenuView;
import com.xcracetiming.android.tttimer.Dialogs.MarshalLocations;
import com.xcracetiming.android.tttimer.Dialogs.OtherRaceResults;
import com.xcracetiming.android.tttimer.Dialogs.SeriesResultsView;
import com.xcracetiming.android.tttimer.Utilities.Loaders;
import com.xcracetiming.android.tttimer.Utilities.TimeFormatter;

public class RaceInfoTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
	
	private String distanceUnitSetting = "mi";
	private String distanceUnit = "mi";
	private float distance;
	private long raceLocation_ID;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_race_info, container, false);    
    }	
	
	@Override
	protected void addClickListeners(){
        getButton(R.id.btnMarshalLocations).setOnClickListener(this);
        getButton(R.id.btnSeriesResults).setOnClickListener(this);
        getButton(R.id.btnPreviousResults).setOnClickListener(this);
        getButton(R.id.btnAdminMenu).setOnClickListener(this);
	}
	
	@Override
	protected void startAllLoaders(){
		// Initialize the cursor loaders for the race info tab
		getActivity().getSupportLoaderManager().restartLoader(Loaders.RACE_INFO_LOADER, null, this);	 
	}
	
	@Override
	protected void destroyAllLoaders(){
		// destroy the cursor loaders for the race info tab
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.RACE_INFO_LOADER);
	    getActivity().getSupportLoaderManager().destroyLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO);	    
	    getActivity().getSupportLoaderManager().destroyLoader(Loaders.COURSE_RECORD_LOADER);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case Loaders.RACE_INFO_LOADER:				
				projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", RaceSeries.SeriesName, Race.RaceDate, Race.RaceLocation_ID, RaceLocation.CourseName, RaceType.HasMultipleLaps, Race.StartInterval, RaceLocation.Distance, RaceLocation.DistanceUnits, RaceWave.NumLaps, RaceType.RaceTypeDescription};
				selection = Race.Instance().getTableName() + "." + Race._ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.Instance().getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.APP_SETTINGS_LOADER_RACEINFO:
				projection = new String[]{AppSettings.AppSettingValue};
				selection = AppSettings.Instance().getTableName() + "." + AppSettings.AppSettingValue + "=?";
				sortOrder = null;
				selectionArgs = new String[]{AppSettings.AppSetting_DistanceUnits_Name};
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.COURSE_RECORD_LOADER:
				projection = new String[]{RaceResults.ElapsedTime};
				selection = Race.Instance().getTableName() + "." + Race.RaceLocation_ID + "=? and " + RaceResults.ElapsedTime + ">= 0";
				selectionArgs = new String[]{Long.toString(raceLocation_ID)};
				sortOrder = RaceResults.ElapsedTime;
				loader = new CursorLoader(getActivity(), RaceInfoResultsView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			cursor.moveToFirst();			
			switch(loader.getId()){
				case Loaders.RACE_INFO_LOADER:
					if(cursor.getCount() > 0){
						// Get all of the values out of the cursors, and set the textboxes to their values
						String raceSeries = cursor.getString(cursor.getColumnIndex(RaceSeries.SeriesName));						
						Long raceDateMS = cursor.getLong(cursor.getColumnIndex(Race.RaceDate));
						String courseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
						raceLocation_ID = cursor.getLong(cursor.getColumnIndex(Race.RaceLocation_ID));
						String raceTypeName = cursor.getString(cursor.getColumnIndex(RaceType.RaceTypeDescription));
						long startInterval = cursor.getLong(cursor.getColumnIndex(Race.StartInterval));
						String startIntervalText = Long.toString(startInterval);
						boolean hasMultipleLaps = cursor.getInt(cursor.getColumnIndex(RaceType.HasMultipleLaps)) > 0;								
						
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
						distance = cursor.getFloat(cursor.getColumnIndex(RaceLocation.Distance)) * (float)numRaceLaps;	
						
						// Set the race date text in the format M/d/yy
						Date raceDateTemp = new Date(raceDateMS);
						SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy");						
						getTextView(R.id.raceDate).setText(formatter.format(raceDateTemp).toString());
						// Set the course name
						getTextView(R.id.raceCourseName).setText(courseName);
						// Set the race type
						getTextView(R.id.raceType).setText(raceTypeName);
						// Set the race start interval, only if there's a real start interval
						if(startInterval > 0){
							getTextView(R.id.raceStartInterval).setText(startIntervalText);
							getTextView(R.id.llStartInterval).setVisibility(View.VISIBLE);
						} else{
							getTextView(R.id.llStartInterval).setVisibility(View.GONE);
						}
						// Set the text of the race series
						getTextView(R.id.raceSeriesName).setText(raceSeries);	
						// Set the text of the number of race laps
						getTextView(R.id.raceLaps).setText(Long.toString(numRaceLaps));
					}
					getActivity().getSupportLoaderManager().restartLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO, null, this);   
				    getActivity().getSupportLoaderManager().restartLoader(Loaders.COURSE_RECORD_LOADER, null, this);
					break;
				case Loaders.APP_SETTINGS_LOADER_RACEINFO:	
					if(cursor != null && cursor.getCount() > 0){
						distanceUnit = cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue));					
						SetDistance();
					}
					break;	
				case Loaders.COURSE_RECORD_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						long elapsedTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
			        	getTextView(R.id.courseRecord).setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
					}
					break;
			}
			Log.v(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}

	private void SetDistance()
	{
		if (distance > 0f && distanceUnit == "")
		{
			getTextView(R.id.raceDistance).setText(Float.toString(distance));
		}
		else if (distanceUnit != "" && distance > 0)
		{
			float convertedDist = ConvertDistanceUnits(distance, distanceUnit, distanceUnitSetting);
			getTextView(R.id.raceDistance).setText(Float.toString(convertedDist) + " " + distanceUnit);
		}
	}
	
	private float ConvertDistanceUnits(float dist, String fromDistance, String toDistance) {
		// Distance units don't match, so convert the actual distance
		if(fromDistance != toDistance){
			// If going from km to mi
			if(fromDistance != "mi"){
				dist /= 2.54f;
			} else{
				// Otherwise mi to km
				dist *= 2.54;
			}
		}
		
		return dist;
	}

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

	public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		switch (v.getId())
		{
			case R.id.btnMarshalLocations:
				showMarshalLocations(v);
				break;
			case R.id.btnPreviousResults:
				showChoosePreviousRace();
				break;
			case R.id.btnAdminMenu:
				if(AppSettings.Instance().ReadBooleanValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false")){
					AdminMenuView adminMenuDialog = new AdminMenuView();
					adminMenuDialog.show(fm, AdminMenuView.LOG_TAG);
				} else {
					AdminAuthView adminAuthDialog = new AdminAuthView();
			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
				}
				break;
			case R.id.btnSeriesResults:
				SeriesResultsView seriesResultsDialog = new SeriesResultsView();
				seriesResultsDialog.show(fm, SeriesResultsView.LOG_TAG);
				break;
		}
	}

	private void showChoosePreviousRace() {
		OtherRaceResults previousResultsDialog = new OtherRaceResults();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		previousResultsDialog.show(fm, OtherRaceResults.LOG_TAG);
	}

	private void showMarshalLocations(View v) {
		MarshalLocations marshalLocationsDialog = new MarshalLocations();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		marshalLocationsDialog.show(fm, MarshalLocations.LOG_TAG);
	}
}
