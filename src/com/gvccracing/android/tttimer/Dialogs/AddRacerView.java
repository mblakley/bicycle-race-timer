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
 				// TODO: show another dialog to ask if the racer had signed up previously
 				racer_ID = previousRacers.getLong(previousRacers.getColumnIndex(Racer._ID));
 				
 				selection = RacerClubInfo.Racer_ID + "=? and " + RacerClubInfo.Upgraded + "=?";
 	 			selectionArgs = new String[]{Long.toString(racer_ID), Long.toString(0l)}; 
 				
 				Cursor racerCategory = RacerClubInfo.Read(getActivity(), new String[]{RacerClubInfo._ID, RacerClubInfo.Category}, selection, selectionArgs, null);
 				
 				if(racerCategory != null && racerCategory.getCount() > 0){
 					racerCategory.moveToFirst();
 					
 					int catCol = racerCategory.getColumnIndex(RacerClubInfo._ID);
	 				Long racerClubInfo_ID = racerCategory.getLong(catCol);
	 				String racerCat = racerCategory.getString(racerCategory.getColumnIndex(RacerClubInfo.Category));
	 				
	 				if(!racerCat.equals(category)){
	 					RacerClubInfo.Update(getActivity(), racerClubInfo_ID, null, null, null, null, null, null, null, null, null, true);
	 				}
 				}
 				
 				if(racerCategory != null){
 					racerCategory.close();
 					racerCategory = null;
 	 			}
// 				AlertDialog.Builder b = new AlertDialog.Builder(getActivity().getApplicationContext());
// 			    b.setTitle("Please enter a password");
// 			    final EditText input = new EditText(getActivity().getApplicationContext());
// 			    b.setView(input);
// 			    b.setPositiveButton("OK", new DialogInterface.OnClickListener()
// 			    {
// 			        public void onClick(DialogInterface dialog, int whichButton)
// 			        {
// 			           // SHOULD NOW WORK
// 			           result = input.getText().toString();
// 			        }
// 			    });
// 			    b.setNegativeButton("CANCEL", null);
// 			    b.create().show();
// 	        	AddLocationView addLocationDialog = new AddLocationView();
// 				FragmentManager fm = getActivity().getSupportFragmentManager();
// 				addLocationDialog.show(fm, AddLocationView.LOG_TAG);
 			}else{
	 			resultUri = Racer.Create(getActivity(), firstName, lastName, Integer.parseInt(usacNumber), 0, 0, "None", 0);
	 			racer_ID = Long.parseLong(resultUri.getLastPathSegment());
 			}
 			if(previousRacers != null){
	 			previousRacers.close();
	 			previousRacers = null;
 			}
 			
 			long racerInfo_ID = -1;
 			// Check if there's a racerClubInfo record for the given barcode for this year
 			int year = Calendar.getInstance().get(Calendar.YEAR);
 			/*Cursor racerClubInfo = RacerClubInfo.Read(getActivity(), barcode, year);
 			
 			if(racerClubInfo != null && racerClubInfo.getCount() > 0){
 				racerClubInfo.moveToFirst();
 				// This barcode already exists in the DB for this year
 				// Figure out if it's associated with the correct racer_ID
 				Long prevRacerID = racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo.Racer_ID));
 				if( prevRacerID == racer_ID){
 					// The barcode is in the DB, and it's associated with the correct racer
 					// TODO: Notify the user that the record already exists
 					AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
 	 			    b.setTitle("Duplicate");
 	 			    TextView message = new TextView(getActivity());
 	 			    message.setText("This racer already exists in the database");
 	 			    b.setView(message);
 	 			    b.setPositiveButton("OK", new DialogInterface.OnClickListener()
 	 			    {
 	 			        public void onClick(DialogInterface dialog, int whichButton)
 	 			        {
 	 			           // Dismiss all dialogs, since the racer has previously been added
 	 			           dismiss();
 	 			        }
 	 			    });
 	 			    b.create().show();
 				}else{
 					// The barcode is in the DB, but it's associated with a different racer
 					// TODO: Ask the racer what needs to be done
 					AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
 	 			    b.setTitle("Duplicate");
 	 			    TextView message = new TextView(getActivity());
 	 			    message.setText("This barcode already exists in the database.  Please enter a different barcode.");
 	 			    b.setView(message);
 	 			    b.setPositiveButton("OK", new DialogInterface.OnClickListener()
 	 			    {
 	 			        public void onClick(DialogInterface dialog, int whichButton)
 	 			        {
 	 			           // Don't need to do anything in here, it was just a message to the user
 	 			        }
 	 			    });
 	 			    b.create().show();
 				}
 			}else*/{
	 			// Create the RacerClubInfo record
	 			int age = 0;
	 			Long gvccID = null;
		     	resultUri = RacerClubInfo.Create(getActivity(), racer_ID, "0", year, category, 0, 0, 0, age, gvccID, false);
		     	racerInfo_ID = Long.parseLong(resultUri.getLastPathSegment());
	 			Log.i(LOG_TAG, "AddNewRacer racerInfo_ID: " + Long.toString(racerInfo_ID));
	 			//SendNotification(racerInfo_ID);
	 			if(checkin){
	 				CheckInHandler task = new CheckInHandler(getActivity());
	 				task.execute(new Long[] { racerInfo_ID });	
	 			}
	 			success = true;
 			}
 			/*if(racerClubInfo != null){
 				racerClubInfo.close();
 				racerClubInfo = null;
 			}*/
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
