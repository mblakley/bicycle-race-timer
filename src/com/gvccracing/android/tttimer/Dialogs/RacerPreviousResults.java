package com.gvccracing.android.tttimer.Dialogs;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.Views.RacerPreviousResultsView;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.CursorAdapters.RacerPreviousResultsCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.ResultsCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

public class RacerPreviousResults extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "RacerPreviousResults";

	private static final int ALL_RACE_RESULTS_LOADER = 0x12;
	 
	private long raceResultID;
	
	private ResultsCursorAdapter lvResults;
	
	private TextView txtCoursePR;
	
	private TextView racerCategory;
	
	private TextView currentRaceResult;
	
	private TextView racerName;
	
	public RacerPreviousResults(long raceResultID) {
		this.raceResultID = raceResultID;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_racer_previous_results, container, false);		
		  
		// Now create a cursor adapter and set it to display using our row
		lvResults = new RacerPreviousResultsCursorAdapter(getActivity(), null);	
		
		ListView resultsList = (ListView) v.findViewById(R.id.lvResults);
		if(resultsList != null){
			resultsList.setAdapter(lvResults);
		}
		
		txtCoursePR = (TextView) v.findViewById(R.id.txtPersonalBest);
		racerCategory = (TextView) v.findViewById(R.id.txtRaceCategory);
		currentRaceResult = (TextView) v.findViewById(R.id.txtCurrentResult);
		racerName = (TextView) v.findViewById(R.id.txtRacerName);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.RaceResults;
	}
	
	@Override
	public void onResume(){
		super.onResume();

		getLoaderManager().restartLoader(ALL_RACE_RESULTS_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			super.onClick(v);
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){			
			case ALL_RACE_RESULTS_LOADER:
				projection = new String[]{RaceResults.Instance().getTableName() + "." + RaceResults._ID + " as _id", Race.RaceDate, Racer.FirstName, Racer.LastName, Race.RaceLocation_ID, RaceLocation.CourseName, RaceResults.ElapsedTime, RacerClubInfo.Category, RaceResults.CategoryPlacing, RaceResults.OverallPlacing, RaceResults.Points};
				selection = Racer.Instance().getTableName() + "." + Racer._ID + " in (" +
							SQLiteQueryBuilder.buildQueryString(true, RacerPreviousResultsView.Instance().getTableName(), new String[]{Racer.Instance().getTableName() + "." + Racer._ID},
																RaceResults.Instance().getTableName() + "." + RaceResults._ID + "=" + raceResultID, null, null, RaceResults.Instance().getTableName() + "." + RaceResults._ID, "1") + ")";
				selectionArgs = null;
				sortOrder = RaceResults.Instance().getTableName() + "." + RaceResults._ID;
				loader = new CursorLoader(getActivity(), RacerPreviousResultsView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_RACE_RESULTS_LOADER:	
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						long coursePR = Long.MAX_VALUE;
						long currentCourseID = -1;
						String raceCategory = "";
						// Get some info about current result
						do{
							long curRaceResultID = cursor.getLong(cursor.getColumnIndex(RaceResults._ID));
							if(curRaceResultID == raceResultID){
								long elapsedTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
								if (currentRaceResult != null) {
						        	currentRaceResult.setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
						        }
								currentCourseID = cursor.getLong(cursor.getColumnIndex(Race.RaceLocation_ID));
								
								int firstNameCol = cursor.getColumnIndex(Racer.FirstName);
								int lastNameCol = cursor.getColumnIndex(Racer.LastName);
								racerName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));
								
								raceCategory = cursor.getString(cursor.getColumnIndex(RacerClubInfo.Category));
								racerCategory.setText(raceCategory);
								break;
							}
						}while(cursor.moveToNext());
						
						// Find the course PR
						cursor.moveToFirst();
						do{
							long elapsedTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
							long courseID = cursor.getLong(cursor.getColumnIndex(Race.RaceLocation_ID));
							if(courseID == currentCourseID && elapsedTime < coursePR){
								coursePR = elapsedTime;
							}				
						}while(cursor.moveToNext());
						
						// Set the course PR
						txtCoursePR.setText(TimeFormatter.Format(coursePR, true, true, true, true, true, false, false, false));
						
						lvResults.swapCursor(cursor);
					}
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
				case ALL_RACE_RESULTS_LOADER:
					lvResults.swapCursor(null);
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

