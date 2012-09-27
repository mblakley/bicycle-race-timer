package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    		
    		fillData(c, v);
    		
            Log.i("StartOrderCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("StartOrderCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v) {
    	int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);
//        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
//        int startTimeCol = c.getColumnIndex(RaceResults.StartTimeOffset);
        int teamCol = c.getColumnIndex(TeamInfo.TeamName);

        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);
//        int startOrder = c.getInt(startOrderCol);
//        int startTime = c.getInt(startTimeCol);
        String teamName = c.getString(teamCol);

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(R.id.lblName);
        if (lblName != null) {
        	String name = firstName + " " + lastName;
        	lblName.setText(name);
        }
        
//        TextView lblStartOrder = (TextView) v.findViewById(R.id.lblStartOrder);
//        if (lblStartOrder != null) {
//        	lblStartOrder.setText(Integer.toString(startOrder));
//        }
        
//        TextView lblStartTime = (TextView) v.findViewById(R.id.lblStartTime);
//        if (lblStartTime != null) {
////        	Time startTimeOffset = new Time(startTime);
////        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss");
////        	lblStartTime.setText(formatter.format(startTimeOffset).toString());
//        	lblStartTime.setText(TimeFormatter.Format(startTime, true, true, true, true, false, false, false, false));
//        }
        
        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeam);
        if (lblTeamName != null) {
        	lblTeamName.setText(teamName);
        }
    }
}
