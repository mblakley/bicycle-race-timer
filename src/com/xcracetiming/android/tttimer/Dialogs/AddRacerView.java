package com.xcracetiming.android.tttimer.Dialogs;

import java.util.Locale;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.AsyncTasks.CheckInHandler;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceRaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceRaceCategoryView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddRacerView extends BaseDialog implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRacerView";
	
	/**
     * This is a special intent action that gets fired when a new racer is added
     */
    public static final String RACER_ADDED_ACTION = "com.xcracetiming.android.tttimer.RACER_ADDED";

	public static final String CHECKIN_RACER_ACTION = "com.xcracetiming.android.tttimer.CHECKIN_RACER";

	private static final int RACE_CATEGORY_LOADER = 1122;
	
	protected Button btnAddRacer;
		
	private SimpleCursorAdapter raceCategoryCA;
	
	private boolean checkin = false;
	
	public AddRacerView(){}
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		
		checkin = args.getBoolean("CheckInAfterAdd");
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_racer, container, false);

		((Button) v.findViewById(R.id.btnAddNewRacer)).setOnClickListener(this);
		
		((EditText) v.findViewById(R.id.txtFirstName)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
		        }
		    }
		});
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String[] columns = new String[] { RaceCategory.FullCategoryName };
		int[] to = new int[] {android.R.id.text1 };		
        
		raceCategoryCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		raceCategoryCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
    	getSpinner(R.id.spinnerCategory).setAdapter(raceCategoryCA);

		this.getLoaderManager().initLoader(RACE_CATEGORY_LOADER, null, this);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddNewRacer;
	}

 	/*
 	 * Add a new racer that can be checked in
 	 */
 	private boolean AddNewRacer(String firstName, String lastName, String usacNumber, long categoryID) 
 	{
 		boolean success = false;
 		// If none of the fields are blank, we're ok to add the record
 		if(firstName.trim().length() != 0 && lastName.trim().length() != 0 && usacNumber.trim().length() != 0){
 	 		long raceSeries_ID = 1l;//TODO: Fix this!  AppSettings.Instance().ReadLongValue(getActivity(), AppSettings.AppSetting_RaceSeriesID_Name, null);
 			String selection = "UPPER(" + Racer.FirstName + ")=? AND UPPER(" + Racer.LastName + ")=?";
 			String[] selectionArgs = new String[]{firstName.trim().toUpperCase(Locale.US), lastName.trim().toUpperCase(Locale.US)};
 			long racer_ID = 0;
 			long racerUSACInfo_ID = 0;
 			Uri resultUri;
 			Cursor previousRacers = Racer.Instance().Read(getActivity(), new String[]{Racer._ID}, selection, selectionArgs, null);
 			if(previousRacers != null && previousRacers.getCount() > 0){
 				previousRacers.moveToFirst();
 				// Found at least one other racer with the same name.
 				racer_ID = previousRacers.getLong(previousRacers.getColumnIndex(Racer._ID));
 				
 				selection = RacerUSACInfo.Racer_ID + "=? AND " + RacerUSACInfo.LicenseType + "='Road'";
 	 			selectionArgs = new String[]{Long.toString(racer_ID)}; 
 				
 				// Get the RacerUSACInfo record attached to this racer
 				Cursor racerUSACInfo = RacerUSACInfo.Instance().Read(getActivity(), new String[]{RacerUSACInfo._ID}, selection, selectionArgs, null);
 				if(racerUSACInfo != null && racerUSACInfo.getCount() > 0){
 					racerUSACInfo.moveToFirst();
 					racerUSACInfo_ID = racerUSACInfo.getLong(racerUSACInfo.getColumnIndex(RacerUSACInfo._ID));
 					
	 				selection = RacerSeriesInfo.RacerUSACInfo_ID + "=? AND " + RacerSeriesInfo.RaceSeries_ID + "=" + Long.toString(raceSeries_ID);
	 	 			selectionArgs = new String[]{Long.toString(racerUSACInfo_ID)}; 
	 				
	 	 			// Get the current category of this racer
	 				Cursor racerCategory = RacerSeriesInfo.Instance().Read(getActivity(), new String[]{RacerSeriesInfo._ID, RacerSeriesInfo.SeriesRacerCategory_ID}, selection, selectionArgs, null);
					// Found a racer category
	 				if(racerCategory != null && racerCategory.getCount() > 0){
	 					racerCategory.moveToFirst();
	 					
		 				Long racerSeriesInfo_ID = racerCategory.getLong(racerCategory.getColumnIndex(RacerSeriesInfo._ID));
		 				Long currentRacerCat = racerCategory.getLong(racerCategory.getColumnIndex(RacerSeriesInfo.SeriesRacerCategory_ID));
		 				// If the new category doesn't equal the old category, do an upgrade
		 				if(!currentRacerCat.equals(categoryID)){
		 					Toast.makeText(getActivity(), R.string.IdenticalRacerUpgrade, Toast.LENGTH_LONG).show();
		 					RacerSeriesInfo.Instance().Update(getActivity(), racerSeriesInfo_ID, null, null, null, categoryID, null, null, null, true, null);
		 				}
	 				}
	 				
	 				if(racerCategory != null){
	 					racerCategory.close();
	 					racerCategory = null;
	 	 			}
 				} else{
 					// Create a racerUSACInfo record  					
 					Uri resultUSACUri = RacerUSACInfo.Instance().Create(getActivity(), racer_ID, "", usacNumber, "5", "Road", true, System.currentTimeMillis());
 					racerUSACInfo_ID = Long.parseLong(resultUSACUri.getLastPathSegment());
 				}
 				if(racerUSACInfo != null){
 					racerUSACInfo.close();
 					racerUSACInfo = null;
 	 			}
 			}else{
	 			resultUri = Racer.Instance().Create(getActivity(), firstName, lastName, Integer.parseInt(usacNumber), 0, 0, "None", 0);
	 			racer_ID = Long.parseLong(resultUri.getLastPathSegment());
	 			
	 			Uri resultUSACUri = RacerUSACInfo.Instance().Create(getActivity(), racer_ID, "", usacNumber, "5", "Road", true, System.currentTimeMillis());
				racerUSACInfo_ID = Long.parseLong(resultUSACUri.getLastPathSegment());
 			}
 			if(previousRacers != null){
	 			previousRacers.close();
	 			previousRacers = null;
 			}
 			
 			long racerInfo_ID = -1;
 			
 			// Create the RacerSeriesInfo record
 			Long onlineRecordID = null;
	     	resultUri = RacerSeriesInfo.Instance().Create(getActivity(), racerUSACInfo_ID, "0", raceSeries_ID, categoryID, 0, 0, 0, onlineRecordID);
	     	racerInfo_ID = Long.parseLong(resultUri.getLastPathSegment());
 			Log.v(LOG_TAG, "AddNewRacer racerInfo_ID: " + Long.toString(racerInfo_ID));
 			if(checkin){
 				CheckInHandler task = new CheckInHandler(getActivity());
 				task.execute(new Long[] { racerInfo_ID });	
 			}
 			success = true;
 		}else{
 			// Show a message that says that something isn't valid    
 			Toast.makeText(getActivity(), "Missing required information", Toast.LENGTH_SHORT).show();
 		}
		return success;
	}

	/**
	 * Sends an intent to the tabs to indicate which one needs to be shown
	 * @param tabId
	 */
	public void SendNotification(long racerInfo_ID) {
		Intent racerAdded = new Intent();
		racerAdded.setAction(RACER_ADDED_ACTION);
		racerAdded.putExtra(RACER_ADDED_ACTION, racerInfo_ID);
		racerAdded.putExtra(CHECKIN_RACER_ACTION, checkin);
		// send to all listeners
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(racerAdded);
	}
	
	@Override
	public void dismiss() {
		// Hide the dialog
    	super.dismiss();
		
		// Set the textboxes in the dialog to an empty string
    	getEditText(R.id.txtFirstName).setText("");
    	getEditText(R.id.txtLastName).setText("");
    	getEditText(R.id.txtUSACNumber).setText("");
	}
	
	@Override
	public void onClick(View v) { 
		try{
			switch(v.getId())
			{
				case R.id.btnAddNewRacer:
					Log.v(LOG_TAG, "btnAddNewRacerClickHandler");
					
					// First name
					String firstName = getEditText(R.id.txtFirstName).getText().toString();
					// Last name
					String lastName = getEditText(R.id.txtLastName).getText().toString();
					// Category
					long category = getSpinner(R.id.spinnerCategory).getSelectedItemId();	
					// USACNumber
					String usacNumber = getEditText(R.id.txtUSACNumber).getText().toString();
			
					if(firstName.trim().equals("")){
						Toast.makeText(getActivity(), "Please enter a first name", Toast.LENGTH_LONG).show();
						return;
					}
					
					if(lastName.trim().equals("")){
						Toast.makeText(getActivity(), "Please enter a last name", Toast.LENGTH_LONG).show();
						return;
					}
	
					if(usacNumber.trim().equals("")){
						Toast.makeText(getActivity(), "Please enter a USAC license number", Toast.LENGTH_LONG).show();
						return;
					}
					
					if(AddNewRacer(firstName, lastName, usacNumber, category)){
						// Hide the dialog
				    	dismiss();
					}
					break;
				default:
					super.onClick(v);
					break;
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnAddNewRacerClickHandler failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case RACE_CATEGORY_LOADER:
				projection = new String[]{RaceCategory.Instance().getTableName() + "." + RaceCategory._ID + " as _id", RaceCategory.FullCategoryName};
				selection = RaceRaceCategory.Instance().getTableName() + "." + RaceRaceCategory.Race_ID + "=1";// + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceCategory.Instance().getTableName() + "." + RaceCategory._ID;
				loader = new CursorLoader(getActivity(), RaceRaceCategoryView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_CATEGORY_LOADER:
					raceCategoryCA.swapCursor(cursor);
					break;
			}
			Log.v(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case RACE_CATEGORY_LOADER:
					break;
			}
			Log.v(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}
}
