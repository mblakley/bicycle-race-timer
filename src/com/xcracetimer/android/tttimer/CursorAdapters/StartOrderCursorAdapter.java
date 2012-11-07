package com.xcracetimer.android.tttimer.CursorAdapters;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StartOrderCursorAdapter extends BaseCursorAdapter {
	
    public StartOrderCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_start_order, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("StartOrderCursorAdapter", "bindView start");
    		
    		fillData(c, v, context);
    		
            Log.i("StartOrderCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("StartOrderCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v, Context context) {
    	int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);
        int teamCol = c.getColumnIndex(TeamInfo.TeamName);
        int raceResultCol = c.getColumnIndex(RaceResults._ID);
        int removedCol = c.getColumnIndex(RaceResults.Removed);

        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);
        String teamName = c.getString(teamCol);
        Long raceResult_ID = c.getLong(raceResultCol);
        boolean removed = Boolean.parseBoolean(c.getString(removedCol));

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(R.id.lblName);
        if (lblName != null) {
        	String name = firstName + " " + lastName;
        	lblName.setText(name);
        }
               
        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeam);
        if (lblTeamName != null) {
        	lblTeamName.setText(teamName);
        }
               
        Button btnRemove = (Button) v.findViewById(R.id.btnCheckIn);
        if(btnRemove != null){
        	btnRemove.setTag(raceResult_ID);
        	
        	// Check if the racer has previously checked in
        	if(!removed){
        		// They have a race result ID already, so set up the button differently
        		btnRemove.setText("Remove");
        	}else{
        		btnRemove.setText("Check In");
        	}
        }
    }
}
