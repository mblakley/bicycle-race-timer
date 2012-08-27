package com.gvccracing.android.tttimer.Utilities;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceCategoryCP.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsTeamOrRacerViewCP.RaceResultsTeamOrRacerView;
import com.gvccracing.android.tttimer.DataAccess.TTProvider;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

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

			Cursor overallResults = context.getContentResolver().query(RaceResultsTeamOrRacerView.CONTENT_URI, new String[] {RaceResults.getTableName() + "." + RaceResults._ID}, 
																	RaceResults.Race_ID + "=?" + 
																	" AND " + RaceResults.EndTime + " IS NOT NULL" + 
																	" AND (" + RaceCategory.FullCategoryName + "!=?" +
																	" OR " + TeamInfo.TeamCategory + "!=?)", 
																	new String[]{Long.toString(race_ID), "G", "G"}, RaceResults.ElapsedTime);
			// Get the total number of racers in this category
			Integer totalRacers = overallResults.getCount();
			// If there are racers in this category, need to update all racers placings and points
			if(totalRacers > 0)
			{
				overallResults.moveToFirst();
				// Set the initial placing
				Integer overallPlacing = 1;
				do{
					Long raceResult_ID = overallResults.getLong(0);
					
					// Update the placing and points for this raceResult					
					operations.add(ContentProviderOperation.newUpdate(RaceResults.CONTENT_URI)
						    .withValue(RaceResults.OverallPlacing, overallPlacing)
						    .withSelection(RaceResults._ID + "=?", new String[]{Long.toString(raceResult_ID)})
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
			Cursor categories = context.getContentResolver().query(CheckInViewExclusive.CONTENT_URI, new String[] {RaceCategory.FullCategoryName}, RaceResults.Race_ID + "=?", 
																	new String[]{Long.toString(race_ID)}, RaceCategory.FullCategoryName);
			if(categories.getCount() > 0)
			{
				categories.moveToFirst();

	    		Long raceType_ID = (Long) Race.getValues(context, race_ID).get(Race.RaceType);
				do{
					// Set the initial placing
					Integer categoryPlacing = 1;
					// Get the category that we'll calculate the results for
					String raceCategory = categories.getString(0);
					// Get the race results for this race and this category
					Cursor categoryResults = context.getContentResolver().query(CheckInViewExclusive.CONTENT_URI, new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.ElapsedTime}, 
																		     RaceResults.Race_ID + "=? AND " + RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceCategory.FullCategoryName + "=?", 
																			 new String[]{Long.toString(race_ID), raceCategory}, RaceResults.ElapsedTime);
					// Get the total number of racers in this category
					Integer totalCategoryRacers = categoryResults.getCount();
					// If there are racers in this category, need to update all racers placings and points
					if(totalCategoryRacers > 0)
					{
						categoryResults.moveToFirst();
						do{
							Long raceResult_ID = categoryResults.getLong(0);
							Long elapsedTime = categoryResults.getLong(categoryResults.getColumnIndex(RaceResults.ElapsedTime));
							// Get the points for the current placing
							Integer points = GetPoints(categoryPlacing, totalCategoryRacers, raceType_ID, elapsedTime);							

							// Update the placing and points for this raceResult					
							operations.add(ContentProviderOperation.newUpdate(RaceResults.CONTENT_URI)
								    .withValue(RaceResults.CategoryPlacing, categoryPlacing)
								    .withValue(RaceResults.Points, points)
								    .withSelection(RaceResults._ID + "=?", new String[]{Long.toString(raceResult_ID)})
								    .build());
							
							// Increment the placing
							categoryPlacing++;
						} while(categoryResults.moveToNext());
					}
					categoryResults.close();
					categoryResults = null;
				} while(categories.moveToNext());
			}
			categories.close();
			categories = null;			
			
			// Run the batch of updates
			context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
		}catch(Exception ex){Log.e(LOG_TAG(), "CalculateCategoryPlacings failed:", ex);}
	}

	/*
	 * Get the number of points for the placing, based on how many total riders there were in that category
	 */
	private static Integer GetPoints(Integer categoryPlacing,
			Integer totalCategoryRacers, Long raceType_ID, Long elapsedTime) {
		Log.v(LOG_TAG(), "GetPoints");
		Integer points = totalCategoryRacers;
		try{
			if(raceType_ID == 0){
				if(elapsedTime == Long.MAX_VALUE){
					// Racer DNFed
					return 0;
				}
				// Tiered points for time trials
				if(totalCategoryRacers < 3){
					// 2 or 1 racers
					points = points - categoryPlacing;
					if( points < 0){
						points = 0;
					}
				}else if(totalCategoryRacers < 5){
					// 4 or 3 racers
					// start out with 1 more point than the number of racers
					points++;
					if(categoryPlacing > 1){
						points -= 1;
					}
					points = points - categoryPlacing;
					if( points < 0){
						points = 0;
					}
				}else{
					// 6 or more racers, make sure the points don't blow out of proportion.  Max points for TTs is 7.
					if(totalCategoryRacers > 6){
						points = 6;
					}
					// start out with 2 more points than the number of racers
					points+=2;
					if(categoryPlacing > 1){
						points -= 1;
					}
					if(categoryPlacing > 2){
						points -= 1;
					}
					points = points - categoryPlacing;
					if( points < 0){
						points = 0;
					}
				}
			}else{
				throw new InvalidParameterException("Unable to calculate points for raceTypeID of " + raceType_ID);
			}
		} catch(Exception ex){Log.e(LOG_TAG(), "GetPoints failed", ex);}
		
		return points;
	}
}
