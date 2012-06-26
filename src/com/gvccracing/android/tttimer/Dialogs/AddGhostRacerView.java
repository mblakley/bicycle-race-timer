package com.gvccracing.android.tttimer.Dialogs;

import java.util.Calendar;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.GhostCheckInHandler;
import com.gvccracing.android.tttimer.Controls.NumberPicker;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembersCP.TeamMembers;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class AddGhostRacerView extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "AddGhostRacerView";
	
	protected Button btnAddGhostRacer;
	protected Button btnCancel;
	
	protected NumberPicker numGhostSpots;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_ghost_racer, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.AddGhostRacer);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnAddGhostRacer = (Button) v.findViewById(R.id.btnAddGhostRacer);
		btnAddGhostRacer.setOnClickListener(this);

		btnCancel = (Button) v.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
		numGhostSpots = (NumberPicker) v.findViewById(R.id.numSpots);
		numGhostSpots.setRange(1, 25);
		numGhostSpots.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		
		return v;
	}

 	/*
 	 * Add a ghost racer that can be checked in
 	 */
 	private boolean AddGhostRacer(int numSpots) 
 	{
 		boolean success = false;
		String selection = "UPPER(" + Racer.FirstName + ")=? AND UPPER(" + Racer.LastName + ")=?";
		String[] selectionArgs = new String[]{"GHOST", "RACER"};
		long racer_ID = 0;
		Uri resultUri;
		Cursor previousGhostRacer = Racer.Read(getActivity(), new String[]{Racer._ID}, selection, selectionArgs, null);
		if(previousGhostRacer != null && previousGhostRacer.getCount() > 0){
			previousGhostRacer.moveToFirst();
			// Found at least one other racer with the same name.
			racer_ID = previousGhostRacer.getLong(previousGhostRacer.getColumnIndex(Racer._ID));
		}else{
 			resultUri = Racer.Create(getActivity(), "GHOST", "RACER", Integer.MIN_VALUE, 0, 0, "None", 0);
 			racer_ID = Long.parseLong(resultUri.getLastPathSegment());
		}
		if(previousGhostRacer != null){
			previousGhostRacer.close();
			previousGhostRacer = null;
		}
		
		// Check if there's a racerClubInfo record for the given racer for this year
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		long racerClubInfo_ID = 0;		
		selection = RacerClubInfo.Racer_ID + "=? and " + RacerClubInfo.Year + "=?";
		selectionArgs = new String[]{Long.toString(racer_ID), Integer.toString(year)};
		Cursor previousGhostRacerClubInfo = RacerClubInfo.Read(getActivity(), new String[]{RacerClubInfo._ID}, selection, selectionArgs, null);
		if(previousGhostRacerClubInfo != null && previousGhostRacerClubInfo.getCount() > 0){
			previousGhostRacerClubInfo.moveToFirst();
			// Found at least one other racerClubInfo with the same racer_ID.
			racerClubInfo_ID = previousGhostRacerClubInfo.getLong(previousGhostRacerClubInfo.getColumnIndex(RacerClubInfo._ID));
		}else{
			// Create the RacerClubInfo record
	     	resultUri = RacerClubInfo.Create(getActivity(), racer_ID, "0", year, "G", 0, 0, 0, 0, null, false);
	     	racerClubInfo_ID = Long.parseLong(resultUri.getLastPathSegment());
		}	
		if(previousGhostRacerClubInfo != null){
			previousGhostRacerClubInfo.close();
			previousGhostRacerClubInfo = null;
		}
		
		long teamInfo_ID = 0;		
		selection = TeamInfo.TeamCategory + "=? and " + TeamInfo.Year + "=?";
		selectionArgs = new String[]{"G", Integer.toString(year)};
		Cursor previousGhostTeamInfo = TeamInfo.Read(getActivity(), new String[]{TeamInfo._ID}, selection, selectionArgs, null);
		if(previousGhostTeamInfo != null && previousGhostTeamInfo.getCount() > 0){
			previousGhostTeamInfo.moveToFirst();
			// Found at least one other teamInfo with the same category (G).
			teamInfo_ID = previousGhostTeamInfo.getLong(previousGhostTeamInfo.getColumnIndex(TeamInfo._ID));
		}else{
			// Create the TeamInfo record
	     	resultUri = TeamInfo.Create(getActivity(), "Ghost Team", "G");
	     	teamInfo_ID = Long.parseLong(resultUri.getLastPathSegment());

			TeamMembers.Update(getActivity(), teamInfo_ID, racerClubInfo_ID, 0, true);
		}
		if(previousGhostTeamInfo != null){
			previousGhostTeamInfo.close();
			previousGhostTeamInfo = null;
		}
		
     	
		Log.i(LOG_TAG, "AddGhostRacer racerClubInfo_ID: " + Long.toString(racerClubInfo_ID) + " teamInfo_ID: " + Long.toString(teamInfo_ID));
		
		// Check in the ghost racers		
		GhostCheckInHandler task = new GhostCheckInHandler(getActivity());
		task.execute(new Long[] { racerClubInfo_ID, teamInfo_ID, (long)numSpots });	
		
		success = true;
		
		return success;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddGhostRacer)
			{
				Log.v(LOG_TAG, "btnAddGhostRacerClickHandler");
				
				// Num Spots
				int numSpots = numGhostSpots.getCurrent();				
				
				if(AddGhostRacer(numSpots)){
					// Hide the dialog
			    	dismiss();
					
			    	numGhostSpots.setCurrent(1);
				}
			} else if(v == btnCancel){
				// Hide the dialog
				dismiss();
				
		    	numGhostSpots.setCurrent(1);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnAddGhostRacerClickHandler failed",ex);
		}
	}
}
