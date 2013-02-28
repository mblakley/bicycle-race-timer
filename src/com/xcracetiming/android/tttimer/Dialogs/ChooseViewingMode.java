package com.xcracetiming.android.tttimer.Dialogs;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.Wizards.AddRaceWizard;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;


public class ChooseViewingMode extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "ChooseViewingMode";	
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.dialog_viewing_mode, container, false);
		  
		((Button) v.findViewById(R.id.btnAddNewRace)).setOnClickListener(this);		
		((Button) v.findViewById(R.id.btnPreviousRaces)).setOnClickListener(this);
	
		return v;
	}

	@Override 
	protected int GetTitleResourceID() {
		return R.string.viewingMode;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	@Override
	public void onClick(View v) { 
		try{
			if (v.getId() == R.id.btnAddNewRace){				
				Intent showAddRaceWizard = new Intent();
				showAddRaceWizard.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
				showAddRaceWizard.putExtra("ShowView", new AddRaceWizard().getClass().getCanonicalName());
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddRaceWizard);
				
				// Hide the dialog
		    	dismiss();
			} else if (v.getId() == R.id.btnPreviousRaces) {
				OtherRaceResults previousRaceResultsView = new OtherRaceResults();
				FragmentManager fm = getFragmentManager();
				previousRaceResultsView.show(fm, OtherRaceResults.LOG_TAG);
				// Hide the dialog
		    	dismiss();
			} else{
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
	
	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
