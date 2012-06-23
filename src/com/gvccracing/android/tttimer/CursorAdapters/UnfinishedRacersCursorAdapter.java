package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UnfinishedRacersCursorAdapter extends BaseCursorAdapter  {

	private Context context;
	
    public UnfinishedRacersCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = context;
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
    		v = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
    		
    		Cursor c = (Cursor)getItem(position);

    		int firstNameCol = c.getColumnIndex(Racer.FirstName);
	        int lastNameCol = c.getColumnIndex(Racer.LastName);
	        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
	        
	        String firstName = c.getString(firstNameCol);
	        String lastName = c.getString(lastNameCol);
	        Integer startOrder = c.getInt(startOrderCol);	   
	
	        /**
	         * Next set the name of the entry.
	         */		        
	        TextView lblName = (TextView) v.findViewById(android.R.id.text2);
	        if (lblName != null) {
	        	lblName.setText(firstName + " " + lastName);
	        }
	        
	        TextView lblStartOrder = (TextView) v.findViewById(android.R.id.text1);
	        if (lblStartOrder != null) {
	        	lblStartOrder.setText(startOrder.toString());
	        }
    	}catch(Exception ex){
    		Log.e("UnfinishedRacersCursorAdapter", ex.toString());
    	}

        return v;
    }
}

