package com.gvccracing.android.tttimer.Dialogs;

import java.util.Date;
import java.util.GregorianCalendar;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class AddMeetView extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceView";
	
	protected Button btnAddNewRace;
	private SimpleCursorAdapter locationsCursorAdapter = null;
	protected Spinner raceLocation = null;
	private EditText txtLaps = null;
	
	/**
     * This is a special intent action that means "load your tab data".
     */
    public static final String RACE_ADDED_ACTION = "com.gvccracing.android.tttimer.RACE_ADDED";	    
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_race, container, false);

		btnAddNewRace = (Button) v.findViewById(R.id.btnAddNewRace);
		btnAddNewRace.setOnClickListener(this);		
		
		raceLocation = (Spinner) v.findViewById(R.id.spinnerRaceLocation);
		
		// TODO I tried to put this in the loader, but it won't show a dialog from inside the onLoadFinished, so figure out how to do this the right way		
		String[] fieldsToRetrieve = new String[]{RaceLocation._ID, RaceLocation.CourseName};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = RaceLocation.CourseName;
		Cursor locations = RaceLocation.Read(getActivity(), fieldsToRetrieve, selection, selectionArgs, sortOrder);
		if(locations != null && locations.getCount() <= 0)
        {
        	// No locations...show another dialog to add a location
        	AddLocationView addLocationDialog = new AddLocationView();
			FragmentManager fm = getActivity().getSupportFragmentManager();
			addLocationDialog.show(fm, AddLocationView.LOG_TAG);
        }
		locations.close();
		locations = null;
		
		txtLaps = (EditText) v.findViewById(R.id.txtNumLaps);
        
//        Spinner raceType = (Spinner) v.findViewById(R.id.spinnerRaceType);
//        raceType.setOnItemSelectedListener(new OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//        		LinearLayout laps = (LinearLayout) getView().findViewById(R.id.llLaps);
//            	if(id == 1){
//            		if(Long.parseLong(txtLaps.getText().toString()) <= 1){
//            			txtLaps.setText("2");
//            		}
//            		laps.setVisibility(View.VISIBLE);
//            	}else{
//            		txtLaps.setText("1");
//            		laps.setVisibility(View.GONE);
//            	}
//            }
//
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//        });
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//        		getActivity(), R.array.race_type_array, R.layout.control_simple_spinner );
//		adapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
//		raceType.setAdapter(adapter);
		
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
		return R.string.AddMeet;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(RACE_LOCATIONS_LOADER, null, this);
	}
	
	protected long GetRaceTypeID(){
		//Spinner raceTypeSpinner = (Spinner) getView().findViewById(R.id.spinnerRaceType);
		
		return 0l;//raceTypeSpinner.getSelectedItemId();
	}
	
	protected Date GetRaceDate(int hour, int minute){
		DatePicker datePicker = (DatePicker) getView().findViewById(R.id.dateRaceDate);
		GregorianCalendar cal = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), hour, minute);		
		return cal.getTime();
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddNewRace){
				Uri raceMeetUri = RaceMeet.Create(getActivity(), GetRaceDate(0, 0), raceLocation.getSelectedItemId());
				long raceMeet_ID = Long.parseLong(raceMeetUri.getLastPathSegment());
				
				AddMeetTeam(raceMeet_ID, 3);
				AddMeetTeam(raceMeet_ID, 4);
				AddMeetTeam(raceMeet_ID, 12);
				AddMeetTeam(raceMeet_ID, 15);
				AddMeetTeam(raceMeet_ID, 23);
				
				// Add races for Boys Varsity, Girls Varsity, and Modified
				AddRace(raceMeet_ID, "Boys", "Varsity", 16, 30, 3.1f, 3);
				AddRace(raceMeet_ID, "Modified", "Both", 17, 0, 2, 2);
				AddRace(raceMeet_ID, "Girls", "Varsity", 17, 30, 3.1f, 3);

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

	private void AddMeetTeam(long raceMeet_ID, long teamInfo_ID) {
		// Add teams to the meet
		ContentValues content = new ContentValues();
		content.put(RaceMeetTeams.RaceMeet_ID, raceMeet_ID);
		content.put(RaceMeetTeams.TeamInfo_ID, teamInfo_ID);
		RaceMeetTeams.Create(getActivity(), content);
	}

	private void AddRace(long raceMeet_ID, String gender, String category, int hour, int minute, float raceDistance, int numSplits) {
		Uri resultUri = Race.Create(getActivity(), raceMeet_ID, GetRaceDate(hour, minute), gender, category, raceDistance, numSplits);
		long race_ID = Long.parseLong(resultUri.getLastPathSegment());
			
			// Broadcast that a race was added
		Intent raceAdded = new Intent();
		raceAdded.setAction(RACE_ADDED_ACTION);
		raceAdded.putExtra(AppSettings.AppSetting_RaceID_Name, race_ID);
		getActivity().sendBroadcast(raceAdded);		
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
				loader = new CursorLoader(getActivity(), RaceLocation.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_LOCATIONS_LOADER:
	            	locationsCursorAdapter = null;
					if(locationsCursorAdapter == null){
						String[] columns = new String[] { RaceLocation.CourseName };
			            int[] to = new int[] {android.R.id.text1 };
			            
						// Create the cursor adapter for the list of races
			            locationsCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			            locationsCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
			        	raceLocation.setAdapter(locationsCursorAdapter);
					}
					locationsCursorAdapter.swapCursor(cursor);
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
