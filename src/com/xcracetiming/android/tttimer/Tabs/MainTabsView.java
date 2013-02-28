package com.xcracetiming.android.tttimer.Tabs;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.Controls.Timer;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;
import com.xcracetiming.android.tttimer.Dialogs.ChooseViewingMode;
import com.xcracetiming.android.tttimer.Dialogs.OtherRaceResults;
import com.xcracetiming.android.tttimer.Wizards.AddRaceWizard;
import com.xcracetiming.android.tttimer.Wizards.AddTeamWizard;

/**
 * @author mab
 *
 */
public class MainTabsView extends Fragment {
	/**
	 * The string used for logging everywhere in this class
	 */
	public static final String LOG_TAG = MainTabsView.class.getSimpleName();
 
    /**
     * The intent filter is used to listen for events that this class can handle.  Subscribed events are set up in onCreate.
     */
	public IntentFilter actionFilter = new IntentFilter();	
	
	/**
	 * This is the current race that this fragment is displaying information for
	 */
	private long currentRaceID = -1l;
    
    /**
     * The tabHost that contains all of the tabs
     */
    private FragmentTabHost tabHost;
    

    /**
     * Represents the current state of the race
     * @author mab
     */
    private enum RaceState{
    	NotAvailable,
    	Available,
    	InProgress,    	
    	Finished
    }
    
	/**
	 * Inflates the view from xml and returns it.  Nothing else!
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Override	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return CreateTabs();    
    }
	
	/**
	 * Set up this view by figuring out the best defaults for right now
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Set up the broadcast receivers
		// TODO: Remove if not needed
		//AddActionFilter(AddRaceView.RACE_ADDED_ACTION);
		//AddActionFilter(Timer.RACE_IS_FINISHED_ACTION);
		AddActionFilter(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
		AddActionFilter(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION);

        // Register for broadcasts when a tab is changed
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mActionReceiver, actionFilter);		
		
//		if(!Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false"))){
//			UpdateRaceState();
//		}else{
//			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
//		}
		
		// Restore preferences
				//SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
				long previousRaceID = Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(-1l)));//settings.getLong(Race._ID, -1);
				
				if(previousRaceID != -1){
					RaceState previousRaceState = getRaceState(previousRaceID);
					switch(previousRaceState){
						case InProgress:
							// If the race is in progress, immediately go back to it.
							SetupRaceInProgress(previousRaceID);
							break;
						default:
							// If the race is not in progress, figure out if there's a better option to switch to.
							UpdateRaceState();
							break;
					}
				}else{
					// TODO: HACK!!! Remove this!
					Intent showAddRaceWizard = new Intent();
					showAddRaceWizard.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAddRaceWizard.putExtra("ShowView", new AddRaceWizard().getClass().getCanonicalName());
					showAddRaceWizard.putExtra("ShowTimer", false);
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddRaceWizard);
				}
	}
	
	/**
	 * This is called when this fragment goes away.  Log the admin user out, since we can't guarantee that we will wake up in the same hands.
	 */
	@Override
	public void onPause() {
		super.onPause();

		// Reset the admin auth, forcing the user to login again if performing admin actions
		// We need an Editor object to make preference changes.
		SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(AppSettings.AppSetting_AdminMode_Name, false);
		
		// Commit the edits!
		editor.commit();		
		
		if(mActionReceiver != null && actionFilter.countActions() > 0){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mActionReceiver);
    	}
	}
	
	/**
     * Create and set up the action receiver for any intents that we're listening for
     */
	private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
		/**
		 * Receives and handles any intents that come in.
		 * @param context - The context that the actions came from
		 * @param intent - The intent that's being received
		 */
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(LOG_TAG, "onReceive");
        	/*if(intent.getAction().equals(AddRaceView.RACE_ADDED_ACTION)){	// Set the raceID to the currently added race
        		Long race_ID = null;
        		if(intent.hasExtra(AppSettings.AppSetting_RaceID_Name)){
        			race_ID = intent.getLongExtra(AppSettings.AppSetting_RaceID_Name, -1l);
        		}
        		AppSettings.Instance().UpdateLong(context, AppSettings.AppSetting_RaceID_Name, race_ID, true);
        		AppSettings.Instance().UpdateLong(context, AppSettings.AppSetting_StartInterval_Name, intent.getLongExtra(AppSettings.AppSetting_StartInterval_Name, 60l), true);
        		SetupAvailableRace();
        	} else*/ if(intent.getAction().equals(TTTimerTabsActivity.CHANGE_VISIBLE_TAB)){
        		String visibleTabTag = intent.getStringExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG);
        		// If requesting the finish tab, but the race was already finished, show the finish tab first
        		// This can happen when unassigning a time from the results list
        		if(visibleTabTag.equals(FinishTab.FinishTabSpecName) && tabHost.getTabWidget().getChildTabViewAt(3).getVisibility() == View.GONE){
        			tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
        		} else if(visibleTabTag.equals(CheckInTab.class.getSimpleName()) && tabHost.getTabWidget().getChildTabViewAt(1).getVisibility() == View.GONE){
        			// If requesting the check in tab, but the race was already finished, show the check in tab first
            		// Not sure when this would happen, but it's here for completeness.
        			tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
        		}
        		tabHost.setCurrentTabByTag(visibleTabTag);
        	} /*else if(intent.getAction().equals(Timer.RACE_IS_FINISHED_ACTION)){
				//timer.CleanUpExtraUnassignedTimes();
				SetupFinishedRace();
        	}*/ else if(intent.getAction().equals(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION)){
        		long raceID = intent.getLongExtra(Race._ID, -1);
        		setCurrentRace(raceID);
        	}
        }
    };

	/**
	 * setCurrentRace() is called when a new race is selected.  It figures out what to display, and then displays it.
	 * @param raceID
	 */
	public void setCurrentRace(long raceID){  
		// TODO: Figure out if there's a race currently going on.  If there is, we need to notify the user, and not make the switch until they confirm that they really want to switch.
		
		// Right now we are assuming that we are good to make the switch.		
		// If we are "switching" to the same raceID as we were already watching, no reason to do anything!
		// TODO: Notify the user if raceID == -1
		if(raceID == currentRaceID || raceID == -1){
			if(raceID < 0){
				Toast.makeText(getActivity(), "Invalid raceID", Toast.LENGTH_SHORT).show();
			}
			return;
		} else{
			// Change the member variable for current raceID
			currentRaceID = raceID;
			
			AppSettings.Instance().UpdateLong(getActivity(), AppSettings.AppSetting_RaceID_Name, raceID, true);
			
//			// We need an Editor object to make preference changes.
//			SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
//			SharedPreferences.Editor editor = settings.edit();
//			editor.putLong(Race._ID, raceID);
//			
//			// Commit the edits!
//			editor.commit();
		}
		
		// Figure out what the state of the given race is.
		switch(getRaceState(raceID)){
			case Available:
				SetupAvailableRace(raceID);
				break;
			case Finished:
				SetupFinishedRace(raceID);
				break;
			case InProgress:
				SetupRaceInProgress(raceID);
				break;
			case NotAvailable:
				// TODO: Notify the user that the race is not available - This is pretty much an error state!
				UpdateRaceState();
				break;
		}
	}
    
    private RaceState getRaceState(long raceID){
    	RaceState currentState = RaceState.NotAvailable;
    	
    	try{
    		// TODO: This projection includes RaceResults.EndTime, which won't be available for races that haven't had a finisher yet.  
    		// That means that we need a left join in the query, or multiple different queries
	    	String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, RaceResults.EndTime, Race.StartInterval};
			String selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
			String[] selectionArgs = new String[]{Long.toString(raceID)}; 
			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
			
			Cursor theRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			if(theRace != null && theRace.getCount() > 0){
				theRace.moveToFirst();
				if(IsRaceInProgress(theRace)){
					currentState = RaceState.InProgress;
				}else if(IsRaceAvailable(theRace)){
					currentState = RaceState.Available;
				} else if(IsRaceFinished(theRace)){
					currentState = RaceState.Finished;
				}else{
					currentState = RaceState.NotAvailable;
				}
			}else{
				currentState = RaceState.NotAvailable;
			}
			
			theRace.close();
			theRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "getRaceState failed", ex);}
    	
    	return currentState;
    }
    

	private boolean IsRaceInProgress(Cursor theRace){
		boolean foundRaceInProgress = false;
		try{			
			if(theRace.getCount() > 0 && theRace.isNull(theRace.getColumnIndex(RaceResults.EndTime)) && 
			   !theRace.isNull(theRace.getColumnIndex(Race.RaceStartTime)) && theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime)) > 0){	
				theRace.moveToFirst();
				Long raceID = theRace.getLong(theRace.getColumnIndex(Race._ID));
				if(System.currentTimeMillis() < theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime)) + 86400000)
				{				
					foundRaceInProgress = true;
				}else {
					// The race was started over 24 hours ago.  This probably isn't correct.  Just stop the race now.
					do{
						ContentValues content = new ContentValues();
						content.put(RaceResults.EndTime, theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime)) + 86400000);
						content.put(RaceResults.ElapsedTime, System.currentTimeMillis() - theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime)));
				
						// Update the race results
						SeriesRaceIndividualResultsView.Instance().Update(getActivity(), content, SeriesRaceIndividualResults.Race_ID + "= ? and " + RaceResults.EndTime + " IS NULL", new String[]{Long.toString(raceID)});
					}while(theRace.moveToNext());
				}
			}
     	}catch(Exception ex){Log.e(LOG_TAG, "RaceIsInProgress failed", ex);}
		
		return foundRaceInProgress;
	}
	
	private boolean IsRaceAvailable(Cursor theRace){
		boolean foundAvailableRace = false;
		try{						
			if(theRace.getCount() > 0 && !theRace.isNull(theRace.getColumnIndex(Race.RaceDate)) && theRace.isNull(theRace.getColumnIndex(Race.RaceStartTime))){	
				foundAvailableRace = true;
			}
			
     	}catch(Exception ex){Log.e(LOG_TAG, "RaceIsAvailable failed", ex);}
		
		return foundAvailableRace;
	}
	
	private boolean IsRaceFinished(Cursor theRace){
		boolean foundFinishedRace = false;
		try{			
			if(theRace.getCount() > 0 && !theRace.isNull(theRace.getColumnIndex(Race.RaceDate)) &&  
			   ((!theRace.isNull(theRace.getColumnIndex(Race.RaceStartTime)) && theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime)) > 0) ||
			   (!theRace.isNull(theRace.getColumnIndex(RaceResults.EndTime)) && theRace.getLong(theRace.getColumnIndex(RaceResults.EndTime)) > 0))){	
				foundFinishedRace = true;
			}
     	}catch(Exception ex){
     		Log.e(LOG_TAG, "RaceIsFinished failed", ex);
 		}
		
		return foundFinishedRace;
	}
	
    /**
     * No raceID was explicitly set, so look for a race that might fit as a default
     */
	public void UpdateRaceState(){
	    // Figure out if a race is currently going on.  
    	// Since races are started and tracked only locally, if a race is still going, it was started on this device
        // If the race already has a raceStartTime, and not all racers are finished, set the startTime variable to raceStartTime and get the timer running
        if(!FindRaceInProgress()) {
        	if (!FindAvailableRace()) {
        		if(!FindFinishedRace()) {        	        	
		        	// If no races are configured for the current date, check if any raceID is set, and display that underneath the upcoming dialog
		        	// Figure out if there's a race_ID in the shared preferences (not -1)
		        	// if so, set up the tabs for that race, since it's being displayed anyway
		        	long race_ID = currentRaceID;
		        	if(race_ID > 0){
		            	String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, RaceResults.EndTime, Race.StartInterval};
		        		String selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
		        		String[] selectionArgs = new String[]{Long.toString(race_ID)}; 
		        		String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
		        		Cursor theRace =  RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
		        		if(theRace != null){
		        			theRace.moveToFirst();
		        		}
		        		if(IsRaceFinished(theRace)){
		        			// Do the same thing as SetupFinishedRace, but don't show the results tab
		        			Intent hideTimer = new Intent();
		        			hideTimer.setAction(TTTimerTabsActivity.SET_TIMER_VISIBILITY);
		    				hideTimer.putExtra("ShowTimer", false);
		    				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(hideTimer);
		        	    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
		        	    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
		        	    	// Remove "finish" tab - There are no unfinished racers
		        	    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);
		        		} else {
		        			SetupAvailableRace(race_ID);
		        		}
		        		if(theRace != null){
		        			theRace.close();
		        			theRace = null;
		        		}
		        	}
		        	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
		        	
		        	// Couldn't find a race in progress, or an unstarted race, or a finished race, so need to add a race, or select a previous one
		        	// Let the user choose whether to add a race or view a previous one	        	
		        	if(FindAnyRaces()){	
		        		ChooseViewingMode cvm = new ChooseViewingMode();
		        		cvm.show(getActivity().getSupportFragmentManager(), ChooseViewingMode.LOG_TAG);

		        		// Show the timer
						Intent showChooseViewingMode = new Intent();
						showChooseViewingMode.setAction(TTTimerTabsActivity.SET_TIMER_VISIBILITY);
						showChooseViewingMode.putExtra("ShowTimer", false);
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showChooseViewingMode);
		        	}else{ 
		        		Intent showAddRaceWizard = new Intent();
						showAddRaceWizard.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
						showAddRaceWizard.putExtra("ShowView", new AddRaceWizard().getClass().getCanonicalName());
						showAddRaceWizard.putExtra("ShowTimer", false);
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddRaceWizard);
		        	}
		        }
        	}
        }
	}
	
	/**
	 * Set up the tabs control display for a finished race
	 */
	private void SetupFinishedRace(long raceID){
    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
    	// Remove "finish" tab - There are no unfinished racers
    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);
    	// Show "results" panel - this is really what people care about when looking at a finished race
    	tabHost.setCurrentTabByTag(ResultsTab.ResultsTabSpecName);
	}
	
	/**
	 * Setup a race that is ready to be started
	 */
	private void SetupAvailableRace(long raceID){
    	// Enable "check in" tab - Allow racers to check in
		tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
    	// Enable "finish" tab - Nothing has even started yet!
    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
    	// Show "race info" panel - Let the user know which race they are looking at
    	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
	}

	/**
	 * Setup a race that is already in progress
	 */
	private void SetupRaceInProgress(long raceID) {
		try{
			String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as " + Race._ID, Race.RaceStartTime};
			String selection = Race._ID + "=?";
			String[] selectionArgs = new String[]{Long.toString(raceID)}; 
			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
			
			Cursor theRace = Race.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			theRace.moveToFirst();
			
			long startTime = theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime));
			
			// Start the timer (using the race's start time)
			Intent startTimer = new Intent();
		 	startTimer.setAction(Timer.START_TIMER_ACTION);
		 	startTimer.putExtra(Timer.START_TIME, startTime);
		 	LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(startTimer);
		 	
		 	// Figure out how many racers are in this race
		 	projection = new String[]{SeriesRaceIndividualResults.RaceResult_ID};
		 	selection = SeriesRaceIndividualResults.Race_ID + " = " + raceID;
			selectionArgs = null; 
			sortOrder = SeriesRaceIndividualResults.RaceResult_ID;
			Cursor totalRacers = SeriesRaceIndividualResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			selection = SeriesRaceIndividualResults.Race_ID + " = " + currentRaceID + " and " + RaceResults.StartTime + " IS NOT NULL";
			Cursor startedRacers = RaceResults.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(startedRacers.getCount() < totalRacers.getCount()) {
				// Navigate to the start tab if not all racers have been started
				ShowTab(StartTab.StartTabSpecName);
			}else{
				// Else, navigate to the finish tab
				ShowTab(FinishTab.FinishTabSpecName);
			}
			
			totalRacers.close();
			totalRacers = null;
			
			startedRacers.close();
			startedRacers = null;
			
		 	theRace.close();
		 	theRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "SetupRaceInProgress failed", ex);}
	}

	/**
     * Add an action that the broadcast receiver should be listening for
     * @param action - The action to receive events for
     */
    protected void AddActionFilter(String action){
    	// Add the action to the intent filter
		actionFilter.addAction(action);
    }
	
	/**
	 * Sends an intent to the tabs to indicate which one needs to be shown
	 * @param tabName - The name of the tab to show
	 */
	public void ShowTab(String tabName) {
    	tabHost.setCurrentTabByTag(tabName);  
	} 
	
	/**
	 * Figure out if there are any previous races in the DB
	 * @return True if there are other races in the DB
	 */
	private boolean FindAnyRaces() {
		boolean foundRace = false;
		try{
			String[] projection = new String[]{Race._ID + " as _id"};
			String selection = null;
			String[] selectionArgs = null; 
			String sortOrder = Race._ID;
			
			Cursor currentRace = Race.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(currentRace.getCount() > 0){	
				foundRace = true;
			}
			
			currentRace.close();
			currentRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindAnyPreviousRaces failed", ex);}
		
		return foundRace;
	}
	
	/**
	 * Figure out if a race is in progress
	 * @return True is a race is currently in progress, and false if no race is in progress
	 */
	private boolean FindRaceInProgress() {
		boolean foundRaceInProgress = false;
		try{
			String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceStartTime, Race.StartInterval};
			String selection = Race.RaceStartTime + " > 0 and " + RaceResults.EndTime + " IS NULL";
			String[] selectionArgs = null; 
			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
			
			Cursor currentRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(currentRace.getCount() > 0){	
				currentRace.moveToFirst();
				int idIndex = currentRace.getColumnIndex(Race._ID);
				Long raceID = currentRace.getLong(idIndex);
				if(System.currentTimeMillis() < currentRace.getLong(currentRace.getColumnIndex(Race.RaceStartTime)) + 86400000)
				{				
					foundRaceInProgress = true;					
					setCurrentRace(raceID);	
				}else {
					// The race was started over 24 hours ago.  Just stop the race now.
					do{
						ContentValues content = new ContentValues();
						content.put(RaceResults.EndTime, currentRace.getLong(currentRace.getColumnIndex(Race.RaceStartTime)) + 86400000);
						content.put(RaceResults.ElapsedTime, System.currentTimeMillis() - currentRace.getLong(currentRace.getColumnIndex(Race.RaceStartTime)));
				
						// Update the race results
						SeriesRaceIndividualResultsView.Instance().Update(getActivity(), content, SeriesRaceIndividualResults.Race_ID + "= ? and " + RaceResults.EndTime + " IS NULL", new String[]{Long.toString(raceID)});
					}while(currentRace.moveToNext());
				}
			}
			
			currentRace.close();
			currentRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindRaceInProgress failed", ex);}
		
		return foundRaceInProgress;
	}
	
	/**
	 * Figure out if a race is available for check-in
	 * @return True if a race is available for check in (but not started), False if no "available" race is found
	 */
	private boolean FindAvailableRace() {
		boolean foundAvailableRace = false;
		try{			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			Long startOfDay = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0).getTime();
			Long endOfDay = startOfDay + 86400000;
			
			String[] projection = new String[]{Race._ID, Race.StartInterval};
			String selection = Race.RaceDate + ">=? AND " + Race.RaceDate + "<? AND " + Race.RaceStartTime + " IS NULL";
			String[] selectionArgs = new String[]{Long.toString(startOfDay), Long.toString(endOfDay)}; 
			String sortOrder = Race._ID + " DESC";
			
			Cursor currentRace = Race.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(currentRace != null && currentRace.getCount() > 0){
				foundAvailableRace = true;
				currentRace.moveToFirst();
				int raceCount = currentRace.getCount();
				if(raceCount == 1){
					int raceIDCol = currentRace.getColumnIndex(Race._ID);
					long raceID = currentRace.getLong(raceIDCol);
					setCurrentRace(raceID);
				}else{
					OtherRaceResults previousRaces = new OtherRaceResults();
					FragmentManager fm = getActivity().getSupportFragmentManager();
					previousRaces.show(fm, OtherRaceResults.LOG_TAG);
				}
			}
			
			currentRace.close();
			currentRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindAvailableRace failed", ex);}
		
		return foundAvailableRace;
	}

	/**
	 * Find a race that's finished on the current date
	 * @return True if a race was finished, False if no finished races were found
	 */
	private boolean FindFinishedRace() {
		boolean foundFinishedRace = false;
		try{			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			Long startOfDay = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0).getTime();
			Long endOfDay = startOfDay + 86400000;
			
			String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID};
			String selection = Race.RaceDate + ">=? AND " + Race.RaceDate + "<? AND " + Race.RaceStartTime + ">0 AND " + RaceResults.EndTime + ">0";
			String[] selectionArgs = new String[]{Long.toString(startOfDay), Long.toString(endOfDay)}; 
			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
			
			Cursor finishedRaceResults = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			if(finishedRaceResults.getCount() > 0){
				finishedRaceResults.moveToFirst();
				int raceIDCol = finishedRaceResults.getColumnIndex(Race._ID);
				long raceID = finishedRaceResults.getLong(raceIDCol);
				setCurrentRace(raceID);
			}
			
			finishedRaceResults.close();
			finishedRaceResults = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindFinishedRace failed", ex);}
		
		return foundFinishedRace;
	}
//	
//	/**
//	 * Set the raceID in AppSettings to the race that's currently in progress
//	 */
//	private void UpdateFromRaceInProgress() {
//		try{
//			String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as " + Race._ID, Race.RaceStartTime};
//			String selection = Race.RaceStartTime + " > 0 and " + RaceResults.EndTime + " IS NULL";
//			String[] selectionArgs = null; 
//			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
//			
//			Cursor currentRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//			
//			currentRace.moveToFirst();
//			
//			UpdateFromRaceInProgress(currentRace);
//			
//		 	currentRace.close();
//		 	currentRace = null;
//     	}catch(Exception ex){Log.e(LOG_TAG, "UpdateFromRaceInProgress failed", ex);}
//	}
	
	
//	private void UpdateFromRaceInProgress(Cursor theRace) {
//		try{
//	    	// Show all tabs		
//	    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
//	    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
//			
//			theRace.moveToFirst();
//			
//			int race_IDCol = theRace.getColumnIndex(Race._ID);
//			long race_ID = theRace.getLong(race_IDCol);
//			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(race_ID), true);
//				
//			long startTime = theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime));
//			
//			// Start the timer (using the race's start time)
//			Intent startTimer = new Intent();
//		 	startTimer.setAction(Timer.START_TIMER_ACTION);
//		 	startTimer.putExtra(Timer.START_TIME, startTime);
//		 	LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(startTimer);
//		 	
//		 	// Figure out how many racers are in this race
//		 	String[] projection = new String[]{SeriesRaceIndividualResults.RaceResult_ID};
//		 	String selection = SeriesRaceIndividualResults.Race_ID + " = " + race_ID;
//			String[] selectionArgs = null; 
//			String sortOrder = SeriesRaceIndividualResults.RaceResult_ID;
//			Cursor totalRacers = SeriesRaceIndividualResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//			
//			selection = SeriesRaceIndividualResults.Race_ID + " = " + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name)+ " and " + RaceResults.StartTime + " IS NOT NULL";
//			Cursor startedRacers = RaceResults.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//			
//			if(startedRacers.getCount() < totalRacers.getCount()) {
//				// Navigate to the start tab if not all racers have been started
//				ShowTab(StartTab.StartTabSpecName);
//			}else{
//				// Else, navigate to the finish tab
//				ShowTab(FinishTab.FinishTabSpecName);
//			}
//			
//			totalRacers.close();
//			totalRacers = null;
//			
//			startedRacers.close();
//			startedRacers = null;
//     	}catch(Exception ex){Log.e(LOG_TAG, "UpdateFromRaceInProgress failed", ex);}
//	}
	
	/**
	 * Create all of the tabs and add them to the tabManager/tabHost
	 */
	private FragmentTabHost CreateTabs() {
		if(tabHost == null){
	        // Set up the tabs
		    tabHost = new FragmentTabHost(getActivity());
		    tabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		    
		    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		    
		    // Race Info Tab
		    tabHost.addTab(tabHost.newTabSpec(RaceInfoTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Race Info")), RaceInfoTab.class, null);
		    // Check In Tab
		    tabHost.addTab(tabHost.newTabSpec(CheckInTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Check In")), CheckInTab.class, null);
		    // Start Tab
		    tabHost.addTab(tabHost.newTabSpec(StartTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Start")), StartTab.class, null);
		    // Finish Tab
		    tabHost.addTab(tabHost.newTabSpec(FinishTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Finish")), FinishTab.class, null);
		    // Results Tab
		    tabHost.addTab(tabHost.newTabSpec(ResultsTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Results")), ResultsTab.class, null);
		    // Other Tab
		    tabHost.addTab(tabHost.newTabSpec(OtherTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Other")), OtherTab.class, null);		    
		}
		
		return tabHost;
	}
	
	private View createTabView(final Context context, final String text) {
	    View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);	
	    TextView tv = (TextView) view.findViewById(R.id.tabsText);	
	    tv.setText(text);	
	    return view;	
	}


//	/**
//	 * Called when a control that is subscribed to this fragment as a click listener is clicked.
//	 * 
//	 * @param v - The view that was clicked
//	 */
//	public void onClick(View v) {
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//		switch (v.getId())
//		{		
//			case R.id.btnAdminMenu:
//				if(AppSettings.Instance().ReadBooleanValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false")){
//					Intent showAdminView = new Intent();
//					showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
//					showAdminView.putExtra("ShowView", new AdminMenuView().getClass().getCanonicalName());
//					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
//				} else {
//					AdminAuthView adminAuthDialog = new AdminAuthView();
//			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
//				}
//				break;
//			case R.id.btnSeriesResults:
//				Intent showSeriesResults = new Intent();
//				showSeriesResults.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
//				showSeriesResults.putExtra("ShowView", new SeriesResultsView().getClass().getCanonicalName());
//				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showSeriesResults);
//				break;
//		}
//	}

	
//	/**
//	 * setupFinishedRace() is called when a finished race is selected.  It still figures out what to display, and then displays it.
//	 * @param raceID
//	 */
//	public void setupFinishedRace(long raceID){  				
//    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
//    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);//.setEnabled(false);
//    	// Remove "finish" tab - There are no unfinished racers
//    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);//.setEnabled(false);
//    	// Show "results" panel - this is really what people care about when looking at a finished race
//    	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
//    	
//    	String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, RaceResults.EndTime, Race.StartInterval};
//		String selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
//		String[] selectionArgs = new String[]{Long.toString(raceID)}; 
//		String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
//		
//		Cursor theRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//		
//		if(theRace != null && theRace.getCount() > 0){
//			theRace.moveToFirst();
//			
//			Long startInterval = theRace.getLong(theRace.getColumnIndex(Race.StartInterval));
//			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
//			
//		    // Figure out if a race is currently going on.  
//	        if(IsRaceInProgress(theRace)) { 
//	        	// Since races are started and tracked only locally, if a race is still going, it was started on this device
//		        // If the race already has a raceStartTime, and not all racers are finished, set the startTime variable to raceStartTime and get the timer running
//	        	UpdateFromRaceInProgress(theRace);
//	        } else if (IsRaceFinished(theRace)) {	
//	        	// Find a finished race on the current date
//	        	SetupFinishedRace();
//	        } 
//		} else {
//			if(theRace != null){
//		        theRace.close();
//		        theRace = null;
//			}
//			projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, Race.StartInterval};
//			theRace = Race.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//
//			if(theRace != null && theRace.getCount() > 0){
//				theRace.moveToFirst();
//
//				Long startInterval = theRace.getLong(theRace.getColumnIndex(Race.StartInterval));
//				AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
//				
//				if (IsRaceAvailable(theRace)){ 
//		        	SetupAvailableRace();
//		        }
//			}
//		}
//		if(theRace != null){
//	        theRace.close();
//	        theRace = null;
//		}
//	}

	
}
