package com.xcracetiming.android.tttimer.Dialogs;

import java.util.Hashtable;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Mark
 *
 */
public abstract class BaseDialog extends DialogFragment implements View.OnClickListener {		

	private Hashtable<Integer, View> viewList = new Hashtable<Integer, View>();
	
	private boolean showCancelButton = true;
	
	protected abstract int GetTitleResourceID();
	
	protected abstract String LOG_TAG();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogTheme);
	}
	
	protected void showCloseButton(boolean show) {
		showCancelButton = show;
		if(showCancelButton){
			getImageButton(R.id.btnBaseDialogClose1).setVisibility(View.VISIBLE);
		}else{
			getImageButton(R.id.btnBaseDialogClose1).setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);

		if(showCancelButton){
			getImageButton(R.id.btnBaseDialogClose1).setVisibility(View.VISIBLE);
		}else{
			getImageButton(R.id.btnBaseDialogClose1).setVisibility(View.GONE);
		}
		
		startAllLoaders();

		getTextView(R.id.title).setText(GetTitleResourceID());
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
		View title = LayoutInflater.from(getActivity()).inflate(R.layout.title_with_back, (ViewGroup)getView(), false);
		ViewGroup dialog = (ViewGroup)getView().findViewById(R.id.dialogContainer);
		dialog.addView(title, 0);
		
		getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getImageButton(R.id.btnBaseDialogClose1).setOnClickListener(this);
		
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
			viewList.put(id, (TextView) getView().findViewById(id));
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
		if (v.getId() == R.id.btnBaseDialogClose1){
			dismiss();
		}
	}
	
	@Override
	public void dismiss() {
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
		super.dismiss();
	}
}
