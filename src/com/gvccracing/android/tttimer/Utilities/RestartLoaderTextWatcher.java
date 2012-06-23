package com.gvccracing.android.tttimer.Utilities;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.text.Editable;
import android.text.TextWatcher;

public class RestartLoaderTextWatcher implements TextWatcher {
	
	private LoaderManager loaderManager;
	private int loaderID = -1;
	private LoaderCallbacks<Cursor> callback;
	
	public RestartLoaderTextWatcher(LoaderManager loaderManager, int loaderID, LoaderCallbacks<Cursor> callback){
		this.loaderManager = loaderManager;
		this.loaderID = loaderID;
		this.callback = callback;
	}

	public void afterTextChanged(Editable s) {
        loaderManager.restartLoader(loaderID, null, callback);
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// Do nothing
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Do nothing		
	}

}
