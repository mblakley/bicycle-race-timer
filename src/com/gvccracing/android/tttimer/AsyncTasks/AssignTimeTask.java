package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerInfoViewCP.RacerInfoView;
import com.gvccracing.android.tttimer.Utilities.AssignResult;
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
			Long endTime = params[0];
			Long raceResult_ID = params[1];
			Long teamInfo_ID = params[2];			
			Long startTime = params[3];
			Long currentRaceLap = params[4];
			Long maxRaceLaps = params[5];
			Integer overallPlacing = Integer.parseInt(Long.toString(params[6]));
			
			Log.i("OverallPlacing", Integer.toString(overallPlacing));
			
	    	Long elapsedTime = endTime - startTime;

			long race_ID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"));
			
			if(raceResult_ID != null && currentRaceLap == maxRaceLaps){
		    	// Get the race result record based on the racerInfo_ID and the race_ID
		    	Cursor raceResultToAssignTo = RaceResults.Read(context, new String[]{RaceResults._ID, RaceResults.StartTime, RaceResults.RacerClubInfo_ID}, RaceResults._ID + " = ?", 
		    																  new String[]{raceResult_ID.toString()}, null);
		    	raceResultToAssignTo.moveToFirst();
		    	
		    	if(teamInfo_ID == null){
			    	Cursor racerClubInfo = RacerClubInfo.Read(context, new String[]{RacerClubInfo.TeamInfo_ID}, RacerClubInfo._ID + " = ?", 
							  new String[]{Long.toString(raceResultToAssignTo.getLong(raceResultToAssignTo.getColumnIndex(RaceResults.RacerClubInfo_ID)))}, null);
			    	racerClubInfo.moveToFirst();
			    	
			    	teamInfo_ID = racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo.TeamInfo_ID));
			    	
			    	racerClubInfo.close();
			    	racerClubInfo = null;
		    	}
		    	
		    	ContentValues content = new ContentValues();
				content.put(RaceResults.EndTime, endTime);
				content.put(RaceResults.ElapsedTime, elapsedTime);
				content.put(RaceResults.OverallPlacing, overallPlacing);
		
				// Update the race result
				RaceResults.Update(context, content, RaceResults._ID + "= ?", new String[]{Long.toString(raceResult_ID)});
		    	
				// Setup notification of assignment
				Hashtable<String, Object> racerValues = RacerInfoView.getValues(context, raceResultToAssignTo.getLong(raceResultToAssignTo.getColumnIndex(RaceResults.RacerClubInfo_ID)));
				String racerName = racerValues.get(Racer.FirstName).toString() + " " + racerValues.get(Racer.LastName).toString();
				
				result.message = "Assigned time " + TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false) + " -> " + racerName;
								
		    	// Delete the unassignedTimes row from the database
				// context.getContentResolver().delete(UnassignedTimes.CONTENT_URI, UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)});	    	
	
		    	raceResultToAssignTo.close();
		    	raceResultToAssignTo = null;
		    	
				// Figure out if the race has been started yet
//				Cursor numStarted = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.StartTime + " IS NOT NULL", new String[]{Long.toString(race_ID)}, null);
//				if(numStarted.getCount() > 0){
//					// Figure out if he's the last finisher, and if so, stop the timer, hide it, and transition to the results screen
//					Cursor numUnfinished = RaceResultsTeamOrRacerView.Read(context, new String[]{RaceResults.getTableName() + "." + RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL AND " + RacerClubInfo.Category + "!=?", new String[]{Long.toString(race_ID), "G"}, null);
//					result.numUnfinishedRacers = numUnfinished.getCount();
//					numUnfinished.close();
//					numUnfinished = null;
//					if(result.numUnfinishedRacers <= 0){
//						ContentValues endUpdate = new ContentValues();
//						endUpdate.put(RaceResults.EndTime, endTime);
//						endUpdate.put(RaceResults.ElapsedTime, 0);
//						
//						RaceResults.Update(context, endUpdate, RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NULL", new String[]{Long.toString(race_ID)});
//						
//						// Stop and hide the timer
//						Intent stopAndHideTimer = new Intent();
//						stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
//						context.sendBroadcast(stopAndHideTimer);
//						
//						Intent raceIsFinished = new Intent();
//		        		raceIsFinished.setAction(Timer.RACE_IS_FINISHED_ACTION);
//		        		context.sendBroadcast(raceIsFinished);
//					}
//				}
//				
//				numStarted.close();
//				numStarted = null;
			} else if(teamInfo_ID != null){
				// If it's the last lap, this is the final time, so it will be used for scoring.
				// Create a RaceResult with the teamInfo_ID
				if(currentRaceLap == maxRaceLaps){
					Uri raceResultsUri = RaceResults.Create(context, null, race_ID, startTime, endTime, elapsedTime, overallPlacing, teamInfo_ID, false);
					raceResult_ID = Long.parseLong(raceResultsUri.getLastPathSegment());
				}
				
				result.message = "Assigned time " + TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false) + " -> " + Long.toString(teamInfo_ID);
			}
			
			Intent messageToShow = new Intent();
			messageToShow.setAction(Timer.SHOW_MESSAGE_ACTION);
			messageToShow.putExtra(Timer.MESSAGE, result.message);
			messageToShow.putExtra(Timer.DURATION, 2300l);
			context.sendBroadcast(messageToShow);
			
			RaceLaps.Create(context, raceResult_ID, teamInfo_ID, currentRaceLap, startTime, endTime, elapsedTime, race_ID);
			
			//UnassignedTimes.Create(context, Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1")), endTime, raceResult_ID, teamInfo_ID, elapsedTime, 0);
			
//	    	// Calculate Category Placing, Overall Placing, Points
//	    	Calculations.CalculateOverallPlacings(context, Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"))); // Do this first, since "category" placings are really team placings based on the sum of the top 5 overall placings
//	    	Calculations.CalculateCategoryPlacings(context, Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_RaceID_Name, "-1"))); 
		}catch(Exception ex){Log.e("AssignTime", "onClick failed:", ex);}
		return result;
	}
}	