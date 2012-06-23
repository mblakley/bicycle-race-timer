package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeamStartOrderCursorAdapter extends BaseCursorAdapter {
	
    public TeamStartOrderCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_team_start_order, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("TeamStartOrderCursorAdapter", "bindView start");
    		
    		fillData(c, v);
    		
            Log.i("TeamStartOrderCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("TeamStartOrderCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v) {
    	int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
    	int racerNamesCol = c.getColumnIndex("RacerNames");
        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
        int startTimeCol = c.getColumnIndex(RaceResults.StartTimeOffset);
        
        String teamName = c.getString(teamNameCol);
        String racerNames = c.getString(racerNamesCol);
        int startOrder = c.getInt(startOrderCol);
        int startTime = c.getInt(startTimeCol);

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(R.id.lblTeamName);
        if (lblName != null) {
        	lblName.setText(teamName);
        }
        
        TextView lblRacerNames = (TextView) v.findViewById(R.id.lblRacerNames);
        if (lblRacerNames != null) {
        	lblRacerNames.setText(racerNames);
        }
        
        TextView lblStartOrder = (TextView) v.findViewById(R.id.lblStartOrder);
        if (lblStartOrder != null) {
        	lblStartOrder.setText(Integer.toString(startOrder));
        }
        
        TextView lblStartTime = (TextView) v.findViewById(R.id.lblStartTime);
        if (lblStartTime != null) {
//        	Time startTimeOffset = new Time(startTime);
//        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
//        	lblStartTime.setText(formatter.format(startTimeOffset).toString());
        	lblStartTime.setText(TimeFormatter.Format(startTime, true, true, true, true, false, false, false, false));
        }    	
    }
}
