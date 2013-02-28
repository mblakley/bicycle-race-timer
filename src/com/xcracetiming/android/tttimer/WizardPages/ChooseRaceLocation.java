package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.Utilities.Loaders;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ChooseRaceLocation extends BaseWizardPage implements OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor>{
	public static final String LOG_TAG = "ChooseRaceLocation";	

	private SimpleCursorAdapter raceLocationsCursorAdapter = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.wp_choose_race_location, container, false);
	}

	@Override
	protected void addListeners() {
		getRadioGroup(R.id.radioGroup).setOnCheckedChangeListener(this);	
	}
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		if(args.containsKey("NewLocation")){
			getRadioButton(R.id.radioNewLocation).setChecked(args.getBoolean("NewLocation"));
			getRadioButton(R.id.radioExistingLocation).setChecked(!args.getBoolean("NewLocation"));
		}
		if(args.containsKey(RaceLocation.CourseName)){
			getTextView(R.id.txtCourseName).setText(args.getString(RaceLocation.CourseName));
		}
		if(args.containsKey(RaceLocation.Distance)){
			getTextView(R.id.txtCourseDistance).setText(args.getString(RaceLocation.Distance));
		}
	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.ChooseRaceLocation;
	}	

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	@Override
	public Bundle Save() throws Exception {
		Bundle b = super.Save();
		RadioButton rb = (RadioButton) getView().findViewById(R.id.radioNewLocation);
		boolean isNewLocation = rb.isChecked();
		
		if(isNewLocation){
			if(getTextView(R.id.txtCourseName).getText().length() > 0){
				b.putString(RaceLocation.CourseName, getTextView(R.id.txtCourseName).getText().toString());
			}else{
				// Notify the user that they need to enter a race course name
				throw new Exception("Please enter a name for this race course");
			}
			
			if(getTextView(R.id.txtCourseDistance).getText().length() > 0){
				b.putString(RaceLocation.Distance, getTextView(R.id.txtCourseDistance).getText().toString());
			}else{
				// Notify the user that they need to enter a race course distance
				throw new Exception("Please enter a distance for this race course");
			}
		}
		b.putBoolean("NewLocation", isNewLocation);
		
		return b;
	}

	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(checkedId){
			case R.id.radioNewLocation:
				// Show the soft keyboard
				getTextView(R.id.txtCourseName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
				    public void onFocusChange(View v, boolean hasFocus) {
				        if (hasFocus) {
				        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
				        }
				    }
				});
				getTextView(R.id.txtCourseName).requestFocus();
				// Show the textboxes for the new location
				getLinearLayout(R.id.llRaceLocationName).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llRaceLocationDistance).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llExistingRaceLocation).setVisibility(View.GONE);
				break;
			case R.id.radioExistingLocation:
				// hide the soft keyboard
				imm.hideSoftInputFromWindow(getTextView(R.id.txtCourseName).getWindowToken(), 0);
				// Show the textbox for choosing an existing location
				getLinearLayout(R.id.llExistingRaceLocation).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llRaceLocationName).setVisibility(View.GONE);
				getLinearLayout(R.id.llRaceLocationDistance).setVisibility(View.GONE);
				break;
		}
	}
	
	@Override
	protected void startAllLoaders() {
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(Loaders.RACE_LOCATIONS_LOADER, null, this);
		this.getLoaderManager().initLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO, null, this);
	}
	
	@Override
	protected void destroyAllLoaders() {
		// Destroy the cursor loaders
		this.getLoaderManager().destroyLoader(Loaders.RACE_LOCATIONS_LOADER);
		this.getLoaderManager().destroyLoader(Loaders.APP_SETTINGS_LOADER_RACEINFO);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		switch(id){
			case Loaders.RACE_LOCATIONS_LOADER:
				loader = Loaders.GetAllCourseNames(getActivity());
				break;
			case Loaders.APP_SETTINGS_LOADER_RACEINFO:				
				loader = Loaders.GetDistanceUnits(getActivity());
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
		            
		            if(cursor.getCount() > 0){	
		            	// Make sure the option is available
		            	getRadioButton(R.id.radioExistingLocation).setVisibility(View.VISIBLE);	
		            	
						// Create the cursor adapter for the list of races
		            	raceLocationsCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		            	raceLocationsCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
			        	getSpinner(R.id.spinnerExistingRaceLocation).setAdapter(raceLocationsCursorAdapter);
						
			        	raceLocationsCursorAdapter.swapCursor(cursor);
		            }else{
		            	// Don't show the option to choose existing
		            	getRadioButton(R.id.radioExistingLocation).setVisibility(View.GONE);
		            	// Set the "New Location" checkbox to checked
	        			getRadioButton(R.id.radioNewLocation).setChecked(true);
	        			getRadioButton(R.id.radioExistingLocation).setChecked(false);
		            }
					break;
				case Loaders.APP_SETTINGS_LOADER_RACEINFO:	
					String distanceUnit = "mi";
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						distanceUnit = cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue));					
					}
					getTextView(R.id.lblDistanceUnit).setText(distanceUnit);
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
					raceLocationsCursorAdapter.swapCursor(null);
					break;
				case Loaders.APP_SETTINGS_LOADER_RACEINFO:
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
