package com.xcracetiming.android.tttimer.Dialogs;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Race;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;


public class ChooseRaceSeriesType extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "ChooseRaceSeriesType";
	
	private Button btnAddIndividualRace;
	private Button btnAddRaceSeries;
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.dialog_race_series_type, container, false);
		  
		btnAddIndividualRace = (Button) v.findViewById(R.id.btnAddIndividualRace);
		btnAddIndividualRace.setOnClickListener(this);		

		btnAddRaceSeries = (Button) v.findViewById(R.id.btnAddRaceSeries);
		btnAddRaceSeries.setOnClickListener(this);
	
		return v;
	}

	@Override 
	protected int GetTitleResourceID() {
		return R.string.ChooseRaceSeriesType;
	}	

	@Override
	public void onClick(View v) { 
		try{
			if (v == btnAddIndividualRace){
				// Find the race series named "Individual", and get the RaceSeries_ID from that
				long raceSeries_ID = GetIndividualRaceSeriesID();
				AddRaceView addRaceDialog = new AddRaceView();
	        	Bundle b = new Bundle();
	        	b.putLong(Race.RaceSeries_ID, raceSeries_ID);
	            addRaceDialog.setArguments(b);
				FragmentManager fm = getFragmentManager();
				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
				// Hide the dialog
		    	dismiss();
			} else if (v == btnAddRaceSeries) {
				// Create a new race series, and get the RaceSeries_ID from that
				AddRaceSeriesView addRaceSeriesDialog = new AddRaceSeriesView();
				FragmentManager fm = getFragmentManager();
				addRaceSeriesDialog.show(fm, AddRaceSeriesView.LOG_TAG);
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
	
	private long GetIndividualRaceSeriesID() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
