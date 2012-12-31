package com.xcracetiming.android.tttimer.Dialogs;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.TTTimerTabsActivity;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.WizardPages.AdminMenuView;
import com.xcracetiming.android.tttimer.WizardPages.BaseWizardPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminAuthView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AdminAuthView";
	
	private Button btnSubmit;
	private EditText txtPassword;
	private BaseWizardPage navigateAfterAuth;
	private BaseDialog navigateAfterAuthDialog;
	
	public AdminAuthView(){
		navigateAfterAuth = null;
	}
	
	public AdminAuthView(BaseWizardPage navigateAfter){
		this.navigateAfterAuth = navigateAfter;
	}
	
	public AdminAuthView(BaseDialog navigateAfter) {
		this.navigateAfterAuthDialog = navigateAfter;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_admin_auth, container, false);

		btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(this);
		
		txtPassword = (EditText) v.findViewById(R.id.txtPassword);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AdminAuth;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
			if(navigateAfterAuth == null){
				GoToAdminMenu();
			}else{
		        Intent showAdminView = new Intent();
				showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
				showAdminView.putExtra("ShowView", navigateAfterAuth.getClass().getCanonicalName());
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
			}
			this.dismiss();
		}else{		
			txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			    public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    }
			});
		}
	}
	
	private void GoToAdminMenu() {
		Intent showAdminView = new Intent();
		showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
		showAdminView.putExtra("ShowView", new AdminMenuView().getClass().getCanonicalName());
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
	}

	public void onClick(View v) { 
		try{
			if(v == btnSubmit){
				String password = txtPassword.getText().toString();
				if(password.equals("gvccracing")){
					AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_AdminMode_Name, Boolean.toString(true), true);
					if(navigateAfterAuth == null){
						GoToAdminMenu();
					}else{
				        Intent showAdminView = new Intent();
						showAdminView.setAction(TTTimerTabsActivity.CHANGE_MAIN_VIEW_ACTION);
						showAdminView.putExtra("ShowView", navigateAfterAuth.getClass().getCanonicalName());
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(showAdminView);
					}
					this.dismiss();
				}else{
					// Display an invalid password message
					Toast.makeText(getActivity(), R.string.InvalidPassword, Toast.LENGTH_LONG).show();
				}
			} else{
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
