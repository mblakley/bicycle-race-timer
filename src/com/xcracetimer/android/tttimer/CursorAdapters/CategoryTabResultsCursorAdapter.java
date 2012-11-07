package com.xcracetimer.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoryTabResultsCursorAdapter extends BaseCursorAdapter {

	long categoryToFilter;
    public CategoryTabResultsCursorAdapter (Context context, Cursor c, long categoryToFilter) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.categoryToFilter = categoryToFilter;
    }    

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
    		if(c.getLong(c.getColumnIndex(RacerClubInfo.Category)) == categoryToFilter){
		        final LayoutInflater inflater = LayoutInflater.from(context);
		        v = inflater.inflate(R.layout.row_result, parent, false);
    		}
    	}catch(Exception ex){
    		Log.e("CategoryTabResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		if(c.getLong(c.getColumnIndex(RacerClubInfo.Category)) == categoryToFilter){
	    		int placingCol = 0;
		        int elapsedTimeCol = c.getColumnIndex(RaceResults.ElapsedTime);
		        int firstNameCol = c.getColumnIndex(Racer.FirstName);
		        int lastNameCol = c.getColumnIndex(Racer.LastName);
		        int categoryCol = c.getColumnIndex(RacerClubInfo.Category);
		        
		        Integer placing = c.getInt(placingCol);
		        Long elapsedTime = c.getLong(elapsedTimeCol);
		        String firstName = c.getString(firstNameCol);
		        String lastName = c.getString(lastNameCol);
		        String category = c.getString(categoryCol);
		
		        /**
		         * Next set the name of the entry.
		         */	
		        TextView lblPlacing = (TextView) v.findViewById(R.id.lblPlacing);
		        if (lblPlacing != null) {
		        	lblPlacing.setText(placing.toString());
		        }
		        
		        TextView lblElapsedTime = (TextView) v.findViewById(R.id.lblElapsedTime);
		        if (lblElapsedTime != null) {
		        	Time elapsed = new Time(elapsedTime);
		        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.S");
		        	if(elapsedTime >= 36000000) {
		        		formatter = new SimpleDateFormat("HH:mm:ss.S");
		        	}
		        	else if(elapsedTime >= 3600000) {
		        		formatter = new SimpleDateFormat("H:mm:ss.S");	
		        	}
		        	lblElapsedTime.setText(formatter.format(elapsed).toString());
		        }
		        
		        TextView lblName = (TextView) v.findViewById(R.id.lblName);
		        if (lblName != null) {
		        	lblName.setText(firstName + " " + lastName);
		        }
		        
		        TextView lblCategory = (TextView) v.findViewById(R.id.lblCategory);
		        if (lblCategory != null) {
		        	lblCategory.setText(category);
		        }
    		}
		}catch(Exception ex){
			Log.e("CategoryTabResultsCursorAdapter", ex.toString());
		}

    }

    /*@Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) { return getFilterQueryProvider().runQuery(constraint); }

        StringBuilder buffer = null;
        String[] args = null;
        if (constraint != null) {
            buffer = new StringBuilder();
            buffer.append("UPPER(");
            buffer.append(People.NAME);
            buffer.append(") GLOB ?");
            args = new String[] { constraint.toString().toUpperCase() + "*" };
        }

        return context.getContentResolver().query(People.CONTENT_URI, null,
                buffer == null ? null : buffer.toString(), args, People.NAME + " ASC");
    }*/
}

