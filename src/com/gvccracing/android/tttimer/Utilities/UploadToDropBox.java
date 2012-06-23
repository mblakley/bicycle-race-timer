package com.gvccracing.android.tttimer.Utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class UploadToDropBox extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	/**
	 * The string used for logging everywhere in this class
	 */
	public static final String LOG_TAG = UploadToDropBox.class.getSimpleName();
	private boolean previouslyAuthenticated = false;

    /**
     * Used for dropbox
     */
    final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private static final int USAC_NUMBERS_LOADER = 0x222;
	private long raceTypeID = 0;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		try{
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			if(AppSettings.ReadValue(this, AppSettings.AppSetting_DropBox_Key_Name, null) == null || AppSettings.ReadValue(this, AppSettings.AppSetting_DropBox_Secret_Name, null) == null){				
				previouslyAuthenticated = false;

				mDBApi.getSession().startAuthentication(UploadToDropBox.this);
			}
		}catch(Exception ex){
			Log.e(LOG_TAG, "onStart failed: ", ex);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		raceTypeID = Race.getValues(this, Long.parseLong(AppSettings.ReadValue(this, AppSettings.AppSetting_RaceID_Name, "-1"))).get(Race.RaceType);
		
        try {
            if(!previouslyAuthenticated){
        		if (mDBApi.getSession().authenticationSuccessful()) {
		            // MANDATORY call to complete auth.
		            // Sets the access token on the session
		            mDBApi.getSession().finishAuthentication();
		            
		            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
	
		            // Provide your own storeKeys to persist the access token pair
		            // A typical way to store tokens is using SharedPreferences
		            AppSettings.Update(this, AppSettings.AppSetting_DropBox_Key_Name, tokens.key, true);
		            AppSettings.Update(this, AppSettings.AppSetting_DropBox_Secret_Name, tokens.secret, true);
        	    }else{
        	    	Log.i(LOG_TAG, "Authentication not successful");
        	    }
            }
        } catch (IllegalStateException e) {
            Log.i(LOG_TAG, "Error authenticating", e);
        }
		
		getSupportLoaderManager().restartLoader(USAC_NUMBERS_LOADER, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case USAC_NUMBERS_LOADER:
				projection = new String[]{Racer.USACNumber};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Racer.USACNumber;
				if(raceTypeID == 1){
					loader = new CursorLoader(this, TeamCheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				}else{
					loader = new CursorLoader(this, CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				}
				
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case USAC_NUMBERS_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						String filename = WriteRosterToFile(cursor);
						UploadFileToDropBox(filename);
						cursor.close();
					}
					cursor = null;
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}			
	}

	private void UploadFileToDropBox(String filename) {
		Log.i(LOG_TAG, "UploadFileToDropBox start: filename=" + filename);
		AccessTokenPair access = new AccessTokenPair(AppSettings.ReadValue(this, AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.ReadValue(this, AppSettings.AppSetting_DropBox_Secret_Name, null));
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
			setResult(Activity.RESULT_CANCELED, resultData);
			finish();
		} catch (DropboxException e) {
		    Log.e(LOG_TAG, "Something went wrong while uploading.");
		    Intent resultData = new Intent();
			resultData.putExtra("Error", "Error uploading: " + e.toString());
			setResult(Activity.RESULT_CANCELED, resultData);
			finish();
		} catch (FileNotFoundException e) {
		    Log.e(LOG_TAG, "File not found.");
		    Intent resultData = new Intent();
			resultData.putExtra("Error", "File not found: " + e.toString());
			setResult(Activity.RESULT_CANCELED, resultData);
			finish();
		} finally {
		    if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		        	
		        }
		    }
		    Intent resultData = new Intent();
			resultData.putExtra("Success", filename);
			setResult(Activity.RESULT_OK, resultData);
			Log.i(LOG_TAG, "UploadFileToDropBox completed: filename=" + filename);
			finish();
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
        	Long raceDateMS = Race.getValues(this, Long.parseLong(AppSettings.ReadValue(this, AppSettings.AppSetting_RaceID_Name, "-1"))).get(Race.RaceDate);
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
					Toast.makeText(getBaseContext(), "Error creating file: " + e.toString(), Toast.LENGTH_LONG).show();
					
					Intent resultData = new Intent();
					resultData.putExtra("Error", "Error creating file: " + e.toString());
					setResult(Activity.RESULT_CANCELED, resultData);
					finish();
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
				Toast.makeText(getBaseContext(), "Error saving to file: " + e.toString(), Toast.LENGTH_LONG).show();
				
				Intent resultData = new Intent();
				resultData.putExtra("Error", "Error saving to file: " + e.toString());
				setResult(Activity.RESULT_CANCELED, resultData);
				finish();
			}
        }else{
        	Toast.makeText(getBaseContext(), "Unable to save usac numbers to file.", Toast.LENGTH_LONG).show();
        	
        	Intent resultData = new Intent();
			resultData.putExtra("Error", "Unable to save usac numbers to file.  External storage not available or not writable.");
			setResult(Activity.RESULT_CANCELED, resultData);
			finish();
        }
        
        return filename;
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case USAC_NUMBERS_LOADER:
					// Do nothing
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
