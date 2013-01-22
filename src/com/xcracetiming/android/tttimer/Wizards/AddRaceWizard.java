package com.xcracetiming.android.tttimer.Wizards;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.WizardPages.AddLocationView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceCategoriesView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceSeriesView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceView;
import com.xcracetiming.android.tttimer.WizardPages.BaseWizardPage;
import com.xcracetiming.android.tttimer.WizardPages.PartOfSeries;

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
		
		// Figure out if this race is part of a race series
		wizardPages.add(new PartOfSeries()); // If part of a pre-existing series, select it and move on
		// If it is part of a series, and the series hasn't been created yet, the create it
		wizardPages.add(new AddRaceSeriesView());
		// Check if there are any locations available, and if not, create one
		wizardPages.add(new AddLocationView());
		// Add race categories to the race (add to RaceCategtories, RaceRaceCategories, and maybe RaceSeriesCategories)
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
	}

	@Override
	protected void SetNextWizardIndex(Bundle args) {
		switch(currentWizardPageIndex){
			case 0:				
				if(args.getBoolean(PartOfSeries.LOG_TAG)){
					currentWizardPageIndex++;
				}else{
					currentWizardPageIndex+=2;
				}
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
