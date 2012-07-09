package com.gvccracing.android.tttimer.Dialogs;

import java.util.Calendar;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.CheckInHandler;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class AddRacerView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AddRacerView";
	
	/**
     * This is a special intent action that gets fired when a new racer is added
     */
    public static final String RACER_ADDED_ACTION = "com.gvccracing.android.tttimer.RACER_ADDED";

	public static final String CHECKIN_RACER_ACTION = "com.gvccracing.android.tttimer.CHECKIN_RACER";
	
	protected Button btnAddRacer;
	
	protected EditText txtFirstName;
	protected EditText txtLastName;
	protected EditText txtUSACNumber;
	protected Spinner spinCategory;
	
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
		
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		getActivity(), R.array.category_array, R.layout.control_simple_spinner );
 		adapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
 		spinCategory.setAdapter(adapter);
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddNewRacer;
	}

 	/*
 	 * Add a new racer that can be checked in
 	 */
 	private boolean AddNewRacer(String firstName, String lastName, String usacNumber, String category) 
 	{
 		boolean success = false;
 		// If none of the fields are blank, we're ok to add the record
 		if(firstName.trim().length() != 0 && lastName.trim().length() != 0 && usacNumber.trim().length() != 0){
 			String selection = "UPPER(" + Racer.FirstName + ")=? AND UPPER(" + Racer.LastName + ")=?";
 			String[] selectionArgs = new String[]{firstName.trim().toUpperCase(), lastName.trim().toUpperCase()};
 			long racer_ID = 0;
 			Uri resultUri;
 			Cursor previousRacers = Racer.Read(getActivity(), new String[]{Racer._ID}, selection, selectionArgs, null);
 			if(previousRacers != null && previousRacers.getCount() > 0){
 				previousRacers.moveToFirst();
 				// Found at least one other racer with the same name.
 				racer_ID = previousRacers.getLong(previousRacers.getColumnIndex(Racer._ID));
 				
 				selection = RacerClubInfo.Racer_ID + "=? and " + RacerClubInfo.Upgraded + "=?";
 	 			selectionArgs = new String[]{Long.toString(racer_ID), Long.toString(0l)}; 
 				
 	 			// Get the current category of this racer
 				Cursor racerCategory = RacerClubInfo.Read(getActivity(), new String[]{RacerClubInfo._ID, RacerClubInfo.Category}, selection, selectionArgs, null);
				// Found a racer category
 				if(racerCategory != null && racerCategory.getCount() > 0){
 					racerCategory.moveToFirst();
 					
 					int catCol = racerCategory.getColumnIndex(RacerClubInfo._ID);
	 				Long racerClubInfo_ID = racerCategory.getLong(catCol);
	 				String racerCat = racerCategory.getString(racerCategory.getColumnIndex(RacerClubInfo.Category));
	 				// If the new category doesn't equal the old category, do an upgrade
	 				if(!racerCat.equals(category)){
	 					Toast.makeText(getActivity(), R.string.IdenticalRacerUpgrade, 4000);
	 					RacerClubInfo.Update(getActivity(), racerClubInfo_ID, null, null, null, null, null, null, null, null, null, true);
	 				}
 				}
 				
 				if(racerCategory != null){
 					racerCategory.close();
 					racerCategory = null;
 	 			}
 			}else{
	 			resultUri = Racer.Create(getActivity(), firstName, lastName, Integer.parseInt(usacNumber), 0, 0, "None", 0);
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
 			Long gvccID = null;
	     	resultUri = RacerClubInfo.Create(getActivity(), racer_ID, "0", year, category, 0, 0, 0, age, gvccID, false);
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
				String category = spinCategory.getSelectedItem().toString();	
				// USACNumber
				String usacNumber = txtUSACNumber.getText().toString();
		
				if(firstName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a first name", 3000).show();
					return;
				}
				
				if(lastName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a last name", 3000).show();
					return;
				}

				if(usacNumber.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a USAC license number", 3000).show();
					return;
				}
				
				if(AddNewRacer(firstName, lastName, usacNumber, category)){
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
}
