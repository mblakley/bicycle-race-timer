package com.xcracetimer.android.tttimer.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;

public class StableSimpleCursorAdapter extends SimpleCursorAdapter {
	
	public StableSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
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
