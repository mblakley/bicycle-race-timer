package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.Context;

import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.SeriesRaceTeamResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.Views.SeriesRaceTeamResultsView;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;

public class TeamCheckInHandler extends CheckInHandler {
	
	public TeamCheckInHandler(Context c){
		super(c);
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long teamInfo_ID = params[0];

		String[] projection = new String[]{SeriesRaceTeamResults.RaceResult_ID, TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = SeriesRaceTeamResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = null;
		String sortOrder = RaceResults.StartOrder;
				
		long race_ID = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = SeriesRaceTeamResultsView.Instance().ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;
     	Long startInterval = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	
		return CheckInRacer(0l, startOrder, startInterval, race_ID, 1, 1).toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	