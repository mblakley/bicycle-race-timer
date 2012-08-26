package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerGroupCP.RacerGroup;
import com.gvccracing.android.tttimer.Utilities.Enums.RaceType;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RacersToFinishCursorAdapter extends BaseCursorAdapter {
	
	private RaceType raceType = RaceType.TimeTrial;
	
    public RacersToFinishCursorAdapter (Context context, Cursor c, RaceType raceType) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    	
    	this.raceType = raceType;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_racer_to_finish, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("RacersToFinishCursorAdapter", "bindView start");
    		
    		fillData(c, v);
    		
            Log.i("RacersToFinishCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("RacersToFinishCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v) {
    	int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);

        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(R.id.lblName);
        if (lblName != null) {
        	String name = firstName + " " + lastName;
        	lblName.setText(name);
        }
        
        TextView lblStartOrder = (TextView) v.findViewById(R.id.lblStartOrder);
        if (lblStartOrder != null) {
        	if(raceType == RaceType.TimeTrial || raceType == RaceType.TeamTimeTrial){
                int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
                int startOrder = c.getInt(startOrderCol);
        		lblStartOrder.setText(Integer.toString(startOrder));
        	} else {
        		int raceGroupCol = c.getColumnIndex(RacerGroup.GroupDescription);
        		String raceGroupAbrev = "";
        		if(!c.isNull(raceGroupCol)){
	                String raceGroup = c.getString(raceGroupCol);
	                raceGroupAbrev = raceGroup.length() > 0 ? raceGroup.substring(0,1) : "";
        		}
        		lblStartOrder.setText(raceGroupAbrev);
        	}
        }
    }
}
