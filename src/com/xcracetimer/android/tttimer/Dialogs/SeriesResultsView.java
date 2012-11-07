package com.xcracetimer.android.tttimer.Dialogs;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.CursorAdapters.SeriesResultsCursorAdapter;
import com.xcracetimer.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.xcracetimer.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

public class SeriesResultsView extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "SeriesResultsView";

	private static final int SERIES_RESULTS_LOADER = 97;
	
	private SeriesResultsCursorAdapter resultsCA;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_series_results, container, false);		
		  
		// Now create a cursor adapter and set it to display using our row
		resultsCA = new SeriesResultsCursorAdapter(getActivity(), null);	
		
		ListView resultsList = (ListView) v.findViewById(R.id.lvResults);
		if(resultsList != null){
			resultsList.setAdapter(resultsCA);
			resultsList.setFocusable(false);
			resultsList.setClickable(false);
			resultsList.setItemsCanFocus(false);
			resultsList.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
//					FragmentManager fm = getActivity().getSupportFragmentManager();
//					RacerPreviousResults showPreviousRaceResultsDialog = new RacerPreviousResults(id);
//					showPreviousRaceResultsDialog.show(fm, RacerPreviousResults.LOG_TAG);
				}
    		});
		}
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.SeriesResults;
	}
	
	@Override
	public void onResume(){
		super.onResume();

		getLoaderManager().restartLoader(SERIES_RESULTS_LOADER, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){			
			case SERIES_RESULTS_LOADER:
				projection = new String[]{RacerClubInfo.getTableName() + "." + RacerClubInfo._ID, Racer.LastName, Racer.FirstName, RacerClubInfo.Category, "SUM(" + DualMeetResults.Team1_Points + ") as " + DualMeetResults.Team1_Points};
				selection = RaceResults.ElapsedTime + " IS NOT NULL AND " + RacerClubInfo.Category + "!=?";
				selectionArgs = new String[]{"G"};
				sortOrder = RacerClubInfo.Category + "," + DualMeetResults.Team1_Points + " DESC," + Racer.LastName;
				loader = new CursorLoader(getActivity(), Uri.withAppendedPath(CheckInViewExclusive.CONTENT_URI, "group by " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + "," + Racer.LastName + "," + Racer.FirstName + "," + RacerClubInfo.Category), projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case SERIES_RESULTS_LOADER:	
					resultsCA.swapCursor(cursor);
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case SERIES_RESULTS_LOADER:
					resultsCA.swapCursor(null);
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}

