package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettings;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
	private BaseDialog navigateAfterAuth;
	
	public AdminAuthView(){
		navigateAfterAuth = null;
	}
	
	public AdminAuthView(BaseDialog navigateAfter){
		this.navigateAfterAuth = navigateAfter;
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
				FragmentManager fm = getFragmentManager();
		        navigateAfterAuth.show(fm, navigateAfterAuth.LOG_TAG());
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
		AdminMenuView adminMenuDialog = new AdminMenuView();
		FragmentManager fm = getFragmentManager();
		adminMenuDialog.show(fm, AdminMenuView.LOG_TAG);
	}

	public void onClick(View v) { 
		try{
			if(v == btnSubmit){
				String password = txtPassword.getText().toString();
				if(password.equals("tiogavelo")){
					AppSettings.Update(getActivity(), AppSettings.AppSetting_AdminMode_Name, Boolean.toString(true), true);
					if(navigateAfterAuth == null){
						GoToAdminMenu();
					}else{
						FragmentManager fm = getFragmentManager();
				        navigateAfterAuth.show(fm, navigateAfterAuth.LOG_TAG());
					}
					this.dismiss();
				}else{
					// Display an invalid password message
					Toast.makeText(getActivity(), R.string.InvalidPassword, 3000).show();
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
