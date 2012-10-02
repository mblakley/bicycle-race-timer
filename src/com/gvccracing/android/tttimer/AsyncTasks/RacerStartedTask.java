package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class RacerStartedTask extends AsyncTask<Long, Void, Void> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return RacerStartedTask.class.getSimpleName();
	}
	
	public RacerStartedTask(Context c){
		context = c;
	}
	
	@Override
	protected Void doInBackground(Long... params) {			
		long startTime = params[0];
		long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));//Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "1"));
		
		ContentValues content = new ContentValues();
		content.put(RaceResults.StartTime, startTime);
		
		context.getContentResolver().update(RaceResults.CONTENT_URI, content, RaceResults.Race_ID + "=?", new String[]{Long.toString(race_ID)});
		
		return null;
	}
}
