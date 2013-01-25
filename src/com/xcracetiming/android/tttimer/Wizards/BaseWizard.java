package com.xcracetiming.android.tttimer.Wizards;

import java.util.ArrayList;
import java.util.Hashtable;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.WizardPages.IWizardPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
import android.widget.Toast;

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
		
		//getImageButton(R.id.btnBaseWizardPageBack).setVisibility(showNavButtons ? View.VISIBLE : View.VISIBLE);
		//getImageButton(R.id.btnBaseWizardPageForward).setVisibility(showNavButtons ? View.VISIBLE : View.VISIBLE);
		
		getTextView(R.id.title).setText(GetTitleResourceID());

		SetupForwardAndBack();
	}
	
	protected void showNavButtons(boolean show) {
		showNavButtons = show;
		//getImageButton(R.id.btnBaseWizardPageBack).setVisibility(showNavButtons ? View.VISIBLE : View.VISIBLE);
		//getImageButton(R.id.btnBaseWizardPageForward).setVisibility(showNavButtons ? View.VISIBLE : View.VISIBLE);
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
		try{
			FragmentManager fragmentManager = getChildFragmentManager();
			switch(v.getId()){			
				case R.id.btnBaseWizardPageBack:		
					// Save the info from the displayed wizard page
					Bundle backArgs = currentWizardPage.Save();				
					
					// Figure out the next wizard page to display
					SetPreviousWizardIndex();
					currentWizardPage = wizardPages.get(currentWizardPageIndex);	
			        ((Fragment)currentWizardPage).setArguments(backArgs);
	
					// Show the next wizard page						
			        fragmentManager.beginTransaction().replace(R.id.wizardFrame, (Fragment)currentWizardPage).commit();	
			        
			        SetupForwardAndBack();
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
			        
			        SetupForwardAndBack();
					break;
				case R.id.btnSave:
					SaveAndContinue();
					break;
				case R.id.btnCancel:
					dismiss();
					break;
			}
		} catch(Exception e){
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	protected abstract void SetPreviousWizardIndex();

	protected abstract void SetNextWizardIndex(Bundle args);

	public void SaveAndContinue() throws Exception {
		
		// Save the info from the displayed wizard page
		Bundle args = currentWizardPage.Save();
		
		if(currentWizardPageIndex == wizardPages.size() - 1){
			dismiss();
			return;
		}		         
        
		// Figure out the next wizard page to display
		SetNextWizardIndex(args);
		currentWizardPage = wizardPages.get(currentWizardPageIndex);
        ((Fragment)currentWizardPage).setArguments(args);	

		// Show the next wizard page		
		FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();						         
        fragmentTransaction.replace(R.id.wizardFrame, (Fragment)currentWizardPage);
		fragmentTransaction.commit();	
		
		SetupForwardAndBack();		
	}
	 
	public void dismiss() {
		getActivity().getSupportFragmentManager().popBackStackImmediate();
	}

	public void setTitleText(int getTitleResourceID) {
		getTextView(R.id.title).setText(getTitleResourceID);
	}
	
	public void SetupForwardAndBack(){
		// Setup the forward and back buttons for edge cases
		String fullTrail = "";
		if(currentWizardPageIndex >= 1){
			fullTrail += "<i>" + getString(wizardPages.get(currentWizardPageIndex-1).GetTitleResourceID()) + "</i>";
		}
		
		fullTrail += " >> <b>" + getString(currentWizardPage.GetTitleResourceID()) + "</b>";
		
		if(currentWizardPageIndex < wizardPages.size() - 1){
			fullTrail += " >> <i>" + getString(wizardPages.get(currentWizardPageIndex+1).GetTitleResourceID()) + "</i>";
		} else{
			fullTrail += " >> <i>Done</i>";
		}
		getTextView(R.id.lblTrail).setText(Html.fromHtml(fullTrail));
    	//getImageButton(R.id.btnBaseWizardPageBack).setEnabled(!(currentWizardPageIndex <= 0));        
    	//getImageButton(R.id.btnBaseWizardPageForward).setEnabled(!(currentWizardPageIndex >= wizardPages.size() - 1));
	}
}
