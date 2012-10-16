package com.gvccracing.android.tttimer.Utilities;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.UnassignedTimesView;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceResultsLapsView;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsTeamOrRacerViewCP.RaceResultsTeamOrRacerView;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TTProvider;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

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
	public static void CalculateCategoryPlacings(Context context, Long race_ID) {
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

	    		//Long raceType_ID = Race.getValues(context, race_ID).get(Race.RaceType);
				do{
					// Get the category that we'll calculate the results for
					Long raceTeam = teams.getLong(0);
					// Get the race results for this race and this category
					Cursor teamResults = context.getContentResolver().query(RaceResultsLapsView.CONTENT_URI, new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID, RaceResults.getTableName() + "." + RaceResults.ElapsedTime, RaceResults.getTableName() + "." + RaceResults.OverallPlacing}, 
																			 RaceResults.getTableName() + "." + RaceResults.Race_ID + "=? AND " + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=? OR " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=?", 
																			 new String[]{Long.toString(race_ID), Long.toString(raceTeam), Long.toString(myTeamID)}, RaceResults.getTableName() + "." + RaceResults.ElapsedTime);
					// Get the total number of racers in this category
					Integer totalTeam2Racers = teamResults.getCount() - numMyTeamResults;
					long resultPoints = 0;
					long numTeam1Racers = 0;
					long numTeam2Racers = 0;
					long numTeam1Points = 0;
					long numTeam2Points = 0;
					// If there are racers in this category, need to update all racers placings and points
					if(totalTeam2Racers > 0/* && totalTeam2Racers >=5*/)
					{
						teamResults.moveToFirst();
						do{
							long teamID = teamResults.getLong(teamResults.getColumnIndex(RaceResults.TeamInfo_ID));
							if(teamID == raceTeam){
								if(numTeam2Racers <= 7){
									resultPoints++;
									numTeam2Racers++;
									if(numTeam2Racers <= 5){
										numTeam2Points += resultPoints;
									}
								}
							}else{
								if(numTeam1Racers <= 7){
									resultPoints++;
									numTeam1Racers++;
									if(numTeam1Racers <= 5){
										numTeam1Points += resultPoints;
									}
								}
							}	
							if(numTeam1Racers > 7 && numTeam2Racers > 7){
								break;
							}
						} while(teamResults.moveToNext());
					}
					
					ContentValues content = new ContentValues();
					content.put(DualMeetResults.Race_ID, race_ID);
					content.put(DualMeetResults.Team1_TeamInfo_ID, myTeamID);
					content.put(DualMeetResults.Team1_Points, numTeam1Points);
					content.put(DualMeetResults.Team2_TeamInfo_ID, raceTeam);
					content.put(DualMeetResults.Team2_Points, numTeam2Points);
					content.put(DualMeetResults.Winning_TeamInfo_ID, numTeam2Points > numTeam1Points ? myTeamID : raceTeam);
					
					DualMeetResults.Update(context, content, DualMeetResults.Race_ID + "=? AND " + DualMeetResults.Team1_TeamInfo_ID + "=? AND " + DualMeetResults.Team2_TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(myTeamID), Long.toString(raceTeam)}, true);
					
					teamResults.close();
					teamResults = null;
				} while(teams.moveToNext());
			}
			teams.close();
			teams = null;			
			
			// Run the batch of updates
			//context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
		}catch(Exception ex){Log.e(LOG_TAG(), "CalculateCategoryPlacings failed:", ex);}
	}

	/*
	 * Get the number of points for the placing, based on how many total riders there were in that category
	 */
	private static Integer GetPoints(Integer overallPlacing,
			Integer totalCategoryRacers, Long raceType_ID, Long elapsedTime) {
		Log.v(LOG_TAG(), "GetPoints");
		Integer points = totalCategoryRacers;
		try{
			if(raceType_ID == 0){
				return overallPlacing;
//				if(elapsedTime == Long.MAX_VALUE){
//					// Racer DNFed
//					return 0;
//				}
//				// Tiered points for time trials
//				if(totalCategoryRacers < 3){
//					// 2 or 1 racers
//					points = points - overallPlacing;
//					if( points < 0){
//						points = 0;
//					}
//				}else if(totalCategoryRacers < 5){
//					// 4 or 3 racers
//					// start out with 1 more point than the number of racers
//					points++;
//					if(overallPlacing > 1){
//						points -= 1;
//					}
//					points = points - overallPlacing;
//					if( points < 0){
//						points = 0;
//					}
//				}else{
//					// 6 or more racers, make sure the points don't blow out of proportion.  Max points for TTs is 7.
//					if(totalCategoryRacers > 6){
//						points = 6;
//					}
//					// start out with 2 more points than the number of racers
//					points+=2;
//					if(overallPlacing > 1){
//						points -= 1;
//					}
//					if(overallPlacing > 2){
//						points -= 1;
//					}
//					points = points - overallPlacing;
//					if( points < 0){
//						points = 0;
//					}
//				}
			}else{
				throw new InvalidParameterException("Unable to calculate points for raceTypeID of " + raceType_ID);
			}
		} catch(Exception ex){Log.e(LOG_TAG(), "GetPoints failed", ex);}
		
		return points;
	}
}
