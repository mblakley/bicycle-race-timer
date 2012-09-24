package com.gvccracing.android.tttimer.Dialogs;

import java.util.Date;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceSeries;
import com.gvccracing.android.tttimer.DataAccess.RaceType;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class AddRaceView extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceView";
	
	protected Button btnAddNewRace;
	private SimpleCursorAdapter locationsCursorAdapter = null;
	private SimpleCursorAdapter raceTypesCursorAdapter = null;
	protected Spinner raceLocation = null;
	protected Spinner raceType = null;
	private EditText txtLaps = null;
	private TextView lblRaceName = null;
	private EditText txtRaceName = null;
	/**
     * This is a special intent action that means "load your tab data".
     */
    public static final String RACE_ADDED_ACTION = "com.gvccracing.android.tttimer.RACE_ADDED";

	private static final int RACE_TYPES_LOADER = 1014;
	private long raceSeries_ID;
	
	public AddRaceView(long raceSeries_ID) {
		this.raceSeries_ID = raceSeries_ID;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceSeriesID_Name, Long.toString(raceSeries_ID), true);
		View v = inflater.inflate(R.layout.dialog_add_race, container, false);

		btnAddNewRace = (Button) v.findViewById(R.id.btnAddNewRace);
		btnAddNewRace.setOnClickListener(this);		
		
		raceLocation = (Spinner) v.findViewById(R.id.spinnerRaceLocation);		
		
		txtLaps = (EditText) v.findViewById(R.id.txtNumLaps);
		
		lblRaceName = (TextView) v.findViewById(R.id.lblRaceName);
		txtRaceName = (EditText) v.findViewById(R.id.txtRaceName);
        
        raceType = (Spinner) v.findViewById(R.id.spinnerRaceType);
        raceType.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        		LinearLayout laps = (LinearLayout) getView().findViewById(R.id.llLaps);
            	if(id == 1){
            		txtLaps.setText("1");
            		laps.setVisibility(View.GONE);
            	}else{
            		if(Long.parseLong(txtLaps.getText().toString()) <= 1){
            			txtLaps.setText("2");
            		}
            		laps.setVisibility(View.VISIBLE);
            	}
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
		
        // Intervals probably won't ever change, so I'm ok with this for now but...
        // TODO change this to use an underlying tag for how long the selected interval is
		Spinner startInterval = (Spinner) v.findViewById(R.id.spinnerStartInterval);
        ArrayAdapter<CharSequence> startIntervalAdapter = ArrayAdapter.createFromResource(
        		getActivity(), R.array.start_interval_array, R.layout.control_simple_spinner );
        startIntervalAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
        startInterval.setAdapter(startIntervalAdapter);
		
		return v;
	}

	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddRace;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if(!FindAnyRaceLocations()){
			// No locations...show another dialog to add a location
        	AddLocationView addLocationDialog = new AddLocationView();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			addLocationDialog.show(fm, AddLocationView.LOG_TAG);
		}
		
		if(raceSeries_ID == -1){
			lblRaceName.setVisibility(View.VISIBLE);
			txtRaceName.setVisibility(View.VISIBLE);
		} else {
			lblRaceName.setVisibility(View.GONE);
			txtRaceName.setVisibility(View.GONE);
		}
		
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(RACE_LOCATIONS_LOADER, null, this);
		this.getLoaderManager().initLoader(RACE_TYPES_LOADER, null, this);
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

	private boolean FindAnyRaceCategories() {
		boolean foundRaceCategories = false;
		try{
			String[] projection = new String[]{RaceCategory._ID + " as _id"};
			String selection = null;
			String[] selectionArgs = null; 
			String sortOrder = RaceCategory._ID;
			
			Cursor raceCat = RaceCategory.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(raceCat != null && raceCat.getCount() > 0){	
				foundRaceCategories = true;
			}
			
			raceCat.close();
			raceCat = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindAnyRaceCategories failed", ex);}
		
		return foundRaceCategories;
	}
	
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
		//Spinner raceTypeSpinner = (Spinner) getView().findViewById(R.id.spinnerRaceType);
		
		return 1;//raceTypeSpinner.getSelectedItemId();
	}
	
	protected long GetRaceLocationID(){
		Spinner raceLocationSpinner = (Spinner) getView().findViewById(R.id.spinnerRaceLocation);
		
		return raceLocationSpinner.getSelectedItemId();
	}
	
	protected Date GetRaceDate(){
		DatePicker datePicker = (DatePicker) getView().findViewById(R.id.dateRaceDate);
		return new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddNewRace){
				String eventName = "";
				Long eventID = 0l;
				String discipline = "";
				String scoring = "Club";
				if(raceSeries_ID == -1){
					Uri seriesCreated = RaceSeries.Instance().Create(getActivity(), txtRaceName.getText().toString(), GetRaceDate(), GetRaceDate(), scoring);
					raceSeries_ID = Long.parseLong(seriesCreated.getLastPathSegment());
				}
				
				Uri resultUri = Race.Instance().Create(getActivity(), GetRaceLocationID(), GetRaceDate(), null, GetRaceTypeID(), GetRaceStartInterval(), eventName, eventID, discipline, raceSeries_ID, scoring);
	 			long race_ID = Long.parseLong(resultUri.getLastPathSegment());
	 			
	 			// Broadcast that a race was added
    			Intent raceAdded = new Intent();
    			raceAdded.setAction(RACE_ADDED_ACTION);
    			raceAdded.putExtra(AppSettings.AppSetting_RaceID_Name, race_ID);
    			raceAdded.putExtra(AppSettings.AppSetting_StartInterval_Name, GetRaceStartInterval());
    			getActivity().sendBroadcast(raceAdded);
    			
    			if(!FindAnyRaceCategories()){
    				AddRaceCategoriesView addRaceCategoriesDialog = new AddRaceCategoriesView();
    				FragmentManager fm = getActivity().getSupportFragmentManager();
    				addRaceCategoriesDialog.show(fm, AddRaceCategoriesView.LOG_TAG);
    			}

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
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_LOCATIONS_LOADER:
				projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceLocation.CourseName;
				loader = new CursorLoader(getActivity(), RaceLocation.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_TYPES_LOADER:
				projection = new String[]{RaceType._ID, RaceType.RaceTypeDescription};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceType._ID;
				loader = new CursorLoader(getActivity(), RaceType.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			String[] columns;
            int[] to;
			switch(loader.getId()){
				case RACE_LOCATIONS_LOADER:
					columns = new String[] { RaceLocation.CourseName };
		            to = new int[] {android.R.id.text1 };
		            
					// Create the cursor adapter for the list of races
		            locationsCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		            locationsCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		        	raceLocation.setAdapter(locationsCursorAdapter);
					
					locationsCursorAdapter.swapCursor(cursor);
					break;
				case RACE_TYPES_LOADER:
					columns = new String[] { RaceType.RaceTypeDescription };
		            to = new int[] {android.R.id.text1 };
		            
					// Create the cursor adapter for the list of races
		            raceTypesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		            raceTypesCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		        	raceType.setAdapter(raceTypesCursorAdapter);
		        	
					raceTypesCursorAdapter.swapCursor(cursor);
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_LOCATIONS_LOADER:
					locationsCursorAdapter.swapCursor(null);
					break;
				case RACE_TYPES_LOADER:
					raceTypesCursorAdapter.swapCursor(null);
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
