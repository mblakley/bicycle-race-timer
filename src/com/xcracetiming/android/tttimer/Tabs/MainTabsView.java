package com.xcracetiming.android.tttimer.Tabs;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.Controls.Timer;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;
import com.xcracetiming.android.tttimer.Dialogs.AdminAuthView;
import com.xcracetiming.android.tttimer.Dialogs.OtherRaceResults;
import com.xcracetiming.android.tttimer.Utilities.TabManagerContainer;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceView;
import com.xcracetiming.android.tttimer.WizardPages.AdminMenuView;
import com.xcracetiming.android.tttimer.WizardPages.SeriesResultsView;

/**
 * @author mab
 *
 */
public class MainTabsView extends Fragment {
	/**
	 * The string used for logging everywhere in getActivity() class
	 */
	public static final String LOG_TAG = MainTabsView.class.getSimpleName();
 
    /**
     * The intent filter is used to listen for events that getActivity() class can handle.  Subscribed events are set up in onCreate.
     */
	public IntentFilter actionFilter = new IntentFilter();
	
    /**
     * The tab manager in charge of creating and showing tabs
     */
    private TabManagerContainer.TabManager tabManager;
    
    /**
     * The tabHost that contains all of the tabs
     */
    private TabHost tabHost;
    
	/**
	 * Inflates the view from xml and returns it.  Nothing else!
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 */
	@Override	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        return inflater.inflate(R.layout.tabs_main, container, false);    
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_AdminMode_Name, Boolean.toString(false), true);
		
		AddActionFilter(AddRaceView.RACE_ADDED_ACTION);
		AddActionFilter(Timer.RACE_IS_FINISHED_ACTION);
		AddActionFilter(TTTimerTabsActivity.CHANGE_VISIBLE_TAB);
		AddActionFilter(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION);

        // Register for broadcasts when a tab is changed
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mActionReceiver, actionFilter);
		
		if(!Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false"))){
			UpdateRaceState();
		}else{
			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
        	if(intent.getAction().equals(AddRaceView.RACE_ADDED_ACTION)){	// Set the raceID to the currently added race
        		Long race_ID = null;
        		if(intent.hasExtra(AppSettings.AppSetting_RaceID_Name)){
        			race_ID = intent.getLongExtra(AppSettings.AppSetting_RaceID_Name, -1l);
        		}
        		AppSettings.Instance().UpdateLong(context, AppSettings.AppSetting_RaceID_Name, race_ID, true);
        		AppSettings.Instance().UpdateLong(context, AppSettings.AppSetting_StartInterval_Name, intent.getLongExtra(AppSettings.AppSetting_StartInterval_Name, 60l), true);
        		SetupAvailableRace();
        	} else if(intent.getAction().equals(TTTimerTabsActivity.CHANGE_VISIBLE_TAB)){
        		String visibleTabTag = intent.getStringExtra(TTTimerTabsActivity.VISIBLE_TAB_TAG);
        		if(visibleTabTag.equals(FinishTab.FinishTabSpecName) && tabHost.getTabWidget().getChildTabViewAt(3).getVisibility() == View.GONE){
        			tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
        		} else if(visibleTabTag.equals(CheckInTab.class.getSimpleName()) && tabHost.getTabWidget().getChildTabViewAt(1).getVisibility() == View.GONE){
        			tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
        		}
        		tabHost.setCurrentTabByTag(visibleTabTag);
        	} else if(intent.getAction().equals(Timer.RACE_IS_FINISHED_ACTION)){
				//timer.CleanUpExtraUnassignedTimes();
				SetupFinishedRace();
        	} else if(intent.getAction().equals(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION)){
        		long raceID = Long.parseLong(intent.getStringExtra(SeriesRaceIndividualResults.Race_ID));
        		UpdatePreviousRaceState(raceID);
        	}
        }
    };

	/**
	 * Called when a control that is subscribed to getActivity() fragment as a click listener is clicked.
	 * 
	 * @param v - The view that was clicked
	 */
	public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		switch (v.getId())
		{		
			case R.id.btnAdminMenu:
				if(AppSettings.Instance().ReadBooleanValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false")){
					Intent showAdminView = new Intent();
					showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
					showAdminView.putExtra("ShowView", new AdminMenuView().getClass().getCanonicalName());
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
				} else {
					AdminAuthView adminAuthDialog = new AdminAuthView();
			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
				}
				break;
			case R.id.btnSeriesResults:
				Intent showSeriesResults = new Intent();
				showSeriesResults.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
				showSeriesResults.putExtra("ShowView", new SeriesResultsView().getClass().getCanonicalName());
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showSeriesResults);
				break;
		}
	}
	
	public void UpdateRaceState(){
//	    // Figure out if a race is currently going on.  
//        if(FindRaceInProgress()) {
//        	// Since races are started and tracked only locally, if a race is still going, it was started on getActivity() device
//	        // If the race already has a raceStartTime, and not all racers are finished, set the startTime variable to raceStartTime and get the timer running
//        	UpdateFromRaceInProgress();
//        } else if (FindAvailableRace()){
//        	// Find a race that's not started yet, but has been set up for getActivity() date
//        	SetupAvailableRace();
//        } else if (FindFinishedRace()) {
//        	// Find a finished race on the current date
//        	SetupFinishedRace();
//        } else {
//        	// If no races are configured for the current date, check if any raceID is in the DB, and display that underneath the upcoming dialog
//        	// Figure out if there's a race_ID in the database (not -1)
//        	// if so, set up the tabs for that race, since it's being displayed anyway
//        	long race_ID = Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(-1l)));
//        	if(race_ID > 0){
//            	String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, RaceResults.EndTime, Race.StartInterval};
//        		String selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
//        		String[] selectionArgs = new String[]{Long.toString(race_ID)}; 
//        		String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
//        		Cursor theRace =  RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
//        		if(theRace != null){
//        			theRace.moveToFirst();
//        		}
//        		if(IsRaceFinished(theRace)){
//        			// Do the same thing as SetupFinishedRace, but don't show the results tab
//        			// Hide the timer
//        	    	timer.setVisibility(View.GONE);     	
//        	    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
//        	    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);//.setEnabled(false);
//        	    	// Remove "finish" tab - There are no unfinished racers
//        	    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);//.setEnabled(false);
//        		} else {
//        			SetupAvailableRace();
//        		}
//        		if(theRace != null){
//        			theRace.close();
//        			theRace = null;
//        		}
//        	}else{
//        		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(-1l), true);
//        	}
//        	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
//        	// Figure out if there's a race series in the DB yet
//        	if(FindAnyRaceSeries()){
//	        	// Couldn't find a race in progress, or an unstarted race, or a finished race, so need to add a race, or select a previous one
//	        	// Let the user choose whether to add a race or view a previous one	        	
//	        	if(FindAnyRaces()){
//					ChooseViewingMode chooseModeDialog = new ChooseViewingMode();
//					FragmentManager fm = getSupportFragmentManager();
//					chooseModeDialog.show(fm, ChooseViewingMode.LOG_TAG);
//	        	}else{ 
//	        		AddRaceView addRaceDialog = new AddRaceView();
//	        		Bundle b = new Bundle();
//		        	b.putLong(Race.RaceSeries_ID, -1);
//		        	addRaceDialog.setArguments(b);
//					FragmentManager fm = getSupportFragmentManager();
//					addRaceDialog.show(fm, AddRaceView.LOG_TAG);
//	        	}
//        	} else{
//        		// No series yet, need to add one
//        		ChooseRaceSeriesType chooseRaceSeriesTypeDialog = new ChooseRaceSeriesType();
//				FragmentManager fm = getSupportFragmentManager();
//				chooseRaceSeriesTypeDialog.show(fm, ChooseRaceSeriesType.LOG_TAG);
//        	}
//        }
	}

	private boolean FindAnyRaceSeries() {
		boolean foundRaceSeries = false;
		try{
			String[] projection = new String[]{RaceSeries._ID + " as _id"};
			String selection = RaceSeries.SeriesName + "!='Individual'";
			String[] selectionArgs = null; 
			String sortOrder = RaceSeries._ID;
			
			Cursor currentRaceSeries = getActivity().getContentResolver().query(RaceSeries.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
			
			if(currentRaceSeries.getCount() > 0){	
				foundRaceSeries = true;
			}
			
			currentRaceSeries.close();
			currentRaceSeries = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindAnyRaceSeries failed", ex);}
		
		return foundRaceSeries;
	}

	/**
	 * getActivity() is called when a previous race was selected from the list.  It still figures out what to display, and then displays it.
	 * @param raceID
	 */
	public void UpdatePreviousRaceState(long raceID){  	
    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);//.setEnabled(false);
    	// Remove "finish" tab - There are no unfinished racers
    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);//.setEnabled(false);
    	// Show "results" panel - getActivity() is really what people care about when looking at a finished race
    	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
    	
    	String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, RaceResults.EndTime, Race.StartInterval};
		String selection = Race.Instance().getTableName() + "." + Race._ID + "=?";
		String[] selectionArgs = new String[]{Long.toString(raceID)}; 
		String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
		
		Cursor theRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
		
		if(theRace != null && theRace.getCount() > 0){
			theRace.moveToFirst();
			
			Long startInterval = theRace.getLong(theRace.getColumnIndex(Race.StartInterval));
			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
			
		    // Figure out if a race is currently going on.  
	        if(IsRaceInProgress(theRace)) { 
	        	// Since races are started and tracked only locally, if a race is still going, it was started on getActivity() device
		        // If the race already has a raceStartTime, and not all racers are finished, set the startTime variable to raceStartTime and get the timer running
	        	UpdateFromRaceInProgress(theRace);
	        } else if (IsRaceFinished(theRace)) {	
	        	// Find a finished race on the current date
	        	SetupFinishedRace();
	        } 
		} else {
			if(theRace != null){
		        theRace.close();
		        theRace = null;
			}
			projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, Race.RaceStartTime, Race.StartInterval};
			theRace = Race.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);

			if(theRace != null && theRace.getCount() > 0){
				theRace.moveToFirst();

				Long startInterval = theRace.getLong(theRace.getColumnIndex(Race.StartInterval));
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
				
				if (IsRaceAvailable(theRace)){ 
		        	SetupAvailableRace();
		        }
			}
		}
		if(theRace != null){
	        theRace.close();
	        theRace = null;
		}
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
					// The race was started over 24 hours ago.  getActivity() probably isn't correct.  Just stop the race now.
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
	 * Create all of the tabs and add them to the tabManager/tabHost
	 */
	private void CreateTabs() {
		if(tabHost == null){
	        // Set up the tabs
		    tabHost = (TabHost)getView().findViewById(android.R.id.tabhost);  // The activity TabHost
		    tabHost.setup();
		    tabManager = new TabManagerContainer.TabManager(getActivity(), tabHost, R.id.realtabcontent);
		    
		    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		    
		    // Race Info Tab
		    tabManager.addTab(tabHost.newTabSpec(RaceInfoTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Race Info")), RaceInfoTab.class, null);
		    // Check In Tab
		    tabManager.addTab(tabHost.newTabSpec(CheckInTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Check In")), CheckInTab.class, null);
		    // Start Tab
		    tabManager.addTab(tabHost.newTabSpec(StartTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Start")), StartTab.class, null);
		    // Finish Tab
		    tabManager.addTab(tabHost.newTabSpec(FinishTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Finish")), FinishTab.class, null);
		    // Results Tab
		    tabManager.addTab(tabHost.newTabSpec(ResultsTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Results")), ResultsTab.class, null);
		    // Other Tab
		    tabManager.addTab(tabHost.newTabSpec(OtherTab.class.getSimpleName()).setIndicator(createTabView(getActivity(),"Other")), OtherTab.class, null);
		}
	}
	
	private View createTabView(final Context context, final String text) {
	    View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);	
	    TextView tv = (TextView) view.findViewById(R.id.tabsText);	
	    tv.setText(text);	
	    return view;	
	}

	/**
	 * Set up the tabs control display for a finished race
	 */
	private void SetupFinishedRace(){
    	// Remove "check in" tab - Don't allow any more racers to check in, the race is done!
    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
    	// Remove "finish" tab - There are no unfinished racers
    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.GONE);
    	// Show "results" panel - getActivity() is really what people care about when looking at a finished race
    	tabHost.setCurrentTabByTag(ResultsTab.ResultsTabSpecName);
	}
	
	/**
	 * Setup a race that could be running
	 */
	private void SetupAvailableRace(){
    	// Enable "check in" tab - Allow racers to check in
		tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
    	// Enable "finish" tab - Nothing has even started yet!
    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);
    	// Show "race info" panel - Let the user know which race they are looking at
    	tabHost.setCurrentTabByTag(RaceInfoTab.class.getSimpleName());
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
	
	@Override
	public void onStart(){
		try{
			super.onStart();
			
			CreateTabs();
    	}catch(Exception ex){
    		Log.e(LOG_TAG, "Unexpected error in onStart", ex);
    	}     
	}
    
    @Override
    public void onStop()
    {
    	try{
	    	super.onStop();
    	}catch(Exception ex){
    		Log.e(LOG_TAG, "Unexpected error in onStop", ex);
    	}        
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
					
					Long startInterval = currentRace.getLong(currentRace.getColumnIndex(Race.StartInterval));
					AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(raceID), true);
					AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
				}else {
					// The race was started over 24 hours ago.  getActivity() probably isn't correct.  Just stop the race now.
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
					AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(raceID), true);

					Long startInterval = currentRace.getLong(currentRace.getColumnIndex(Race.StartInterval));
					AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_StartInterval_Name, Long.toString(startInterval), true);
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
				foundFinishedRace = true;
			}
			
			finishedRaceResults.close();
			finishedRaceResults = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "FindFinishedRace failed", ex);}
		
		return foundFinishedRace;
	}
	
	/**
	 * Set the raceID in AppSettings to the race that's currently in progress
	 */
	private void UpdateFromRaceInProgress() {
		try{
			String[] projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as " + Race._ID, Race.RaceStartTime};
			String selection = Race.RaceStartTime + " > 0 and " + RaceResults.EndTime + " IS NULL";
			String[] selectionArgs = null; 
			String sortOrder = Race.Instance().getTableName() + "." + Race._ID;
			
			Cursor currentRace = RaceInfoResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			currentRace.moveToFirst();
			
			int race_IDCol = currentRace.getColumnIndex(Race._ID);
			long race_ID = currentRace.getLong(race_IDCol);
			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(race_ID), true);
			
			UpdateFromRaceInProgress(currentRace);
			
		 	currentRace.close();
		 	currentRace = null;
     	}catch(Exception ex){Log.e(LOG_TAG, "UpdateFromRaceInProgress failed", ex);}
	}
	
	private void UpdateFromRaceInProgress(Cursor theRace) {
		try{
	    	// Show all tabs		
	    	tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);//.setEnabled(false);
	    	tabHost.getTabWidget().getChildTabViewAt(3).setVisibility(View.VISIBLE);//.setEnabled(false);
			
			theRace.moveToFirst();
			
			int race_IDCol = theRace.getColumnIndex(Race._ID);
			long race_ID = theRace.getLong(race_IDCol);
			AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(race_ID), true);
				
			long startTime = theRace.getLong(theRace.getColumnIndex(Race.RaceStartTime));
			
			// Start the timer (using the race's start time)
			Intent startTimer = new Intent();
		 	startTimer.setAction(Timer.START_TIMER_ACTION);
		 	startTimer.putExtra(Timer.START_TIME, startTime);
		 	LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(startTimer);
		 	
		 	// Figure out how many racers are in getActivity() race
		 	String[] projection = new String[]{SeriesRaceIndividualResults.RaceResult_ID};
		 	String selection = SeriesRaceIndividualResults.Race_ID + " = " + race_ID;
			String[] selectionArgs = null; 
			String sortOrder = SeriesRaceIndividualResults.RaceResult_ID;
			Cursor totalRacers = SeriesRaceIndividualResultsView.Instance().Read(getActivity(), projection, selection, selectionArgs, sortOrder);
			
			selection = SeriesRaceIndividualResults.Race_ID + " = " + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name)+ " and " + RaceResults.StartTime + " IS NOT NULL";
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
     	}catch(Exception ex){Log.e(LOG_TAG, "UpdateFromRaceInProgress failed", ex);}
	}
}
