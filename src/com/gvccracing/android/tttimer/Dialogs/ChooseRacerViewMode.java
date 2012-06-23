package com.gvccracing.android.tttimer.Dialogs;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;

public class ChooseRacerViewMode extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "ChooseViewingMode";
	
	private Button btnShowRacerInfo;
	private Button btnEditRacerTime;
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_racer_view_mode, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.viewingMode);
		titleView.setTextAppearance(getActivity(), R.style.Large);
		  
		btnShowRacerInfo = (Button) v.findViewById(R.id.btnViewRacerInfo);
		btnShowRacerInfo.setOnClickListener(this);

		btnEditRacerTime = (Button) v.findViewById(R.id.btnCorrectRacerTime);
		btnEditRacerTime.setOnClickListener(this);
		
		return v;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnShowRacerInfo){
				// TODO: Fill the racerID in correctly
				ShowRacerInfo showRacerInfoDialog = new ShowRacerInfo(1);
				FragmentManager fm = getFragmentManager();
				showRacerInfoDialog.show(fm, ShowRacerInfo.LOG_TAG);
			} else if (v == btnEditRacerTime) {
				//EditRaceResultView editRaceResultView = new EditRaceResultView();
				//FragmentManager fm = getFragmentManager();
				//editRaceResultView.show(fm, EditRaceResultView.LOG_TAG);
			}
			// Hide the dialog
	    	dismiss();
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}

