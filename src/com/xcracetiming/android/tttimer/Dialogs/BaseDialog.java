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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;

/**
 * @author Mark
 *
 */
public abstract class BaseDialog extends DialogFragment implements View.OnClickListener {	
	
	private ImageButton btnBack;

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
			getButton(R.id.btnBaseDialogClose1).setVisibility(View.VISIBLE);
		}else{
			getButton(R.id.btnBaseDialogClose1).setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "true", true);

		if(showCancelButton){
			getButton(R.id.btnBaseDialogClose1).setVisibility(View.VISIBLE);
		}else{
			getButton(R.id.btnBaseDialogClose1).setVisibility(View.GONE);
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
		View title = LayoutInflater.from(getActivity()).inflate(R.layout.title_with_back, (ViewGroup)getView(), false);
		ViewGroup dialog = (ViewGroup)getView().findViewById(R.id.dialogContainer);
		dialog.addView(title, 0);
		
		getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getButton(R.id.btnBaseDialogClose1).setOnClickListener(this);
		
		addClickListeners();
	}
	
	protected void addClickListeners(){};
	
	public TextView getTextView(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (TextView)viewList.get(id);
	}
	
	public LinearLayout getLinearLayout(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (LinearLayout)viewList.get(id);
	}
	
	public Button getButton(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (TextView) getView().findViewById(id));
		}
		return (Button)viewList.get(id);
	}
	
	public EditText getEditText(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (EditText) getView().findViewById(id));
		}
		return (EditText)viewList.get(id);
	}
	
	public Spinner getSpinner(int id){
		if(!viewList.containsKey(id)){
			viewList.put(id, (Spinner) getView().findViewById(id));
		}
		return (Spinner)viewList.get(id);
	}

	protected void startAllLoaders(){};
	
	protected void destroyAllLoaders(){};

	public void onClick(View v) {
		if (v == btnBack){
			dismiss();
		}
	}
	
	@Override
	public void dismiss() {
		AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_ResumePreviousState_Name, "false", true);
		super.dismiss();
	}
}
