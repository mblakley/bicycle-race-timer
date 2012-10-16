package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class CheckInHandler extends AsyncTask<Long, Void, String> {
	
	protected Context context;
	
	public CheckInHandler(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Long... params) {			
		long raceResult_ID = params[0];
		
		Integer result = 0;
		Cursor raceResult = RaceResults.Read(context, new String[]{RaceResults.Removed}, RaceResults._ID + "=?", new String[]{Long.toString(raceResult_ID)}, null);
		if(raceResult != null && raceResult.getCount() > 0){
			raceResult.moveToFirst();
			boolean removed = Boolean.parseBoolean(raceResult.getString(0));
			
			ContentValues content = new ContentValues();
			content.put(RaceResults.Removed, Boolean.toString(!removed));
			
			result = RaceResults.Update(context, content, RaceResults._ID + "=?", new String[]{Long.toString(raceResult_ID)});		
		}
		return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	}
}	