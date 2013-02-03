package com.xcracetiming.android.tttimer.WizardPages;

import java.util.Date;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.Utilities.Loaders;

import android.database.Cursor;
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
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class AddRaceInfoView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceInfoView";
	
	private SimpleCursorAdapter raceTypesCursorAdapter = null;	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		return inflater.inflate(R.layout.wp_add_race_info, container, false);
	}
	
	@Override
	protected void addListeners() {	
		getSpinner(R.id.spinnerRaceType).setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	// Need to just check the "HasMultipleLaps" field to see if we should configure the number of laps
            	Cursor selectedRaceType = raceTypesCursorAdapter.getCursor();
            	selectedRaceType.moveToPosition(position);            	          
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
		return R.string.AddRaceInfo;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// Continue with setting up the race
		
        // Intervals probably won't ever change, so I'm ok with this for now but...
        // TODO: change this to use an underlying tag for how long the selected interval is
        ArrayAdapter<CharSequence> startIntervalAdapter = ArrayAdapter.createFromResource(
        		getActivity(), R.array.start_interval_array, R.layout.control_simple_spinner );
        startIntervalAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
        getSpinner(R.id.spinnerStartInterval).setAdapter(startIntervalAdapter);	        
	}
	
	@Override
	protected void startAllLoaders() {
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(Loaders.RACE_TYPES_LOADER, null, this);
	}
	
	@Override
	protected void destroyAllLoaders() {
		// Destroy the cursor loaders
		this.getLoaderManager().destroyLoader(Loaders.RACE_TYPES_LOADER);
	}
	
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
	
	protected Date GetRaceDate(){
		DatePicker datePicker = getDatePicker(R.id.dateRaceDate);
		return new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		switch(id){
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

	@Override
	public Bundle Save() throws Exception {
		Bundle b = super.Save();		
		// Race event name (can be blank)
		b.putString(Race.EventName, getTextView(R.id.txtRaceName).getText().toString());
		
		// Race date
		Date raceDate = GetRaceDate();
		b.putLong(Race.RaceDate, raceDate.getTime());
		
		// Race type
		b.putLong(Race.RaceType_ID, getSpinner(R.id.spinnerRaceType).getSelectedItemId());
		
		// Start interval
		b.putLong(Race.StartInterval, GetRaceStartInterval());		
		
		return b;
	}
}
