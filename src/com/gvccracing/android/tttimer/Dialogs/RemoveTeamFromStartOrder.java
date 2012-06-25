package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class RemoveTeamFromStartOrder extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "RemoveFromStartOrder";
	
	Button btnRemoveTeam;
	long teamRaceResultIDToRemove;
	
	public RemoveTeamFromStartOrder(long teamRaceResultIDToRemove) {
		this.teamRaceResultIDToRemove = teamRaceResultIDToRemove;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_remove_team, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.RemoveTeam);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnRemoveTeam = (Button) v.findViewById(R.id.btnRemoveTeam);
		btnRemoveTeam.setOnClickListener(this);
		
		((Button) v.findViewById(R.id.btnCancel)).setOnClickListener(this);
		
		return v;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnRemoveTeam)
			{
				Log.v(LOG_TAG, "btnRemoveTeamClickHandler");

				RaceResults.Delete(getActivity(), RaceResults._ID + "=?", new String[]{Long.toString(teamRaceResultIDToRemove)});
				
				// Update all race results that have higher start order than the racer to delete.  Change the start order and start time offset
				Cursor checkins = RaceResults.Read(getActivity(), new String[]{RaceResults._ID}, RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, RaceResults.StartOrder);
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
				if(checkins != null){
					checkins.close();
					checkins = null;
				}
				
				// Hide the dialog
		    	dismiss();
			} else if(v.getId() == R.id.btnCancel){
				// Hide the dialog
		    	dismiss();
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnRemoveTeamClickHandler failed",ex);
		}
	}
}
