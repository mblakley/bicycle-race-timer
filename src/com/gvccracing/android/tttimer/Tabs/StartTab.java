package com.gvccracing.android.tttimer.Tabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.AllRacersStartedTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.StartOrderCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.TeamStartOrderCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class StartTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	public static final String StartTabSpecName =  "StartActivity";

	private static final int TEAM_ON_DECK_LOADER = 0x77;

	private static final int TEAM_START_ORDER_LOADER = 0x78;

	private static final int RACE_INFO_LOADER_START = 0x79;

	private static final int START_ORDER_LOADER_START = 0x99;

	private static final int ON_DECK_LOADER_START = 0x112;
	
	private CursorAdapter startOrderCA;
	private CursorAdapter onDeckCA;
	
	private ListView startOrderList;
	private TextView lblStartPosition;
	private TextView lblName;
	//private ListView onDeckRacer;
	private LinearLayout timerControls;

	private Loader<Cursor> startOrderLoader = null;
	private Loader<Cursor> onDeckLoader = null;
	private Loader<Cursor> teamsStartOrderLoader = null;
	private Loader<Cursor> teamOnDeckLoader = null;
	
	private boolean initialLoad = false;
	
	private Long raceTypeID = 0l;
	
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
		
		lblStartPosition = (TextView) getView().findViewById(R.id.lblStartPosition);
		lblName = (TextView) getView().findViewById(R.id.lblName);
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

        initialLoad = true;
		
		// Initialize the cursor loader for the race info
        getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_START, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(getActivity().getSupportLoaderManager().getLoader(RACE_INFO_LOADER_START) != null){
			getActivity().getSupportLoaderManager().destroyLoader(RACE_INFO_LOADER_START);
		}
		if(getActivity().getSupportLoaderManager().getLoader(ON_DECK_LOADER_START) != null){
			getActivity().getSupportLoaderManager().destroyLoader(ON_DECK_LOADER_START);
		}
		if(getActivity().getSupportLoaderManager().getLoader(START_ORDER_LOADER_START) != null){
			getActivity().getSupportLoaderManager().destroyLoader(START_ORDER_LOADER_START);
		}
		if(getActivity().getSupportLoaderManager().getLoader(TEAM_ON_DECK_LOADER) != null){
			getActivity().getSupportLoaderManager().destroyLoader(TEAM_ON_DECK_LOADER);
		}
		if(getActivity().getSupportLoaderManager().getLoader(TEAM_START_ORDER_LOADER) != null){
			getActivity().getSupportLoaderManager().destroyLoader(TEAM_START_ORDER_LOADER);		
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
			Long raceStartTime = race.getLong(race.getColumnIndex(Race.RaceStartTime));
			if(raceStartTime <= 0){	
				// We're starting the race, so update the raceStartTime
				ContentValues content = new ContentValues();
				content.put(Race._ID, race_ID);
				content.put(Race.RaceStartTime, startTime);
				Uri fullUri = Uri.withAppendedPath(Race.CONTENT_URI, Long.toString(race_ID));
				getActivity().getContentResolver().update(fullUri, content, null, null);
			} 				
		}
		else{
			Toast.makeText(getActivity(), "Unable to find race to start", 3000).show();
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
    	// TODO: Check if a racer already started, and if so, ask if all of the racers start times should be reset
		Intent resetTimer = new Intent();
		resetTimer.setAction(Timer.RESET_TIMER_ACTION);
		getActivity().sendBroadcast(resetTimer);
    }
     
	private void showStopButton(){
		if(getView() != null){
			Button startButton = (Button)getView().findViewById(R.id.btnStartTimer);
			if(startButton != null){
				startButton.setVisibility(View.GONE);
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
		Uri fullUri;
		switch(id){
			case ON_DECK_LOADER_START:
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.StartTime + " IS NULL";
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				fullUri = Uri.withAppendedPath(CheckInViewExclusive.CONTENT_URI, "OnDeck");
				fullUri = Uri.withAppendedPath(fullUri, "1");
				loader = new CursorLoader(getActivity(), fullUri, projection, selection, selectionArgs, sortOrder);
				break;
			case START_ORDER_LOADER_START:
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_ON_DECK_LOADER:
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.StartTime + " IS NULL";
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				fullUri = Uri.withAppendedPath(TeamCheckInViewExclusive.CONTENT_URI, "OnDeck");
				fullUri = Uri.withAppendedPath(fullUri, "1");
				loader = new CursorLoader(getActivity(), fullUri, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_START_ORDER_LOADER:
				projection = new String[]{TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset, "group_concat(" + Racer.FirstName + "||' '||" + Racer.LastName + ", ',\n') as RacerNames"};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), Uri.withAppendedPath(TeamCheckInViewExclusive.CONTENT_URI, "group by " + TeamInfo.getTableName() + "." + TeamInfo._ID + "," + TeamInfo.TeamName + "," + RaceResults.StartOrder + "," + RaceResults.StartTimeOffset), projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_START:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.RaceType, Race.NumLaps};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ON_DECK_LOADER_START:
//					onDeckCA = new OnDeckCursorAdapter(getActivity(), null);
//					
//			        if( onDeckRacer != null){
//			        	onDeckRacer.setAdapter(onDeckCA);
//			        }
//				
//					onDeckCA.swapCursor(cursor);

					if(getView() != null){
						if(cursor != null && cursor.getCount() > 0){
							cursor.moveToFirst();
							lblName.setText(cursor.getString(cursor.getColumnIndex(Racer.FirstName)) + " " + cursor.getString(cursor.getColumnIndex(Racer.LastName)));
							lblStartPosition.setText(Long.toString(cursor.getLong(cursor.getColumnIndex(RaceResults.StartOrder))));
						}
						
				    	LinearLayout llOnDeck = (LinearLayout) getView().findViewById(R.id.llOnDeckRacer);
				    	TextView lblOnDeck = (TextView) getView().findViewById(R.id.lblOnDeck);
					    if( cursor.getCount() > 0) {
					    	cursor.moveToFirst();		    	
					    	
					    	if( llOnDeck != null) {
					    		llOnDeck.setVisibility(View.VISIBLE);
					    	}
					    	if( lblOnDeck != null) {
					    		lblOnDeck.setVisibility(View.VISIBLE);
					    	}
					    	
					    	((Button)getView().findViewById(R.id.btnStartTimer)).setEnabled(true);
						    ((Button)getView().findViewById(R.id.resetButton)).setEnabled(true);
					    }else{					    	
							// Nobody on deck!
				        	if(llOnDeck != null){
				        		llOnDeck.setVisibility(View.GONE);
				        	}
				        	if(lblOnDeck != null){
				        		lblOnDeck.setVisibility(View.GONE);
				        	}
				        	
				        	// Move to the finish tab (if the race is actually running), since there are no more racers to start
				        	if(getParentActivity().timer.racerStarted){
				        		if(!initialLoad){
					        		// request move to finish tab (if possible) using an async task
					        		AllRacersStartedTask task = new AllRacersStartedTask(getActivity());
					        		task.execute();
				        		}
				        	}
				        }
					}
					// Reset initialLoad to false, since we've obviously gone through this case once.  
					// This will prevent the start tab from always trying to switch to the finish tab while the race is running, or after
					initialLoad = false;
					break;
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
				case TEAM_ON_DECK_LOADER:
//					
//					onDeckCA = new TeamOnDeckCursorAdapter(getActivity(), null);
//
//			        if( onDeckRacer != null){
//			        	onDeckRacer.setAdapter(onDeckCA);
//			        }
//			
//					onDeckCA.swapCursor(cursor);

					if(getView() != null){
						if(cursor != null && cursor.getCount() > 0){
							cursor.moveToFirst();
							lblName.setText(cursor.getString(cursor.getColumnIndex(TeamInfo.TeamName)));
							lblStartPosition.setText(Long.toString(cursor.getLong(cursor.getColumnIndex(RaceResults.StartOrder))));
						}
						
				    	LinearLayout llOnDeck = (LinearLayout) getView().findViewById(R.id.llOnDeckRacer);
				    	TextView lblOnDeck = (TextView) getView().findViewById(R.id.lblOnDeck);
					    if( cursor.getCount() > 0) {
					    	cursor.moveToFirst();		    	
					    	
					    	if( llOnDeck != null) {
					    		llOnDeck.setVisibility(View.VISIBLE);
					    	}
					    	if( lblOnDeck != null) {
					    		lblOnDeck.setVisibility(View.VISIBLE);
					    	}
					    	
					    	((Button)getView().findViewById(R.id.btnStartTimer)).setEnabled(true);
						    ((Button)getView().findViewById(R.id.resetButton)).setEnabled(true);
					    }else{					    	
							// Nobody on deck!
				        	if(llOnDeck != null){
				        		llOnDeck.setVisibility(View.GONE);
				        	}
				        	if(lblOnDeck != null){
				        		lblOnDeck.setVisibility(View.GONE);
				        	}
				        	
				        	// Move to the finish tab (if the race is actually running), since there are no more racers to start
				        	if(getParentActivity().timer.racerStarted){
				        		if(!initialLoad){
					        		// request move to finish tab (if possible) using an async task
					        		AllRacersStartedTask task = new AllRacersStartedTask(getActivity());
					        		task.execute();
				        		}
				        	}
				        }
					}
					// Reset initialLoad to false, since we've obviously gone through this case once.  
					// This will prevent the start tab from always trying to switch to the finish tab while the race is running, or after
					initialLoad = false;
					break;
				case TEAM_START_ORDER_LOADER:
					startOrderCA = new TeamStartOrderCursorAdapter(getActivity(), null);
					
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
						raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						if(getView() != null){
							if(raceTypeID == 1){
								if( teamsStartOrderLoader == null){
									teamsStartOrderLoader = getActivity().getSupportLoaderManager().initLoader(TEAM_START_ORDER_LOADER, null, this);
								} else {
									teamsStartOrderLoader = getActivity().getSupportLoaderManager().restartLoader(TEAM_START_ORDER_LOADER, null, this);
								}
								
								if( teamOnDeckLoader == null){
									teamOnDeckLoader = getActivity().getSupportLoaderManager().initLoader(TEAM_ON_DECK_LOADER, null, this);
								} else {
									teamOnDeckLoader = getActivity().getSupportLoaderManager().restartLoader(TEAM_ON_DECK_LOADER, null, this);
								}
							}else {
								if(onDeckLoader == null){
								    // Initialize the cursor loader for the on deck list
									onDeckLoader = getActivity().getSupportLoaderManager().initLoader(ON_DECK_LOADER_START, null, this);
								} else {
									onDeckLoader = getActivity().getSupportLoaderManager().restartLoader(ON_DECK_LOADER_START, null, this);
								}
								
								if(startOrderLoader == null){
									startOrderLoader = getActivity().getSupportLoaderManager().initLoader(START_ORDER_LOADER_START, null, this);
								} else {
									startOrderLoader = getActivity().getSupportLoaderManager().restartLoader(START_ORDER_LOADER_START, null, this);
								}
							}
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
				case ON_DECK_LOADER_START:
					if(onDeckCA != null){
						onDeckCA.swapCursor(null);
					}
					break;
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
}
