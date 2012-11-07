package com.xcracetimer.android.tttimer.CursorAdapters;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeamRacerCursorAdapter extends BaseCursorAdapter {

	private Context context;
    public TeamRacerCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("TeamRacerCursorAdapter", "bindView start");
    		
    		fillData(c, v, false);
    		
            Log.i("TeamRacerCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("TeamRacerCursorAdapter", ex.toString());
		}
    }
    
    private View fillData(Cursor c, View v, boolean fillCategory) {
    	int firstNameCol = c.getColumnIndex(Racer.FirstName);
        int lastNameCol = c.getColumnIndex(Racer.LastName);
        int categoryCol = c.getColumnIndex(RacerClubInfo.Category);

        String firstName = c.getString(firstNameCol);
        String lastName = c.getString(lastNameCol);
        String category = c.getString(categoryCol);

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(android.R.id.text1);
        if (lblName != null) {
        	String name = firstName + " " + lastName;
        	if(fillCategory){
        		name += "     " + category;
        	}
        	lblName.setText(name);
        } 
        
        return v;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	View v = null;
    	try{	  
    		final LayoutInflater inflater = LayoutInflater.from(context);
    		v = inflater.inflate(R.layout.control_simple_spinner_dropdown, parent, false);
    		
    		Cursor c = (Cursor)getItem(position);

    		v = fillData(c, v, true);
    	}catch(Exception ex){
    		Log.e("UnfinishedRacersCursorAdapter", ex.toString());
    	}

        return v;
    }
}
