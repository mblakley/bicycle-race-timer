package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Calendar;

import android.content.Context;
import android.net.Uri;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class GhostCheckInHandler extends CheckInHandler {
	
	public GhostCheckInHandler(Context c) {
		super(c);
	}

	@Override
	protected String doInBackground(Long... params) {			
		Long racerClubInfo_ID = params[0];
		Long teamInfo_ID = params[1];
		long numSpots = params[2];

		String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID, Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = RacerClubInfo.Year + "= ? AND " + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = new String[]{ Integer.toString(Calendar.getInstance().get(Calendar.YEAR))};
		String sortOrder = RaceResults.StartOrder;
		
     	// StartOrder (count of current check-ins + 1)
     	int nextOrder = CheckInViewExclusive.ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;

     	long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	long startInterval = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	Uri result = RacerClubInfo.CONTENT_URI;
     	for(int startOrder = nextOrder; startOrder < nextOrder + numSpots; startOrder++){
     		// Do the check in
     		result = CheckInRacer(racerClubInfo_ID, teamInfo_ID, startOrder, startInterval, race_ID);
     	}
     			
		return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	