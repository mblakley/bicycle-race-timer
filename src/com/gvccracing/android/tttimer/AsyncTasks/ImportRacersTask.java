package com.gvccracing.android.tttimer.AsyncTasks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.Utilities.ImageFormatter;


public class ImportRacersTask extends AsyncTask<Void, Integer, Void> {		
	
	final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private Context context;
	
	public ImportRacersTask(Context context){
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... notUsed) {

		try {
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			
			AccessTokenPair access = new AccessTokenPair(AppSettings.ReadValue(context, AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.ReadValue(context, AppSettings.AppSetting_DropBox_Secret_Name, null));
			mDBApi.getSession().setAccessTokenPair(access);

			// Get the list of all images from the "LocationImages" folder on dropbox
		    Entry imageFiles = mDBApi.metadata("/RacerLists", 1000, null, true, null);

			// Loop through and fill in the list of images with DownloadedImage objects
		    for(Entry file : imageFiles.contents){
		    	String filename = file.fileName();
		    	byte[] fileBytes = ImageFormatter.GetImageBytesFromDropBox("/RacerLists/" + filename, context, null);
		    	InputStream is = new ByteArrayInputStream(fileBytes);
		    	BufferedReader br = new BufferedReader(new java.io.InputStreamReader(is));
		    	String content = "";
		        try {
					while((content = br.readLine())!=null)
					{
						String category = "Varsity";
						if(filename.contains("modified")){
							category = "Modified";						
						}
						String[] fields = content.split(",");
						String lastName = fields[0];
						String firstName = fields[1];
						Long grade = Long.parseLong(fields[2].trim());
						String gender = fields[3];
						float speedLevel = Float.parseFloat(fields[4].trim());
						
						Uri racerUri = Racer.Create(context, firstName, lastName, gender, 0, 0, null, 0);
						
						Long racer_ID = Long.parseLong(racerUri.getLastPathSegment());
						
						RacerClubInfo.Create(context, racer_ID, 23l, category, grade, speedLevel);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		} catch (DropboxUnlinkedException e) {
		    // User has unlinked, ask them to link again here.
		    Log.e("", "User has unlinked.", e);
		} catch (DropboxException e) {
		    Log.e("", "Something went wrong while uploading.", e);
		}			
		
		return null;
	}
}