package com.gvccracing.android.tttimer.Dialogs;

import java.util.Calendar;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.CheckInHandler;
import com.gvccracing.android.tttimer.CursorAdapters.StableSimpleCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnassignedTimeCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;


public class AddRacerView extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRacerView";
	
	/**
     * This is a special intent action that gets fired when a new racer is added
     */
    public static final String RACER_ADDED_ACTION = "com.gvccracing.android.tttimer.RACER_ADDED";

	public static final String CHECKIN_RACER_ACTION = "com.gvccracing.android.tttimer.CHECKIN_RACER";

	private static final int TEAM_LOADER = 11234;
	
	protected Button btnAddRacer;
	
	protected EditText txtFirstName;
	protected EditText txtLastName;
	protected EditText txtUSACNumber;
	protected Spinner spinCategory;

	private StableSimpleCursorAdapter teamsCA = null;
	
	private boolean checkin = false;
	
	public AddRacerView(boolean checkinAfterAdd){
		checkin = checkinAfterAdd;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_racer, container, false);

		btnAddRacer = (Button) v.findViewById(R.id.btnAddNewRacer);
		btnAddRacer.setOnClickListener(this);
		
		txtFirstName = (EditText) v.findViewById(R.id.txtFirstName);
		txtLastName = (EditText) v.findViewById(R.id.txtLastName);
		txtUSACNumber = (EditText) v.findViewById(R.id.txtUSACNumber);
		spinCategory = (Spinner) v.findViewById(R.id.spinnerCategory);
		txtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
//		
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//        		getActivity(), R.array.category_array, R.layout.control_simple_spinner );
// 		adapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
// 		spinCategory.setAdapter(adapter);
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		String[] columns = new String[] { TeamInfo.TeamName };
        int[] to = new int[] {android.R.id.text1 };
        
		// Create the cursor adapter for the list of race locations
        teamsCA = new StableSimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        teamsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
    	spinCategory.setAdapter(teamsCA);

		// Initialize the cursor loader for the teams list
		this.getLoaderManager().restartLoader(TEAM_LOADER, null, this);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddNewRacer;
	}

 	/*
 	 * Add a new racer that can be checked in
 	 */
 	private boolean AddNewRacer(String firstName, String lastName, long teamInfo_ID) 
 	{
 		boolean success = false;
 		// If none of the fields are blank, we're ok to add the record
 		if(firstName.trim().length() != 0 && lastName.trim().length() != 0/* && usacNumber.trim().length() != 0*/){
 			String selection = "UPPER(" + Racer.FirstName + ")=? AND UPPER(" + Racer.LastName + ")=?";
 			String[] selectionArgs = new String[]{firstName.trim().toUpperCase(), lastName.trim().toUpperCase()};
 			long racer_ID = 0;
 			Uri resultUri;
 			Cursor previousRacers = Racer.Read(getActivity(), new String[]{Racer._ID}, selection, selectionArgs, null);
 			if(previousRacers != null && previousRacers.getCount() > 0){
 				previousRacers.moveToFirst();
 				// Found at least one other racer with the same name.
 				racer_ID = previousRacers.getLong(previousRacers.getColumnIndex(Racer._ID));
 				
// 				selection = RacerClubInfo.Racer_ID + "=? and " + RacerClubInfo.Upgraded + "=?";
// 	 			selectionArgs = new String[]{Long.toString(racer_ID), Long.toString(0l)}; 
// 				
// 	 			// Get the current category of this racer
// 				Cursor racerCategory = RacerClubInfo.Read(getActivity(), new String[]{RacerClubInfo._ID, RacerClubInfo.Category}, selection, selectionArgs, null);
//				// Found a racer category
// 				if(racerCategory != null && racerCategory.getCount() > 0){
// 					racerCategory.moveToFirst();
// 					
// 					int catCol = racerCategory.getColumnIndex(RacerClubInfo._ID);
//	 				Long racerClubInfo_ID = racerCategory.getLong(catCol);
//	 				String racerCat = racerCategory.getString(racerCategory.getColumnIndex(RacerClubInfo.Category));
//	 				// If the new category doesn't equal the old category, do an upgrade
//	 				if(!racerCat.equals(category)){
//	 					Toast.makeText(getActivity(), R.string.IdenticalRacerUpgrade, Toast.LENGTH_LONG).show();
//	 					RacerClubInfo.Update(getActivity(), racerClubInfo_ID, null, null, null, null, null, null, null, null, null, true);
//	 				}
// 				}
// 				
// 				if(racerCategory != null){
// 					racerCategory.close();
// 					racerCategory = null;
// 	 			}
 			}else{
	 			resultUri = Racer.Create(getActivity(), firstName, lastName, 0, 0, 0, "None", 0);
	 			racer_ID = Long.parseLong(resultUri.getLastPathSegment());
 			}
 			if(previousRacers != null){
	 			previousRacers.close();
	 			previousRacers = null;
 			}
 			
 			long racerInfo_ID = -1;
 			int year = Calendar.getInstance().get(Calendar.YEAR);
 			
 			// Create the RacerClubInfo record
 			int age = 0;
	     	resultUri = RacerClubInfo.Create(getActivity(), racer_ID, "0", year, "All", 0, 0, 0, age, spinCategory.getSelectedItemId(), false);
	     	racerInfo_ID = Long.parseLong(resultUri.getLastPathSegment());
 			Log.i(LOG_TAG, "AddNewRacer racerInfo_ID: " + Long.toString(racerInfo_ID));
 			if(checkin){
 				CheckInHandler task = new CheckInHandler(getActivity());
 				task.execute(new Long[] { racerInfo_ID });	
 			}
 			success = true;
 		}else{
 			// Show a message that says that something isn't valid    
 			Log.e(LOG_TAG, "AddNewRacer failed");
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
		getActivity().sendBroadcast(racerAdded);
	}
	
	@Override
	public void dismiss() {
		// Hide the dialog
    	super.dismiss();
		
		// Set the textboxes in the dialog to an empty string
		txtFirstName.setText("");
		txtLastName.setText("");
		txtUSACNumber.setText("");
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddRacer)
			{
				Log.v(LOG_TAG, "btnAddNewRacerClickHandler");
				
				// First name
				String firstName = txtFirstName.getText().toString();
				// Last name
				String lastName = txtLastName.getText().toString();
				// Category
				//String category = spinCategory.getSelectedItem().toString();	
				// USACNumber
				//String usacNumber = txtUSACNumber.getText().toString();
		
				if(firstName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a first name", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(lastName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a last name", Toast.LENGTH_LONG).show();
					return;
				}

//				if(usacNumber.trim().equals("")){
//					Toast.makeText(getActivity(), "Please enter a USAC license number", 3000).show();
//					return;
//				}				
				
				if(AddNewRacer(firstName, lastName, spinCategory.getSelectedItemId())){
					// Hide the dialog
			    	dismiss();
				}
			} else {
				super.onClick(v);
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
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case TEAM_LOADER:
				projection = new String[]{TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName};
				selection = null;
				selectionArgs = null;
				sortOrder = TeamInfo.TeamName;
				loader = new CursorLoader(getActivity(), TeamInfo.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			Log.i(LOG_TAG(), "onLoadFinished start: id="	+ Integer.toString(loader.getId()));
			switch (loader.getId()) {
				case TEAM_LOADER:					
					cursor.moveToFirst();
					teamsCA.swapCursor(cursor);
					break;
			}
			Log.i(LOG_TAG(),"onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoadFinished error", ex);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try {
			Log.i(LOG_TAG(),
					"onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch (loader.getId()) {
				case TEAM_LOADER:
					teamsCA.swapCursor(null);
					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoaderReset error", ex);
		}	
	}
}
