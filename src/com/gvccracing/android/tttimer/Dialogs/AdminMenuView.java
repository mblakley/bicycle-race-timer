package com.gvccracing.android.tttimer.Dialogs;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.UploadUSACNumbersToDropboxTask;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.Utilities.Calculations;

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
	private Button btnGetImagesFromDropbox;
	private TextView txtMessage;

    private Handler messageTimerHandler = new Handler();
    private Runnable hideMessage = new Runnable() {
	    public void run() {
			txtMessage.setVisibility(View.INVISIBLE);
			txtMessage.setText("");
		}
	};	

    /**
     * Used for dropbox
     */
    final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
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
		
		btnGetImagesFromDropbox = (Button) v.findViewById(R.id.btnGetImagesFromDropbox);
		btnGetImagesFromDropbox.setOnClickListener(this);
		
		txtMessage = (TextView) v.findViewById(R.id.txtMessage);
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AdminMenu;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// If we're resuming this activity from going out and attempting to authenticate dropbox account, do extra stuff
		if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "false"))){
			AppSettings.Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "false", true);
    		if (mDBApi.getSession().authenticationSuccessful()) {
	            // MANDATORY call to complete auth.
	            // Sets the access token on the session
	            mDBApi.getSession().finishAuthentication();
	            
	            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

	            // Provide your own storeKeys to persist the access token pair
	            // A typical way to store tokens is using SharedPreferences
	            AppSettings.Update(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, tokens.key, true);
	            AppSettings.Update(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, tokens.secret, true);
    	    }else{
    	    	Log.i(LOG_TAG, "Authentication not successful");
    	    }
        }
	}
	
	@Override
	public void onPause() {
		super.onPause();

		txtMessage.setVisibility(View.INVISIBLE);
		txtMessage.setText("");
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
				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
				mDBApi = new DropboxAPI<AndroidAuthSession>(session);
				if(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null) == null || AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null) == null){				
					AppSettings.Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);
					AppSettings.Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "true", true);
					mDBApi.getSession().startAuthentication(getActivity());
				}else{
					// Create a file on the SD card for the results and roster, and upload the roster to dropbox
					UploadUSACNumbersToDropboxTask uploadTask = new UploadUSACNumbersToDropboxTask(getActivity());
					uploadTask.execute();
				}
				txtMessage.setVisibility(View.VISIBLE);
				txtMessage.setText("Started roster upload.");
				// Hide the message after a few seconds
				messageTimerHandler.removeCallbacks(hideMessage);
				messageTimerHandler.postDelayed(hideMessage, 3000);
			} else if (v == btnEditLocation){
		    	EditLocation editLocation = new EditLocation();
		    	editLocation.show(fm, EditLocation.LOG_TAG);
			} else if (v == btnGetImagesFromDropbox){
				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
				mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		    	
				if(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null) == null || AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null) == null){				
					AppSettings.Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);
					AppSettings.Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "true", true);
					mDBApi.getSession().startAuthentication(getActivity());
				}
	            AddLocationImages downloadNewImages = new AddLocationImages();
		    	downloadNewImages.show(fm, AddLocationImages.LOG_TAG);
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
}
