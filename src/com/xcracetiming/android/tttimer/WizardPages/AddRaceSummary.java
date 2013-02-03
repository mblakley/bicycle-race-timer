package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddRaceSummary extends BaseWizardPage {
	public static final String LOG_TAG = "AddRaceSummary";	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.wp_add_race_summary, container, false);
	}
	
	@Override
	public void setArguments(Bundle args) {

	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.RaceInfo;
	}	

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
