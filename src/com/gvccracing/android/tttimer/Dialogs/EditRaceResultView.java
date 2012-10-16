package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.Controls.TimePicker;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Tabs.FinishTab;
import com.gvccracing.android.tttimer.Utilities.Calculations;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


public class EditRaceResultView extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "EditRaceResult";
	
	private Button btnSaveChanges;
	private Button btnDNF;
	private Button btnUnassignTime;
	private TimePicker tpElapsed;
	private EditText txtPoints;
	private TextView lblDNF;
	private long raceResultID;
	private long raceID;

	private static final int RACE_RESULT_LOADER = 0x19;
	
	private Long initialPoints;
	private Long initialTime;
	
    public EditRaceResultView(long raceResultID) {
		this.raceResultID = raceResultID;
	}
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_edit_race_result, container, false);

		btnSaveChanges = (Button) v.findViewById(R.id.btnSaveResultChanges);
		btnSaveChanges.setOnClickListener(this);
		
		btnDNF = (Button) v.findViewById(R.id.btnDNF);
		btnDNF.setOnClickListener(this);
		
		btnUnassignTime = (Button) v.findViewById(R.id.btnUnassignTime);
		btnUnassignTime.setOnClickListener(this);
		
		tpElapsed = (TimePicker) v.findViewById(R.id.elapsedTime);
		
		txtPoints = (EditText) v.findViewById(R.id.txtPoints);
		lblDNF = (TextView) v.findViewById(R.id.lblDNF);
			
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.EditRaceResult;
	}
	
	@Override
	public void onStart() {		
		super.onStart();
		// Initialize the cursor loader for the races list
		this.getLoaderManager().initLoader(RACE_RESULT_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnSaveChanges){
				if(initialTime != tpElapsed.GetTime()){
					ContentValues content = new ContentValues();
					if(tpElapsed.getVisibility() == View.VISIBLE){
						// If the timePicker is visible, get the time from it
						content.put(RaceResults.ElapsedTime, tpElapsed.GetTime());
					}else{
						// Otherwise we are a DNF, so set to Long.MAX_VALUE
						content.put(RaceResults.ElapsedTime, Long.MAX_VALUE);
					}
				
					// Update the race results elapsed time
					RaceResults.Update(getActivity(), content, RaceResults._ID + "= ?", new String[]{Long.toString(raceResultID)});

					// Recalculate placings and points
			    	Calculations.CalculateCategoryPlacings(getActivity(), raceID);
			    	Calculations.CalculateOverallPlacings(getActivity(), raceID); 
				}
		    	
		    	if(initialPoints != Long.parseLong(txtPoints.getText().toString())){
					ContentValues content = new ContentValues();
					//content.put(RaceResults.Points, txtPoints.getText().toString());
					
			    	// Update the points - we changed this for a reason!
					RaceResults.Update(getActivity(), content, RaceResults._ID + "= ?", new String[]{Long.toString(raceResultID)});
		    	}
				
	 			// Hide the dialog
	 			dismiss();
			} else if(v==btnDNF){
				if(tpElapsed.getVisibility() == View.VISIBLE){					
					showDNF();			
					initialTime = 0l;
				}else{
					showTime();
				}
			} else if(v==btnUnassignTime){
				ContentValues content = new ContentValues();
				content.putNull(UnassignedTimes.RaceResult_ID);
				
				// Update the unassigned time's raceResult_ID to null, which will place that unassigned time into the list again
				UnassignedTimes.Update(getActivity(), content, UnassignedTimes.Race_ID + "=? AND " + UnassignedTimes.RaceResult_ID + "=?", new String[]{Long.toString(raceID), Long.toString(raceResultID)});

				ContentValues result = new ContentValues();
				result.putNull(RaceResults.EndTime);
				result.putNull(RaceResults.ElapsedTime);
				
				RaceResults.Update(getActivity(), result, RaceResults._ID + "=?", new String[]{Long.toString(raceResultID)});
				
				dismiss();
				
				// Make sure the finish tab is visible
				Intent changeTab = new Intent();
				changeTab.setAction(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
				changeTab.putExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG, FinishTab.FinishTabSpecName);
				getActivity().sendBroadcast(changeTab);
				
				// Recalculate placings and points
		    	Calculations.CalculateCategoryPlacings(getActivity(), raceID);
		    	Calculations.CalculateOverallPlacings(getActivity(), raceID); 
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	private void showTime() {

		// It's going to show the TimePicker
		btnDNF.setText(R.string.DNF);
		
		tpElapsed.setVisibility(View.VISIBLE);
		lblDNF.setVisibility(View.GONE);
	}

	private void showDNF() {
		// It's going to show the DNF text resource instead of the TimePicker
		// Set the button to lblRacerFinished
		btnDNF.setText(R.string.lblRacerFinished);

		tpElapsed.setVisibility(View.GONE);
		lblDNF.setVisibility(View.VISIBLE);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_RESULT_LOADER:
				projection = new String[]{RaceResults._ID, RaceResults.Race_ID, RaceResults.ElapsedTime/*, RaceResults.Points*/};
				selection = RaceResults._ID + "=?";
				selectionArgs = new String[]{Long.toString(raceResultID)};
				sortOrder = RaceResults._ID;
				loader = new CursorLoader(getActivity(), RaceResults.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_RESULT_LOADER:
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						initialTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
						if(initialTime == Long.MAX_VALUE){
							showDNF();
						}else{
							showTime();
							tpElapsed.SetTime(initialTime);
						}
						raceID = cursor.getLong(cursor.getColumnIndex(RaceResults.Race_ID));
						initialPoints = 0l;//cursor.getLong(cursor.getColumnIndex(RaceResults.Points));
						txtPoints.setText(Long.toString(initialPoints));
					}
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
				case RACE_RESULT_LOADER:
					// Do nothing, just here to be consistent
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
