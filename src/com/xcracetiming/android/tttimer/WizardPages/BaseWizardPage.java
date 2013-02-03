package com.xcracetiming.android.tttimer.WizardPages;

import java.util.Hashtable;

import com.xcracetiming.android.tttimer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Mark
 *
 */
public abstract class BaseWizardPage extends Fragment implements View.OnClickListener, IWizardPage {	

	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();	
	
	public abstract int GetTitleResourceID();
	
	protected abstract String LOG_TAG();

	public Bundle Save() throws Exception{
		Bundle b = getArguments();
		if(b == null){
			b = new Bundle();
		}
		
		return b;
	}
	
	@Override
	public void onResume() {
		super.onResume();		
		
		startAllLoaders();		

		//((BaseWizard)getParentFragment()).setTitleText(GetTitleResourceID());
	}
	
	@Override
	public void onPause() {
		super.onPause();

    	destroyAllLoaders();    	
	}
	
	@Override
	public void setArguments(Bundle args) {
	}
	
	@Override
	public void onStart() {
		super.onStart();	
		
		getView().setOnTouchListener(new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });
		
		addListeners();
	}
	
	protected void addListeners(){};
	
	protected RadioGroup getRadioGroup(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (RadioGroup) getView().findViewById(id));
		}
		return (RadioGroup)viewList.get(id);
	}
	
	protected RadioButton getRadioButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (RadioButton) getView().findViewById(id));
		}
		return (RadioButton)viewList.get(id);
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
	
	protected ListView getListView(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (ListView) getView().findViewById(id));
		}
		return (ListView)viewList.get(id);
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
		getFragmentManager().popBackStackImmediate();
	}
}
