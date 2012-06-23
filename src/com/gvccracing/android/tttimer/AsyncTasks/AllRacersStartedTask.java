package com.gvccracing.android.tttimer.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.Tabs.FinishTab;

public class AllRacersStartedTask  extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	
	protected String LOG_TAG() {
		return AllRacersStartedTask.class.getSimpleName();
	}
	
	public AllRacersStartedTask(Context c){
		context = c;
	}
	
	@Override
	protected Void doInBackground(Void... params) {					
		return null;
	}
	
	@Override
	protected void onPostExecute(Void nothing) {
		// Transition to the results tab
		Intent changeTab = new Intent();
		changeTab.setAction(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
		changeTab.putExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG, FinishTab.FinishTabSpecName);
		context.sendBroadcast(changeTab);
	}
}
