package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;

public class GhostCheckInHandler extends CheckInHandler {
	
	public GhostCheckInHandler(Context c) {
		super(c);
	}

	@Override
	protected String doInBackground(Long... params) {			
		Long racerClubInfo_ID = params[0];
		Long teamInfo_ID = params[1];
		long numSpots = params[2];

     	int nextOrder = GetNextOrder();

     	long race_ID = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
     	long startInterval = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	Uri result = RacerClubInfo.Instance().CONTENT_URI;
     	for(int startOrder = nextOrder; startOrder < nextOrder + numSpots; startOrder++){
     		// Do the check in
     		result = CheckInRacer(racerClubInfo_ID, teamInfo_ID, startOrder, startInterval, race_ID);
     	}
     			
		return result.toString();
	}
	
	public int GetNextOrder(){
		String[] projection = new String[]{"MAX(" + RaceResults.Instance().getTableName() + "." + RaceResults.StartOrder + ") as MaxStartOrder"};
		String selection = RaceResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = null;
		String sortOrder = null;
		
     	// StartOrder (count of current check-ins + 1)
		Cursor maxStartOrder = RaceResults.Instance().Read(context, projection, selection, selectionArgs, sortOrder);
		int nextOrder = -1;
		if(maxStartOrder != null && maxStartOrder.getCount() > 0){
			maxStartOrder.moveToFirst();
			nextOrder = maxStartOrder.getInt(maxStartOrder.getColumnIndex("MaxStartOrder"));
		}
		if(maxStartOrder != null){
			maxStartOrder.close();
			maxStartOrder = null;
		}
		
		return nextOrder + 1;		
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	