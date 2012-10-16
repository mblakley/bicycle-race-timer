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
	
	private Context context;
	
    public SingleStringCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = context;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
		// This is so freakin stupid, but sometimes the spinner comes up with a request for position -1.  Make it just go to zero!
		if(position < 0){
			position = 0;
		}
    	View v = super.getView(position, convertView, parent);
    	return v;
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
//	        String name = c.getString(1);	   
//	        
//	        TextView lblName = (TextView) v.findViewById(R.id.text1);
//	        if (lblName != null) {
//	        	lblName.setText(name);
//	        }
    		fillData(c,v);
		}catch(Exception ex){
			Log.e("SingleFieldCursorAdapter", ex.toString());
		}
    }
    
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//    	View v = null;
//    	try{	  
//    		final LayoutInflater inflater = LayoutInflater.from(context);
//    		v = inflater.inflate(R.layout.control_simple_spinner_dropdown, parent, false);
//    		
//    		Cursor c = (Cursor)getItem(position);
//
//    		String name = c.getString(1);	   
//            
//            TextView lblName = (TextView) v.findViewById(android.R.id.text1);
//            if (lblName != null) {
//            	lblName.setText(name);
//            }
//    	}catch(Exception ex){
//    		Log.e("UnfinishedRacersCursorAdapter", ex.toString());
//    	}
//
//        return v;
//    }
    
    private View fillData(Cursor c, View v) {
    	String name = c.getString(1);	   
        
        TextView lblName = (TextView) v.findViewById(R.id.text1);
        if (lblName != null) {
        	lblName.setText(name);
        }
        
        return v;
    }
}

