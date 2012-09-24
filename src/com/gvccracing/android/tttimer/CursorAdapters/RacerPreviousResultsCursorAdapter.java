package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RacerPreviousResultsCursorAdapter extends ResultsCursorAdapter {

    public RacerPreviousResultsCursorAdapter (Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_racer_previous_result, parent, false);

    	}catch(Exception ex){
    		Log.e("RacerPreviousResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	v = super.setupView(v, c);
    	try{
	        int raceDateCol = c.getColumnIndex(Race.RaceDate);
	        int courseNameCol = c.getColumnIndex(RaceLocation.CourseName);
	        
	        Long raceDate = c.getLong(raceDateCol);
	        String courseName = c.getString(courseNameCol);	   
	
	        /**
	         * Next set the name of the entry.
	         */	
	        TextView lblCourseName = (TextView) v.findViewById(R.id.lblCourseName);
	        if (lblCourseName != null) {
	        	lblCourseName.setText(courseName.toString());
	        }
	        
	        TextView lblRaceDate = (TextView) v.findViewById(R.id.lblRaceDate);
	        if (lblRaceDate != null) {
	        	Time raceDateFromTime = new Time(raceDate);
	        	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
	        	lblRaceDate.setText(formatter.format(raceDateFromTime).toString());
	        }
		}catch(Exception ex){
			Log.e("RacerPreviousResultsCursorAdapter", ex.toString());
		}

    }
}

