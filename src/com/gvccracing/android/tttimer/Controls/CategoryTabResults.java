package com.gvccracing.android.tttimer.Controls;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.CursorAdapters.CategoryTabResultsCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class CategoryTabResults extends LinearLayout implements LoaderManager.LoaderCallbacks<Cursor> {

	/**
	 * The tag to use for logging
	 */
	public static final String LOG_TAG = "CategoryTabResults";
	
	private static final int CATEGORY_TAB_LOADER = 0x64;
	
	private CategoryTabResultsCursorAdapter resultsCursorAdapter;

	public CategoryTabResults(Context context, AttributeSet attributes) {
		super(context, attributes);
		View.inflate(context, R.layout.control_category_tab_results, this);

        ((FragmentActivity) getContext()).getSupportLoaderManager().initLoader(CATEGORY_TAB_LOADER, null, this);
	}	
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		switch(id){
			case CATEGORY_TAB_LOADER:
				String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.ElapsedTime, RacerClubInfo.Category, RaceResults.CategoryPlacing, RaceResults.Points};
				String selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.ElapsedTime + " IS NOT NULL";
				String[] selectionArgs = null;
				String sortOrder = RacerClubInfo.Category + "," + RaceResults.ElapsedTime;
				loader = new CursorLoader(getContext(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case CATEGORY_TAB_LOADER:
				    if( cursor != null && cursor.getCount() > 0) {
				    	cursor.moveToFirst();
				    	
				    	//unassigned.swapCursor(cursor);
				    }
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {}

}
