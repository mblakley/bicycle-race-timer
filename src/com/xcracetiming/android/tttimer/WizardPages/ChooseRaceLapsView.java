package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ChooseRaceLapsView extends BaseWizardPage implements OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor>{
	public static final String LOG_TAG = "ChooseRaceLaps";	

	private SimpleCursorAdapter raceSeriesCursorAdapter = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.wp_choose_race_laps, container, false);
	}

	@Override
	protected void addListeners() {
		getRadioGroup(R.id.radioGroup).setOnCheckedChangeListener(this);	
	}
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		if(args.containsKey(LOG_TAG)){
			getRadioButton(R.id.radioNewSeries).setChecked(args.getBoolean(LOG_TAG));
			getRadioButton(R.id.radioNo).setChecked(!args.getBoolean(LOG_TAG));
		}
		if(args.containsKey("RaceSeriesName")){
			getTextView(R.id.txtRaceSeriesName).setText(args.getString("RaceSeriesName"));
		}
	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.RaceLaps;
	}	

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	@Override
	public Bundle Save() throws Exception {
		Bundle b = super.Save();
		boolean isNewSeries = getRadioButton(R.id.radioNewSeries).isChecked();
		// If the race is in a series, validate that a series name was entered
		if(isNewSeries){
			if(getTextView(R.id.txtRaceSeriesName).getText().length() > 0){
				b.putString("RaceSeriesName", getTextView(R.id.txtRaceSeriesName).getText().toString());
			}else{
				// Notify the user that they need to enter a race series name
				throw new Exception("Please enter a race series name");
			}
		}
		boolean isExistingSeries = getRadioButton(R.id.radioExistingSeries).isChecked();
		// If the race is in a series, validate that a series name was entered
		if(isExistingSeries){
			if(getTextView(R.id.txtRaceSeriesName).getText().length() > 0){
				b.putString("RaceSeriesName", getTextView(R.id.txtRaceSeriesName).getText().toString());
			}else{
				// Notify the user that they need to enter a race series name
				throw new Exception("Please enter a race series name");
			}
		}
		b.putBoolean(LOG_TAG, isNewSeries);
		
		return b;
	}

	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(checkedId){
			case R.id.radioNewSeries:
				// Show the soft keyboard
				getTextView(R.id.txtRaceSeriesName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
				    public void onFocusChange(View v, boolean hasFocus) {
				        if (hasFocus) {
				        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
				        }
				    }
				});
				getTextView(R.id.txtRaceSeriesName).requestFocus();
				// Show the textbox for series name
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.GONE);
				break;
			case R.id.radioExistingSeries:
				// hide the soft keyboard
				imm.hideSoftInputFromWindow(getTextView(R.id.txtRaceSeriesName).getWindowToken(), 0);
				// Show the textbox for series name
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.GONE);
				break;
			case R.id.radioNo:
				// hide the soft keyboard
				imm.hideSoftInputFromWindow(getTextView(R.id.txtRaceSeriesName).getWindowToken(), 0);
				// Hide the textbox for series name
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.GONE);
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.GONE);
				break;
		}
	}
	
	@Override
	protected void startAllLoaders() {
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(Loaders.RACE_SERIES_LOADER, null, this);
	}
	
	@Override
	protected void destroyAllLoaders() {
		// Destroy the cursor loaders
		this.getLoaderManager().destroyLoader(Loaders.RACE_SERIES_LOADER);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		switch(id){
			case Loaders.RACE_SERIES_LOADER:
				loader = Loaders.GetAllRaceSeriesNames(getActivity());
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
				case Loaders.RACE_SERIES_LOADER:
					columns = new String[] { RaceSeries.SeriesName };
		            to = new int[] {android.R.id.text1 };
		            
		            if(cursor.getCount() > 0){	
		            	// Make sure the option is available
		            	getRadioButton(R.id.radioExistingSeries).setVisibility(View.VISIBLE);	
		            	
						// Create the cursor adapter for the list of races
			            raceSeriesCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			            raceSeriesCursorAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
			        	getSpinner(R.id.spinnerExistingRaceSeries).setAdapter(raceSeriesCursorAdapter);
						
			        	raceSeriesCursorAdapter.swapCursor(cursor);
		            }else{
		            	// Don't show the option to choose existing
		            	getRadioButton(R.id.radioExistingSeries).setVisibility(View.GONE);
		            }
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
				case Loaders.RACE_SERIES_LOADER:
					raceSeriesCursorAdapter.swapCursor(null);
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
