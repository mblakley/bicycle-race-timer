package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Calendar;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class CheckInHandler extends AsyncTask<Long, Void, String> {
	
	protected Context context;
	
	public CheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long raceResult_ID = params[0];
		
		Integer result = RaceResults.Delete(context, RaceResults._ID + "=?", new String[]{Long.toString(raceResult_ID)});		
     			
		return result.toString();
	}
	
	protected Uri CheckInRacer(Long racerClubInfo_ID, Long teamInfo_ID, int startOrder, long startInterval, long race_ID){
		// StartTimeOffset (startInterval * (StartOrder - 1)) - Will be adjusted based on initial start time
     	Long startTimeOffset = 0l;//(startInterval * startOrder) * 1000l;
     	// Start Time (null, since we haven't started yet
     	Long startTime = null;
     	// EndTime (null, since we won't know until the end)
     	Long endTime = null;
     	// ElapsedTime (null, since we don't have end time)
     	Long elapsedTime = null;
     	// OverallPlacing (null, no results yet)
     	Integer overallPlacing = null;
     	// CategoryPlacing (null, no results yet)
     	Integer categoryPlacing = null;
     	// Points (default 0)
     	Integer points = 0;
     	// PrimePoints (default 0)
     	Integer primePoints = 0;
     	
     	return RaceResults.Create(context, racerClubInfo_ID, race_ID, startOrder, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints, teamInfo_ID);
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	