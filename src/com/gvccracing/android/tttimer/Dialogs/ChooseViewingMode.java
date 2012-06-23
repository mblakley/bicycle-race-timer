package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.viewingMode);
		titleView.setTextAppearance(getActivity(), R.style.Large);
		  
		btnAddNewRace = (Button) v.findViewById(R.id.btnAddNewRace);
		btnAddNewRace.setOnClickListener(this);

		btnPreviousRaces = (Button) v.findViewById(R.id.btnPreviousRaces);
		btnPreviousRaces.setOnClickListener(this);
	
		return v;
	}

	public void onClick(View v) { 
		try{
			if (v == btnAddNewRace){
				AddRaceView addRaceDialog = new AddRaceView();
				FragmentManager fm = getFragmentManager();
				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
			} else if (v == btnPreviousRaces) {
				PreviousRaceResults previousRaceResultsView = new PreviousRaceResults();
				FragmentManager fm = getFragmentManager();
				previousRaceResultsView.show(fm, PreviousRaceResults.LOG_TAG);
			}
			// Hide the dialog
	    	dismiss();
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}
