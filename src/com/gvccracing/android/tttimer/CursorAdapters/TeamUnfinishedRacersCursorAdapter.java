package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeamUnfinishedRacersCursorAdapter extends BaseCursorAdapter  {

	private Context context;
	private long totalLaps;
	
    public TeamUnfinishedRacersCursorAdapter (Context context, Cursor c, long totalLaps) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = context;
        this.totalLaps = totalLaps;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

    	}catch(Exception ex){
    		Log.e("UnfinishedRacersCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
	        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
	        
	        Integer startOrder = c.getInt(startOrderCol);	   
	        
	        TextView lblStartOrder = (TextView) v.findViewById(android.R.id.text1);
	        if (lblStartOrder != null) {
	        	lblStartOrder.setText(startOrder.toString());
	        }
		}catch(Exception ex){
			Log.e("UnfinishedRacersCursorAdapter", ex.toString());
		}
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	View v = null;
    	try{	  
    		final LayoutInflater inflater = LayoutInflater.from(context);
    		v = inflater.inflate(com.gvccracing.android.tttimer.R.layout.row_unfinished_racer, parent, false);
    		
    		Cursor c = (Cursor)getItem(position);

    		int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
	        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
	        int lapsCompletedCol = c.getColumnIndex("LapsCompleted");
	        
	        String teamName = c.getString(teamNameCol);
	        Long startOrder = c.getLong(startOrderCol);	   
	        Long lapsCompleted = c.getLong(lapsCompletedCol);
	
	        /**
	         * Next set the name of the entry.
	         */		        
	        TextView lblName = (TextView) v.findViewById(com.gvccracing.android.tttimer.R.id.lblName);
	        if (lblName != null) {
	        	lblName.setText(teamName);
	        }
	        
	        TextView lblStartOrder = (TextView) v.findViewById(com.gvccracing.android.tttimer.R.id.lblStartPosition);
	        if (lblStartOrder != null) {
	        	lblStartOrder.setText(startOrder.toString());
	        }
	        
	        TextView lblLaps = (TextView) v.findViewById(com.gvccracing.android.tttimer.R.id.lblLaps);
	        if (lblLaps != null) {
	        	lblLaps.setText(Long.toString(lapsCompleted + 1) + "/" + Long.toString(totalLaps));
	        }
    	}catch(Exception ex){
    		Log.e("UnfinishedRacersCursorAdapter", ex.toString());
    	}

        return v;
    }
}

