package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


public class EditLocation extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "EditLocationView";

	private static final int CURRENT_RACE_LOCATION_LOADER = 98;

	private static final int ALL_LOCATIONS_LOADER = 103;

	private static final int SELECTED_RACE_LOCATION_LOADER = 201;
	
	private Button btnSaveChanges;
	private Spinner raceLocationSpinner = null;
	private SimpleCursorAdapter raceLocationsCA;
	
	private long selectedRaceLocationID;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_edit_location, container, false);

		btnSaveChanges = (Button) v.findViewById(R.id.btnSaveChanges);
		btnSaveChanges.setOnClickListener(this);
		
		TextView lblDistanceUnit = (TextView) v.findViewById(R.id.lblDistanceUnit);
		raceLocationSpinner = (Spinner) v.findViewById(R.id.spinnerRaceLocation);
		
		raceLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View v, int position, long id) {
				selectedRaceLocationID = id;
				getActivity().getSupportLoaderManager().restartLoader(SELECTED_RACE_LOCATION_LOADER, null, EditLocation.this);
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		Integer distanceUnitID = Integer.parseInt(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, "1"));
		String distanceUnitText = "mi";
		switch(distanceUnitID){
			case 1:
				distanceUnitText = "mi";
				break;
			case 2:
				distanceUnitText = "km";
				break;
			default:
				distanceUnitText = "mi";
				break;
		}
		lblDistanceUnit.setText(distanceUnitText);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		String[] columns = new String[] { RaceLocation.CourseName };
        int[] to = new int[] {android.R.id.text1 };
		
		raceLocationsCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		raceLocationsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		raceLocationSpinner.setAdapter(raceLocationsCA);

		this.getLoaderManager().restartLoader(ALL_LOCATIONS_LOADER, null, this);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.EditLocation;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnSaveChanges){
				// First name
				EditText mCourseName = (EditText) getView().findViewById(R.id.txtCourseName);
				String courseName = mCourseName.getText().toString();
				
				EditText mDistance = (EditText) getView().findViewById(R.id.txtDistance);
				String distance = mDistance.getText().toString();
				
				Spinner mLocationID = (Spinner) getView().findViewById(R.id.spinnerRaceLocation);
				long raceLocation_ID = mLocationID.getSelectedItemId();
		
				RaceLocation.Update(getActivity(), raceLocation_ID, courseName, distance);
					    			
				// Hide the dialog
				dismiss();
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnStartCheckIn failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case ALL_LOCATIONS_LOADER:
				projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceLocation.CourseName;
				loader = new CursorLoader(getActivity(), RaceLocation.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case SELECTED_RACE_LOCATION_LOADER:
				projection = new String[]{RaceLocation._ID, RaceLocation.CourseName, RaceLocation.Distance};
				selection = RaceLocation._ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedRaceLocationID)};
				sortOrder = RaceLocation.CourseName;
				loader = new CursorLoader(getActivity(), RaceLocation.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_RACE_LOCATION_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID, Race.RaceLocation_ID};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = null;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_LOCATIONS_LOADER:
					cursor.moveToFirst();
					
					raceLocationsCA.swapCursor(cursor);
					
					this.getLoaderManager().restartLoader(CURRENT_RACE_LOCATION_LOADER, null, this);
					break;
				case SELECTED_RACE_LOCATION_LOADER:
					cursor.moveToFirst();

					// Course Name
					String selectedCourseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
					EditText mCourseName = (EditText) getView().findViewById(R.id.txtCourseName);
					mCourseName.setText(selectedCourseName);
					
					// Distance
					double selectedDistance = cursor.getDouble(cursor.getColumnIndex(RaceLocation.Distance));
					EditText mDistance = (EditText) getView().findViewById(R.id.txtDistance);
					mDistance.setText(Double.toString(selectedDistance));
					break;
				case CURRENT_RACE_LOCATION_LOADER:
					try{
						cursor.moveToFirst();
						// Race Location from RaceLocation_ID
						Long raceLocationID = cursor.getLong(cursor.getColumnIndex(Race.RaceLocation_ID));
						SetRaceLocationSelectionByID(raceLocationSpinner, raceLocationID);
					}catch(Exception ex){
						Log.e(LOG_TAG, "Unexpected error loading current race", ex);
					}
					// Destroy this loader so it doesn't go past the first load
					this.getLoaderManager().destroyLoader(CURRENT_RACE_LOCATION_LOADER);
					// Start the selected loader to display the correct data in the textboxes
					this.getLoaderManager().restartLoader(SELECTED_RACE_LOCATION_LOADER, null, this);
					break;						
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}
	
	private void SetRaceLocationSelectionByID(Spinner raceLocation, long raceLocationID) {
		for (int i = 0; i < raceLocation.getCount(); i++) {
		    long id = raceLocation.getItemIdAtPosition(i);
		    if (id == raceLocationID) {
		    	raceLocation.setSelection(i);
		    	break;
		    }
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case ALL_LOCATIONS_LOADER:
					raceLocationsCA.swapCursor(null);
					break;
				case SELECTED_RACE_LOCATION_LOADER:
					break;
				case CURRENT_RACE_LOCATION_LOADER:
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
