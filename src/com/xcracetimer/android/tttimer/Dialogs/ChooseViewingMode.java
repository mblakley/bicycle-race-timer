package com.xcracetimer.android.tttimer.Dialogs;

import com.xcracetimer.android.tttimer.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;


public class ChooseViewingMode extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "ChooseViewingMode";
	
	private Button btnAddNewRace;
	private Button btnPreviousRaces;
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.dialog_viewing_mode, container, false);
		  
		btnAddNewRace = (Button) v.findViewById(R.id.btnAddNewRace);
		btnAddNewRace.setOnClickListener(this);		

		btnPreviousRaces = (Button) v.findViewById(R.id.btnPreviousRaces);
		btnPreviousRaces.setOnClickListener(this);
	
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
			if (v == btnAddNewRace){
//				AddRaceView addRaceDialog = new AddRaceView();
//				FragmentManager fm = getFragmentManager();
//				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
//				// Hide the dialog
//		    	dismiss();
			} else if (v == btnPreviousRaces) {
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
