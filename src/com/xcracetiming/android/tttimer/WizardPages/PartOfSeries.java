package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PartOfSeries extends BaseWizardPage {
	public static final String LOG_TAG = "PartOfSeries";	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.wp_part_of_series, container, false);
	}
	
	@Override
	public void setArguments(Bundle args) {
		
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddRace;
	}	

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		Bundle b = getArguments();
		if(b == null){
			b = new Bundle();
		}
		b.putBoolean(LOG_TAG, getRadioButton(R.id.radioYes).isChecked());		
		
		return getArguments();
	}
}
