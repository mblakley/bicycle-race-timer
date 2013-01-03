package com.xcracetiming.android.tttimer.WizardPages;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.AsyncTasks.UploadUSACNumbersToDropboxTask;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.Utilities.Calculations;
import com.xcracetiming.android.tttimer.Wizards.AddRaceWizard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdminMenuView extends BaseWizardPage implements View.OnClickListener {
	public static final String LOG_TAG = "AdminMenuView";
	
    private Handler messageTimerHandler = new Handler();
    private Runnable hideMessage = new Runnable() {
	    public void run() {
			getTextView(R.id.txtMessage).setVisibility(View.INVISIBLE);
			getTextView(R.id.txtMessage).setText("");
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
		return inflater.inflate(R.layout.dialog_admin_menu, container, false);		
	}
	
	@Override
	public void setArguments(Bundle args) {
		args.getInt("TitleBar");
	}
	
	@Override
	protected void addListeners() {
		super.addListeners();	
		
		getButton(R.id.btnAddLocation).setOnClickListener(this);
		getButton(R.id.btnEditLocation).setOnClickListener(this);
		getButton(R.id.btnAddRace).setOnClickListener(this);
		getButton(R.id.btnEditRace).setOnClickListener(this);
		getButton(R.id.btnRecalculateResults).setOnClickListener(this);
		getButton(R.id.btnSettings).setOnClickListener(this);
		getButton(R.id.btnUploadToDropbox).setOnClickListener(this);		
		getButton(R.id.btnGetImagesFromDropbox).setOnClickListener(this);
		
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AdminMenu;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// If we're resuming this activity from going out and attempting to authenticate dropbox account, do extra stuff
		if(Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "false"))){
			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "false", true);
    		if (mDBApi.getSession().authenticationSuccessful()) {
	            // MANDATORY call to complete auth.
	            // Sets the access token on the session
	            mDBApi.getSession().finishAuthentication();
	            
	            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

	            // Provide your own storeKeys to persist the access token pair
	            // A typical way to store tokens is using SharedPreferences
	            AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, tokens.key, true);
	            AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, tokens.secret, true);
    	    }else{
    	    	Log.v(LOG_TAG, "Authentication not successful");
    	    }
        }
	}
	
	@Override
	public void onPause() {
		super.onPause();

		getTextView(R.id.txtMessage).setVisibility(View.INVISIBLE);
		getTextView(R.id.txtMessage).setText("");
		messageTimerHandler.removeCallbacks(hideMessage);
	}
	
	public void onClick(View v) { 
		try{
			switch(v.getId()){			
				case R.id.btnAddRace:
					Intent showAddRace = new Intent();
					showAddRace.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAddRace.putExtra("ShowView", new AddRaceWizard().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddRace);
					break;
				case R.id.btnAddLocation:
					Intent showAddLocation = new Intent();
					showAddLocation.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAddLocation.putExtra("ShowView", new AddLocationView().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddLocation);
					break;
				case R.id.btnEditRace:
					EditRaceConfiguration editRaceDialog = new EditRaceConfiguration();
					Bundle b = new Bundle();
					editRaceDialog.setArguments(b);
					//editRaceDialog.show(fm, EditRaceConfiguration.LOG_TAG);
					break;
				case R.id.btnRecalculateResults:
					try{
						getTextView(R.id.txtMessage).setVisibility(View.VISIBLE);
						getTextView(R.id.txtMessage).setText("Recalculating results");
						RecalculateResults();
						getTextView(R.id.txtMessage).setText("Recalculation complete");
					}catch(Exception ex){
						getTextView(R.id.txtMessage).setText("Unable to recalculate results");
					}
					// Hide the message after a few seconds
					messageTimerHandler.removeCallbacks(hideMessage);
					messageTimerHandler.postDelayed(hideMessage, 3000);
					break;
				case R.id.btnSettings:
			        Intent showAppSettings = new Intent();
					showAppSettings.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAppSettings.putExtra("ShowView", new AppSettingsView().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAppSettings);
					break;
				case R.id.btnUploadToDropbox:
					AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
					AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
					mDBApi = new DropboxAPI<AndroidAuthSession>(session);
					if(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null) == null || AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null) == null){				
						AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);
						AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "true", true);
						mDBApi.getSession().startAuthentication(getActivity());
					}else{
						// Create a file on the SD card for the results and roster, and upload the roster to dropbox
						UploadUSACNumbersToDropboxTask uploadTask = new UploadUSACNumbersToDropboxTask(getActivity());
						uploadTask.execute();
					}
					getTextView(R.id.txtMessage).setVisibility(View.VISIBLE);
					getTextView(R.id.txtMessage).setText("Started roster upload.");
					// Hide the message after a few seconds
					messageTimerHandler.removeCallbacks(hideMessage);
					messageTimerHandler.postDelayed(hideMessage, 3000);
					break;
				case R.id.btnEditLocation:
			    	//EditLocation editLocation = new EditLocation();
			    	//editLocation.show(fm, EditLocation.LOG_TAG);
			    	break;
				case R.id.btnGetImagesFromDropbox:
					AppKeyPair appKeys2 = new AppKeyPair(APP_KEY, APP_SECRET);
					AndroidAuthSession session2 = new AndroidAuthSession(appKeys2, ACCESS_TYPE);
					mDBApi = new DropboxAPI<AndroidAuthSession>(session2);
			    	
					if(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null) == null || AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null) == null){				
						AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);
						AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_AuthenticatingDropbox_Name, "true", true);
						mDBApi.getSession().startAuthentication(getActivity());
					}
			    	Intent showAddLocationImages = new Intent();
					showAddLocationImages.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAddLocationImages.putExtra("ShowView", new AddLocationImages().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddLocationImages);
					break;
				default:
					super.onClick(v);
					break;
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
	
	private void RecalculateResults() {
    	Calculations.CalculateCategoryPlacings(getActivity(), Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "0")));
    	Calculations.CalculateOverallPlacings(getActivity(), Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "0")));  
	}
	
	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}
