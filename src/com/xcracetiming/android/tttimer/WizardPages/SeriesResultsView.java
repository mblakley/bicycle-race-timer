package com.xcracetiming.android.tttimer.WizardPages;

import android.database.Cursor;
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

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.CursorAdapters.SeriesResultsCursorAdapter;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;

public class SeriesResultsView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
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
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){			
			case SERIES_RESULTS_LOADER:
				projection = new String[]{RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID, Racer.LastName, Racer.FirstName, RaceCategory.FullCategoryName, "SUM(" + RaceResults.Points + ") as " + RaceResults.Points};
				selection = RaceResults.ElapsedTime + " IS NOT NULL AND " + RaceCategory.FullCategoryName + "!=?";
				selectionArgs = new String[]{"G"};
				sortOrder = RaceCategory.FullCategoryName + "," + RaceResults.Points + " DESC," + Racer.LastName;
				loader = new CursorLoader(getActivity(), SeriesRaceIndividualResultsView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.GroupBy, "group by " + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + "," + Racer.LastName + "," + Racer.FirstName + "," + RaceCategory.FullCategoryName).build(), projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case SERIES_RESULTS_LOADER:	
					resultsCA.swapCursor(cursor);
					break;
			}
			Log.v(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case SERIES_RESULTS_LOADER:
					resultsCA.swapCursor(null);
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}

