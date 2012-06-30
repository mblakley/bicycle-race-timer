package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OtherRacesCursorAdapter extends BaseCursorAdapter {

    public OtherRacesCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.control_previous_race_spinner, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("PreviousRacesCursorAdapter", "bindView start");
    		
    		fillData(c, v);
    		
            Log.i("PreviousRacesCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("PreviousRacesCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v) {
    	int raceLocationCol = c.getColumnIndex(RaceLocation.CourseName);
        int raceDateCol = c.getColumnIndex(Race.RaceDate);

        String raceLocation = c.getString(raceLocationCol);
        Long raceDate = c.getLong(raceDateCol);
        
        TextView lblRaceDate = (TextView) v.findViewById(R.id.raceDate1);
        if (lblRaceDate != null) {
        	Time raceDateFromTime = new Time(raceDate);
        	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        	lblRaceDate.setText(formatter.format(raceDateFromTime).toString() + " - " + raceLocation);
        }    	
    }
}
