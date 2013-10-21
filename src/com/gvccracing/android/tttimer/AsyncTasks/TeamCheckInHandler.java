package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.Context;

import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.Views.TeamCheckInViewExclusive;

public class TeamCheckInHandler extends CheckInHandler {
	
	public TeamCheckInHandler(Context c){
		super(c);
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long teamInfo_ID = params[0];

		String[] projection = new String[]{RaceResults.Instance().getTableName() + "." + RaceResults._ID, TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = RaceResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = null;
		String sortOrder = RaceResults.StartOrder;
				
		long race_ID = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = TeamCheckInViewExclusive.Instance().ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;
     	Long startInterval = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	
		return CheckInRacer(null, teamInfo_ID, startOrder, startInterval, race_ID).toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	