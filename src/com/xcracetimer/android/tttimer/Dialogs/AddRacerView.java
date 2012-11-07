package com.xcracetimer.android.tttimer.Dialogs;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.AsyncTasks.CheckInHandler;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

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
    public static final String RACER_ADDED_ACTION = "com.xcracetimer.android.tttimer.RACER_ADDED";

	public static final String CHECKIN_RACER_ACTION = "com.xcracetimer.android.tttimer.CHECKIN_RACER";
	
	protected Button btnAddRacer;
	
	protected EditText txtFirstName;
	protected EditText txtLastName;
	protected EditText txtUSACNumber;
	protected Spinner spinGrade;

	private ArrayAdapter<CharSequence> gradesCA = null;
	
	private boolean checkin = false;
	private long teamInfo_ID;
	private String gender;
	private String category;
	
	public AddRacerView(boolean checkinAfterAdd, long teamInfo_ID, String gender, String category){
		checkin = checkinAfterAdd;
		this.gender = gender;
		this.teamInfo_ID = teamInfo_ID;
		this.category = category;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_racer, container, false);

		btnAddRacer = (Button) v.findViewById(R.id.btnAddNewRacer);
		btnAddRacer.setOnClickListener(this);
		
		txtFirstName = (EditText) v.findViewById(R.id.txtFirstName);
		txtLastName = (EditText) v.findViewById(R.id.txtLastName);
		spinGrade = (Spinner) v.findViewById(R.id.spinnerGrade);
		txtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		gradesCA = ArrayAdapter.createFromResource(
      		  getActivity(), R.array.grades_array, android.R.layout.simple_spinner_item );
		gradesCA.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
		spinGrade.setAdapter(gradesCA);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddNewRacer;
	}

 	/*
 	 * Add a new racer that can be checked in
 	 */
 	private boolean AddNewRacer(String firstName, String lastName, long grade) 
 	{
 		boolean success = false;
 		// If none of the fields are blank, we're ok to add the record
 		if(firstName.trim().length() != 0 && lastName.trim().length() != 0){
 			String selection = "UPPER(" + Racer.FirstName + ")=? AND UPPER(" + Racer.LastName + ")=?";
 			String[] selectionArgs = new String[]{firstName.trim().toUpperCase(), lastName.trim().toUpperCase()};
 			long racer_ID = 0;
 			Uri resultUri;
 			Cursor previousRacers = Racer.Read(getActivity(), new String[]{Racer._ID}, selection, selectionArgs, null);
 			if(previousRacers != null && previousRacers.getCount() > 0){
 				previousRacers.moveToFirst();
 				// Found at least one other racer with the same name.
 				racer_ID = previousRacers.getLong(previousRacers.getColumnIndex(Racer._ID));
 			}else{
	 			resultUri = Racer.Create(getActivity(), firstName, lastName, gender, 0, 0, "", 0);
	 			racer_ID = Long.parseLong(resultUri.getLastPathSegment());
 			}
 			if(previousRacers != null){
	 			previousRacers.close();
	 			previousRacers = null;
 			} 			
 			
 			// Create the RacerClubInfo record
	     	resultUri = RacerClubInfo.Create(getActivity(), racer_ID, teamInfo_ID, category, grade, 1);
	     	long racerInfo_ID = Long.parseLong(resultUri.getLastPathSegment());
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
				// Grade
				Long grade = Long.parseLong(spinGrade.getSelectedItem().toString());	
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
			
				if(AddNewRacer(firstName, lastName, grade)){
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
