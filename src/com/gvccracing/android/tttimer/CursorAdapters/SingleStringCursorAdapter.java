package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleStringCursorAdapter extends BaseCursorAdapter  {
	
    public SingleStringCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_single_field, parent, false);

    	}catch(Exception ex){
    		Log.e("SingleFieldCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{	        
	        String name = c.getString(1);	   
	        
	        TextView lblName = (TextView) v.findViewById(R.id.text1);
	        if (lblName != null) {
	        	lblName.setText(name);
	        }
		}catch(Exception ex){
			Log.e("SingleFieldCursorAdapter", ex.toString());
		}
    }
}

