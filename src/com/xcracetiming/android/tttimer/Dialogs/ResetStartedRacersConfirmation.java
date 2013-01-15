package com.xcracetiming.android.tttimer.Dialogs;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.AsyncTasks.ResetAllRacersStartTime;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ResetStartedRacersConfirmation extends BaseDialog implements View.OnClickListener {

	public static final String LOG_TAG = "ResetTimerConfirmation";
	
	private Button btnYes;
	private Button btnNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_racers_confirmation, container);
        
        btnYes = (Button)view.findViewById(R.id.btnYesReset);
        btnYes.setOnClickListener(this);
        btnNo = (Button)view.findViewById(R.id.btnNoReset);
        btnNo.setOnClickListener(this);
        
        return view;
    }
    
    public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btnYesReset:
				long race_ID = AppSettings.Instance().ReadLongValue(getActivity(), AppSettings.AppSetting_RaceID_Name, null);
				
				if(race_ID > 0){
					// Reset all racers in this race
					ResetAllRacersStartTime task = new ResetAllRacersStartTime(getActivity());
		 			task.execute(new Long[]{race_ID});
		 			
		            this.dismiss();
				}else{
					Toast.makeText(getActivity(), "Invalid race ID", 3000).show();
				}
				break;
			case R.id.btnNoReset:
				// Don't do anything but close the dialog
	            this.dismiss();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	@Override
	protected int GetTitleResourceID() {
		return R.string.ResetTimer;
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}