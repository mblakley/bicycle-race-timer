package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class TeamCheckInHandler extends AsyncTask<Long, Void, String> {
	
	private Context context;
	
	public TeamCheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long teamInfo_ID = params[0];

		String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID, TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = null;
		String sortOrder = RaceResults.StartOrder;
		
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = TeamCheckInViewExclusive.ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;
     	// StartTimeOffset (startInterval * (StartOrder - 1)) - Will be adjusted based on initial start time
     	Long startInterval = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	Long startTimeOffset = (startInterval * startOrder) * 1000l;
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
     	// RacerInfo_ID (default null)
     	Long racerInfo_ID = null;
     	long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	
     	Uri result = RaceResults.Create(context, racerInfo_ID, race_ID, startOrder, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints, teamInfo_ID);
     	
		return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	