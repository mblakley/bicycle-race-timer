package com.xcracetimer.android.tttimer.AsyncTasks;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.xcracetimer.android.tttimer.Controls.Timer;
import com.xcracetimer.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsTeamOrRacerViewCP.RaceResultsTeamOrRacerView;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.xcracetimer.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.xcracetimer.android.tttimer.Utilities.AssignResult;
import com.xcracetimer.android.tttimer.Utilities.Calculations;
import com.xcracetimer.android.tttimer.Utilities.TimeFormatter;

public class AssignLapTimeTask extends AsyncTask<Long, Void, AssignResult> {

	private Context context;
	
	protected String LOG_TAG() {
		return AssignTimeTask.class.getSimpleName();
	}
	
	public AssignLapTimeTask(Context c){
		context = c;
	}
	
	@Override
	protected AssignResult doInBackground(Long... params) {	
		AssignResult result = new AssignResult();
		result.numUnfinishedRacers = 1;
		try{	
			Long unassignedTime_ID = params[0];
			Long raceResult_ID = params[1];
			Long teamInfo_ID = params[2];

    		Long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));	    	
	    	
			// Figure out if the race has been started yet
			Cursor numStarted = RaceResults.Read(context, new String[]{RaceResults._ID, RaceResults.StartTime}, RaceResults.Race_ID + "=? AND " + RaceResults.StartTime + " IS NOT NULL", new String[]{Long.toString(race_ID)}, null);
			if(numStarted.getCount() > 0){
				Cursor unassignedTime = UnassignedTimes.Read(context, new String[]{UnassignedTimes.FinishTime}, UnassignedTimes._ID + " = ?", new String[]{Long.toString(unassignedTime_ID)}, null);
				unassignedTime.moveToFirst();
				
				int finishTimeCol = unassignedTime.getColumnIndex(UnassignedTimes.FinishTime);	
				Long endTime = unassignedTime.getLong(finishTimeCol); // get the endTime from the timer
				
				unassignedTime.close();
				unassignedTime = null;
							
				UnassignedTimes.Update(context, unassignedTime_ID, null, null, raceResult_ID, teamInfo_ID);
				
				// Get the race result record based on the raceResult_ID
				Cursor raceResultToAssignTo = RaceResults.Read(context, new String[]{RaceResults._ID, RaceResults.StartTime, RaceResults.TeamInfo_ID}, RaceResults._ID + " = ?", 
											  new String[]{raceResult_ID.toString()}, null);
				raceResultToAssignTo.moveToFirst();
				
				Hashtable<String, Object> raceValues = Race.getValues(context, race_ID);
				Long totalRaceLaps = (Long) raceValues.get(Race.NumSplits); 
				
				// Find any other laps associated with this raceResultID
				Cursor raceLaps = RaceLaps.Read(context , new String[]{RaceLaps._ID, RaceLaps.RaceResult_ID, RaceLaps.LapNumber, RaceLaps.StartTime, RaceLaps.FinishTime, RaceLaps.ElapsedTime}, RaceLaps.RaceResult_ID + " = ?", new String[]{raceResult_ID.toString()}, RaceLaps.LapNumber + " desc");
				raceLaps.moveToFirst();
				int numRaceLaps = raceLaps.getCount();
				// If the number of laps >= Race.NumLaps, there's a problem because we have assigned too many laps to this team somehow, so don't do anything but return
				if(numRaceLaps >= totalRaceLaps){
					return result;
				} else if(numRaceLaps < totalRaceLaps){
					Long elapsedTime;
					// If the number of laps == 0, the lap start time = the raceResults start time
					if(numRaceLaps == 0){
						// Get the start time for the given raceResult_ID
						int startTimeCol = raceResultToAssignTo.getColumnIndex(RaceResults.StartTime);	    	
						Long startTime = raceResultToAssignTo.getLong(startTimeCol);
						elapsedTime = endTime - startTime;
						RaceLaps.Create(context, raceResult_ID, null, numRaceLaps + 1, startTime, endTime, elapsedTime, race_ID);
						numRaceLaps++;
					} else {
						// There is a previous lap to pull info from
						// If the number of laps < Race.NumLaps, we need to create a lap
						// raceResult_ID, LapNumber, StartTime, FinishTime, ElapsedTime
						Long startTime = raceLaps.getLong(raceLaps.getColumnIndex(RaceLaps.FinishTime));
						elapsedTime = endTime - startTime;
						RaceLaps.Create(context, raceResult_ID, null,  numRaceLaps + 1, startTime, endTime, elapsedTime, race_ID);
						numRaceLaps++;
					}
					
					Hashtable<String, Object> teamValues = TeamInfo.getValues(context, raceResultToAssignTo.getLong(raceResultToAssignTo.getColumnIndex(RaceResults.TeamInfo_ID)));
					String teamName = teamValues.get(TeamInfo.TeamName).toString();
					
					result.message = "Assigned time " + TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false) + " -> " + teamName;

					Intent messageToShow = new Intent();
					messageToShow.setAction(Timer.SHOW_MESSAGE_ACTION);
					messageToShow.putExtra(Timer.MESSAGE, result.message);
					messageToShow.putExtra(Timer.DURATION, 2300l);
					context.sendBroadcast(messageToShow);
					
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
						
						// Calculate Category Placing, Overall Placing, Points
				    	Calculations.CalculateOverallPlacings(context, race_ID); 
				    	Calculations.CalculateCategoryPlacings(context, race_ID, teamInfo_ID);
					}
				}

				raceLaps.close();
				raceLaps = null;

				raceResultToAssignTo.close();
				raceResultToAssignTo = null;
		    	
		    	// Delete the unassignedTimes row from the database
				// context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});
				
				// Figure out if he's the last finisher, and if so, stop the timer, hide it, and transition to the results screen
				Cursor numUnfinished = RaceResultsTeamOrRacerView.Read(context, new String[]{RaceResults.getTableName() + "." + RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL AND (" + RacerClubInfo.Category + "!=?)", new String[]{Long.toString(race_ID), "G", "G"}, null);
				result.numUnfinishedRacers = numUnfinished.getCount();
				numUnfinished.close();
				numUnfinished = null;
				if(result.numUnfinishedRacers <= 0){
					ContentValues endUpdate = new ContentValues();
					endUpdate.put(RaceResults.EndTime, endTime);
					endUpdate.put(RaceResults.ElapsedTime, 0);
					
					RaceResults.Update(context, endUpdate, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL", new String[]{Long.toString(race_ID)});
					
					// Stop and hide the timer
					Intent stopAndHideTimer = new Intent();
					stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
					context.sendBroadcast(stopAndHideTimer);
					
					Intent raceIsFinished = new Intent();
	        		raceIsFinished.setAction(Timer.RACE_IS_FINISHED_ACTION);
	        		context.sendBroadcast(raceIsFinished);
				} 
			} else{
				result.message = "No racers have started yet, so the unassigned time would never be used";

				Intent messageToShow = new Intent();
				messageToShow.setAction(Timer.SHOW_MESSAGE_ACTION);
				messageToShow.putExtra(Timer.MESSAGE, result.message);
				messageToShow.putExtra(Timer.DURATION, 2300l);
				context.sendBroadcast(messageToShow);
			}
			
			numStarted.close();
			numStarted = null;
		}catch(Exception ex){Log.e("AssignTime", "onClick failed:", ex);}
		return result;
	}
}	