package com.xcracetiming.android.tttimer.WizardPages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;

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
	public void onResume() {
		super.onResume();
		long raceSeries_ID = -1;
		Bundle args = getArguments();
		// Determine if we should show the series info
		if(args.containsKey(Race.RaceSeries_ID)){
			raceSeries_ID = args.getLong(Race.RaceSeries_ID);
		}
		if(raceSeries_ID > 1){
			getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.VISIBLE);
			getTextView(R.id.txtRaceSeriesName).setText(args.getString(RaceSeries.SeriesName));
		}else{
			getLinearLayout(R.id.llRaceSeriesName).setVisibility(View.GONE);
		}
		
		if(args.containsKey(Race.EventName) && args.getString(Race.EventName) == ""){
			getLinearLayout(R.id.llRaceName).setVisibility(View.VISIBLE);
		} else{
			getLinearLayout(R.id.llRaceName).setVisibility(View.GONE);
		}
		
		Date raceDateTemp = new Date(args.getLong(Race.RaceDate));		
		SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy", Locale.US);						
		getTextView(R.id.raceDate).setText(formatter.format(raceDateTemp).toString());
		
		getTextView(R.id.raceStartInterval).setText(Long.toString(args.getLong(Race.StartInterval)));
		
		getTextView(R.id.raceCourseName).setText(args.getString(RaceLocation.CourseName));
		
		getTextView(R.id.raceDistance).setText(args.getString(RaceLocation.Distance));
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
