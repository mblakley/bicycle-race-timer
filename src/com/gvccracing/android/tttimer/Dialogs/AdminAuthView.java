package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AdminAuthView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AdminAuthView";
	
	private Button btnSubmit;
	private Button btnCancel;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_admin_auth, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.AppSettings);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(this);

		btnCancel = (Button) v.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
			GoToAdminMenu();
			this.dismiss();
		}		
	}
	
	private void GoToAdminMenu() {
		AdminMenuView adminMenuDialog = new AdminMenuView();
		FragmentManager fm = getFragmentManager();
		adminMenuDialog.show(fm, AdminMenuView.LOG_TAG);
	}

	public void onClick(View v) { 
		try{
			if (v == btnCancel){
				dismiss();
			}else if(v == btnSubmit){
				EditText txtPassword = (EditText) getView().findViewById(R.id.editText1);
				String password = txtPassword.getText().toString();
				if(password.equals("gvccracing")){
					AppSettings.Update(getActivity(), AppSettings.AppSetting_AdminMode_Name, Boolean.toString(true), true);
					
					GoToAdminMenu();
					
					this.dismiss();
				}else{
					// Display an invalid password message
					Toast.makeText(getActivity(), R.string.InvalidPassword, 3000).show();
				}
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
}
