package com.xcracetimer.android.tttimer.Dialogs;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.xcracetimer.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.Utilities.Enums.RaceType;
import com.xcracetimer.android.tttimer.Utilities.Enums.StartInterval;

public class EditRaceMeetConfiguration extends AddMeetView implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "EditRaceConfiguration";
	
	private static final int RACE_INFO_LOADER = 0x06;
	
	private long raceMeet_ID;

	public EditRaceMeetConfiguration(long raceMeet_ID) {
		this.raceMeet_ID = raceMeet_ID;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = super.onCreateView(inflater, container, savedInstanceState);
    	
    	btnAddNewRace.setText(R.string.SaveChanges);
    	
		return v;
    }
    
    @Override
    protected int GetTitleResourceID() {
    	return R.string.RaceConfiguration;
    }
    
    @Override
    public void onResume() {
    	super.onResume();

		this.getLoaderManager().initLoader(RACE_INFO_LOADER, null, this);
    }
    
    @Override
    public void onClick(View v) { 
		try{
			if (v == btnAddNewRace){
				throw new java.lang.Exception("Unable to update race...missing implementation");
				//Race.Update(getActivity(), Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, raceMeet_ID, GetRaceDate(), null, gender, category, null, null);	 			
				
				// Figure out if checkin has already started.  If checkin has started, and start interval has changed, update the start intervals of everyone.
//				Cursor checkins = RaceResults.Read(getActivity(), new String[]{RaceResults._ID, RaceResults.StartOrder}, RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, RaceResults.StartOrder);
//				if(checkins.getCount() > 0){
//					checkins.moveToFirst();
//					// Checkin was already started, so update all startIntervals
//					do{
//						long raceResultID = checkins.getLong(checkins.getColumnIndex(RaceResults._ID));
//						Long startTimeOffset = 0l;
//						ContentValues content = new ContentValues();
//						content.put(RaceResults.StartTimeOffset, startTimeOffset);
//						RaceResults.Update(getActivity(), content,  RaceResults._ID + "=?", new String[]{Long.toString(raceResultID)});
//					} while(checkins.moveToNext());
//				}
//				if(checkins != null){
//					checkins.close();
//					checkins = null;
//				}
//				
//	 			// Hide the dialog
//	 			dismiss();
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_INFO_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID, Race.Gender, Race.Category, RaceLocation.CourseName, Race.RaceStartTime};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = null;
				loader = new CursorLoader(getActivity(), RaceInfoView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			default:
		    	loader = (CursorLoader) super.onCreateLoader(id, args);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}
    
    @Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					// Race Type from ID
					int cursorIndex = cursor.getColumnIndex(Race.Gender);
					Long raceTypeValue = cursor.getLong(cursorIndex);
					Spinner raceType = (Spinner) getView().findViewById(R.id.spinnerRaceType);
					SetRaceTypeSelectionByValue(raceType, raceTypeValue);					
					// Race Location from RaceLocation_ID
					String raceCourseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
					SetRaceLocationSelectionByValue(raceLocation, raceCourseName);
					// Date from RaceDate
					DatePicker date = (DatePicker) getView().findViewById(R.id.dateRaceDate);
					Long raceDateValue = cursor.getLong(cursor.getColumnIndex(Race.RaceStartTime));
					Date tempDate = new Date(raceDateValue);
					date.updateDate(tempDate.getYear(), tempDate.getMonth(), tempDate.getDate());					
					break;
				default:
					super.onLoadFinished(loader, cursor);
					break;
						
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}
    
    private void SetRaceTypeSelectionByValue(Spinner raceType, Long raceTypeID) {
    	String dbDesc = RaceType.DescriptionFromRaceTypeID(raceTypeID);
		for (int i = 0; i < raceType.getCount(); i++) {
		    String desc = raceType.getItemAtPosition(i).toString();
		    if (desc.equalsIgnoreCase(dbDesc)) {
		    	raceType.setSelection(i);
		    	break;
		    }
		}
	}

	private void SetRaceLocationSelectionByValue(Spinner raceLocation, String raceCourseName) {
		for (int i = 0; i < raceLocation.getCount(); i++) {
		    Cursor value = (Cursor) raceLocation.getItemAtPosition(i);
			String raceLocationInSpinner = value.getString(value.getColumnIndex(RaceLocation.CourseName));
		    if (raceLocationInSpinner.equalsIgnoreCase(raceCourseName)) {
		    	raceLocation.setSelection(i);
		    	break;
		    }
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					break;
				default:
					super.onLoaderReset(loader);
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
