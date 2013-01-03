package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddLocationView extends BaseWizardPage implements View.OnClickListener {
	public static final String LOG_TAG = "AddLocationView";
	
	//private Button btnAddLocation;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_location, container, false);

		//btnAddLocation = (Button) v.findViewById(R.id.btnAddLocation);
		//btnAddLocation.setOnClickListener(this);
		
		TextView lblDistanceUnit = (TextView) v.findViewById(R.id.lblDistanceUnit);
		
		Integer distanceUnitID = Integer.parseInt(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, "1"));
		String distanceUnitText = "mi";
		switch(distanceUnitID){
			case 1:
				distanceUnitText = "mi";
				break;
			case 2:
				distanceUnitText = "km";
				break;
			default:
				distanceUnitText = "mi";
				break;
		}
		lblDistanceUnit.setText(distanceUnitText);
		
		EditText mCourseName = (EditText) v.findViewById(R.id.txtCourseName);
		mCourseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
		        }
		    }
		});
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddLocation;
	}
	
	public void onClick(View v) { 
		try{
//			if (v == btnAddLocation){
//				// First name
//				EditText mCourseName = (EditText) getView().findViewById(R.id.txtCourseName);
//				String courseName = mCourseName.getText().toString();
//				
//				EditText mDistance = (EditText) getView().findViewById(R.id.txtDistance);
//				String distance = mDistance.getText().toString();
//		
//				RaceLocation.Instance().Create(getActivity(), courseName, distance);
//					    			
//				// Hide the dialog
//				dismiss();
//			} else {
				super.onClick(v);
//			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnStartCheckIn failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}
