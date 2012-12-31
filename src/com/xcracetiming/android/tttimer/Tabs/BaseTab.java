/**
 * 
 */
package com.xcracetiming.android.tttimer.Tabs;

import java.util.Hashtable;

import com.xcracetiming.android.tttimer.TTTimerTabsActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Perry
 *
 */
public abstract class BaseTab extends Fragment {

	private IntentFilter filter = new IntentFilter();
	
	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();
	
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
	
	@Override
	public void onStart() {
		super.onStart();
		addClickListeners();
	}
	
	public TextView getTextView(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (TextView)viewList.get(id);
	}
	
	public EditText getEditText(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (EditText) getView().findViewById(id));
		}
		return (EditText)viewList.get(id);
	}
	
	public LinearLayout getLinearLayout(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (LinearLayout)viewList.get(id);
	}
	
	public Button getButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (Button) getView().findViewById(id));
		}
		return (Button)viewList.get(id);
	}
	
	public ImageButton getImageButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (ImageButton) getView().findViewById(id));
		}
		return (ImageButton)viewList.get(id);
	}
	
	protected void startAllLoaders(){};
	
	protected void destroyAllLoaders(){};	
	
	protected void addClickListeners(){};
	
	protected TTTimerTabsActivity getParentActivity(){
		return (TTTimerTabsActivity)this.getActivity();
	}

    /**
     * Add an action that the broadcast receiver should be listening for
     * @param action
     */
    protected void AddActionFilter(String action){
    	// First unregister from any broadcasts before changing the filter
    	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mActionReceiver);
    	// Add the action to the intent filter
		filter.addAction(action);
        // Register for broadcasts when a tab is changed
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mActionReceiver, filter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.v(LOG_TAG(), "onResume");
    	
    	startAllLoaders();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.v(LOG_TAG(), "onPause");
    	
    	destroyAllLoaders();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.v(LOG_TAG(), "onStop - Unregistering all receivers");
    	if(mActionReceiver != null && filter.countActions() > 0){
    		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mActionReceiver);
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
    protected String LOG_TAG(){
    	return this.getClass().getSimpleName();
    }
    /**
     * 
     * @return The name of the tabspec ID
     */
	public String TabSpecName(){
    	return this.getClass().getSimpleName();
    }
}
