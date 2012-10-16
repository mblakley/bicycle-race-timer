package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoveUnassignedTime extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "RemoveUnassignedTime";
	
	Button btnRemoveUnassignedTime;
	long unassignedTimeIDToRemove;
	
	public RemoveUnassignedTime(long unassignedTimeIDToRemove) {
		this.unassignedTimeIDToRemove = unassignedTimeIDToRemove;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_remove_unassigned_time, container, false);

		btnRemoveUnassignedTime = (Button) v.findViewById(R.id.btnRemoveUnassignedTime);
		btnRemoveUnassignedTime.setOnClickListener(this);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.RemoveUnassignedTime;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnRemoveUnassignedTime)
			{
				UnassignedTimes.Delete(getActivity(), UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTimeIDToRemove)});
				
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
