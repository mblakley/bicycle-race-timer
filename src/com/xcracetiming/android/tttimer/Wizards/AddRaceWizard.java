package com.xcracetiming.android.tttimer.Wizards;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.WizardPages.AddLocationView;
import com.xcracetiming.android.tttimer.WizardPages.AddRaceView;
import com.xcracetiming.android.tttimer.WizardPages.AdminMenuView;

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
	protected int GetTitleResourceID() {
		return R.string.AddRace;
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public void Save() {
		FragmentManager fragmentManager = getChildFragmentManager();
		String className = new AdminMenuView().getClass().getCanonicalName();
		try {				
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment mainView = (Fragment)Class.forName(className).newInstance();		            
            fragmentTransaction.add(R.id.wizardFrame, mainView);
			fragmentTransaction.commit();
		} catch (ClassNotFoundException e) {
			Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
		} catch (IllegalAccessException e) {
			Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
		} catch (java.lang.InstantiationException e) {
			Log.e("TTTimerTabsActivity.onReceive", "Unable to create class of type " + className, e);
		}
	}
	
	@Override
	public void onClick(View v) { 
		try{
			switch(v.getId()){
				case R.id.btnSave:
					Save();
					break;	
				default:
					super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}
