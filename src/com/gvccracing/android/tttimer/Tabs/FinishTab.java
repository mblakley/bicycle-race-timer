/**
 * 
 */
package com.gvccracing.android.tttimer.Tabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.AssignTimeTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.RacersToFinishCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnassignedTimeCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnfinishedRacersCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.UnassignedTimesView;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Dialogs.RemoveUnassignedTime;

/**
 * @author Perry
 * 
 */
public class FinishTab extends BaseTab implements View.OnClickListener,	LoaderManager.LoaderCallbacks<Cursor> {

	public static final String FinishTabSpecName = "Finish";

	private static final int RACE_INFO_LOADER_FINISH = 0x177;

	private static final int TEAMS_LOADER_FINISH = 0x115;

	private static final int FINISH_ORDER_LOADER_FINISH = 0x116;

	private static final int SPLITS_LOADER_FINISH = 8958480;
	
	private Long numRaceLaps = 1l;

	private CursorAdapter myRacersCA;
	private CursorAdapter teamsCA;
	private CursorAdapter splitsCA;	

	private ListView teams;
	private ListView myRacers;
	private ListView splits;
	
	/**
	 * 
	 */
	public FinishTab() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tab_finish, container, false);
	
		view.setKeepScreenOn(true);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		teams = (ListView) getView().findViewById(R.id.lvTeams);		
		myRacers = (ListView) getView().findViewById(R.id.lvMyRacers);
		splits = (ListView) getView().findViewById(R.id.lvFinishOrder);

		// Initialize the cursor loader for the unassigned times list
		getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_FINISH, null, this);
		getActivity().getSupportLoaderManager().restartLoader(TEAMS_LOADER_FINISH, null, this);
		getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);
		getActivity().getSupportLoaderManager().restartLoader(SPLITS_LOADER_FINISH, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().getSupportLoaderManager().destroyLoader(TEAMS_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(RACE_INFO_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(FINISH_ORDER_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(SPLITS_LOADER_FINISH);
	}

	@Override
	public String TabSpecName() {
		return FinishTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return FinishTabSpecName;
	}

	public void RacerFinished() {
		try {
			Log.v(LOG_TAG(), "RacerFinished");

			// Create an unassigned time record
			long finishTime = System.currentTimeMillis(); // get the endTime from the timer
			Long race_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1"));
			
			ContentValues content = new ContentValues();
			content.put(UnassignedTimes.FinishTime, finishTime);
			content.put(UnassignedTimes.Race_ID, race_ID);

			getActivity().getContentResolver().insert(UnassignedTimes.CONTENT_URI, content);

		} catch (Exception ex) {
			Log.e(LOG_TAG(), "RacerFinished failed", ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch (id) {			
			case TEAMS_LOADER_FINISH:
				// Create the cursor adapter for the list of unassigned times
				teamsCA = new UnfinishedRacersCursorAdapter(getActivity(), null);//, getParentActivity().timer.GetStartTime(), numRaceLaps);

				if (teams != null) {
		        	
					teams.setAdapter(teamsCA);

					teams.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int pos, long teamInfo_ID) {
								AssignTimeTask task = new AssignTimeTask(FinishTab.this.getActivity());
								task.execute(new Long[] { System.currentTimeMillis(), null, teamInfo_ID, raceStartTime });	
							}
						});
				}
				projection = new String[] { TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName + " as Name" };
				selection = RaceMeetTeams.RaceMeet_ID	+ "=1";// + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + UnassignedTimes.RaceResult_ID + " IS NULL";
				selectionArgs = null;
				sortOrder = TeamInfo.TeamName;
				loader = new CursorLoader(getActivity(), MeetTeamsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case FINISH_ORDER_LOADER_FINISH:
				myRacersCA = new UnfinishedRacersCursorAdapter(getActivity(), null);
				
		        if( myRacers != null){
		        	myRacers.setAdapter(myRacersCA);
		        	
		        	myRacers.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int pos, long raceResult_ID) {
							AssignTimeTask task = new AssignTimeTask(FinishTab.this.getActivity());
							task.execute(new Long[] { System.currentTimeMillis(), raceResult_ID, null, raceStartTime });	
						}
					});
		        }
				projection = new String[] {	RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.FirstName + "||' '||" + Racer.LastName + " as Name" };
				selection = RaceResults.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NOT NULL AND " + RaceResults.EndTime + " IS NULL";
				selectionArgs = null;
				sortOrder = RacerClubInfo.SpeedLevel + " DESC," + Racer.LastName;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case SPLITS_LOADER_FINISH:
				splitsCA = new UnfinishedRacersCursorAdapter(getActivity(), null);
				
		        if( splits != null){
		        	splits.setAdapter(splitsCA);
		        }
				projection = new String[] {	UnassignedTimes.getTableName() + "." + UnassignedTimes._ID + " as _id", TeamInfo.TeamName + " as Name" };
				selection = UnassignedTimes.getTableName() + "." + UnassignedTimes.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NOT NULL";// + " AND " + RaceResults.EndTime + " IS NULL";
				selectionArgs = null;
				sortOrder = UnassignedTimes.getTableName() + "." + UnassignedTimes._ID + " DESC";//RacerClubInfo.SpeedLevel + "," + Racer.LastName;
				loader = new CursorLoader(getActivity(), UnassignedTimesView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_FINISH:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.Gender, Race.Category, Race.NumSplits, Race.RaceStartTime};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}
	
	private long raceStartTime;

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			Log.i(LOG_TAG(),
					"onLoadFinished start: id="	+ Integer.toString(loader.getId()));
			switch (loader.getId()) {
				case TEAMS_LOADER_FINISH:					
					teamsCA.swapCursor(cursor);	
					break;
				case FINISH_ORDER_LOADER_FINISH:					
					myRacersCA.swapCursor(cursor);	
					break;
				case SPLITS_LOADER_FINISH:					
					splitsCA.swapCursor(cursor);	
					break;
				case RACE_INFO_LOADER_FINISH:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						int numLapsCol = cursor.getColumnIndex(Race.NumSplits);
						numRaceLaps = cursor.getLong(numLapsCol);
						
						raceStartTime = cursor.getLong(cursor.getColumnIndex(Race.RaceStartTime));
						
						// Add buttons for each split
					}
					break;
			}
			Log.i(LOG_TAG(),"onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoadFinished error", ex);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try {
			Log.i(LOG_TAG(),
					"onLoaderReset start: id="
							+ Integer.toString(loader.getId()));
			switch (loader.getId()) {
				case TEAMS_LOADER_FINISH:
					teamsCA.swapCursor(null);
					break;
				case FINISH_ORDER_LOADER_FINISH:
					myRacersCA.swapCursor(null);
					break;
				case SPLITS_LOADER_FINISH:
					splitsCA.swapCursor(null);
					break;
				case RACE_INFO_LOADER_FINISH:
					// Do Nothing
					break;
			}
			Log.i(LOG_TAG(),
					"onLoaderReset complete: id="
							+ Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoaderReset error", ex);
		}
	}

	public void onClick(View v) {
		try {
			// Stop and hide the timer
			Intent stopAndHideTimer = new Intent();
			stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
			getActivity().sendBroadcast(stopAndHideTimer);
			
			Intent raceIsFinished = new Intent();
    		raceIsFinished.setAction(Timer.RACE_IS_FINISHED_ACTION);
    		getActivity().sendBroadcast(raceIsFinished);
//			if (Long.parseLong(v.getTag().toString()) == 1) {
//				RacerFinished();
//			}
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "btnNewRacerClick failed", ex);
		}
	}
}
