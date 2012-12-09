package com.xcracetiming.android.tttimer.Dialogs;

import java.util.Calendar;
import java.util.Date;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddRaceSeriesView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AddRaceSeriesView";
	
	private Button btnAddRaceSeries;
	private EditText txtRaceSeriesName;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_race_series, container, false);

		btnAddRaceSeries = (Button) v.findViewById(R.id.btnAddRaceSeries);
		btnAddRaceSeries.setOnClickListener(this);
		
		txtRaceSeriesName = (EditText) v.findViewById(R.id.txtRaceSeriesName);
		txtRaceSeriesName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddRaceSeries;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddRaceSeries){
				// Race Series name
				String raceSeriesName = txtRaceSeriesName.getText().toString();
		
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				DatePicker datePickerStart = (DatePicker) getView().findViewById(R.id.dateRaceSeriesStartDate);
				DatePicker datePickerEnd = (DatePicker) getView().findViewById(R.id.dateRaceSeriesEndDate);
				Date startOfSeries = new Date(datePickerStart.getYear(), datePickerStart.getMonth(), datePickerStart.getDayOfMonth());
				Date endOfSeries = new Date(datePickerEnd.getYear(), datePickerEnd.getMonth(), datePickerEnd.getDayOfMonth(), 23, 59);
				
				Uri raceSeriesUri = RaceSeries.Instance().Create(getActivity(), raceSeriesName, startOfSeries, endOfSeries, "Club");
				
				long raceSeries_ID = Long.parseLong(raceSeriesUri.getLastPathSegment());
				
				AddRaceView addRaceDialog = new AddRaceView(raceSeries_ID);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				addRaceDialog.show(fm, AddRaceView.LOG_TAG);
				
				// Hide the dialog
				dismiss();
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnAddRaceSeries failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
