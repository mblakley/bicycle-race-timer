package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class RacerStartedTask  extends AsyncTask<Long, Void, Void> {
	
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
		long startTimeOffsetOnDeck = params[1];
		long raceResult_IDOnDeck = params[2];
		
		ContentValues content = new ContentValues();
		content.put(RaceResults.StartTime, startTime + startTimeOffsetOnDeck);
		context.getContentResolver().update(RaceResults.CONTENT_URI, content, RaceResults._ID + "=?", new String[]{Long.toString(raceResult_IDOnDeck)});
		
		return null;
	}
}
