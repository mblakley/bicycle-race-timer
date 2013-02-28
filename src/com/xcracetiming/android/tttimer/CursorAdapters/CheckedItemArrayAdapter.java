package com.xcracetiming.android.tttimer.CursorAdapters;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.CheckedItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckedItemArrayAdapter extends BaseArrayAdapter<CheckedItem> { 
	
    public CheckedItemArrayAdapter(Context context, int textViewResourceId, CheckedItem[] checkedItems) {
		super(context, textViewResourceId, checkedItems);
	}   
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("CheckedItemArrayAdapter", "bindView start");
    	View v = convertView;
    	
    	if(v == null){
    		// Didn't previously exist, so create it
	    	try{	
		        final LayoutInflater inflater = LayoutInflater.from(getContext());
		        v = inflater.inflate(R.layout.row_checked_array, parent, false);
	    	}catch(Exception ex){
	    		Log.e("CheckedItemArrayAdapter", "Error in getView()", ex);
	    	}
    	} else{

	        long id = this.getItem(position)._ID;
	        String text = this.getItem(position).Text;
	        boolean isChecked = this.getItem(position).IsChecked;
	        
	        /**
	         * Next set the name of the entry.
	         */     
	        TextView lblText = (TextView) v.findViewById(R.id.lblText);
	        if (lblText != null) {
	        	lblText.setText(text);
	        }

	        CheckBox chkAddRemove = (CheckBox) v.findViewById(R.id.chkAddRemove);
	        if(chkAddRemove != null){
	        	chkAddRemove.setTag(id);
	        	
	        	// Check if the item was previously checked	        	
        		chkAddRemove.setChecked(isChecked);	        	
	        }
    	}
        Log.v("CheckedItemArrayAdapter", "getView complete");
        
        return v;
    }
}
