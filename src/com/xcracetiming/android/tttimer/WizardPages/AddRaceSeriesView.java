package com.xcracetiming.android.tttimer.WizardPages;

import java.util.Calendar;
import java.util.Date;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

public class AddRaceSeriesView extends BaseWizardPage {
	public static final String LOG_TAG = "AddRaceSeriesView";	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 return inflater.inflate(R.layout.dialog_add_race_series, container, false);
	}
	
	@Override
	protected void addListeners() {			
		getEditText(R.id.txtRaceSeriesName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
		        }
		    }
		});	
	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.AddRaceSeries;
	}
	
	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		// Race Series name
		String raceSeriesName = getEditText(R.id.txtRaceSeriesName).getText().toString();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		DatePicker datePickerStart = (DatePicker) getView().findViewById(R.id.dateRaceSeriesStartDate);
		DatePicker datePickerEnd = (DatePicker) getView().findViewById(R.id.dateRaceSeriesEndDate);
		Date startOfSeries = new Date(datePickerStart.getYear(), datePickerStart.getMonth(), datePickerStart.getDayOfMonth());
		Date endOfSeries = new Date(datePickerEnd.getYear(), datePickerEnd.getMonth(), datePickerEnd.getDayOfMonth(), 23, 59);
		
		Uri raceSeriesUri = RaceSeries.Instance().Create(getActivity(), raceSeriesName, startOfSeries, endOfSeries, "Club");
		
		long raceSeries_ID = Long.parseLong(raceSeriesUri.getLastPathSegment());
		
		return new Bundle();
	}
}
