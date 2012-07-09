package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.Utilities.Calculations;
import com.gvccracing.android.tttimer.Utilities.UploadToDropBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AdminMenuView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AdminMenuView";
	
	private Button btnAddLocation;
	private Button btnEditLocation;
	private Button btnAddRace;
	private Button btnEditRace;
	private Button btnRecalculateResults;
	private Button btnSettings;
	private Button btnUploadToDropbox;
	private TextView txtMessage;

    private Handler messageTimerHandler = new Handler();
    private Runnable hideMessage = new Runnable() {
	    public void run() {
			txtMessage.setVisibility(View.INVISIBLE);
			txtMessage.setText("");
		}
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_admin_menu, container, false);

		btnAddLocation = (Button) v.findViewById(R.id.btnAddLocation);
		btnAddLocation.setOnClickListener(this);

		btnEditLocation = (Button) v.findViewById(R.id.btnEditLocation);
		btnEditLocation.setOnClickListener(this);
		
		btnAddRace = (Button) v.findViewById(R.id.btnAddRace);
		btnAddRace.setOnClickListener(this);
		
		btnEditRace = (Button) v.findViewById(R.id.btnEditRace);
		btnEditRace.setOnClickListener(this);
		
		btnRecalculateResults = (Button) v.findViewById(R.id.btnRecalculateResults);
		btnRecalculateResults.setOnClickListener(this);
		
		btnSettings = (Button) v.findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(this);
		
		btnUploadToDropbox = (Button) v.findViewById(R.id.btnUploadToDropbox);
		btnUploadToDropbox.setOnClickListener(this);
		
		txtMessage = (TextView) v.findViewById(R.id.txtMessage);
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AdminMenu;
	}
	
	@Override
	public void onPause() {
		super.onPause();

		messageTimerHandler.removeCallbacks(hideMessage);
	}
	
	public void onClick(View v) { 
		try{
			FragmentManager fm = getFragmentManager();
			if (v == btnAddRace){
				AddRaceView addRaceDialog = new AddRaceView();
				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
			} else if (v == btnAddLocation){
				AddLocationView addLocationDialog = new AddLocationView();
				addLocationDialog.show(fm, AddLocationView.LOG_TAG);
			} else if (v == btnEditRace){
				EditRaceConfiguration editRaceDialog = new EditRaceConfiguration();
				editRaceDialog.show(fm, EditRaceConfiguration.LOG_TAG);
			} else if (v == btnRecalculateResults){
				try{
					txtMessage.setVisibility(View.VISIBLE);
					txtMessage.setText("Recalculating results");
					RecalculateResults();
					txtMessage.setText("Recalculation complete");
				}catch(Exception ex){
					txtMessage.setText("Unable to recalculate results");
				}
				// Hide the message after a few seconds
				messageTimerHandler.removeCallbacks(hideMessage);
				messageTimerHandler.postDelayed(hideMessage, 3000);
			} else if (v == btnSettings){
		    	AppSettingsView appSettingsDialog = new AppSettingsView();
		        appSettingsDialog.show(fm, AppSettingsView.LOG_TAG);
			} else if (v.getId() == R.id.btnUploadToDropbox) {
				// Create a file on the SD card for the results and roster, and upload the roster to dropbox
				Intent intent = new Intent(this.getActivity(), UploadToDropBox.class);
            	//EditText mFileName = (EditText) findViewById(R.id.txtFileName);
		        //intent.putExtra("FILENAME", mFileName.getText().toString());
				startActivityForResult(intent, 0);
			} else if (v == btnEditLocation){
		    	EditLocation editLocation = new EditLocation();
		    	editLocation.show(fm, EditLocation.LOG_TAG);
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
	
	private void RecalculateResults() {
    	Calculations.CalculateCategoryPlacings(getActivity(), Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "0")));
    	Calculations.CalculateOverallPlacings(getActivity(), Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "0")));  
	}
	
	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == Activity.RESULT_OK) {
//	            String contents = intent.getStringExtra("SCAN_RESULT");
//	            // Handle successful scan
//	            //Toast.makeText(getBaseContext(), "The barcode is " + contents, Toast.LENGTH_LONG).show();
//	            //EditText mFileName = (EditText) findViewById(R.id.txtFileName);
//				String filename = mFileName.getText().toString();
//				
//				Date dateNow = new Date();
//				SimpleDateFormat format = new SimpleDateFormat("h:mm:ss a  M/dd/yyyy");
//			    contents = contents + ", " + format.format(dateNow) + "\n";
//				
//	            WriteToFile(filename, contents);
//	            
//	            
//	            intent = new Intent("com.google.zxing.client.android.SCAN");
//		        intent.setPackage("com.google.zxing.client.android");
//		        intent.putExtra("SCAN_MODE", "ONE_D_MODE");
//		        startActivityForResult(intent, 0);
	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // Handle cancel
//	            Toast.makeText(getBaseContext(), "Done scanning", Toast.LENGTH_LONG).show();	        	
	        }
	    }
	}
}
