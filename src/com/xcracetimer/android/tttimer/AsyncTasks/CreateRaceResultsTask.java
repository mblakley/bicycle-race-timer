package com.xcracetimer.android.tttimer.AsyncTasks;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.xcracetimer.android.tttimer.DataAccess.TTProvider;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.xcracetimer.android.tttimer.DataAccess.RacerInfoViewCP.RacerInfoView;

public class CreateRaceResultsTask  extends AsyncTask<Object, Void, Void> {
	
	final Lock lock = new ReentrantLock();
	
	private Context context;
	
	protected String LOG_TAG() {
		return CreateRaceResultsTask.class.getSimpleName();
	}
	
	public CreateRaceResultsTask(Context c){
		context = c;
	}

	@Override
	protected Void doInBackground(Object... params) {
		long race_ID = (Long)params[0];
		long teamInfo_ID = (Long)params[1];
		String category = (String)params[2];
		String gender = (String)params[3];
		
		try{
			lock.tryLock(500, TimeUnit.MILLISECONDS);
			Cursor raceResults = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.Race_ID + "=? AND " + RaceResults.TeamInfo_ID + "=?", new String[]{Long.toString(race_ID), Long.toString(teamInfo_ID)}, null);
			
			// Create the list of operations to perform in the batch
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			if(raceResults != null && raceResults.getCount() <= 0){
				Cursor racerClubInfo = RacerInfoView.Read(context, new String[]{RacerClubInfo.getTableName() + "." + RacerClubInfo._ID}, RacerClubInfo.TeamInfo_ID + "=? AND " + RacerClubInfo.Category + "=? AND " + Racer.Gender + "=?", new String[]{Long.toString(teamInfo_ID), category, gender}, RacerClubInfo.getTableName() + "." + RacerClubInfo._ID);
				
				racerClubInfo.moveToFirst();
				while(!racerClubInfo.isAfterLast()){			     	
			     	ContentValues content = new ContentValues();
			     	content.putNull(RaceResults.StartTime);
			     	content.putNull(RaceResults.EndTime);
			     	content.putNull(RaceResults.ElapsedTime);
			     	content.putNull(RaceResults.OverallPlacing);
			     	content.put(RaceResults.RacerClubInfo_ID, racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo._ID)));
				    content.put(RaceResults.Race_ID, race_ID);
				    content.put(RaceResults.TeamInfo_ID, teamInfo_ID);
				    content.put(RaceResults.Removed, Boolean.toString(false));
			     	
			        // Create a new raceResult				
					operations.add(ContentProviderOperation.newInsert(RaceResults.CONTENT_URI).withValues(content).build());
			     	
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
			
			raceResults.close();
			raceResults = null;
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