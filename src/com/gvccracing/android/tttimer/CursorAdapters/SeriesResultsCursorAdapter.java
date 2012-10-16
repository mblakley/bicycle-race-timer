package com.gvccracing.android.tttimer.CursorAdapters;

import java.util.HashMap;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SeriesResultsCursorAdapter extends BaseCursorAdapter {
	
	private HashMap<String, Integer> categoryMap = new HashMap<String, Integer>();

    public SeriesResultsCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_series_result, parent, false);

    	}catch(Exception ex){
    		Log.e("SeriesResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		setupView(v, c);
		}catch(Exception ex){
			Log.e("SeriesResultsCursorAdapter", ex.toString());
		}

    }

	public View setupView(View v, Cursor c) {
        int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);
        int categoryCol = c.getColumnIndex(RacerClubInfo.Category);
    	int pointsCol = 0;//c.getColumnIndex(RaceResults.Points);
        
        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);
        String category = c.getString(categoryCol);
        int categoryStartPosition = 0;
    	if(categoryMap.containsKey(category)){
    		categoryStartPosition = categoryMap.get(category);
    	}else{
        	categoryStartPosition = c.getPosition();
        	categoryMap.put(category, categoryStartPosition);
    	}    
        Integer points = c.getInt(pointsCol);        
        Integer placing = (c.getPosition() - categoryStartPosition) + 1;

        /**
         * Next set the name of the entry.
         */	
        TextView lblPlacing = (TextView) v.findViewById(R.id.lblPlacing);
        if (lblPlacing != null) {
        	lblPlacing.setText(placing.toString());
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

