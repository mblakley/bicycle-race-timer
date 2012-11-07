package com.xcracetimer.android.tttimer.Utilities;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xcracetimer.android.tttimer.DataAccess.TTProvider;
import com.xcracetimer.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.xcracetimer.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.xcracetimer.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.xcracetimer.android.tttimer.DataAccess.RaceLapsCP.RaceResultsLapsView;
import com.xcracetimer.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class Calculations {
	public static String LOG_TAG(){
		return "Calculations";
	}
	
	/*
     * Calculate and update the overall placings for this race
     */
    public static void CalculateOverallPlacings(Context context, Long race_ID) {
		try{
			Log.v(LOG_TAG(), "CalculateOverallPlacings");
			
			// Create the list of operations to perform in the batch
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

			Cursor overallResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[] {RaceResults.getTableName() + "." + RaceResults._ID}, 
																	RaceResults.getTableName() + "." + RaceResults.Race_ID + "=? AND " + RaceLaps.LapNumber + "=" + Race.NumSplits, 
																	new String[]{Long.toString(race_ID)}, RaceResults.getTableName() + "." + RaceResults.ElapsedTime);
			// Get the total number of racers in this category
			Integer totalRacers = overallResults.getCount();
			// If there are racers in this category, need to update all racers placings and points
			if(totalRacers > 0)
			{
				overallResults.moveToFirst();
				// Set the initial placing
				Integer overallPlacing = 1;
				do{
					Long raceResults_ID = overallResults.getLong(0);
					
					// Update the placing and points for this raceResult					
					operations.add(ContentProviderOperation.newUpdate(RaceResults.CONTENT_URI)
						    .withValue(RaceResults.OverallPlacing, overallPlacing)
						    .withSelection(RaceResults._ID + "=?", new String[]{Long.toString(raceResults_ID)})
						    .build());

					// Increment the placing
					overallPlacing++;
				} while(overallResults.moveToNext());
			}
			
			overallResults.close();
			overallResults = null;
			
			// Run the batch of updates
			context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
		} catch(Exception ex) {Log.e(LOG_TAG(), "CalculateOverallPlacing failed", ex);}
	}

    /*
     * Calculate and update the category placings and points
     */
	public static void CalculateCategoryPlacings(Context context, Long race_ID, Long currentLapNumber) {
		try{
			Log.v(LOG_TAG(), "CalculateCategoryPlacings");
			// Create the list of operations to perform in the batch
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			// Get the teams that are participating in this race
			Cursor teams = context.getContentResolver().query(TeamInfo.CONTENT_URI, new String[] {TeamInfo.getTableName() + "." + TeamInfo._ID}, null, 
																	null, TeamInfo.getTableName() + "." + TeamInfo._ID);
			if(teams.getCount() > 0)
			{
				teams.moveToFirst();							

				do{
					// Get the category that we'll calculate the results for
					Long otherTeamID = teams.getLong(0);
					// Get the race results for this race and this category
					Cursor teamResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID, RaceResults.getTableName() + "." + RaceResults.ElapsedTime, RaceResults.getTableName() + "." + RaceResults.OverallPlacing}, 
																			 RaceResults.getTableName() + "." + RaceResults.Race_ID + "=? AND (" + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=?)", 
																			 new String[]{Long.toString(race_ID), Long.toString(otherTeamID)}, RaceResults.getTableName() + "." + RaceResults.ElapsedTime);

					if(teamResults != null && teamResults.getCount() > 0){
						long numRacers = 0;
						long numPoints = 0;
						// If there are racers in this category, need to update all racers placings and points
						teamResults.moveToFirst();
						do{
							long overallPlacing = teamResults.getLong(teamResults.getColumnIndex(RaceResults.OverallPlacing));
							if(numRacers < 5){
								numRacers++;
								numPoints += overallPlacing;								
							}						
							if(numRacers > 7){
								break;
							}
						} while(teamResults.moveToNext());
						
						operations.add(ContentProviderOperation.newUpdate(DualMeetResults.CONTENT_URI)
							    .withValue(DualMeetResults.Race_ID, race_ID)
								.withValue(DualMeetResults.Team1_TeamInfo_ID, otherTeamID)
								.withValue(DualMeetResults.Team1_Points, numPoints)
								.withValue(DualMeetResults.Team2_TeamInfo_ID, otherTeamID)
								.withValue(DualMeetResults.Team2_Points, 0)
								.withValue(DualMeetResults.Winning_TeamInfo_ID, otherTeamID)
							    .withSelection(DualMeetResults.Race_ID + "=? AND " + DualMeetResults.Team1_TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(otherTeamID)})
							    .build());
						
						teamResults.close();
						teamResults = null;
					}
				} while(teams.moveToNext());
			}
			teams.close();
			teams = null;			
			
			// Run the batch of updates
			context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
		}catch(Exception ex){Log.e(LOG_TAG(), "CalculateCategoryPlacings failed:", ex);}
	}
}
