package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Calendar;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class CheckInHandler extends AsyncTask<Long, Void, String> {
	
	private Context context;
	
	public CheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long racerInfo_ID = params[0];

		String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID, Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = RacerClubInfo.Year + "= ? AND " + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = new String[]{ Integer.toString(Calendar.getInstance().get(Calendar.YEAR))};
		String sortOrder = RaceResults.StartOrder;
		
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = CheckInViewExclusive.ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;//DBHelper.fetchNumberOfRaceCheckIns(race_ID) + 1;
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
     	// TeamInfo_ID (default null)
     	Long teamInfo_ID = null;
     	long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	
     	Uri result = RaceResults.Create(context, racerInfo_ID, race_ID, startOrder, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints, teamInfo_ID);
     	
		return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	