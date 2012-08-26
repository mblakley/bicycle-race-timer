package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Calendar;
import java.util.Hashtable;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.Utilities.Enums;

public class CheckInHandler extends AsyncTask<Long, Void, String> {
	
	protected Context context;
	
	public CheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long racerClubInfo_ID = params[0];

     	long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	int startOrder = 0;
     	long startInterval = 0;
		Hashtable<String, Long> raceValues = Race.getValues(context, race_ID);
		long raceType = raceValues.get(Race.RaceType);
		if(raceType == Enums.RaceType.TimeTrial.ID()){
			String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID, Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
			String selection = RacerClubInfo.Year + "= ? AND " + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
			String[] selectionArgs = new String[]{ Integer.toString(Calendar.getInstance().get(Calendar.YEAR))};
			String sortOrder = RaceResults.StartOrder;
			
	     	// StartOrder (count of current check-ins + 1)
	     	startOrder = CheckInViewExclusive.ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;
	     	// Start interval
	     	startInterval = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
		}
     	// Do the check in
     	Uri result = CheckInRacer(racerClubInfo_ID, null, startOrder, startInterval, race_ID); 
     			
		return result.toString();
	}
	
	protected Uri CheckInRacer(Long racerClubInfo_ID, Long teamInfo_ID, int startOrder, long startInterval, long race_ID){
		// StartTimeOffset (startInterval * (StartOrder - 1)) - Will be adjusted based on initial start time
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
     	
     	return RaceResults.Create(context, racerClubInfo_ID, race_ID, startOrder, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints, teamInfo_ID);
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	