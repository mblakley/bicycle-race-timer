package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultsCursorAdapter extends BaseCursorAdapter {

	boolean showOverall = true;
    public ResultsCursorAdapter (Context context, Cursor c, boolean overall) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        showOverall = overall;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_result, parent, false);

    	}catch(Exception ex){
    		Log.e("ResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		setupView(v, c);
		}catch(Exception ex){
			Log.e("ResultsCursorAdapter", ex.toString());
		}

    }

	public View setupView(View v, Cursor c) {
		int placingCol = 0;
        if(showOverall){
        	placingCol = c.getColumnIndex(RaceResults.OverallPlacing);
        }else{
        	placingCol = c.getColumnIndex(RaceResults.CategoryPlacing);
        }
        int elapsedTimeCol = c.getColumnIndex(RaceResults.ElapsedTime);
        int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);
        int categoryCol = c.getColumnIndex(RacerClubInfo.Category);
    	int pointsCol = c.getColumnIndex(RaceResults.Points);
        
        Integer placing = c.getInt(placingCol);
        Long elapsedTime = c.getLong(elapsedTimeCol);
        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);
        String category = c.getString(categoryCol);
        Integer points = c.getInt(pointsCol);	   

        /**
         * Next set the name of the entry.
         */	
        TextView lblPlacing = (TextView) v.findViewById(R.id.lblPlacing);
        if (lblPlacing != null) {
        	lblPlacing.setText(placing.toString());
        }
        
        TextView lblElapsedTime = (TextView) v.findViewById(R.id.lblElapsedTime);
        if (lblElapsedTime != null) {
        	if(elapsedTime == Long.MAX_VALUE){
        		lblElapsedTime.setText("DNF");
        	} else if(elapsedTime == Long.MAX_VALUE - 1){
        		lblElapsedTime.setText("DNS");
        	} else{
//	        	Time elapsed = new Time(elapsedTime);
//	        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
//	        	if(elapsedTime >= 36000000) {
//	        		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
//	        	}
//	        	else if(elapsedTime >= 3600000) {
//	        		formatter = new SimpleDateFormat("H:mm:ss.SSS");	
//	        	}
//	        	lblElapsedTime.setText(formatter.format(elapsed).toString());
	        	lblElapsedTime.setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, true, true, false));
        	}
        }
        
        TextView lblName = (TextView) v.findViewById(R.id.lblName);
        if (lblName != null) {
        	lblName.setText(firstName + " " + lastName);
        }
        
        TextView lblCategory = (TextView) v.findViewById(R.id.lblCategory);
        if (lblCategory != null) {
        	lblCategory.setText(category);
        }
        
        TextView lblPoints = (TextView) v.findViewById(R.id.lblPoints);
        if (lblPoints != null) {
        	lblPoints.setText(points.toString());
        }
        
		return v;
	}
}

