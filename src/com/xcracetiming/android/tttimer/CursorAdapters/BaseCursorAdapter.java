package com.xcracetiming.android.tttimer.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.xcracetiming.android.tttimer.TTTimerTabsActivity;

public class BaseCursorAdapter extends CursorAdapter {

	private TTTimerTabsActivity context = null;
	
	public BaseCursorAdapter(Context context, Cursor c, int flagRegisterContentObserver) {
		super(context, c, flagRegisterContentObserver);
		
		this.context = (TTTimerTabsActivity) context;
	}
	
	protected TTTimerTabsActivity getParentActivity(){
		return (TTTimerTabsActivity)context;
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		return;
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return null;
	}
}
