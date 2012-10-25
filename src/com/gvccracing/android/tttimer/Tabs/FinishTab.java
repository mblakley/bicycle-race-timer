/**
 * 
 */
package com.gvccracing.android.tttimer.Tabs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.AssignTimeTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.SplitTimesCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.TeamFinishCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnfinishedRacersCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceResultsLapsView;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsRacerViewCP.RaceResultsRacerView;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Utilities.Calculations;

/**
 * @author Perry, Mark
 * 
 */
public class FinishTab extends BaseTab implements View.OnClickListener,	LoaderManager.LoaderCallbacks<Cursor>/*, TabHost.TabContentFactory*/ {

	public static final String FinishTabSpecName = "Finish";

	private static final int RACE_INFO_LOADER_FINISH = 0x177;

	private static final int TEAMS_LOADER_FINISH = 0x115;

	private static final int FINISH_ORDER_LOADER_FINISH = 0x116;

	private static final int SPLITS_LOADER_FINISH = 8958480;

	private static final int CURRENT_LAP_LOADER_FINISH = 99898;

	private static final int CURRENT_PLACING_LOADER_FINISH = 5543433;

	private static final int TEAM_NUM_FINISHERS_LOADER = 776332;

	private static final int TEAM_POINTS_LOADER = 1000098;
	
	private Long numRaceLaps = 1l;

	private CursorAdapter myRacersCA;
	private TeamFinishCursorAdapter teamsCA;
	private CursorAdapter splitsCA;

	private ListView teams;
	private ListView myRacers;
	private ListView splits;	
	
	private LinearLayout llSplitButtons;
	
	private long teamInfo_ID;
	private long overallPlacing = 1;
	
	private long currentRaceLap = 1;
	
	private Hashtable<Long, Long> teamFinishers = new Hashtable<Long, Long>();
	
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
		llSplitButtons = (LinearLayout) getView().findViewById(R.id.llSplitButtons);
		
		splits = (ListView) getView().findViewById(R.id.lvSplits);
	    
		teamInfo_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_TeamID_Name, "-1"));

		// Initialize the cursor loader for the unassigned times list
		getActivity().getSupportLoaderManager().restartLoader(CURRENT_PLACING_LOADER_FINISH, null, this);
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
				teamsCA = new TeamFinishCursorAdapter(getActivity(), null, teamFinishers);				

				if (teams != null) {
		        	
					teams.setAdapter(teamsCA);

					teams.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int pos, long teamInfo_ID) {
								overallPlacing++;
								
								teamsCA.addToNumFinished(teamInfo_ID);
								
								AssignTimeTask task = new AssignTimeTask(FinishTab.this.getActivity());
								task.execute(new Long[] { System.currentTimeMillis(), null, teamInfo_ID, raceStartTime, currentRaceLap, numRaceLaps, overallPlacing });	
							}
						});
				}
				projection = new String[] { TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName };
				selection = RaceMeetTeams.RaceMeet_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceMeet_ID_Name);
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
							overallPlacing++;
							
							teamsCA.addToNumFinished(teamInfo_ID);
							teamsCA.notifyDataSetChanged();
							
							AssignTimeTask task = new AssignTimeTask(FinishTab.this.getActivity());
							task.execute(new Long[] { System.currentTimeMillis(), raceResult_ID, teamInfo_ID, raceStartTime, currentRaceLap, numRaceLaps, overallPlacing });	
						}
					});
		        }
				projection = new String[] {	RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.FirstName + "||' '||" + Racer.LastName + " as Name" };
				selection = RaceResults.getTableName() + "." + RaceResults.Removed + "='false' AND " + RaceResults.getTableName() + "." + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_TeamID_Name) + " AND NOT EXISTS (Select " + RaceLaps._ID + " FROM " + RaceLaps.getTableName() + " t1 WHERE t1." + RaceLaps.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND t1." + RaceLaps.LapNumber + "=? AND t1." + RaceLaps.RaceResult_ID + "=" + RaceResults.getTableName() + "." + RaceResults._ID + ")";
				selectionArgs = new String[]{Long.toString(currentRaceLap)};
				sortOrder = RacerClubInfo.SpeedLevel + " DESC," + Racer.FirstName;
				loader = new CursorLoader(getActivity(), RaceResultsRacerView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case SPLITS_LOADER_FINISH:
				splitsCA = new SplitTimesCursorAdapter(getActivity(), null);
				
		        if( splits != null){
		        	splits.setAdapter(splitsCA);
		        }
				projection = new String[] {	RaceLaps.getTableName() + "." + RaceLaps._ID + " as _id", TeamInfo.TeamName, RaceLaps.getTableName() + "." + RaceLaps.ElapsedTime };
				selection = RaceLaps.getTableName() + "." + RaceLaps.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceLaps.getTableName() + "." + RaceLaps.ElapsedTime + " IS NOT NULL AND " + RaceLaps.LapNumber + "=?";
				selectionArgs = new String[]{Long.toString(currentRaceLap)};
				sortOrder = RaceLaps.getTableName() + "." + RaceLaps.ElapsedTime + " DESC";
				loader = new CursorLoader(getActivity(), RaceResultsLapsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_NUM_FINISHERS_LOADER:
				projection = new String[]{RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID, "COUNT(" + RaceLaps._ID + ") as NumFinishers"};
				selection = RaceLaps.getTableName() + "." + RaceLaps.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceLaps.LapNumber + "=?";
				selectionArgs = new String[]{Long.toString(currentRaceLap)};
				sortOrder = RaceLaps.getTableName() + "." + RaceLaps.TeamInfo_ID;
				loader = new CursorLoader(getActivity(), Uri.withAppendedPath(RaceLaps.CONTENT_URI, "group by " + RaceLaps.TeamInfo_ID), projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_POINTS_LOADER:
				projection = new String[]{DualMeetResults.getTableName() + "." + DualMeetResults.Team2_TeamInfo_ID + " as _id", DualMeetResults.Team2_Points, DualMeetResults.Team1_Points};
				selection = DualMeetResults.getTableName() + "." + DualMeetResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + DualMeetResults.Team1_TeamInfo_ID + "=?";
				selectionArgs = new String[]{Long.toString(teamInfo_ID)};
				sortOrder = DualMeetResults.getTableName() + "." + DualMeetResults.Team2_TeamInfo_ID;
				loader = new CursorLoader(getActivity(), DualMeetResults.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_FINISH:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.Gender, Race.Category, Race.NumSplits, Race.RaceStartTime};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_LAP_LOADER_FINISH:
				projection = new String[]{"MAX(" + RaceLaps.LapNumber + ")"};
				selection = RaceLaps.getTableName() + "." + RaceLaps.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = null;
				loader = new CursorLoader(getActivity(), RaceLaps.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_PLACING_LOADER_FINISH:
				projection = new String[]{"MAX(" + RaceResults.OverallPlacing + ")"};
				selection = RaceResults.getTableName() + "." + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.OverallPlacing + ">0";
				selectionArgs = null;
				sortOrder = null;
				loader = new CursorLoader(getActivity(), RaceResults.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}
	
	private long raceStartTime;

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			Log.i(LOG_TAG(), "onLoadFinished start: id="	+ Integer.toString(loader.getId()));
			cursor.moveToFirst();
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
						// Set up the tab based on the race information
						int numLapsCol = cursor.getColumnIndex(Race.NumSplits);
						numRaceLaps = cursor.getLong(numLapsCol);
						
						raceStartTime = cursor.getLong(cursor.getColumnIndex(Race.RaceStartTime));
						String tabText;
						
						boolean foundSelectedRaceLap = false;						
						
						// Add a button for each split
						for(long i = 0; i < numRaceLaps; i++){							
							if(i == numRaceLaps - 1){
								tabText = "Finish";
							}else{
								tabText = "Split " + Long.toString(i + 1);
							}
							View btnSplit = createTabView(tabText);
							
							LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, (0.9f/((float)numRaceLaps + 1.0f)));
							btnSplit.setTag(i + 1);						
							
							btnSplit.setOnClickListener(this);
							
							llSplitButtons.addView(btnSplit, (int)i, params);
							
							if(currentRaceLap == i + 1){
								btnSplit.setSelected(true);
								foundSelectedRaceLap = true;
							}
						}
						
						tabText = "Done";
						View btnSplit = createTabView(tabText);
						
						LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, (0.9f/((float)numRaceLaps + 1.0f)));
						btnSplit.setTag(numRaceLaps + 1);						
						
						btnSplit.setOnClickListener(this);
						
						llSplitButtons.addView(btnSplit, Integer.parseInt(Long.toString(numRaceLaps)), params);
						if(!foundSelectedRaceLap){
							btnSplit.setSelected(true);
						}
					}
					break;
				case TEAM_NUM_FINISHERS_LOADER:										
					teamFinishers.clear();
					
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						
						do{
							teamFinishers.put(cursor.getLong(cursor.getColumnIndex(RaceLaps.TeamInfo_ID)), cursor.getLong(cursor.getColumnIndex("NumFinishers")));
						} while(cursor.moveToNext());
						
						getActivity().getSupportLoaderManager().destroyLoader(TEAM_NUM_FINISHERS_LOADER);
					}
					// Restart the team loader
					getActivity().getSupportLoaderManager().restartLoader(TEAMS_LOADER_FINISH, null, this);
					break;
				case CURRENT_LAP_LOADER_FINISH:										
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						currentRaceLap = cursor.getLong(0);
						
						getActivity().getSupportLoaderManager().destroyLoader(CURRENT_LAP_LOADER_FINISH);
					}
					// Restart the loaders
					getActivity().getSupportLoaderManager().restartLoader(TEAM_NUM_FINISHERS_LOADER, null, this);
					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_FINISH, null, this);
					getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);
					getActivity().getSupportLoaderManager().restartLoader(SPLITS_LOADER_FINISH, null, this);
					break;
				case CURRENT_PLACING_LOADER_FINISH:		
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						overallPlacing = cursor.getLong(0);
						
						getActivity().getSupportLoaderManager().destroyLoader(CURRENT_PLACING_LOADER_FINISH);
					}
					// Restart the loaders
					getActivity().getSupportLoaderManager().restartLoader(CURRENT_LAP_LOADER_FINISH, null, this);
					break;
			}
			Log.i(LOG_TAG(),"onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoadFinished error", ex);
		}
	}
	
	private View createTabView(final String text) {
	    View view = LayoutInflater.from(getActivity()).inflate(R.layout.tabs_bg, null);	
	    TextView tv = (TextView) view.findViewById(R.id.tabsText);	
	    tv.setText(text);	
	    return view;	
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try {
			Log.i(LOG_TAG(), "onLoaderReset start: id="	+ Integer.toString(loader.getId()));
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
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoaderReset error", ex);
		}
	}

	public void onClick(View v) {
		try {
			// De-select all of the buttons first
			for(int i = 0; i < llSplitButtons.getChildCount(); i++){
				llSplitButtons.getChildAt(i).setSelected(false);
			}
			// Set the clicked button as selected
			v.setSelected(true);
			long raceLap = Long.parseLong(v.getTag().toString());
			if(raceLap > 0 && currentRaceLap != raceLap){
				// TODO: Set the overall placing to the number of results for the selected lap
				
				currentRaceLap = raceLap;
				getActivity().getSupportLoaderManager().restartLoader(TEAM_NUM_FINISHERS_LOADER, null, this);
				getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);
				getActivity().getSupportLoaderManager().restartLoader(SPLITS_LOADER_FINISH, null, this);
				
				if(currentRaceLap == numRaceLaps + 1){
					// Stop and hide the timer
					Intent stopAndHideTimer = new Intent();
					stopAndHideTimer.setAction(Timer.STOP_AND_HIDE_TIMER_ACTION);
					getActivity().sendBroadcast(stopAndHideTimer);
					
					Intent raceIsFinished = new Intent();
		    		raceIsFinished.setAction(Timer.RACE_IS_FINISHED_ACTION);
		    		getActivity().sendBroadcast(raceIsFinished);
		    		
		    		// Calculate the dual meet results (points vs overall list)
		    		Calculations.CalculateCategoryPlacings(getActivity(), Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1")), teamInfo_ID);
				}
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "btnNewRacerClick failed", ex);
		}
	}
}
