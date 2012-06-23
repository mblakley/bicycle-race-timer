package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Tabs.ResultsTab;
import com.gvccracing.android.tttimer.Utilities.Calculations;

public class AssignLapTimeTask extends AsyncTask<Long, Void, Integer> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return AssignTimeTask.class.getSimpleName();
	}
	
	public AssignLapTimeTask(Context c){
		context = c;
	}
	
	@Override
	protected Integer doInBackground(Long... params) {	
		Integer numUnfinishedRacers = 1;
		try{	
			Long unassignedTime_ID = params[0];
			Long raceResult_ID = params[1];

    		Long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));	    	
	    	
			// Figure out if the race has been started yet
			Cursor numStarted = RaceResults.Read(context, new String[]{RaceResults._ID, RaceResults.StartTime}, RaceResults.Race_ID + "=? AND " + RaceResults.StartTime + " IS NOT NULL", new String[]{Long.toString(race_ID)}, null);
			if(numStarted.getCount() > 0){
				Cursor unassignedTime = context.getContentResolver().query(UnassignedTimes.CONTENT_URI, new String[]{UnassignedTimes.FinishTime}, UnassignedTimes._ID + " = ?", 
							new String[]{Long.toString(unassignedTime_ID)}, null);
				unassignedTime.moveToFirst();
				
				int finishTimeCol = unassignedTime.getColumnIndex(UnassignedTimes.FinishTime);	
				Long endTime = unassignedTime.getLong(finishTimeCol); // get the endTime from the timer
				
				unassignedTime.close();
				unassignedTime = null;
				
				// Get the race result record based on the raceResult_ID
				Cursor raceResultToAssignTo = context.getContentResolver().query(RaceResults.CONTENT_URI, new String[]{RaceResults._ID, RaceResults.StartTime}, RaceResults._ID + " = ?", 
											  new String[]{raceResult_ID.toString()}, null);
				raceResultToAssignTo.moveToFirst();
				
				Hashtable<String, Long> raceValues = Race.getValues(context, race_ID);
				Long totalRaceLaps = raceValues.get(Race.NumLaps); 
				
				// Find any other laps associated with this raceResultID
				Cursor raceLaps = context.getContentResolver().query(RaceLaps.CONTENT_URI, new String[]{RaceLaps._ID, RaceLaps.RaceResult_ID, RaceLaps.LapNumber, RaceLaps.StartTime, RaceLaps.FinishTime, RaceLaps.ElapsedTime}, RaceLaps.RaceResult_ID + " = ?", new String[]{raceResult_ID.toString()}, RaceLaps.LapNumber + " desc");
				raceLaps.moveToFirst();
				int numRaceLaps = raceLaps.getCount();
				// If the number of laps >= Race.NumLaps, there's a problem because we have assigned too many laps to this team somehow, so don't do anything but return
				if(numRaceLaps >= totalRaceLaps){
					return Integer.MAX_VALUE;
				} else if(raceLaps.getCount() < totalRaceLaps){
					Long elapsedTime;
					// If the number of laps == 0, the lap start time = the raceResults start time
					if(numRaceLaps == 0){
						// Get the start time for the given raceResult_ID
						int startTimeCol = raceResultToAssignTo.getColumnIndex(RaceResults.StartTime);	    	
						Long startTime = raceResultToAssignTo.getLong(startTimeCol);
						elapsedTime = endTime - startTime;
						RaceLaps.Create(context, raceResult_ID, raceLaps.getCount() + 1, startTime, endTime, elapsedTime);
						numRaceLaps++;
					} else {
						// There is a previous lap to pull info from
						// If the number of laps < Race.NumLaps, we need to create a lap
						// raceResult_ID, LapNumber, StartTime, FinishTime, ElapsedTime
						Long startTime = raceLaps.getLong(raceLaps.getColumnIndex(RaceLaps.FinishTime));
						elapsedTime = endTime - startTime;
						RaceLaps.Create(context, raceResult_ID, raceLaps.getCount() + 1, startTime, endTime, elapsedTime);
						numRaceLaps++;
					}
					
					// We added a race lap, so if the total number of race laps for this raceResult equals the total number of laps in the race...
					if(numRaceLaps == totalRaceLaps){
						int startTimeCol = raceResultToAssignTo.getColumnIndex(RaceResults.StartTime);	    	
						Long startTime = raceResultToAssignTo.getLong(startTimeCol);
						elapsedTime = endTime - startTime;
						
						// If the number of laps == Race.NumLaps, it will be the last lap for this RaceResult, so set the RaceResult.FinishTIme
				    	ContentValues content = new ContentValues();
						content.put(RaceResults.EndTime, endTime);
						content.put(RaceResults.ElapsedTime, elapsedTime);
				
						// Update the race result
						RaceResults.Update(context, content, RaceResults._ID + "= ?", new String[]{Long.toString(raceResult_ID)});
					}
				}

				raceResultToAssignTo.close();
				raceResultToAssignTo = null;
		    	
		    	// Delete the unassignedTimes row from the database
				context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});
				
				// Figure out if he's the last finisher, and if so, stop the timer, hide it, and transition to the results screen
				Cursor numUnfinished = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL", new String[]{Long.toString(race_ID)}, null);
				numUnfinishedRacers = numUnfinished.getCount();
				numUnfinished.close();
				numUnfinished = null;
				if(numUnfinishedRacers <= 0){
					Log.w(LOG_TAG(), "Getting ready to STOP_AND_HIDE_TIMER_ACTION");
					// Stop and hide the timer
					Intent stopAndHideTimer = new Intent();
					stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
					context.sendBroadcast(stopAndHideTimer);
					
					Intent raceIsFinished = new Intent();
	        		raceIsFinished.setAction(Timer.RACE_IS_FINISHED_ACTION);
	        		context.sendBroadcast(raceIsFinished);
				}
				
				// Calculate Category Placing, Overall Placing, Points
		    	Calculations.CalculateCategoryPlacings(context, race_ID);
		    	Calculations.CalculateOverallPlacings(context, race_ID);  
			} else{
				Toast.makeText(context, "No racers have started yet, so the unassigned time would never be used", 3000).show();
			}
		}catch(Exception ex){Log.e("AssignTime", "onClick failed:", ex);}
		return numUnfinishedRacers;
	}
	
	@Override
	protected void onPostExecute(Integer numUnfinishedRacers) {
		if(numUnfinishedRacers <= 0){
			Log.w(LOG_TAG(), "Transition to Results tab");
			// Transition to the results tab
			Intent changeTab = new Intent();
			changeTab.setAction(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
			changeTab.putExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG, ResultsTab.ResultsTabSpecName);
			context.sendBroadcast(changeTab);
		}
	}
}	