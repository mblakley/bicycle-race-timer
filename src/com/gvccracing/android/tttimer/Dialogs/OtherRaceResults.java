package com.gvccracing.android.tttimer.Dialogs;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceInfoView;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.CursorAdapters.OtherRacesCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;

public class OtherRaceResults extends BaseDialog implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
	public static final String LOG_TAG = "PreviousRaceResults";
	private static final int RACE_INFO_LOADER = 0x56;
	private static final int SELECTED_RACE_INFO_LOADER = 0x57;
	private static final int ALL_RACES_LOADER = 0x58;
	
	private Spinner spinnerRaceToView = null;
	private OtherRacesCursorAdapter raceInfoCursorAdapter = null;
	private long selectedRaceID = 0;
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_other_race_results, container, false);
		
		selectedRaceID = Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "0"));

		spinnerRaceToView = ((Spinner) v.findViewById(R.id.spinnerRaceToView));
		
		spinnerRaceToView.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View v, int position, long id) {
				selectedRaceID = id;
				getActivity().getSupportLoaderManager().restartLoader(SELECTED_RACE_INFO_LOADER, null, OtherRaceResults.this);
				getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, OtherRaceResults.this);
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		((Button) v.findViewById(R.id.btnSaveSelection)).setOnClickListener(this);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.OtherRaceResults;
	}

	@Override
	public void onResume(){
		super.onResume();

	    getActivity().getSupportLoaderManager().restartLoader(ALL_RACES_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			if (v.getId() == R.id.btnSaveSelection){
				String selected = Long.toString(spinnerRaceToView.getSelectedItemId());
				
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, selected, true);
				
				Intent raceHasChanged = new Intent();
				raceHasChanged.setAction(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION);
				raceHasChanged.putExtra(RaceResults.Race_ID, Long.toString(spinnerRaceToView.getSelectedItemId()));
        		getActivity().sendBroadcast(raceHasChanged);
			} else {
				super.onClick(v);
			}
			// Hide the dialog
	    	dismiss();
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case ALL_RACES_LOADER:
				projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, RaceLocation.CourseName};
				selection = null;
				selectionArgs = null;
				sortOrder = Race.RaceDate;
				loader = new CursorLoader(getActivity(), RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER:
				projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, RaceLocation.CourseName, Race.RaceType, Race.StartInterval, RaceLocation.Distance, Race.NumLaps};
				selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedRaceID)};
				sortOrder = Race.RaceDate;
				loader = new CursorLoader(getActivity(), RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case SELECTED_RACE_INFO_LOADER:
				projection = new String[]{RaceResults.Instance().getTableName() + "." + RaceResults._ID + " as _id"};
				selection = RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedRaceID)};
				sortOrder = RaceResults.Instance().getTableName() + "." + RaceResults.Race_ID;
				loader = new CursorLoader(getActivity(), RaceResults.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_RACES_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){				            
						// Create the cursor adapter for the list of races
			            raceInfoCursorAdapter = new OtherRacesCursorAdapter(getActivity(), cursor);
			        	spinnerRaceToView.setAdapter(raceInfoCursorAdapter);
					}
					break;
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){	
						double raceDistance = cursor.getDouble(cursor.getColumnIndex(RaceLocation.Distance));
						long numLaps = cursor.getLong(cursor.getColumnIndex(Race.NumLaps));
						double totalDistance = raceDistance * (double)numLaps;
						if( getView() != null){
							((TextView) getView().findViewById(R.id.txtRaceDistance)).setText(Double.toString(totalDistance));
						}
					}
					break;
				case SELECTED_RACE_INFO_LOADER:
					cursor.moveToFirst();
					int racerCount = cursor.getCount();	
					if(getView() != null){
						TextView numRacers = (TextView) getView().findViewById(R.id.txtNumberOfRacers);
						if(numRacers != null){
							numRacers.setText(Integer.toString(racerCount));
						}
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
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case ALL_RACES_LOADER:
					raceInfoCursorAdapter.swapCursor(null);
					break;
				case RACE_INFO_LOADER:
					// Do nothing
					break;
				case SELECTED_RACE_INFO_LOADER:
					// Do nothing
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

