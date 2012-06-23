package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerInfoViewCP.RacerInfoView;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Tabs.ResultsTab;
import com.gvccracing.android.tttimer.Utilities.AssignResult;
import com.gvccracing.android.tttimer.Utilities.Calculations;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

public class AssignTimeTask extends AsyncTask<Long, Void, AssignResult> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return AssignTimeTask.class.getSimpleName();
	}
	
	public AssignTimeTask(Context c){
		context = c;
	}
	
	@Override
	protected AssignResult doInBackground(Long... params) {	
		AssignResult result = new AssignResult();
		result.numUnfinishedRacers = 1;
		try{	
			Long unassignedTime_ID = params[0];
			Long raceResult_ID = params[1];

    		Long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
	    	Cursor unassignedTime = context.getContentResolver().query(UnassignedTimes.CONTENT_URI, new String[]{UnassignedTimes.FinishTime}, UnassignedTimes._ID + " = ?", 
				  													new String[]{Long.toString(unassignedTime_ID)}, null);
	    	unassignedTime.moveToFirst();
	    	
	    	int finishTimeCol = unassignedTime.getColumnIndex(UnassignedTimes.FinishTime);	
	    	Long endTime = unassignedTime.getLong(finishTimeCol); // get the endTime from the timer
	    	
	    	unassignedTime.close();
	    	unassignedTime = null;
	    	
	    	// Get the race result record based on the racerInfo_ID and the race_ID
	    	Cursor raceResultToAssignTo = context.getContentResolver().query(RaceResults.CONTENT_URI, new String[]{RaceResults._ID, RaceResults.StartTime, RaceResults.RacerClubInfo_ID}, RaceResults._ID + " = ?", 
	    																  new String[]{raceResult_ID.toString()}, null);
	    	raceResultToAssignTo.moveToFirst();
	    	
	    	// Get the start time for the given raceResult_ID
	    	int startTimeCol = raceResultToAssignTo.getColumnIndex(RaceResults.StartTime);	    	
	    	Long startTime = raceResultToAssignTo.getLong(startTimeCol);
	    	
	    	Long elapsedTime = endTime - startTime;
	    	
	    	ContentValues content = new ContentValues();
			content.put(RaceResults.EndTime, endTime);
			content.put(RaceResults.ElapsedTime, elapsedTime);
	
			// Update the race result
			RaceResults.Update(context, content, RaceResults._ID + "= ?", new String[]{Long.toString(raceResult_ID)});
	    	
	    	// Delete the unassignedTimes row from the database
			context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});
	    	
			Hashtable<String, Object> racerValues = RacerInfoView.getValues(context, raceResultToAssignTo.getLong(raceResultToAssignTo.getColumnIndex(RaceResults.RacerClubInfo_ID)));
			String racerName = racerValues.get(Racer.FirstName).toString() + " " + racerValues.get(Racer.LastName).toString();
			
			result.message = "Assigned time " + TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false) + " to racer " + racerName;

	    	raceResultToAssignTo.close();
	    	raceResultToAssignTo = null;
	    	
			// Figure out if the race has been started yet
			Cursor numStarted = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.StartTime + " IS NOT NULL", new String[]{Long.toString(race_ID)}, null);
			if(numStarted.getCount() > 0){
				// Figure out if he's the last finisher, and if so, stop the timer, hide it, and transition to the results screen
				Cursor numUnfinished = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL", new String[]{Long.toString(race_ID)}, null);
				result.numUnfinishedRacers = numUnfinished.getCount();
				numUnfinished.close();
				numUnfinished = null;
				if(result.numUnfinishedRacers <= 0){
					// Stop and hide the timer
					Intent stopAndHideTimer = new Intent();
					stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
					context.sendBroadcast(stopAndHideTimer);
				}
			}
			
	    	// Calculate Category Placing, Overall Placing, Points
	    	Calculations.CalculateCategoryPlacings(context, race_ID);
	    	Calculations.CalculateOverallPlacings(context, race_ID);  
		}catch(Exception ex){Log.e("AssignTime", "onClick failed:", ex);}
		return result;
	}
	
	@Override
	protected void onPostExecute(AssignResult result) {
		Toast messageToast = Toast.makeText(context, result.message, 2300);
		messageToast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 30);
		messageToast.show();
		if(result.numUnfinishedRacers <= 0){
			// Transition to the results tab
			Intent changeTab = new Intent();
			changeTab.setAction(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
			changeTab.putExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG, ResultsTab.ResultsTabSpecName);
			context.sendBroadcast(changeTab);
		}
	}
}	