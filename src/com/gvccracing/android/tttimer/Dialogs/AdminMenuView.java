package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AdminMenuView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AdminMenuView";
	
	private Button btnAddLocation;
	private Button btnAddRace;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_admin_menu, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.AdminMenu);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnAddLocation = (Button) v.findViewById(R.id.btnAddLocation);
		btnAddLocation.setOnClickListener(this);

		btnAddRace = (Button) v.findViewById(R.id.btnAddRace);
		btnAddRace.setOnClickListener(this);
		
		return v;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddRace){
				AddRaceView addRaceDialog = new AddRaceView();
				FragmentManager fm = getFragmentManager();
				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
			}else if(v == btnAddLocation){
				AddLocationView addLocationDialog = new AddLocationView();
				FragmentManager fm = getFragmentManager();
				addLocationDialog.show(fm, AddLocationView.LOG_TAG);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}
