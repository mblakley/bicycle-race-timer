package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.RaceResults;

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
		context.getContentResolver().update(RaceResults.Instance().CONTENT_URI, content, RaceResults.Race_ID + "=?", new String[]{Long.toString(race_ID)});
		
		return null;
	}
}
