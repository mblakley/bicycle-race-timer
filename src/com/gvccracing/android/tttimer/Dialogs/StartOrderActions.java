package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StartOrderActions extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "RemoveFromStartOrder";
	
	private Button btnRemoveRacer;
	private Button btnReorderRacer;
	long raceResultID;
	
	public StartOrderActions(long RaceResultID) {
		this.raceResultID = RaceResultID;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_start_order_actions, container, false);

		btnRemoveRacer = (Button) v.findViewById(R.id.btnRemoveRacer);
		btnRemoveRacer.setOnClickListener(this);
		
		btnReorderRacer = (Button) v.findViewById(R.id.btnReorderRacer);
		btnReorderRacer.setOnClickListener(this);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.StartOrderActions;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnRemoveRacer)
			{
				Log.v(LOG_TAG, "btnRemoveRacerClickHandler");

				RaceResults.Delete(getActivity(), RaceResults._ID + "=?", new String[]{Long.toString(raceResultID)});
				
				// Update all race results that have higher start order than the racer to delete.  Change the start order and start time offset
				Cursor checkins = RaceResults.Read(getActivity(), new String[]{RaceResults._ID}, RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, null);
				if(checkins.getCount() > 0){
					Long startInterval = 0l;
					long startOrder = 1;
					checkins.moveToFirst();
					do{
						Long startTimeOffset = (startInterval * startOrder) * 1000l;
						
						ContentValues content = new ContentValues();
						//content.put(RaceResults.StartOrder, startOrder);
						//content.put(RaceResults.StartTimeOffset, startTimeOffset);
						RaceResults.Update(getActivity(), content, RaceResults._ID + "=?", new String[]{Long.toString(checkins.getLong(checkins.getColumnIndex(RaceResults._ID)))});
						startOrder++;
					}while(checkins.moveToNext());
				}
				if(checkins != null){
					checkins.close();
					checkins = null;
				}
				
				// Hide the dialog
		    	dismiss();
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnRemoveRacerClickHandler failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
