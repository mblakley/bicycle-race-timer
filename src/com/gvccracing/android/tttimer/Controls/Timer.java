package com.gvccracing.android.tttimer.Controls;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.RacerStartedTask;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoResultsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceLapsInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Utilities.Enums.RaceType;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Timer extends LinearLayout implements LoaderManager.LoaderCallbacks<Cursor> {

	/**
	 * The tag to use for logging
	 */
	public static final String LOG_TAG = "Timer";
	/**
     * This action is fired when the timer is started
     */
    public static final String START_TIMER_ACTION = "com.gvccracing.android.tttimer.START_TIMER";
    /**
     * This action is fired when the timer is stopped
     */
	public static final String STOP_TIMER_ACTION = "com.gvccracing.android.tttimer.STOP_TIMER";
	/**
	 * This action is fired when the timer is reset
	 */
	public static final String RESET_TIMER_ACTION = "com.gvccracing.android.tttimer.RESET_TIMER";
	/**
	 * This action is fired when a new racer is on deck
	 */
	public static final String NEW_RACER_ON_DECK_ACTION = "com.gvccracing.android.tttimer.NEW_RACER_ON_DECK";
	/**
	 * This is the key that contains the racerID that's on deck
	 */
	public static final String NEW_RACER_ON_DECK = "Racer_ID_OnDeck";
	/**
	 * This is the key that contains the start offset for the racer on deck
	 */
	public static final String START_TIME_OFFSET_ON_DECK = "StartTimeOffsetOnDeck";
	/**
	 * This is the key that contains the new start time for the timer
	 */
	public static final String START_TIME = "StartTime";	
	/**
	 * This is the key that contains the message to show under the timer
	 */
	public static final String MESSAGE = "Message";
	/**
	 * This is the key that contains the duration to show the message
	 */
	public static final String DURATION = "Duration";
	/**
     * This action is fired when the timer is stopped
     */
	public static final String STOP_AND_HIDE_TIMER_ACTION = "com.gvccracing.android.tttimer.STOP_AND_HIDE_TIMER";
	/**
     * This action is fired when the race is finished
     */
	public static final String RACE_IS_FINISHED_ACTION = "com.gvccracing.android.tttimer.RACE_IS_FINISHED";
	/**
     * This action is fired when a message should be shown
     */
	public static final String SHOW_MESSAGE_ACTION = "com.gvccracing.android.tttimer.SHOW_MESSAGE_ACTION";
	/**
	 * The ID of the on deck loader that we'll use to keep track of who to start next
	 */
	private static final int ON_DECK_LOADER_TIMER = 0x10;

	private static final int UNFINISHED_RACERS_LOADER = 0x122;
	private static final int START_INTERVAL_LOADER = 0x55;
	private static final int RACE_INFO_LOADER_TIMER = 0x143;
	private static final int CURRENT_LAPS_LOADER = 0x144;
	/**
	 * The timer at the bottom of the screen - displayed in the format HH:MM:SS.m
	 */
    private TextView txtTimer = null;
    /**
     * The Handler that deals with running the timer in a separate thread.
     * Call RemoveCallbacks to stop execution
     * Call PostDelayed(__, 0) to run callback again immediately
     * Call PostDelayed(__, REFRESH_RATE) to run callback again after the specified number of milliseconds
     */
    private Handler mTimerHandler = new Handler();
    /**
     * The start time in milliseconds since January 1, 1970
     */
    private long startTime;
    /**
     * The elapsed time in milliseconds.  This will be converted into the HH:MM:SS.m format
     */
    public long elapsedTime;
    /**
     * Refresh rate determines how long to wait between callbacks.  This is set to 100 to make sure that
     * the app has enough time to do all processing necessary to display the new elapsed time
     */
    private final int REFRESH_RATE = 125;
    /**
     * These fields are used to store the current calculated values for seconds, minutes, and hours from the elapsed milliseconds
     */
    private long secs,mins;
    /**
     * Set to true when the timer is "paused".  This will get set by pushing the "stop" button
     */
    public boolean paused = true;
    /**
     * Set to true when the timer has been started, and is false when the timer is reset.
     */
    public boolean started = false;
    
    /**
     * Set to true if a racer has actually started to race.
     */
    public boolean racerStarted = false;
    /**
     * The user friendly representation of the elapsed time that gets displayed on the screen.
     * This is the string that gets assigned into txtTimer.Text
     */
    private String elapsed = "00:00:00.0";

	/**
	 * The text view that contains the actual text that will be displayed in the time toast
	 */
	private TextView toast_text;
	/**
	 * The label that will show the number of laps for lapped races
	 */
	private TextView lblLaps;
	
	/**
	 * The label that will show a message for a given duration
	 */
	private TextView lblMessage;
	
	/**
	 * The linear layout that holds the toast text
	 */
	private LinearLayout llToast;
	/**
	 * The raceResultID that will be the next to start.  Set this to -1 if there are no more racers to start.
	 * Faster to access this from here instead of having to query the database every time.
	 */
	private long raceResult_IDOnDeck = -1;
	/**
	 * The time offset of the next racer to start, or -1 if there is no racer to start.
	 * Faster to access this from here instead of having to query the database every time.
	 */
	private long startTimeOffsetOnDeck = -1;

    /**
     * The intent filter is used to listen for events that this class can handle.  Subscribed events are set up in onCreate.
     */
	private IntentFilter actionFilter = new IntentFilter();
	
	/**
	 * The interval between started racers
	 */
	private long startTimeInterval = 60l;
	/**
	 * The total number of laps in this race
	 */
	private long totalRaceLaps = 1l;
	/**
	 * The race type ID of this race
	 */
	private long raceTypeID = 0l;
	/**
	 * The current number of race laps
	 */
	private long currentLaps = 1l;
	/**
	 * The loader for the current number of laps
	 */
	private Loader<Cursor> currentLapsLoader = null;
	
	private Handler messageTimerHandler = new Handler();
    private Runnable hideMessage = new Runnable() {
	    public void run() {
			lblMessage.setVisibility(View.INVISIBLE);
			lblMessage.setText("");
		}
	};
	
	protected void setNewRacerOnDeck(long raceResult_ID, long startTimeOffset) {
		raceResult_IDOnDeck = raceResult_ID;
		startTimeOffsetOnDeck = startTimeOffset;
	}
	
	/**
     * Create and set up the action receiver for any intents that we're listening for
     */
	private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
		/**
		 * Receives and handles any intents that come in.
		 */
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(LOG_TAG, "onReceive");
        	
        	if(intent.getAction().equals(START_TIMER_ACTION)) {	// If the timer needs to be started
        		if(started && paused){
        			// If we were previously started, and we're just paused, don't set the start time to a new value
        		} else {
	        		// Set the start time to the time included in the bundle, since there might have been a delay before receiving this intent
	        		startTime = intent.getLongExtra(START_TIME, System.currentTimeMillis());
        		}
        		// Show the timer, since we're in a new race
        		if(txtTimer != null){
        			txtTimer.setVisibility(View.VISIBLE);
        		}
        		// Start the timer, so that it continues to run
        		startTimer();
        	} else if(intent.getAction().equals(STOP_TIMER_ACTION)){	// Stop the timer
        		stopTimer();
        	} else if(intent.getAction().equals(RESET_TIMER_ACTION)){	// Reset the timer to 0
        		resetStartTime();
        	} else if(intent.getAction().equals(STOP_AND_HIDE_TIMER_ACTION)){	// Stop the timer and hide it...the race is done
        		stopTimer();
        		// hide the timer
        		Timer.this.setVisibility(View.GONE);
        		lblMessage.setVisibility(View.INVISIBLE);
        		lblLaps.setVisibility(View.INVISIBLE);
        	} else if(intent.getAction().equals(SHOW_MESSAGE_ACTION)) {	// If the timer needs to be started
        		// Set the start time to the time included in the bundle, since there might have been a delay before receiving this intent
        		String message = intent.getStringExtra(MESSAGE);
        		Long duration = intent.getLongExtra(DURATION, 2300l);
        		showMessage(message, duration);
        	} 
        }
    };

	public Timer(Context context, AttributeSet attributes) {
		super(context, attributes);
		View.inflate(context, R.layout.control_timer, this);
		
		// Setup the view that will display the formatted time
		txtTimer = ((TextView)findViewById(R.id.txtTimerBottom));
		
		// Setup the toast that will be used for time notifications
    	toast_text = (TextView) findViewById(R.id.lblTimerToast);	
    	
    	lblLaps = (TextView) findViewById(R.id.lblLaps);
    	lblMessage = (TextView) findViewById(R.id.lblMessage);

		llToast = (LinearLayout) findViewById(R.id.llToast); 
		
		startTimeInterval = Long.parseLong(AppSettings.ReadValue(getContext(), AppSettings.AppSetting_StartInterval_Name, "60"));

        ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(ON_DECK_LOADER_TIMER, null, this);
        ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(UNFINISHED_RACERS_LOADER, null, this);
        ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(START_INTERVAL_LOADER, null, this);
        ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(RACE_INFO_LOADER_TIMER, null, this);
	}	
	
	public void RegisterReceiver() {
		AddActionFilter(START_TIMER_ACTION);
		AddActionFilter(STOP_TIMER_ACTION);
		AddActionFilter(RESET_TIMER_ACTION);
		AddActionFilter(STOP_AND_HIDE_TIMER_ACTION);
		AddActionFilter(SHOW_MESSAGE_ACTION);

        // Register for broadcasts when a tab is changed
		getContext().registerReceiver(mActionReceiver, actionFilter);
	}

	/**
     * Add an action that the broadcast receiver should be listening for
     * @param action
     */
    protected void AddActionFilter(String action){
    	// Add the action to the intent filter
		actionFilter.addAction(action);
    }
    
    public void UnregisterReceiver(){
    	try{
	    	if(mActionReceiver != null && actionFilter.countActions() > 0){
	    		getContext().unregisterReceiver(mActionReceiver);
	    	}
    	}catch(Exception e){
    		Log.e(LOG_TAG, "Error in UnregisterReceiver", e);
    	}
    }
    
	public void startTimer() {		
		mTimerHandler.removeCallbacks(startTimer);
		started = true;
		paused = false;
		mTimerHandler.postDelayed(startTimer, 0);
    }
     
    private void stopTimer() {
    	// stop the timer!
     	mTimerHandler.removeCallbacks(startTimer);
     	paused = true;
    }
    
    public void resetTimer(){
    	racerStarted = false;
     	paused = true;
     	started = false;
     	elapsedTime = 0l;
 		txtTimer.setText("00:00:00.0");
    }
     
    private void resetStartTime (){     	
 		// Reset the start time value in the database
 		ContentValues content = new ContentValues();
 		Long race_ID = Long.parseLong(AppSettings.ReadValue(getContext(), AppSettings.AppSetting_RaceID_Name, "-1"));
		content.put(Race._ID, race_ID);
		content.putNull(Race.RaceStartTime);
		Uri fullUri = Uri.withAppendedPath(Race.CONTENT_URI, Long.toString(race_ID));
		getContext().getContentResolver().update(fullUri, content, null, null);
		
		resetTimer();
    }    
    
    public void showMessage(String message, long duration){
    	// Show the message
    	lblMessage.setText("\u00A0" + message + "\u00A0");
    	lblMessage.setVisibility(View.VISIBLE);

		// Hide the message after a few seconds
		messageTimerHandler.removeCallbacks(hideMessage);
		messageTimerHandler.postDelayed(hideMessage, duration);
    }
     
    private long toastTime = 0;
    private long toastVisibilityTime = 900;
    private String toastText = "";
    private boolean showingToast = false;
    private Long toastStartTime;
    private Long toastEndTime;
    private void updateTimer (Long time, long updatePerformedTime){
 		
		elapsed = TimeFormatter.Format(time, true, true, true, true, true, false, false, true);    	
     	secs = (long)(time/1000);
 		mins = (long)(secs/60);
 		secs = secs % 60;
     	mins = mins % 60;
 		
		txtTimer.setText(elapsed);
		
		if(showingToast && (updatePerformedTime > toastEndTime)){
			llToast.setVisibility(View.INVISIBLE);
			showingToast = false;
		}		
 		
		if(startTimeOffsetOnDeck >= 0 && 
		   ((startTimeInterval == 30 && 
		   ((racerStarted && secs == 0) || secs == 15 || secs == 30 || secs == 45 || (secs >= 25 && secs <= 29) || (secs >= 55 && secs <= 59))) ||
		   (startTimeInterval == 60 && 
		   ((racerStarted && secs == 0) || secs == 30 || secs == 45 || (secs >= 55 && secs <= 59)))) && 
		   toastTime != secs){
			
			toastTime = secs;
			String toastSecs = startTimeInterval > secs ? Long.toString(startTimeInterval - secs) : Long.toString((startTimeInterval * 2) - secs);
			toastText = (secs == 0 || secs == startTimeInterval) ? getContext().getResources().getString(R.string.GO) : toastSecs;
			toastVisibilityTime = (secs == 0 || secs == startTimeInterval) ? 2500 : 900;
					
			if(!showingToast){
				llToast.setVisibility(View.VISIBLE); 
				long remainderMS = time%1000;		
				toastStartTime = updatePerformedTime - remainderMS;
				showingToast = true;
			}
			toastEndTime = toastStartTime + toastVisibilityTime;
	    	toast_text.setText("\u00A0" + toastText + "\u00A0");         
		}
		
 		// Figure out if the racer on deck has started yet, and if so, do some other updates
 		if(startTimeOffsetOnDeck >= 0 && startTime + startTimeOffsetOnDeck <= startTime + time){
			
 			racerStarted = true;
 			
 			// Time has passed the racer's start time, so update them as started, and refill the onDeck racer
 			RacerStartedTask task = new RacerStartedTask(getContext());
 			task.execute(new Long[]{startTime, startTimeOffsetOnDeck, raceResult_IDOnDeck});
 			
 			// Set the start offset for now so that we won't come back into this case until we're ready.
			startTimeOffsetOnDeck = -1; 			
 		}
 	}
     
    private Runnable startTimer = new Runnable() {
	    public void run() {
	    	long currentTime = System.currentTimeMillis();
	    	elapsedTime = currentTime - startTime;
	    	updateTimer(elapsedTime, currentTime);
	    	mTimerHandler.postDelayed(this,REFRESH_RATE);
		}
	};	

	public void CleanUpExtraUnassignedTimes(){
		getContext().getContentResolver().delete(UnassignedTimes.CONTENT_URI, UnassignedTimes.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i("Timer", "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case ON_DECK_LOADER_TIMER:
				projection = new String[]{RaceResults._ID, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NULL";
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getContext(), RaceResults.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case START_INTERVAL_LOADER:
				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				sortOrder = null;
				selectionArgs = new String[]{AppSettings.AppSetting_StartInterval_Name};
				loader = new CursorLoader(getContext(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_TIMER:
				projection = new String[]{Race.RaceType, Race.NumLaps};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getContext(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_LAPS_LOADER:
				projection = new String[]{"MAX(" + RaceLaps.LapNumber + ") as " + RaceLaps.LapNumber};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = null;
				loader = new CursorLoader(getContext(), RaceLapsInfoView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i("Timer", "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ON_DECK_LOADER_TIMER:					
			    	// Reset these variables, so it doesn't affect the race lists (by default)
					long raceResult_ID = -1;
			    	long startTimeOffset = -1;
				    if( cursor != null && cursor.getCount() > 0) {
				    	cursor.moveToFirst();
				    	// We found a race, so get the raceResult_ID and StartTimeOffset
				    	raceResult_ID = cursor.getLong(cursor.getColumnIndex(RaceResults._ID));
				    	startTimeOffset = cursor.getLong(cursor.getColumnIndex(RaceResults.StartTimeOffset));

					    setNewRacerOnDeck(raceResult_ID, startTimeOffset);
				    }
					break;
				case START_INTERVAL_LOADER:
					startTimeInterval = Long.parseLong(AppSettings.ReadValue(getContext(), AppSettings.AppSetting_StartInterval_Name, "60"));
					break;
				case RACE_INFO_LOADER_TIMER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						totalRaceLaps = cursor.getLong(cursor.getColumnIndex(Race.NumLaps));
						
						if(raceTypeID == RaceType.TeamTimeTrial.ID()){
							lblLaps.setVisibility(View.VISIBLE);
							if( currentLapsLoader == null){
								currentLapsLoader = ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(CURRENT_LAPS_LOADER, null, this);
							} else {
								currentLapsLoader = ((FragmentActivity) getContext()).getSupportLoaderManager().restartLoader(CURRENT_LAPS_LOADER, null, this);
							}
						}else{
							lblLaps.setVisibility(View.INVISIBLE);
						}
					}
					break;
				case CURRENT_LAPS_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						currentLaps = cursor.getLong(cursor.getColumnIndex(RaceLaps.LapNumber)) + 1l;
						if(currentLaps > totalRaceLaps){
							currentLaps = totalRaceLaps;
						}
						lblLaps.setText("Lap " + Long.toString(currentLaps) + " of " + Long.toString(totalRaceLaps));
					}
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}
	

	public boolean RaceInProgress(long race_ID) {
		boolean foundRaceInProgress = false;
		
		Cursor currentRace = GetCurrentRace(race_ID);
		
		if(currentRace != null){
			if(currentRace.getCount() > 0){	
			 	foundRaceInProgress = true;
			}
			
			currentRace.close();
			currentRace = null;
		}
		
		return foundRaceInProgress;
	}
	
	public Cursor GetCurrentRace(long race_ID){
		Cursor currentRace = null;
		
		try{
			String[] projection = new String[]{Race.getTableName() + "." + Race._ID, Race.RaceStartTime};
			String selection = Race.RaceStartTime + " > 0 and " + RaceResults.EndTime + " IS NULL AND " + Race.getTableName() + "." + Race._ID + "=?";
			String[] selectionArgs = new String[]{Long.toString(race_ID)}; 
			String sortOrder = Race.getTableName() + "." + Race._ID;
			
			currentRace = getContext().getContentResolver().query(RaceInfoResultsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
     	}catch(Exception ex){Log.e(LOG_TAG, "GetCurrentRace failed:", ex);}
	
		return currentRace;
	}

	public void onLoaderReset(Loader<Cursor> loader) {}

	public long GetStartTime() {
		if(startTime > 0){
			return startTime;
		}else{
			Long race_ID = Long.parseLong(AppSettings.ReadValue(getContext(), AppSettings.AppSetting_RaceID_Name, "-1"));

			Cursor currentRace = GetCurrentRace(race_ID);
			
			if(currentRace.getCount() > 0){	
				currentRace.moveToFirst();
			 	startTime = currentRace.getLong(currentRace.getColumnIndex(Race.RaceStartTime));
			}
			
			currentRace.close();
			currentRace = null;
			return startTime;
		}
	}
}
