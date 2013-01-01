package com.xcracetiming.android.tttimer.WizardPages;

import java.util.Hashtable;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.Wizards.BaseWizard;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Mark
 *
 */
public abstract class BaseWizardPage extends Fragment implements View.OnClickListener, IWizard {	

	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();	
	
	protected abstract int GetTitleResourceID();
	
	protected abstract String LOG_TAG();

	@Override
	public void onResume() {
		super.onResume();
		
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);
		
		startAllLoaders();		

		//((BaseWizard)getParentFragment()).setTitleText(GetTitleResourceID());
	}
	
	@Override
	public void onPause() {
		super.onPause();

    	destroyAllLoaders();
    	
		dismiss();
	}
	
	@Override
	public void onStart() {
		super.onStart();	
		
		addListeners();
	}
	
	protected void addListeners(){};
	
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

	public void onClick(View v) {
		if (v.getId() == R.id.btnBaseWizardPageBack){
			dismiss();
		} else if (v.getId() == R.id.btnBaseWizardPageForward){
			dismiss();
		}
	}
	
	public void dismiss() {
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
		getFragmentManager().popBackStack();
	}
}
