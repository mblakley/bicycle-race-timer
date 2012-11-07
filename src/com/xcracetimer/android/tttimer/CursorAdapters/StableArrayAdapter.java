package com.xcracetimer.android.tttimer.CursorAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class StableArrayAdapter extends ArrayAdapter<CharSequence> {

	public StableArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// This is so freakin stupid, but sometimes the spinner comes up with a request for position -1.  Make it just go to zero!
		if(position < 0){
			position = 0;
		}
			
		return super.getView(position, convertView, parent);
	}
}
