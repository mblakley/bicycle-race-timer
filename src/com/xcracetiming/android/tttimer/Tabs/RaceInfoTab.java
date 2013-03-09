package com.xcracetiming.android.tttimer.Tabs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.Dialogs.AdminAuthView;
import com.xcracetiming.android.tttimer.Dialogs.OtherRaceResults;
import com.xcracetiming.android.tttimer.Loaders.IRaceInfoTabLoaderFactory;
import com.xcracetiming.android.tttimer.Loaders.BicycleRaceInfoLoaderFactory;
import com.xcracetiming.android.tttimer.Utilities.Loaders;
import com.xcracetiming.android.tttimer.Utilities.TimeFormatter;
import com.xcracetiming.android.tttimer.WizardPages.AdminMenuView;
import com.xcracetiming.android.tttimer.WizardPages.MarshalLocations;
import com.xcracetiming.android.tttimer.Wizards.AddRaceWizard;

/**
 * RaceInfoTab displays relevant information about the selected race.  This includes:
 * - Race Series Name
 * - Date
 * - Course name
 * - Race type (TT, TTT, Crit, etc)
 * - Start interval (for TT races)
 * - Course distance (w/distance unit)
 * - Course record
 * 
 * @author mab
 *
 */
public class RaceInfoTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private String distanceUnitSetting = "mi";
	private String distanceUnit = "mi";
	private float distance;
	
	//private TestLoader loader;
	
	private IRaceInfoTabLoaderFactory loaderFactory;
	
	/**
	 * Inflates the view from xml, adds click listeners, and returns it
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Override	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tab_race_info, container, false);
		
		addClickListener(view, R.id.btnSeriesResults);
		addClickListener(view, R.id.btnPreviousResults);
		addClickListener(view, R.id.btnAdminMenu);
		addClickListener(view, R.id.btnMarshalLocations);
		        
		//loader = new TestLoader(getActivity(), view);
		loaderFactory = new BicycleRaceInfoLoaderFactory();
		
        return view;    
    }
	
	/**
	 * Start the chain of loaders.  Called from base.onResume
	 */
	@Override
	protected void startAllLoaders(){
		// Initialize the cursor loaders for the race info tab
		getActivity().getSupportLoaderManager().restartLoader(Loaders.RACE_INFO_LOADER, null, this);	 
	}
	
	/**
	 * Shut down the all of the loaders when the screen is hidden, so we're not doing updates unless the screen is focused
	 */
	@Override
	protected void destroyAllLoaders(){
		// destroy the cursor loaders for the race info tab
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.RACE_INFO_LOADER);
	    getActivity().getSupportLoaderManager().destroyLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO);	    
	    getActivity().getSupportLoaderManager().destroyLoader(Loaders.COURSE_RECORD_LOADER);
	}
	
	
	/**
	 * Called when a control that is subscribed to this fragment as a click listener is clicked.
	 * 
	 * @param v - The view that was clicked
	 */
	@Override
	public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		switch (v.getId())
		{
			case R.id.btnMarshalLocations:
				showMarshalLocations();
				break;
			case R.id.btnPreviousResults:
				showChoosePreviousRace();
				break;
			case R.id.btnAdminMenu:
				if(AppSettings.Instance().ReadBooleanValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false")){					
					Intent showAdminView = new Intent();
					showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAdminView.putExtra("ShowView", new AdminMenuView().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
				} else {
					AdminAuthView adminAuthDialog = new AdminAuthView();
			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
				}
				break;
			case R.id.btnSeriesResults:
				Intent showSeriesResultsView = new Intent();
				showSeriesResultsView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
				showSeriesResultsView.putExtra("ShowView", new AddRaceWizard().getClass().getCanonicalName());
				showSeriesResultsView.putExtra("ShowTimer", false);
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showSeriesResultsView);
				break;
		}
	}
	
	/**
	 * Set the distance display.  May need conversion between units.
	 * 
	 * @param dist - The number of miles or km
	 * @param fromDistance - The distance unit to convert from
	 * @param toDistance - The distance unit to convert to
	 */
	private void SetDistance(float dist, String fromDistance, String toDistance)
	{
		if (dist > 0f && fromDistance == "")
		{
			getTextView(R.id.raceDistance).setText(Float.toString(dist));
		}
		else if (fromDistance != "" && dist > 0)
		{
			float convertedDist = ConvertDistanceUnits(dist, fromDistance, toDistance);
			getTextView(R.id.raceDistance).setText(Float.toString(convertedDist) + " " + fromDistance);
		}
	}
	
	/**
	 * Convert the distance units from whatever unit they are stored in within the RaceLocation table to the unit that is configured in the AppSettings table
	 * - If going from km to miles, multiply by 0.621371
	 * - If going from miles to km, divide by 0.621371
	 * @param dist - The distance to be converted
	 * @param fromDistance - The unit to convert from - Can be "mi" or "km"
	 * @param toDistance - The unit to convert from - Can be "mi" or "km"
	 * @return The converted distance in the unit from the AppSettings table
	 */
	private float ConvertDistanceUnits(float dist, String fromDistance, String toDistance) {
		// Distance units don't match, so convert the actual distance
		if(fromDistance != toDistance){
			// If going from km to mi
			if(fromDistance != "mi"){
				dist *= 0.621371f;
			} else{
				// Otherwise mi to km
				dist /= 0.621371f;
			}
		}
		
		return dist;
	}

	/**
	 * Show a dialog to allow the user to choose a different race to display
	 */
	private void showChoosePreviousRace() {
		OtherRaceResults previousResultsDialog = new OtherRaceResults();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		previousResultsDialog.show(fm, OtherRaceResults.LOG_TAG);
	}

	/**
	 * Show a dialog with the marshal locations for the current course
	 */
	private void showMarshalLocations() {			
		Intent showMarshalLocationsView = new Intent();
		showMarshalLocationsView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
		showMarshalLocationsView.putExtra("ShowView", new MarshalLocations().getClass().getCanonicalName());
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showMarshalLocationsView);
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
				loader = loaderFactory.GetRaceInfo(getActivity());
				break;
			case Loaders.APP_SETTINGS_LOADER_RACEINFO:				
				loader = Loaders.GetDistanceUnits(getActivity());
				break;
			case Loaders.COURSE_RECORD_LOADER:
				loader = loaderFactory.GetCourseRecord(getActivity(), args);
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
						distance = cursor.getFloat(cursor.getColumnIndex(RaceLocation.Distance)) * (float)numRaceLaps;	
						
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
						distanceUnitSetting = cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue));					
						SetDistance(distance, distanceUnit, distanceUnitSetting);
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
