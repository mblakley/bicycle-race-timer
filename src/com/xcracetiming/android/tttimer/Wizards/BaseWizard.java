package com.xcracetiming.android.tttimer.Wizards;

import java.util.ArrayList;
import java.util.Hashtable;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.WizardPages.IWizardPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class BaseWizard extends Fragment implements View.OnClickListener, IWizard {
	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();
	
	private boolean showNavButtons = true;
	
	protected abstract int GetTitleResourceID();
	
	protected abstract String LOG_TAG();
	
	protected IWizardPage currentWizardPage;
	protected int currentWizardPageIndex = 0;
	
	protected ArrayList<IWizardPage> wizardPages = new ArrayList<IWizardPage>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		return inflater.inflate(R.layout.wizard_with_nav, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(showNavButtons){
			getImageButton(R.id.btnBaseWizardPageBack).setVisibility(View.VISIBLE);
			getImageButton(R.id.btnBaseWizardPageForward).setVisibility(View.VISIBLE);
		}else{
			getImageButton(R.id.btnBaseWizardPageBack).setVisibility(View.GONE);
			getImageButton(R.id.btnBaseWizardPageForward).setVisibility(View.GONE);
		}
		
		getTextView(R.id.title).setText(GetTitleResourceID());
	}
	
	protected void showNavButtons(boolean show) {
		showNavButtons = show;
		if(showNavButtons){
			getImageButton(R.id.btnBaseWizardPageBack).setVisibility(View.VISIBLE);
			getImageButton(R.id.btnBaseWizardPageForward).setVisibility(View.VISIBLE);
		}else{
			getImageButton(R.id.btnBaseWizardPageBack).setVisibility(View.GONE);
			getImageButton(R.id.btnBaseWizardPageForward).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		getImageButton(R.id.btnBaseWizardPageBack).setOnClickListener(this);
		getImageButton(R.id.btnBaseWizardPageForward).setOnClickListener(this);
		getButton(R.id.btnCancel).setOnClickListener(this);
		getButton(R.id.btnSave).setOnClickListener(this);
		
		addListeners();
	}
	
	protected void addListeners(){};
	
	protected FrameLayout getFrameLayout(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (FrameLayout) getView().findViewById(id));
		}
		return (FrameLayout)viewList.get(id);
	}
	
	protected TextView getTextView(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (TextView)viewList.get(id);
	}
	
	protected LinearLayout getLinearLayout(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (LinearLayout) getView().findViewById(id));
		}
		return (LinearLayout)viewList.get(id);
	}
	
	protected Button getButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (Button) getView().findViewById(id));
		}
		return (Button)viewList.get(id);
	}	
	
	protected ImageButton getImageButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (ImageButton) getView().findViewById(id));
		}
		return (ImageButton)viewList.get(id);
	}
	
	protected EditText getEditText(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (EditText) getView().findViewById(id));
		}
		return (EditText)viewList.get(id);
	}
	
	protected Spinner getSpinner(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (Spinner) getView().findViewById(id));
		}
		return (Spinner)viewList.get(id);
	}
	
	protected DatePicker getDatePicker(int id) {
		if(!viewList.containsKey(id)){
			viewList.put(id, (DatePicker) getView().findViewById(id));
		}
		return (DatePicker)viewList.get(id);
	}

	protected void startAllLoaders(){};
	
	protected void destroyAllLoaders(){};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	public void onClick(View v) {
		FragmentManager fragmentManager = getChildFragmentManager();
		switch(v.getId()){			
			case R.id.btnBaseWizardPageBack:		
				// Save the info from the displayed wizard page
				Bundle backArgs = currentWizardPage.Save();
				
				// Figure out the next wizard page to display
				currentWizardPageIndex--;
				currentWizardPage = wizardPages.get(currentWizardPageIndex);	
		        ((Fragment)currentWizardPage).setArguments(backArgs);

				// Show the next wizard page						
		        fragmentManager.beginTransaction().replace(R.id.wizardFrame, (Fragment)currentWizardPage).commit();	
		        
		        if(currentWizardPageIndex <= 0){
		        	getImageButton(R.id.btnBaseWizardPageBack).setEnabled(false);
		        }
				break;
			case R.id.btnBaseWizardPageForward:
				// Save the info from the displayed wizard page
				Bundle args = currentWizardPage.Save();
				
				// Figure out the next wizard page to display
				currentWizardPageIndex++;
				currentWizardPage = wizardPages.get(currentWizardPageIndex);	
		        ((Fragment)currentWizardPage).setArguments(args);

				// Show the next wizard page						
		        fragmentManager.beginTransaction().replace(R.id.wizardFrame, (Fragment)currentWizardPage).commit();
		        
		        if(currentWizardPageIndex >= wizardPages.size() - 1){
		        	getImageButton(R.id.btnBaseWizardPageForward).setEnabled(false);
		        }
//				Intent showAddRace = new Intent();
//				showAddRace.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
//				showAddRace.putExtra("ShowWizard", new MainTabsView().getClass().getCanonicalName());
//				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAddRace);
				break;
			case R.id.btnSave:
				SaveAndContinue();
				break;
			case R.id.btnCancel:
				dismiss();
				break;
		}
	}
	
	public void dismiss() {
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
		getFragmentManager().popBackStackImmediate();
	}

	public void setTitleText(int getTitleResourceID) {
		getTextView(R.id.title).setText(getTitleResourceID);
	}
}
