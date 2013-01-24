package com.xcracetiming.android.tttimer.Wizards;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.WizardPages.AddLocationView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceCategoriesView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceSeriesView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceView;
import com.xcracetiming.android.tttimer.WizardPages.ChooseRaceSeries;

public class AddRaceWizard extends BaseWizard implements View.OnClickListener {
	public static final String LOG_TAG = "AddRaceWizard";
	
	// AddRaceSeries (Series? Yes/No, Yes - Series Name, Start Date, End Date.  Need another wizard for advanced configuration) 
	// AddRaceLocation (choose from a list or new Course Name, Distance.  Need another wizard for advanced configuration)
	// AddRaceCategories (Add Category on top with "add" button, with checked list below.  Need another wizard for advanced configuration)
	// AddUSACInfo (Event Name, USACEventID, if ScoringType='USAC')
	// AddRace (RaceType, RaceDate, StartTime, StartInterval)
	
	// Inherit from wizard page for nav buttons, frame layout, cancel button, and save and continue button
	// Save and continue calls the "WizardPage.Save" function on the currently selected page fragment, and moves the next one in the list into the frame layout
	// Cancel just kills the containing wizard fragment
	
	ArrayList<String> pageList = new ArrayList<String>(Arrays.asList("AddRaceSeries"));
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			return;
		}
		
		// What info do you need to set up a race, at it's most basic form
		// Race Date/Race Type (TT or Team TT for now)/Race Start Interval
		// (optional) Race Laps (yes or no?, how many?)
		// Race Series (Individual or custom)
		// Race Location
		// Race Categories (most likely more than 1)
		
		// Figure out if this race is part of a race series
		wizardPages.add(new ChooseRaceSeries()); // If part of a pre-existing series, select it and move on
		// If it is part of a series, and the series hasn't been created yet, the create it
		wizardPages.add(new AddRaceSeriesView());
		// Choose a race location
		//wizardPages.add(new ChooseRaceLocation()); // If held at a pre-existing location, select it and move on
		// Check if there are any locations available, and if not, create one
		wizardPages.add(new AddLocationView());
		// Add race categories to the race (add to RaceCategories, RaceRaceCategories, and maybe RaceSeriesCategories)
		wizardPages.add(new AddRaceCategoriesView());
		//wizardPages.add(new AddUSACInfoView());
		wizardPages.add(new AddRaceView());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		currentWizardPage = wizardPages.get(currentWizardPageIndex);
		
		FragmentManager fragmentManager = getChildFragmentManager();				
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();	            
        fragmentTransaction.add(R.id.wizardFrame, (Fragment)currentWizardPage);
		fragmentTransaction.commit();
		
    	getImageButton(R.id.btnBaseWizardPageBack).setEnabled(!(currentWizardPageIndex <= 0));
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
	public void SaveAndContinue() throws Exception {
		// Check if we are on the last page, and if so, do the final save to create all of the database records in order
		if(currentWizardPageIndex >= wizardPages.size() - 1){
			// This is the last page.  Do extra stuff.
			Bundle args = currentWizardPage.Save();
			Long raceSeries_ID;
			if(args.getBoolean("createSeries")){
				// Save the race series info into the database
				ContentValues content = new ContentValues();
				content.put(RaceSeries.SeriesName, args.getString(RaceSeries.SeriesName));
				content.put(RaceSeries.SeriesStartDate, args.getLong(RaceSeries.SeriesStartDate));
				content.put(RaceSeries.SeriesEndDate, args.getLong(RaceSeries.SeriesEndDate));
				content.put(RaceSeries.SeriesScoringType, args.getLong(RaceSeries.SeriesScoringType));
				Uri createdSeriesUri = RaceSeries.Instance().Create(getActivity(), content);
				raceSeries_ID = Long.parseLong(createdSeriesUri.getLastPathSegment());
			} else{
				raceSeries_ID = args.getLong(Race.RaceSeries_ID);
			}
			
			Long raceLocation_ID;
			if(args.getBoolean("createLocation")){
				ContentValues content = new ContentValues();
				content.put(RaceLocation.CourseName, args.getString(RaceLocation.CourseName));
				content.put(RaceLocation.Distance, args.getFloat(RaceLocation.Distance));
				content.put(RaceLocation.DistanceUnit, args.getString(RaceLocation.DistanceUnit));				
				Uri createdLocationUri = RaceLocation.Instance().Create(getActivity(), content);
				raceLocation_ID = Long.parseLong(createdLocationUri.getLastPathSegment());
			}else{
				raceLocation_ID = args.getLong(Race.RaceLocation_ID);
			}
			
			// Create the race
			ContentValues raceContent = new ContentValues();
			raceContent.put(Race.RaceSeries_ID, raceSeries_ID);
			raceContent.put(Race.RaceLocation_ID, raceLocation_ID);
			raceContent.put(Race.EventName, args.getString(Race.EventName));
			raceContent.put(Race.RaceDate, args.getLong(Race.RaceDate));
			raceContent.put(Race.RaceStartTime, args.getLong(Race.RaceStartTime));
			raceContent.put(Race.RaceType_ID, args.getLong(Race.RaceType_ID));
			raceContent.put(Race.ScoringType, args.getLong(Race.ScoringType));
			raceContent.put(Race.StartInterval, args.getLong(Race.StartInterval));
			raceContent.put(Race.USACEventID, args.getLong(Race.USACEventID));
			Race.Instance().Create(getActivity(), raceContent);
			
			// Create the new race categories
			// This must be done after the race is created, otherwise we don't know what to link it to.
			ArrayList<String> newCategories = args.getStringArrayList(RaceCategory.FullCategoryName);
			for(String categoryName: newCategories){
				ContentValues categoryContent = new ContentValues();
				categoryContent.put(RaceCategory.FullCategoryName, categoryName);
				categoryContent.put(RaceCategory.RaceSeries_ID, raceSeries_ID);
				RaceCategory.Instance().Create(getActivity(), categoryContent);
			}			
		}else {
			super.SaveAndContinue();
		}
	}

	@Override
	protected void SetNextWizardIndex(Bundle args) {
		switch(currentWizardPageIndex){
			case 0:				
				if(args.getBoolean(ChooseRaceSeries.LOG_TAG)){
					currentWizardPageIndex++;
				}else{
					currentWizardPageIndex+=2;
				}
				break;
			default:
				currentWizardPageIndex++;
				break;
		}
	}
	
	@Override
	protected void SetPreviousWizardIndex() {
//		Bundle args = ((BaseWizardPage)currentWizardPage).getArguments();
//		switch(currentWizardPageIndex){
//			case 0:				
//				if(args.getBoolean(PartOfSeries.LOG_TAG)){
					currentWizardPageIndex--;
//				}else{
//					dismiss();
//				}
//				break;
//		}
	}
}
