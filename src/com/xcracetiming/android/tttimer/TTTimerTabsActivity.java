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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

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
	 * Set the visibility of the timer at the bottom
	 */
	public static final String SET_TIMER_VISIBILITY = "com.xcracetiming.android.tttimer.SET_TIMER_VISIBILITY";
	
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
			            
			            Fragment nextView = (Fragment)Class.forName(className).newInstance();
			            nextView.setArguments(intent.getBundleExtra("args"));
			            fragmentTransaction.add(R.id.fragment_container, nextView);
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
    			
        		showTimer(intent);    			
        	}
        	
        	// Set the visibility of the timer
        	// Used when setting up different race statuses
        	if(intent.getAction().equals(TTTimerTabsActivity.SET_TIMER_VISIBILITY)){
        		showTimer(intent);
        	}
        }
    };

	private void showTimer(Intent intent) {
		boolean showTimer = true;
		if(intent.hasExtra("ShowTimer")){
			showTimer = intent.getBooleanExtra("ShowTimer", true);
		}
		
		timer.setVisibility(showTimer? View.VISIBLE : View.GONE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,0,(float) (showTimer? 0.85:1));
		findViewById(R.id.fragment_container).setLayoutParams(lp);	
	}
    
    /**
     * Add an action that the broadcast receiver should be listening for
     * @param action - The action to receive events for
     */
    protected void AddActionFilter(String action){
    	// Add the action to the intent filter
		actionFilter.addAction(action);
    }
	
	@Override
	protected void onResume() {
		super.onResume();

		// Set up the timer view
		timer = ((Timer)findViewById(R.id.TimerBottom));

		AddActionFilter(TTTimerTabsActivity.SET_TIMER_VISIBILITY);
		AddActionFilter(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);

        // Register for broadcasts when a tab is changed
		LocalBroadcastManager.getInstance(this).registerReceiver(mActionReceiver, actionFilter);		
		
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
