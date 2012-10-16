package com.gvccracing.android.tttimer.Tabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.RacerStartedTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.StartOrderCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Dialogs.ResetStartedRacersConfirmation;
import com.gvccracing.android.tttimer.Dialogs.ResetTimerConfirmation;
import com.gvccracing.android.tttimer.Dialogs.StartOrderActions;
import com.gvccracing.android.tttimer.Dialogs.ResetTimerConfirmation.ResetTimerDialogListener;

public class StartTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ResetTimerDialogListener {

	public static final String StartTabSpecName =  "StartActivity";

	private static final int RACE_INFO_LOADER_START = 0x79;

	private static final int START_ORDER_LOADER_START = 0x99;
	
	private CursorAdapter startOrderCA;
	private CursorAdapter onDeckCA;
	
	private ListView startOrderList;
	private LinearLayout timerControls;
	
	private String raceCategory = "Varsity";
	private String raceGender = "Boys";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_start, container, false);
        
        timerControls = (LinearLayout) view.findViewById(R.id.llTimerControls);
        ((Button) view.findViewById(R.id.btnStartTimer)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.stopButton)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.resetButton)).setOnClickListener(this);
        
        view.setKeepScreenOn(true);
        
        return view;
    }
	
	@Override
	public void onResume() { 
		super.onResume(); 
		
        startOrderList = (ListView) getView().findViewById(R.id.svStartOrder);
        
        if(getParentActivity().timer.getVisibility() == View.VISIBLE)
        {
	        if(!getParentActivity().timer.paused){
	        	// The timer is running, so set up the buttons
	        	showStopButton();
	        }else{
	        	// The timer is not running, so show the start button
	        	hideStopButton();
	        }
        }else{
        	// Make the start order list expand to the entire length of the tab
        	ExpandStartOrderList();
        	// The timer isn't visible, so no reason to show the controls
        	timerControls.setVisibility(View.GONE);
        }
		
		// Initialize the cursor loader for the race info
        getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_START, null, this);
        getActivity().getSupportLoaderManager().restartLoader(START_ORDER_LOADER_START, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(getActivity().getSupportLoaderManager().getLoader(RACE_INFO_LOADER_START) != null){
			getActivity().getSupportLoaderManager().destroyLoader(RACE_INFO_LOADER_START);
		}
		if(getActivity().getSupportLoaderManager().getLoader(START_ORDER_LOADER_START) != null){
			getActivity().getSupportLoaderManager().destroyLoader(START_ORDER_LOADER_START);
		}
	}

	private void ExpandStartOrderList() {
		LinearLayout llStartOrderList = (LinearLayout) getView().findViewById(R.id.llStartOrderList);
		LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f);
		llStartOrderList.setLayoutParams(param);
		LinearLayout llBottom = (LinearLayout) getView().findViewById(R.id.llBottom);
		LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f);
		llBottom.setLayoutParams(param2);
	}

	@Override
	public String TabSpecName() {
		return StartTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return StartTabSpecName;
	}
	
	@Override
	protected void HandleAction(Intent intent) {
		super.HandleAction(intent);
		
		if(intent.getAction().equals(Timer.STOP_AND_HIDE_TIMER_ACTION)){
        	// Make the start order list expand to the entire length of the tab
			ExpandStartOrderList();
    		timerControls.setVisibility(View.GONE);
    	}	
	}
	
	public void startTimerClick(View view) {
	 	showStopButton();
	 	
		Long race_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1"));
	 	long startTime = -1l;
			
		// Get the race and figure out if it's already started
		Uri raceUri = Uri.withAppendedPath(Race.CONTENT_URI, Long.toString(race_ID));
		Cursor race = getActivity().getContentResolver().query(raceUri, new String[]{Race.RaceStartTime}, null, null, null);
		if(race.getCount() > 0)
		{
			race.moveToFirst();
			// Update the start time
			if(getParentActivity().timer.paused){
				startTime = System.currentTimeMillis() - getParentActivity().timer.elapsedTime;
			}
			else{
				startTime = System.currentTimeMillis();
			}
			//Long raceStartTime = race.getLong(race.getColumnIndex(Race.RaceStartTime));
			//if(raceStartTime <= 0){	
				// We're starting the race, so update the raceStartTime
				ContentValues content = new ContentValues();
				content.put(Race._ID, race_ID);
				content.put(Race.RaceStartTime, startTime);
				Uri fullUri = Uri.withAppendedPath(Race.CONTENT_URI, Long.toString(race_ID));
				getActivity().getContentResolver().update(fullUri, content, null, null);
				
				// Update all racers start times with the newly repurposed RacerStartedTask
				RacerStartedTask task = new RacerStartedTask(getActivity());
	 			task.execute(new Long[]{startTime, 0l, 1l});
	 			
			//} 				
		}
		else{
			Toast.makeText(getActivity(), "Unable to find race to start", Toast.LENGTH_LONG).show();
		}
		race.close();
		race = null;
	 	
	 	Intent startTimer = new Intent();
	 	startTimer.setAction(Timer.START_TIMER_ACTION);
	 	startTimer.putExtra(Timer.START_TIME, startTime);
	 	getActivity().sendBroadcast(startTimer);
    }
     
    public void stopTimerClick(View view) {
    	hideStopButton();
    	
    	Intent stopTimer = new Intent();
    	stopTimer.setAction(Timer.STOP_TIMER_ACTION);
    	getActivity().sendBroadcast(stopTimer);
    }
     
    public void resetClick (View view){
    	FragmentManager fm = getActivity().getSupportFragmentManager();
    	ResetTimerConfirmation resetTimer = new ResetTimerConfirmation(this);
    	resetTimer.show(fm, ResetStartedRacersConfirmation.LOG_TAG);
    }
     
	private void showStopButton(){
		if(getView() != null){
			Button startButton = (Button)getView().findViewById(R.id.btnStartTimer);
			if(startButton != null){
				startButton.setVisibility(View.GONE);
				// Set the start button to "continue", since that is what it will do
				startButton.setText(R.string.btnContinue);
			}
			Button resetButton = (Button)getView().findViewById(R.id.resetButton);
			if(resetButton != null){
				resetButton.setVisibility(View.GONE);
			}
			Button stopButton = (Button)getView().findViewById(R.id.stopButton);
			if(stopButton != null){
				stopButton.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void hideStopButton(){
		if(getView() != null){
		    ((Button)getView().findViewById(R.id.btnStartTimer)).setVisibility(View.VISIBLE);
		    ((Button)getView().findViewById(R.id.resetButton)).setVisibility(View.VISIBLE);
		    ((Button)getView().findViewById(R.id.stopButton)).setVisibility(View.GONE);
		    
		    if(startOrderCA != null && startOrderCA.getCursor() != null && startOrderCA.getCursor().getCount() > 0){
			    ((Button)getView().findViewById(R.id.btnStartTimer)).setEnabled(true);
			    ((Button)getView().findViewById(R.id.resetButton)).setEnabled(true);
		    } else{
		    	((Button)getView().findViewById(R.id.btnStartTimer)).setEnabled(false);
			    ((Button)getView().findViewById(R.id.resetButton)).setEnabled(false);
		    }
		}
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_INFO_LOADER_START:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.Gender, Race.Category, Race.NumSplits};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case START_ORDER_LOADER_START:
				// Create the cursor adapter for the start order list
				startOrderCA = new StartOrderCursorAdapter(getActivity(), null);
				
				SetupList(startOrderList, startOrderCA, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int pos, long id) {
						StartOrderActions startOrderActionsDialog = new StartOrderActions(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						startOrderActionsDialog.show(fm, StartOrderActions.LOG_TAG);
						return false;
					}
	    		});
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, TeamInfo.TeamName, RaceResults.Removed};
				selection = RaceResults.getTableName() + "." + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_TeamID_Name) + " AND " + Race.getTableName() + "." + Race.Category + "=? AND " + Race.getTableName() + "." + Race.Gender + "=?";
				selectionArgs = new String[]{raceCategory, raceGender};
				sortOrder = TeamInfo.TeamName + "," + Racer.LastName;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}
	
	private void SetupList(ListView list, CursorAdapter ca, OnItemLongClickListener listener) {	
		if(getView() != null){
	        if( list != null){
	        	list.setAdapter(ca);
	        	
	        	list.setFocusable(true);
	        	list.setClickable(true);
	        	list.setItemsCanFocus(true);
				
	        	list.setOnItemLongClickListener( listener );
	        }
		}
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case START_ORDER_LOADER_START:
					startOrderCA = new StartOrderCursorAdapter(getActivity(), null);
					
			        if( startOrderList != null){
			        	startOrderList.setAdapter(startOrderCA);
			        }
					
					startOrderCA.swapCursor(cursor);
					
					if(getParentActivity().timer.getVisibility() == View.VISIBLE)
			        {
				        if(!getParentActivity().timer.paused){
				        	// The timer is running, so set up the buttons
				        	showStopButton();
				        }else{
				        	// The timer is not running, so show the start button
				        	hideStopButton();
				        }
			        }else{
			        	// The timer isn't visible, so no reason to show the controls
			        	timerControls.setVisibility(View.GONE);
			        }
					
					// If there are no racers to start, show the start and reset buttons as disabled
					if(cursor == null || cursor.getCount() <=0){
						hideStopButton();
						((Button)getView().findViewById(R.id.btnStartTimer)).setEnabled(false);
					    ((Button)getView().findViewById(R.id.resetButton)).setEnabled(false);
					}
					break;
				case RACE_INFO_LOADER_START:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						raceCategory = cursor.getString(cursor.getColumnIndex(Race.Category));
						raceGender = cursor.getString(cursor.getColumnIndex(Race.Gender));
						
						if(getView() != null){
							getActivity().getSupportLoaderManager().restartLoader(START_ORDER_LOADER_START, null, this);							
						}
					}
					break;
			}
			Log.i(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case START_ORDER_LOADER_START:
					if(onDeckCA != null){
						startOrderCA.swapCursor(null);
					}
					break;
				case RACE_INFO_LOADER_START:
					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}

	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnStartTimer:
				startTimerClick(v);
				break;
			case R.id.stopButton:
				stopTimerClick(v);
				break;
			case R.id.resetButton:
				resetClick(v);
				break;
		}
	}

	public void onFinishResetTimerDialog(boolean doReset) {
		if(doReset){
			Intent resetTimer = new Intent();
			resetTimer.setAction(Timer.RESET_TIMER_ACTION);
			getActivity().sendBroadcast(resetTimer);
			
			// Set the start button text to "Start"
			Button startButton = (Button)getView().findViewById(R.id.btnStartTimer);
			startButton.setText(R.string.Start);
		}
	}
}
