package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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

        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
        
        Integer placing = 1;
        
        String teamName = c.getString(teamNameCol);
        TextView lblCategory = (TextView) v.findViewById(R.id.lblCategory);
        TextView lblElapsedTime = (TextView) v.findViewById(R.id.lblElapsedTime);
		if(showOverall){
	        int firstNameCol = c.getColumnIndex(Racer.FirstName);
	        int lastNameCol = c.getColumnIndex(Racer.LastName);
	        
	        String firstName = c.getString(firstNameCol);
	        String lastName = c.getString(lastNameCol);
	        
        	placingCol = c.getColumnIndex(RaceResults.OverallPlacing);
        
	        //int categoryCol = c.getColumnIndex(RacerClubInfo.Category);

            int elapsedTimeCol = c.getColumnIndex(RaceResults.ElapsedTime);
	        Long elapsedTime = c.getLong(elapsedTimeCol);
	        
	        //String category = c.getString(categoryCol);  
	
	        /**
	         * Next set the name of the entry.
	         */	        
	        
	        
	        if (lblElapsedTime != null) {
	        	if(elapsedTime == Long.MAX_VALUE){
	        		lblElapsedTime.setText("DNF");
	        	} else if(elapsedTime == Long.MAX_VALUE - 1){
	        		lblElapsedTime.setText("DNS");
	        	} else{
		        	lblElapsedTime.setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, true, true, false));
	        	}
	        }
	        
	        TextView lblName = (TextView) v.findViewById(R.id.lblName);
	        if (lblName != null) {
	        	lblName.setText(firstName + " " + lastName);
	        }	        

	        placing = c.getInt(placingCol);
        }else{
        	//placingCol = c.getColumnIndex(RaceResults.CategoryPlacing);
        	placing = c.getPosition() + 1;
        	
        	TextView lblName = (TextView) v.findViewById(R.id.lblName);
        	lblName.setVisibility(View.GONE);
        	lblElapsedTime.setVisibility(View.GONE);
        	LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.7f);
        	lblCategory.setLayoutParams(param);


	    	int pointsCol = c.getColumnIndex(RaceResults.Points);
	        Integer points = c.getInt(pointsCol);	 

			TextView lblPoints = (TextView) v.findViewById(R.id.lblPoints);
			
	        param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f);
	        lblPoints.setLayoutParams(param);
        	
			lblPoints.setVisibility(View.VISIBLE);
			if (lblPoints != null) {
				lblPoints.setText(points.toString());
			}
        }

        
		TextView lblPlacing = (TextView) v.findViewById(R.id.lblPlacing);
        if (lblPlacing != null) {
        	lblPlacing.setText(placing.toString());
        }
        
        
        if (lblCategory != null) {
        	lblCategory.setText(teamName);
        }        
        
		return v;
	}
}

