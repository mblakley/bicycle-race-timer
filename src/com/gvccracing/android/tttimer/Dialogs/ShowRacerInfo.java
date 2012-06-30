package com.gvccracing.android.tttimer.Dialogs;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvccracing.android.tttimer.R;

public class ShowRacerInfo extends BaseDialog implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
	public static final String LOG_TAG = "ShowRacerInfo";
	
	//private Long racerID;
	
	public ShowRacerInfo(long racerID) {
		//this.racerID = racerID;
	}
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_racer_info, container, false);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.viewingMode;
	}
	
	public void onClick(View v) { 
		try{
			super.onClick(v);
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
