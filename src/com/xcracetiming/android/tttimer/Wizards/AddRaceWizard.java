package com.xcracetiming.android.tttimer.Wizards;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			return;
		}
		
		wizardPages.add(new PartOfSeries());
		wizardPages.add(new AddRaceSeriesView());
		wizardPages.add(new AddLocationView());
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
