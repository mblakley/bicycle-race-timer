package com.gvccracing.android.tttimer.AsyncTasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;

public class UploadUSACNumbersToDropboxTask extends AsyncTask<Void, Void, Void> {
	private Context context;
	/**
	 * The string used for logging everywhere in this class
	 */
	public static final String LOG_TAG = UploadUSACNumbersToDropboxTask.class.getSimpleName();

    /**
     * Used for dropbox
     */
	private DropboxAPI<AndroidAuthSession> mDBApi;
    final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private long raceTypeID = 0;
	
	protected String LOG_TAG() {
		return AllRacersStartedTask.class.getSimpleName();
	}
	
	public UploadUSACNumbersToDropboxTask(Context c){
		context = c;

		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
	}
	
	@Override
	protected Void doInBackground(Void... params) {					
		raceTypeID = Race.getValues(context, Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"))).get(Race.RaceType);
		
        try {
        	String[] projection;
    		String selection;
    		String[] selectionArgs = null;
    		String sortOrder;
        	projection = new String[]{Racer.USACNumber};
			selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
			selectionArgs = null;
			sortOrder = Racer.USACNumber;
			Cursor usacNumbersCursor;
			if(raceTypeID == 1){
				usacNumbersCursor = TeamCheckInViewExclusive.Read(context, projection, selection, selectionArgs, sortOrder);
			}else{
				usacNumbersCursor = CheckInViewExclusive.Read(context, projection, selection, selectionArgs, sortOrder);
			}
			if(usacNumbersCursor != null && usacNumbersCursor.getCount() > 0){
				String filename = WriteRosterToFile(usacNumbersCursor);
				UploadFileToDropBox(filename);
				usacNumbersCursor.close();
			}
			usacNumbersCursor = null;
        } catch (IllegalStateException e) {
            Log.i(LOG_TAG, "Error authenticating", e);
        }
		return null;
	}
	
	@Override
	protected void onPostExecute(Void nothing) {
		// Transition to the results tab
//		Intent changeTab = new Intent();
//		changeTab.setAction(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
//		changeTab.putExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG, ResultsTab.ResultsTabSpecName);
//		context.sendBroadcast(changeTab);
	}
	
	private void UploadFileToDropBox(String filename) {
		Log.i(LOG_TAG, "UploadFileToDropBox start: filename=" + filename);
		AccessTokenPair access = new AccessTokenPair(AppSettings.ReadValue(context, AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.ReadValue(context, AppSettings.AppSetting_DropBox_Secret_Name, null));
		mDBApi.getSession().setAccessTokenPair(access);
		
		// Uploading content.
		FileInputStream inputStream = null;
		try {
			File rootDir = Environment.getExternalStorageDirectory();
        	File rostersDirectory = new File(rootDir.toString() + "/gvcc_rosters/");
			File file = new File(rostersDirectory, filename);
		    inputStream = new FileInputStream(file);
		    Entry newEntry = mDBApi.putFile("/" + filename, inputStream, file.length(), null, null);
		    Log.i(LOG_TAG, "The uploaded file's rev is: " + newEntry.rev);
		} catch (DropboxUnlinkedException e) {
		    // User has unlinked, ask them to link again here.
		    Log.e(LOG_TAG, "User has unlinked.");
		    Intent resultData = new Intent();
			resultData.putExtra("Error", "User has unlinked: " + e.toString());
		} catch (DropboxException e) {
		    Log.e(LOG_TAG, "Something went wrong while uploading.");
		    Intent resultData = new Intent();
			resultData.putExtra("Error", "Error uploading: " + e.toString());
		} catch (FileNotFoundException e) {
		    Log.e(LOG_TAG, "File not found.");
		    Intent resultData = new Intent();
			resultData.putExtra("Error", "File not found: " + e.toString());
		} finally {
		    if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		        	
		        }
		    }
		    Intent resultData = new Intent();
			resultData.putExtra("Success", filename);
			Log.i(LOG_TAG, "UploadFileToDropBox completed: filename=" + filename);
		}
	}

	private String WriteRosterToFile(Cursor cursor) {
		boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        String filename = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        if( mExternalStorageAvailable && mExternalStorageWriteable){
        	Long raceDateMS = Race.getValues(context, Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"))).get(Race.RaceDate);
    		Date raceDateTemp = new Date(raceDateMS);
    		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
    		filename = dateFormat.format(raceDateTemp) + "_GTourRoster.csv";
			// Create a path where we will place our private file on external
			// storage.
        	File rootDir = Environment.getExternalStorageDirectory();
        	File rostersDirectory = new File(rootDir.toString() + "/gvcc_rosters/");
        	// have the object build the directory structure, if needed.
        	if( !rostersDirectory.exists()){
        		rostersDirectory.mkdirs();
        	}
			File file = new File(rostersDirectory, filename);
			if( !file.exists()){
            	try {
					file.createNewFile();
				} catch (IOException e) {
					Toast.makeText(context, "Error creating file: " + e.toString(), Toast.LENGTH_LONG).show();
					
					Intent resultData = new Intent();
					resultData.putExtra("Error", "Error creating file: " + e.toString());
				}
			}
			// try to write the content
			try {
				// Note that if external storage is
			    // not currently mounted this will silently fail.
				FileWriter os = new FileWriter(file, true);
				cursor.moveToFirst();
				while(!cursor.isAfterLast()){
				    os.write(Long.toString(cursor.getLong(cursor.getColumnIndex(Racer.USACNumber))) + "\n");
				    cursor.moveToNext();
				}
			    os.close();
			    os = null;
			} catch (IOException e) {
			    // Unable to create file, likely because external storage is
			    // not currently mounted.
				Toast.makeText(context, "Error saving to file: " + e.toString(), Toast.LENGTH_LONG).show();
				
				Intent resultData = new Intent();
				resultData.putExtra("Error", "Error saving to file: " + e.toString());
			}
        }else{
        	Toast.makeText(context, "Unable to save usac numbers to file.", Toast.LENGTH_LONG).show();
        	
        	Intent resultData = new Intent();
			resultData.putExtra("Error", "Unable to save usac numbers to file.  External storage not available or not writable.");
        }
        
        return filename;
	}
}
