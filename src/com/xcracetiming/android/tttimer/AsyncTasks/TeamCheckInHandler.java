package com.xcracetiming.android.tttimer.AsyncTasks;

import android.content.Context;

import com.xcracetiming.android.tttimer.DataAccess.AppSettings;

public class TeamCheckInHandler extends CheckInHandler {
	
	public TeamCheckInHandler(Context c){
		super(c);
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		//long teamInfo_ID = params[0];

//		String[] projection = new String[]{SeriesRaceTeamResults.RaceResult_ID, TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
//		String selection = SeriesRaceTeamResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
//		String[] selectionArgs = null;
//		String sortOrder = RaceResults.StartOrder;
				
		Long race_ID = AppSettings.Instance().ReadLongValue(context, AppSettings.AppSetting_RaceID_Name, null);
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = 0;//SeriesRaceTeamResultsView.Instance().ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1; TODO: Fix this!
     	Long startInterval = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	
		return CheckInRacer(0l, startOrder, startInterval, race_ID, 1, 1).toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	