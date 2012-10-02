package com.gvccracing.android.tttimer.Utilities;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.UnassignedTimesView;
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

			Cursor overallResults = context.getContentResolver().query(UnassignedTimesView.CONTENT_URI, new String[] {UnassignedTimes.getTableName() + "." + UnassignedTimes._ID}, 
																	UnassignedTimes.getTableName() + "." + UnassignedTimes.Race_ID + "=?", 
																	new String[]{Long.toString(race_ID)}, UnassignedTimes.getTableName() + "." + UnassignedTimes.ElapsedTime);
			// Get the total number of racers in this category
			Integer totalRacers = overallResults.getCount();
			// If there are racers in this category, need to update all racers placings and points
			if(totalRacers > 0)
			{
				overallResults.moveToFirst();
				// Set the initial placing
				Integer overallPlacing = 1;
				do{
					Long unassignedTime_ID = overallResults.getLong(0);
					
					// Update the placing and points for this raceResult					
					operations.add(ContentProviderOperation.newUpdate(UnassignedTimes.CONTENT_URI)
						    .withValue(UnassignedTimes.OverallPlacing, overallPlacing)
						    .withSelection(UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)})
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
			// Get the categories that are checked in for this race
			Cursor teams = context.getContentResolver().query(MeetTeamsView.CONTENT_URI, new String[] {RaceMeetTeams.getTableName() + "." + RaceMeetTeams.TeamInfo_ID}, RaceMeetTeams.RaceMeet_ID + "=1", 
																	null, RaceMeetTeams.getTableName() + "." + RaceMeetTeams.TeamInfo_ID);
			if(teams.getCount() > 0)
			{
				teams.moveToFirst();

	    		//Long raceType_ID = Race.getValues(context, race_ID).get(Race.RaceType);
				do{
					// Get the category that we'll calculate the results for
					Long raceTeam = teams.getLong(0);
					// Get the race results for this race and this category
					Cursor categoryResults = context.getContentResolver().query(UnassignedTimesView.CONTENT_URI, new String[]{UnassignedTimes.getTableName() + "." + UnassignedTimes._ID + " as _id", UnassignedTimes.getTableName() + "." + UnassignedTimes.ElapsedTime, UnassignedTimes.getTableName() + "." + UnassignedTimes.OverallPlacing}, 
																			 UnassignedTimes.getTableName() + "." + UnassignedTimes.Race_ID + "=? AND " + UnassignedTimes.getTableName() + "." + UnassignedTimes.ElapsedTime + " IS NOT NULL AND " + UnassignedTimes.getTableName() + "." + UnassignedTimes.TeamInfo_ID + "=?", 
																			 new String[]{Long.toString(race_ID), Long.toString(raceTeam)}, UnassignedTimes.getTableName() + "." + UnassignedTimes.ElapsedTime);
					// Get the total number of racers in this category
					Integer totalCategoryRacers = categoryResults.getCount();
					long teamRacers = 0;
					// If there are racers in this category, need to update all racers placings and points
					if(totalCategoryRacers > 0 && totalCategoryRacers >=5)
					{
						categoryResults.moveToFirst();
						do{
							teamRacers++;
							if(teamRacers > 5){
								break;
							}else{
								Long unassignedTime_ID = categoryResults.getLong(0);
								Long points = categoryResults.getLong(categoryResults.getColumnIndex(UnassignedTimes.OverallPlacing));
								//Long elapsedTime = categoryResults.getLong(categoryResults.getColumnIndex(RaceResults.ElapsedTime));
								// Get the points for the current placing
								//Integer points = GetPoints(categoryPlacing, totalCategoryRacers, 0l, elapsedTime);							
	
								// Update the placing and points for this raceResult					
								operations.add(ContentProviderOperation.newUpdate(UnassignedTimes.CONTENT_URI)
									    .withValue(UnassignedTimes.Points, points)
									    .withSelection(UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTime_ID)})
									    .build());
								
								// Increment the placing
								//categoryPlacing++;
							}
						} while(categoryResults.moveToNext());
					}
					categoryResults.close();
					categoryResults = null;
				} while(teams.moveToNext());
			}
			teams.close();
			teams = null;			
			
			// Run the batch of updates
			context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
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
