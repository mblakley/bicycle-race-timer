package com.xcracetiming.android.tttimer.WizardPages;

import java.util.Date;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.Utilities.Loaders;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class AddRaceView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceView";
	
	private SimpleCursorAdapter locationsCursorAdapter = null;
	private SimpleCursorAdapter raceTypesCursorAdapter = null;
	/**
     * This is a special intent action that means "load your tab data".
     */
    public static final String RACE_ADDED_ACTION = "com.xcracetiming.android.tttimer.RACE_ADDED";

	private Long raceSeries_ID;	
	
	@Override
	public void setArguments(Bundle args) {
		//this.raceSeries_ID = args.getLong(Race.RaceSeries_ID);
		//AppSettings.Instance().UpdateLong(getActivity(), AppSettings.AppSetting_RaceSeriesID_Name, raceSeries_ID, true);
	}	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		return inflater.inflate(R.layout.wp_add_race, container, false);
	}
	
	@Override
	protected void addListeners() {	
		getSpinner(R.id.spinnerRaceType).setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	// Need to just check the "HasMultipleLaps" field to see if we should configure the number of laps
            	Cursor selectedRaceType = raceTypesCursorAdapter.getCursor();
            	selectedRaceType.moveToPosition(position);
            	
            	// If the race has multiple laps, show the num laps
            	if(selectedRaceType.getInt(selectedRaceType.getColumnIndex(RaceType.HasMultipleLaps)) > 0){
            		if(Long.parseLong(getEditText(R.id.txtNumLaps).getText().toString()) <= 1){
            			getEditText(R.id.txtNumLaps).setText("2");
            		}
            		getLinearLayout(R.id.llLaps).setVisibility(View.VISIBLE);
            	}else{
            		// The race is only a single "lap", so don't show the number of laps
            		getEditText(R.id.txtNumLaps).setText("1");
            		getLinearLayout(R.id.llLaps).setVisibility(View.GONE);
            	}
            }

            public void onNothingSelected(AdapterView<?> parentView) {
            	// Try to select the first item in the list
            	// TODO: Check this!
            	getSpinner(R.id.spinnerRaceType).setSelection(0, false);
            }
        });
	}

	@Override 
	public int GetTitleResourceID() {
		return R.string.AddRace;
	}
	
	@Override
	public void onResume() {
		super.onResume();

//		if(!FindAnyRaceLocations()){
//			// No locations...show another dialog to add a location
//			getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new AddLocationView()).commit();
//		} else{
			// Continue with setting up the race
			
	        // Intervals probably won't ever change, so I'm ok with this for now but...
	        // TODO: change this to use an underlying tag for how long the selected interval is
	        ArrayAdapter<CharSequence> startIntervalAdapter = ArrayAdapter.createFromResource(
	        		getActivity(), R.array.start_interval_array, R.layout.control_simple_spinner );
	        startIntervalAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
	        getSpinner(R.id.spinnerStartInterval).setAdapter(startIntervalAdapter);
			
	        // TODO: Don't like the specific handling for invalid raceSeries_ID - Maybe use a checkbox for "Is this race part of a series?" that will hide or show the available series list, with an option for "Add new series"        
			if(raceSeries_ID == null){
				getTextView(R.id.lblRaceName).setVisibility(View.VISIBLE);
				getEditText(R.id.txtRaceName).setVisibility(View.VISIBLE);
			} else {
				getTextView(R.id.lblRaceName).setVisibility(View.GONE);
				getEditText(R.id.txtRaceName).setVisibility(View.GONE);
			}		
		//}
	}
	
	@Override
	protected void startAllLoaders() {
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(Loaders.RACE_LOCATIONS_LOADER, null, this);
		this.getLoaderManager().initLoader(Loaders.RACE_TYPES_LOADER, null, this);
	}
	
	@Override
	protected void destroyAllLoaders() {
		// Destroy the cursor loaders
		this.getLoaderManager().destroyLoader(Loaders.RACE_LOCATIONS_LOADER);
		this.getLoaderManager().destroyLoader(Loaders.RACE_TYPES_LOADER);
	}
	
	private boolean FindAnyRaceLocations() {
		boolean foundRaceLocations = false;
		try{			
			String[] fieldsToRetrieve = new String[]{RaceLocation._ID, RaceLocation.CourseName};
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = RaceLocation.CourseName;
			Cursor locations = RaceLocation.Instance().Read(getActivity(), fieldsToRetrieve, selection, selectionArgs, sortOrder);
			if(locations != null && locations.getCount() > 0)
	        {
	        	foundRaceLocations = true;
	        }
			locations.close();
			locations = null;
		}catch(Exception ex){Log.e(LOG_TAG, "FindAnyRaceLocations failed", ex);}
		
		return foundRaceLocations;
	}	

//	private boolean FindAnyRaceCategories() {
//		boolean foundRaceCategories = false;
//		try{
//			String[] projection = new String[]{RaceCategory._ID};
//			String selection = null;
//			String[] selectionArgs = null; 
//			String sortOrder = RaceCategory._ID;
//			
//			Cursor raceCat = RaceCategory.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//			
//			if(raceCat != null && raceCat.getCount() > 0){	
//				foundRaceCategories = true;
//			}
//			
//			raceCat.close();
//			raceCat = null;
//     	}catch(Exception ex){Log.e(LOG_TAG, "FindAnyRaceCategories failed", ex);}
//		
//		return foundRaceCategories;
//	}
	
	// TODO: Don't like that this is so hard coded - Need options for start interval, but start interval should be configurable
	protected long GetRaceStartInterval(){
			Spinner startIntervalSpinner = (Spinner) getView().findViewById(R.id.spinnerStartInterval);
			int raceStartIntervalID = (int)startIntervalSpinner.getSelectedItemId();
			long raceStartInterval = 30l;
			switch(raceStartIntervalID){
				case 0:
					raceStartInterval = 30l;
					break;
				case 1:
					raceStartInterval = 60l;
					break;
			}
			
			return raceStartInterval;
	}
	
	protected long GetRaceTypeID(){		
		return getSpinner(R.id.spinnerRaceType).getSelectedItemId();
	}
	
	protected long GetRaceLocationID(){		
		return getSpinner(R.id.spinnerRaceLocation).getSelectedItemId();
	}
	
	protected Date GetRaceDate(){
		DatePicker datePicker = getDatePicker(R.id.dateRaceDate);
		return new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	}

	public void onClick(View v) { 
		try{
			if (v.getId() == R.id.btnAddNewRace){
				// Add the new race
				// TODO: Fill in event name and id (for USAC)
				String eventName = "";
				Long eventID = 0l;
				
				// TODO: What's the difference between club scoring and USAC scoring? - Club scoring gives points based on the club's preferences.  USAC gives upgrade points based on the race type and number of racers starting.
				String scoring = "Club";
				// Don't like the special handling for invalid raceSeries_ID - Should not be necessary
				if(raceSeries_ID == null){
					Uri seriesCreated = RaceSeries.Instance().Create(getActivity(), getEditText(R.id.txtRaceName).getText().toString(), GetRaceDate(), GetRaceDate(), scoring);
					raceSeries_ID = Long.parseLong(seriesCreated.getLastPathSegment());
				}
				
				Uri resultUri = Race.Instance().Create(getActivity(), GetRaceLocationID(), GetRaceDate(), null, GetRaceTypeID(), GetRaceStartInterval(), eventName, eventID, raceSeries_ID, scoring);
	 			long race_ID = Long.parseLong(resultUri.getLastPathSegment());
	 			
	 			// Broadcast that a race was added
    			Intent raceAdded = new Intent();
    			raceAdded.setAction(RACE_ADDED_ACTION);
    			raceAdded.putExtra(AppSettings.AppSetting_RaceID_Name, race_ID);
    			raceAdded.putExtra(AppSettings.AppSetting_StartInterval_Name, GetRaceStartInterval());
    			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(raceAdded);
    			
    			
    			// TODO: Add categories to the race that we just added.  Otherwise, everyone will be in the "General" category
    			//if(!FindAnyRaceCategories()){
    				getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new AddRaceCategoriesView()).commit();
    			//}

    			// Hide the dialog
     			dismiss();
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		switch(id){
			case Loaders.RACE_LOCATIONS_LOADER:
				loader = Loaders.GetAllCourseNames(getActivity());
				break;
			case Loaders.RACE_TYPES_LOADER:
				loader = Loaders.GetAllRaceTypeDescriptions(getActivity());
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			String[] columns;
            int[] to;
			switch(loader.getId()){
				case Loaders.RACE_LOCATIONS_LOADER:
					columns = new String[] { RaceLocation.CourseName };
		            to = new int[] {android.R.id.text1 };
		            
					// Create the cursor adapter for the list of races
		            locationsCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		            locationsCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		        	getSpinner(R.id.spinnerRaceLocation).setAdapter(locationsCursorAdapter);
					
					locationsCursorAdapter.swapCursor(cursor);
					break;
				case Loaders.RACE_TYPES_LOADER:
					columns = new String[] { RaceType.RaceTypeDescription };
		            to = new int[] {android.R.id.text1 };
		            
					// Create the cursor adapter for the list of races
		            raceTypesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		            raceTypesCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		            getSpinner(R.id.spinnerRaceType).setAdapter(raceTypesCursorAdapter);
		        	
					raceTypesCursorAdapter.swapCursor(cursor);
					break;
			}
			Log.v(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case Loaders.RACE_LOCATIONS_LOADER:
					locationsCursorAdapter.swapCursor(null);
					break;
				case Loaders.RACE_TYPES_LOADER:
					raceTypesCursorAdapter.swapCursor(null);
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}
