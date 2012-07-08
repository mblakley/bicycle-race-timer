package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoResultsView;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.Tabs.StartTab;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ResetTimerConfirmation extends BaseDialog implements View.OnClickListener {

	public static final String LOG_TAG = "ResetTimerConfirmation";
	
	private Button btnYes;
	private Button btnNo;	
	private StartTab context;
	
    public interface ResetTimerDialogListener {
        void onFinishResetTimerDialog(boolean doReset);
    }
    
    public ResetTimerConfirmation(StartTab context){
    	this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_timer_confirmation, container);
        
        btnYes = (Button)view.findViewById(R.id.btnYesReset);
        btnYes.setOnClickListener(this);
        btnNo = (Button)view.findViewById(R.id.btnNoReset);
        btnNo.setOnClickListener(this);
        
        return view;
    }
    
    public void onClick(View v) {
		FragmentManager fm = getFragmentManager();
		switch (v.getId())
		{
			case R.id.btnYesReset:
				// Return input text to activity
	            context.onFinishResetTimerDialog(true);
	            this.dismiss();
				
				String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID};
				String selection = RaceResults.Race_ID + " = " + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + Race.RaceStartTime + ">0 AND " + RaceResults.EndTime + ">0";
				String[] selectionArgs = null; 
				String sortOrder = Race.getTableName() + "." + Race._ID;
				
				Cursor finishedRaceResults = getActivity().getContentResolver().query(RaceInfoResultsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	            boolean anyRacerFinished = finishedRaceResults != null && finishedRaceResults.getCount() > 0;
	            if(!anyRacerFinished){
	            	projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID};
					selection = RaceResults.Race_ID + " = " + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + "<=" + System.currentTimeMillis();
					selectionArgs = null; 
					sortOrder = Race.getTableName() + "." + Race._ID;
					
					Cursor startedRaceResults = getActivity().getContentResolver().query(RaceInfoResultsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
					boolean anyRacerStarted = startedRaceResults != null && startedRaceResults.getCount() > 0;
		            if(anyRacerStarted){		            
			            ResetStartedRacersConfirmation resetStartedRacers = new ResetStartedRacersConfirmation();
			            resetStartedRacers.show(fm, ResetStartedRacersConfirmation.LOG_TAG);
		            }
		            if(startedRaceResults != null){
			            startedRaceResults.close();
			            startedRaceResults = null;
		            }
	            }
	            if(finishedRaceResults != null){
	            	finishedRaceResults.close();
	            	finishedRaceResults = null;
	            }
				break;
			case R.id.btnNoReset:
				// Return input text to activity
	            context.onFinishResetTimerDialog(false);
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