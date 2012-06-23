/**
 * 
 */
package com.gvccracing.android.tttimer.Tabs;

import com.gvccracing.android.tttimer.TTTimerTabsActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * @author Perry
 *
 */
public abstract class BaseTab extends Fragment {

	private IntentFilter filter = new IntentFilter();
	
    /**
     * 
     */
	private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.v(LOG_TAG(), "onReceive");
        	
        	HandleAction(intent);
        }
    };

    /**
     * 
     */
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	protected TTTimerTabsActivity getParentActivity(){
		return (TTTimerTabsActivity)this.getActivity();
	}

    /**
     * Add an action that the broadcast receiver should be listening for
     * @param action
     */
    protected void AddActionFilter(String action){
    	// First unregister from any broadcasts before changing the filter
    	getActivity().unregisterReceiver(mActionReceiver);
    	// Add the action to the intent filter
		filter.addAction(action);
        // Register for broadcasts when a tab is changed
		getActivity().registerReceiver(mActionReceiver, filter);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.i(LOG_TAG(), "onPause");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.i(LOG_TAG(), "onStop - Unregistering all receivers");
    	if(mActionReceiver != null && filter.countActions() > 0){
    		getActivity().unregisterReceiver(mActionReceiver);
    	}
    }
	
    /**
     * Handle any broadcasted intents that come in.  Since this is the base, handle the load data action
     * @param intent
     */
    protected void HandleAction(Intent intent) {
    	Log.e(LOG_TAG(), "In BaseTab HandleAction.  WE SHOULD NEVER BE IN HERE!");
    }

	/**
     * 
     * @return Logging Method Name
     */
    protected abstract String LOG_TAG();
    /**
     * 
     * @return The name of the tabspec ID
     */
	public abstract String TabSpecName();
}
