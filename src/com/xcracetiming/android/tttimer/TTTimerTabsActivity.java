package com.xcracetiming.android.tttimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.Controls.Timer;
import com.xcracetiming.android.tttimer.Tabs.MainTabsView;

public class TTTimerTabsActivity extends FragmentActivity {
	
	/**
	 * The string used for logging everywhere in this class
	 */
	public static final String LOG_TAG = TTTimerTabsActivity.class.getSimpleName();
    
	/**
	 * The race ID has changed
	 */
	public static final String RACE_ID_CHANGED_ACTION = "com.xcracetiming.android.tttimer.RACE_ID_CHANGED";

	/**
	 * This action is fired when a new racer is on deck
	 */
	public static final String CHANGE_VISIBLE_TAB = "com.xcracetiming.android.tttimer.CHANGE_VISIBLE_TAB";
	
	/**
	 * This is the name of the tag that contains the tab name to make visible
	 */
	public static final String VISIBLE_TAB_TAG = "VisibleTabTag";

	/**
	 * This is the name of the tag that contains the view to change the main frame layout
	 */
	public static final String CHANGE_MAIN_VIEW_ACTION = "com.xcracetiming.android.tttimer.CHANGE_MAIN_VIEW_ACTION";
	
    /**
     * The intent filter is used to listen for events that getActivity() class can handle.  Subscribed events are set up in onCreate.
     */
	public IntentFilter actionFilter = new IntentFilter();

    /**
     * The timer object that will be doing the timing calculations and display the results
     */
    public Timer timer = null;    
	
	/**
	 * Does some basic setup, including setting the layout to use, setting up action filters, creating the tabs, setting some member variables,
	 * and setting up which tabs are visible, and what state they're in.
	 * 
	 * @param savedInstanceState - The saved instance state of this activity that could be used to restore the previous state that we were in before being killed off
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_main);
			
			if (findViewById(R.id.fragment_container) != null) {

		        // However, if we're being restored from a previous state,
		        // then we don't need to do anything and should return or else
		        // we could end up with overlapping fragments.
		        if (savedInstanceState != null) {
		            return;
		        }

		        // Create an instance of MainTabsView
		        MainTabsView firstFragment = new MainTabsView();

		        // In case this activity was started with special instructions from an Intent,
		        // pass the Intent's extras to the fragment as arguments
		        firstFragment.setArguments(getIntent().getExtras());

		        // Add the fragment to the 'fragment_container' FrameLayout
		        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
		    }
		    
 			// Set up the timer view
 			timer = ((Timer)findViewById(R.id.TimerBottom));
		}catch(Exception ex){
    		Log.e(LOG_TAG, "onCreate failed: ", ex);
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
        	// Change the current view in the main view container
        	if(intent.getAction().equals(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION)){
        		// The view to show
        		if(intent.hasExtra("ShowView")){        			
        			// Create the new view
        			String className = intent.getStringExtra("ShowView");
        			
            		try {						
						FragmentManager fragmentManager = getSupportFragmentManager();
			            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			            Fragment mainView = (Fragment)Class.forName(className).newInstance();
			            fragmentTransaction.add(R.id.fragment_container, mainView);
			            fragmentTransaction.addToBackStack(className);
			            fragmentTransaction.commit();
					} catch (ClassNotFoundException e) {
						Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
					} catch (IllegalAccessException e) {
						Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
					} catch (InstantiationException e) {
						Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
					}
        		}
        	}
        }
    };
    
    /**
     * Add an action that the broadcast receiver should be listening for
     * @param action - The action to receive events for
     */
    protected void AddActionFilter(String action){
    	// Add the action to the intent filter
		actionFilter.addAction(action);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		AddActionFilter(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION);
		AddActionFilter(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);

        // Register for broadcasts when a tab is changed
		LocalBroadcastManager.getInstance(this).registerReceiver(mActionReceiver, actionFilter);
		
		// Do all of the necessary stuff to figure out if there's a current race, or if we need to add one
		
		timer.RegisterReceiver();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	
		if(mActionReceiver != null && actionFilter.countActions() > 0){
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mActionReceiver);
    	}
		
		timer.UnregisterReceiver();
	}
}
