package com.xcracetimer.android.tttimer.CursorAdapters;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TeamCheckInCursorAdapter extends BaseCursorAdapter {

    public TeamCheckInCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);	
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
    		Log.i("TeamCheckInCursorAdapter", "newView start");
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_team_checkin, parent, false);

	        Log.i("TeamCheckInCursorAdapter", "newView complete");
    	}catch(Exception ex){
    		Log.e("TeamCheckInCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("TeamCheckInCursorAdapter", "bindView start");
	    	int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
	        int teamMemberNamesCol = c.getColumnIndex("RacerNames");
	
	        String teamName = c.getString(teamNameCol);
	        String teamMemberNames = c.getString(teamMemberNamesCol);
	        Integer teamInfo_ID = c.getInt(0);

	        Cursor raceResult = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.TeamInfo_ID + "=? AND " + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), new String[]{Integer.toString(teamInfo_ID)}, null);
	        /**
	         * Next set the name of the entry.
	         */     
	        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeamName);
	        if (lblTeamName != null) {
	        	lblTeamName.setText(teamName);
	        }
	        
	        TextView lblTeamMemberNames = (TextView) v.findViewById(R.id.lblTeamMembers);
	        if (lblTeamMemberNames != null) {
	        	lblTeamMemberNames.setText(teamMemberNames);
	        }

	        Button btnCheckIn = (Button) v.findViewById(R.id.btnTeamCheckIn);
	        if(btnCheckIn != null){
	        	btnCheckIn.setTag(teamInfo_ID);
	        	
	        	// Check if the racer has previously checked in
	        	if(raceResult != null && raceResult.getCount() > 0){
	        		// They have a race result ID already, so set up the button differently
	        		btnCheckIn.setText("Ready!");
	        		btnCheckIn.setEnabled(false);
	        	}else{
	        		btnCheckIn.setText("Check In");
	        		btnCheckIn.setEnabled(true);
	        	}
	        }
	        
	        if(raceResult != null){
	        	raceResult.close();
	        	raceResult = null;
	        }
	        Log.i("TeamCheckInCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("TeamCheckInCursorAdapter", ex.toString());
		}
    }
}
