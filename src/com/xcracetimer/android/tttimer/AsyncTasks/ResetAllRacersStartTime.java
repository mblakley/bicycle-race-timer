package com.xcracetimer.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class ResetAllRacersStartTime  extends AsyncTask<Long, Void, Void> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return ResetAllRacersStartTime.class.getSimpleName();
	}
	
	public ResetAllRacersStartTime(Context c){
		context = c;
	}

	@Override
	protected Void doInBackground(Long... params) {
		long race_ID = params[0];
		
		ContentValues content = new ContentValues();
		content.putNull(RaceResults.StartTime);
		context.getContentResolver().update(RaceResults.CONTENT_URI, content, RaceResults.Race_ID + "=?", new String[]{Long.toString(race_ID)});		
		
		return null;
	}
}
