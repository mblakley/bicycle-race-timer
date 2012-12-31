package com.xcracetiming.android.tttimer.AsyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;

public class CheckInHandler extends AsyncTask<Long, Void, String> {
	
	protected Context context;
	
	public CheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long racerSeriesInfo_ID = params[0];

		String[] projection = new String[]{SeriesRaceIndividualResults.RaceResult_ID, Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
		String selection = SeriesRaceIndividualResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
		String[] selectionArgs = null;
		String sortOrder = RaceResults.StartOrder;
		
     	// StartOrder (count of current check-ins + 1)
     	int startOrder = SeriesRaceIndividualResultsView.Instance().ReadCount(context, projection, selection, selectionArgs, sortOrder) + 1;

     	long race_ID = AppSettings.Instance().ReadLongValue(context, AppSettings.AppSetting_RaceID_Name, null);
     	long startInterval = Long.parseLong(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_StartInterval_Name, "60"));
     	
     	// Do the check in
     	Uri result = CheckInRacer(racerSeriesInfo_ID, startOrder, startInterval, race_ID, 1, 1); 
     			
		return result.toString();
	}
	
	protected Uri CheckInRacer(Long racerSeriesInfo_ID, int startOrder, long startInterval, long race_ID, long raceCategory_ID, long bibNumber){
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
     	    	
     	Uri resultUri = RaceResults.Instance().Create(context, raceCategory_ID, bibNumber, startOrder, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints);
     	long raceResult_ID = Long.parseLong(resultUri.getLastPathSegment());
     	
     	return SeriesRaceIndividualResults.Instance().Create(context, race_ID, racerSeriesInfo_ID, raceCategory_ID, raceResult_ID);
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	