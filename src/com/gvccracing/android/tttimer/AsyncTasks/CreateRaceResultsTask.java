package com.gvccracing.android.tttimer.AsyncTasks;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.gvccracing.android.tttimer.DataAccess.TTProvider;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class CreateRaceResultsTask  extends AsyncTask<Long, Void, Void> {
	
	final Lock lock = new ReentrantLock();
	
	private Context context;
	
	protected String LOG_TAG() {
		return CreateRaceResultsTask.class.getSimpleName();
	}
	
	public CreateRaceResultsTask(Context c){
		context = c;
	}

	@Override
	protected Void doInBackground(Long... params) {
		long race_ID = params[0];
		long teamInfo_ID = params[1];
		
		try{
			lock.tryLock(500, TimeUnit.MILLISECONDS);
			Cursor raceResults = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(teamInfo_ID)}, null);
			
			// Create the list of operations to perform in the batch
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			if(raceResults != null && raceResults.getCount() <= 0){
				Cursor racerClubInfo = RacerClubInfo.Read(context, new String[]{RacerClubInfo._ID}, RacerClubInfo.TeamInfo_ID + "=?", new String[]{Long.toString(teamInfo_ID)}, RacerClubInfo._ID);
				
				racerClubInfo.moveToFirst();
				while(!racerClubInfo.isAfterLast()){
					// StartTimeOffset (startInterval * (StartOrder - 1)) - Will be adjusted based on initial start time
			     	Long startTimeOffset = 0l;//(startInterval * startOrder) * 1000l;
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
			     	
			        // Create a new raceResult				
					operations.add(ContentProviderOperation.newInsert(RaceResults.CONTENT_URI)
						    .withValue(RaceResults.RacerClubInfo_ID, racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo._ID)))
						    .withValue(RaceResults.Race_ID, race_ID)
						    .withValue(RaceResults.StartOrder, 0)
						    .withValue(RaceResults.StartTimeOffset, startTimeOffset)
						    .withValue(RaceResults.StartTime, startTime)
						    .withValue(RaceResults.EndTime, endTime)
						    .withValue(RaceResults.ElapsedTime, elapsedTime)
						    .withValue(RaceResults.OverallPlacing, overallPlacing)
						    .withValue(RaceResults.CategoryPlacing, categoryPlacing)
						    .withValue(RaceResults.Points, points)
						    .withValue(RaceResults.PrimePoints, primePoints)
						    .withValue(RaceResults.TeamInfo_ID, teamInfo_ID)
						    .build());
			     	
			     	//RaceResults.Create(context, racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo._ID)), race_ID, 0, startTimeOffset, startTime, endTime, elapsedTime, overallPlacing, categoryPlacing, points, primePoints, teamInfo_ID);
	
					racerClubInfo.moveToNext();
				}

				try {
					context.getContentResolver().applyBatch(TTProvider.PROVIDER_NAME.toString(), operations);
				} catch (RemoteException e) {
					Log.e("CreateRaceResultsTask", "doInBackground failed", e);
				} catch (OperationApplicationException e) {
					Log.e("CreateRaceResultsTask", "doInBackground failed", e);
				}
			}			
		} catch (InterruptedException e) {
			Log.e("CreateRaceResultsTask", "Unable to lock", e);
		}
		finally{
			lock.unlock();			
		}
		
		//ContentValues content = new ContentValues();
		//content.putNull(RaceResults.StartTime);
		//context.getContentResolver().update(RaceResults.CONTENT_URI, content, RaceResults.Race_ID + "=?", new String[]{Long.toString(race_ID)});		
		
		return null;
	}
}