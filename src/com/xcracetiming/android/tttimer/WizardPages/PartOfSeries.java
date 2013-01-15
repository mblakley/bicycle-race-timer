package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class PartOfSeries extends BaseWizardPage implements OnCheckedChangeListener {
	public static final String LOG_TAG = "PartOfSeries";	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.wp_part_of_series, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getRadioGroup(R.id.radioGroup).setOnCheckedChangeListener(this);
	}
	
	@Override
	public void setArguments(Bundle args) {
		if(args.containsKey(LOG_TAG)){
			getRadioButton(R.id.radioNewSeries).setChecked(args.getBoolean(LOG_TAG));
			getRadioButton(R.id.radioNo).setChecked(!args.getBoolean(LOG_TAG));
		}
		if(args.containsKey("RaceSeriesName")){
			getTextView(R.id.txtRaceSeriesName).setText(args.getString("RaceSeriesName"));
		}
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddRace;
	}	

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	@Override
	public Bundle Save() throws Exception {
		Bundle b = super.Save();
		boolean isNewSeries = getRadioButton(R.id.radioNewSeries).isChecked();
		// If the race is in a series, validate that a series name was entered
		if(isNewSeries){
			if(getTextView(R.id.txtRaceSeriesName).getText().length() > 0){
				b.putString("RaceSeriesName", getTextView(R.id.txtRaceSeriesName).getText().toString());
			}else{
				// Notify the user that they need to enter a race series name
				throw new Exception("Please enter a race series name");
			}
		}
		boolean isExistingSeries = getRadioButton(R.id.radioExistingSeries).isChecked();
		// If the race is in a series, validate that a series name was entered
		if(isExistingSeries){
			if(getTextView(R.id.txtRaceSeriesName).getText().length() > 0){
				b.putString("RaceSeriesName", getTextView(R.id.txtRaceSeriesName).getText().toString());
			}else{
				// Notify the user that they need to enter a race series name
				throw new Exception("Please enter a race series name");
			}
		}
		b.putBoolean(LOG_TAG, isNewSeries);
		
		return b;
	}

	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		switch(checkedId){
			case R.id.radioNewSeries:
				// Show the textbox for series name
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.GONE);
				break;
			case R.id.radioExistingSeries:
				// Show the textbox for series name
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.VISIBLE);
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.GONE);
				break;
			case R.id.radioNo:
				// Hide the textbox for series name
				getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.GONE);
				getLinearLayout(R.id.llExistingRaceSeries).setVisibility(View.GONE);
				break;
		}
	}
}
