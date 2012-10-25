package com.gvccracing.android.tttimer.Utilities;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceResultsLapsView;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TTProvider;

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
			Cursor teams = context.getContentResolver().query(MeetTeamsView.CONTENT_URI, new String[] {RaceMeetTeams.getTableName() + "." + RaceMeetTeams.TeamInfo_ID}, RaceMeetTeams.RaceMeet_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceMeet_ID_Name) + " AND " + RaceMeetTeams.TeamInfo_ID + "!=" + AppSettings.getParameterSql(AppSettings.AppSetting_TeamID_Name), 
																	null, RaceMeetTeams.getTableName() + "." + RaceMeetTeams.TeamInfo_ID);
			if(teams.getCount() > 0)
			{
				teams.moveToFirst();
				Cursor myTeamResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.getTableName() + "." + RaceResults.ElapsedTime, RaceResults.getTableName() + "." + RaceResults.OverallPlacing}, 
						 RaceResults.getTableName() + "." + RaceResults.Race_ID + "=? AND " + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_TeamID_Name), 
						 new String[]{Long.toString(race_ID)}, RaceResults.getTableName() + "." + RaceResults.ElapsedTime);
				
				int numMyTeamResults = myTeamResults.getCount();
				
				myTeamResults.close();
				myTeamResults = null;
				
				long myTeamID = Long.parseLong(AppSettings.ReadValue(context, AppSettings.AppSetting_TeamID_Name, "-1"));

				do{
					// Get the category that we'll calculate the results for
					Long otherTeamID = teams.getLong(0);
					// Get the race results for this race and this category
					Cursor teamResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID, RaceResults.getTableName() + "." + RaceResults.ElapsedTime, RaceResults.getTableName() + "." + RaceResults.OverallPlacing}, 
																			 RaceResults.getTableName() + "." + RaceResults.Race_ID + "=? AND (" + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=? OR " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=?)", 
																			 new String[]{Long.toString(race_ID), Long.toString(otherTeamID), Long.toString(myTeamID)}, RaceResults.getTableName() + "." + RaceResults.ElapsedTime);
					// Get the total number of racers in this category
					Integer totalTeam2Racers = teamResults.getCount() - numMyTeamResults;
					long resultPoints = 0;
					long numTeam1Racers = 0;
					long numTeam2Racers = 0;
					long numTeam1Points = 0;
					long numTeam2Points = 0;
					long lastFinishedPlace = 0;
					// If there are racers in this category, need to update all racers placings and points
					if(totalTeam2Racers > 0)
					{
						teamResults.moveToFirst();
						do{
							long teamID = teamResults.getLong(teamResults.getColumnIndex(RaceResults.TeamInfo_ID));
							if(teamID == otherTeamID){
								if(numTeam2Racers < 7){
									resultPoints++;
									lastFinishedPlace = resultPoints;
									if(numTeam2Racers < 5){
										numTeam2Points += resultPoints;
									}
									numTeam2Racers++;
								}
							}else{
								if(numTeam1Racers < 7){
									resultPoints++;
									lastFinishedPlace = resultPoints;
									if(numTeam1Racers < 5){
										numTeam1Points += resultPoints;
									}
									numTeam1Racers++;
								}
							}	
							if(numTeam1Racers > 7 && numTeam2Racers > 7){
								break;
							}
						} while(teamResults.moveToNext());
						
//						Cursor prevLapResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[]{RaceLaps.getTableName() + "." + RaceLaps._ID + " as _id", RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID, RaceLaps.getTableName() + "." + RaceLaps.ElapsedTime}, 
//								RaceLaps.getTableName() + "." + RaceLaps.Race_ID + "=? AND (" + RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID + "=? OR " + RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID + "=?) AND " + RaceLaps.getTableName() + "." + RaceLaps.LapNumber + "=?" , 
//								 new String[]{Long.toString(race_ID), Long.toString(otherTeamID), Long.toString(myTeamID), Long.toString(currentLapNumber - 1)}, RaceLaps.getTableName() + "." + RaceLaps.ElapsedTime);

//						do{
//							
//						} while(prevLapResults.moveToNext());
						
//						long team1Gap = 5 - (numTeam1Racers < 5 ? numTeam1Racers : 5);
//						long team2Gap = 5 - (numTeam2Racers < 5 ? numTeam2Racers : 5);
//						while(team2Gap > 0 || team1Gap > 0){
//							if(lastFinishedTeam == 1){
//								// Get the [totalTeamRacers + team2Gap] index of the previous lap
//								
//								
//								team1Gap--;
//							} else if(lastFinishedTeam == 2){
//								// Get the [totalTeamRacers + team2Gap] index of the previous lap
//								
//								
//								team2Gap--;
//							}
//						}
					}
					
//					ContentValues content = new ContentValues();
//					content.put(DualMeetResults.Race_ID, race_ID);
//					content.put(DualMeetResults.Team1_TeamInfo_ID, myTeamID);
//					content.put(DualMeetResults.Team1_Points, numTeam1Points);
//					content.put(DualMeetResults.Team2_TeamInfo_ID, raceTeam);
//					content.put(DualMeetResults.Team2_Points, numTeam2Points);
//					content.put(DualMeetResults.Winning_TeamInfo_ID, numTeam2Points > numTeam1Points ? myTeamID : raceTeam);
					
					//DualMeetResults.Update(context, content, DualMeetResults.Race_ID + "=? AND " + DualMeetResults.Team1_TeamInfo_ID + "=? AND " + DualMeetResults.Team2_TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(myTeamID), Long.toString(raceTeam)}, true);
					operations.add(ContentProviderOperation.newUpdate(DualMeetResults.CONTENT_URI)
						    .withValue(DualMeetResults.Race_ID, race_ID)
							.withValue(DualMeetResults.Team1_TeamInfo_ID, myTeamID)
							.withValue(DualMeetResults.Team1_Points, numTeam1Points)
							.withValue(DualMeetResults.Team2_TeamInfo_ID, otherTeamID)
							.withValue(DualMeetResults.Team2_Points, numTeam2Points)
							.withValue(DualMeetResults.Winning_TeamInfo_ID, numTeam2Points > numTeam1Points ? myTeamID : otherTeamID)
						    .withSelection(DualMeetResults.Race_ID + "=? AND " + DualMeetResults.Team1_TeamInfo_ID + "=? AND " + DualMeetResults.Team2_TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(myTeamID), Long.toString(otherTeamID)})
						    .build());
					
					teamResults.close();
					teamResults = null;
				} while(teams.moveToNext());
			}
			teams.close();
			teams = null;			
			
			// Run the batch of updates
			context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
		}catch(Exception ex){Log.e(LOG_TAG(), "CalculateCategoryPlacings failed:", ex);}
	}
}
