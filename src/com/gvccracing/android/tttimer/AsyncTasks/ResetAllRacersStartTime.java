package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.SeriesRaceIndividualResults;
import com.gvccracing.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;

public class ResetAllRacersStartTime  extends AsyncTask<Long, Void, Void> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return ResetAllRacersStartTime.class.getSimpleName();
	}
	
	public ResetAllRacersStartTime(Context c){
		context = c;
	}

	@Override
	protected Void doInBackground(Long... params) {
		long race_ID = params[0];
		
		ContentValues content = new ContentValues();
		content.putNull(RaceResults.StartTime);
		SeriesRaceIndividualResultsView.Instance().Update(context, content, SeriesRaceIndividualResults.Race_ID + "=?", new String[]{Long.toString(race_ID)});		
		
		return null;
	}
}
