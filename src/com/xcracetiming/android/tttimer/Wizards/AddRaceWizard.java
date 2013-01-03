package com.xcracetiming.android.tttimer.Wizards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceSeriesView;
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
		
		currentWizardPage = wizardPages.get(currentWizardPageIndex);
		
		FragmentManager fragmentManager = getChildFragmentManager();				
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();	            
        fragmentTransaction.add(R.id.wizardFrame, (Fragment)currentWizardPage);
		fragmentTransaction.commit();
	}

	@Override
	protected int GetTitleResourceID() {
		return R.string.AddRace;
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public void SaveAndContinue() {
		
		// Save the info from the displayed wizard page
		Bundle args = currentWizardPage.Save();
		
		// Figure out the next wizard page to display
		currentWizardPageIndex++;
		currentWizardPage = wizardPages.get(currentWizardPageIndex);	
        ((Fragment)currentWizardPage).setArguments(args);

		// Show the next wizard page						
		FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();		         
        fragmentTransaction.replace(R.id.wizardFrame, (Fragment)currentWizardPage);
		fragmentTransaction.commit();		
	}
	
	@Override
	public void onClick(View v) { 
		try{
			switch(v.getId()){			
				default:
					super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}
