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
import android.widget.TextView;


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
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.RemoveRacer);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnRemoveUnassignedTime = (Button) v.findViewById(R.id.btnRemoveUnassignedTime);
		btnRemoveUnassignedTime.setOnClickListener(this);
		
		((Button) v.findViewById(R.id.btnCancel)).setOnClickListener(this);
		
		return v;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnRemoveUnassignedTime)
			{
				UnassignedTimes.Delete(getActivity(), UnassignedTimes._ID + "=?", new String[]{Long.toString(unassignedTimeIDToRemove)});
				
				// Update all race results that have higher start order than the racer to delete.  Change the start order and start time offset
				Cursor checkins = RaceResults.Read(getActivity(), new String[]{RaceResults._ID}, RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, RaceResults.StartOrder);
				getActivity().startManagingCursor(checkins);
				if(checkins.getCount() > 0){
					Long startInterval = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_StartInterval_Name, "60"));
					long startOrder = 1;
					checkins.moveToFirst();
					do{
						Long startTimeOffset = (startInterval * startOrder) * 1000l;
						
						ContentValues content = new ContentValues();
						content.put(RaceResults.StartOrder, startOrder);
						content.put(RaceResults.StartTimeOffset, startTimeOffset);
						RaceResults.Update(getActivity(), content, RaceResults._ID + "=?", new String[]{Long.toString(checkins.getLong(checkins.getColumnIndex(RaceResults._ID)))});
						startOrder++;
					}while(checkins.moveToNext());
				}
				
				// Hide the dialog
		    	dismiss();
			} else if(v.getId() == R.id.btnCancel){
				// Hide the dialog
		    	dismiss();
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnRemoveRacerClickHandler failed",ex);
		}
	}
}
