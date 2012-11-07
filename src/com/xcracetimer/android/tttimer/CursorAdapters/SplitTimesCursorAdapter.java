package com.xcracetimer.android.tttimer.CursorAdapters;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.xcracetimer.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplitTimesCursorAdapter extends BaseCursorAdapter {

    public SplitTimesCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_split_time, parent, false);
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{	        	        
	        int elapsedTimeCol = c.getColumnIndex(RaceLaps.ElapsedTime);
	        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
    		
	        Long elapsedTime = c.getLong(elapsedTimeCol);
	        String teamName = c.getString(teamNameCol);
	        
	        TextView lblElapsedTime = (TextView) v.findViewById(R.id.lblElapsedTime);
	        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeamName);	
	
	        /**
	         * Set the elapsed time of the entry.
	         */	        
	        if (lblElapsedTime != null) {	        	
	        	lblElapsedTime.setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
	        }
	        
	        // Set the team name
	        if (lblTeamName != null) {	        	
	        	lblTeamName.setText(teamName);
	        }
		}catch(Exception ex){
			Log.e(LOG_TAG(), ex.toString());
		}
    }
    
	protected String LOG_TAG() {
		return "UnassignedTimeCursorAdapter";
	}
}

