package com.xcracetiming.android.tttimer.CursorAdapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.xcracetiming.android.tttimer.TTTimerTabsActivity;

public class BaseArrayAdapter<T> extends ArrayAdapter<T> {

	private TTTimerTabsActivity context = null;	
	
	public BaseArrayAdapter(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, objects);

        this.context = (TTTimerTabsActivity) context;  
    }
	
	protected TTTimerTabsActivity getParentActivity(){
		return (TTTimerTabsActivity)context;
	}
}
