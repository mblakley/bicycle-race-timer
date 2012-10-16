/**
 * 
 */
package com.gvccracing.android.tttimer.Tabs;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.AssignTimeTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.SplitTimesCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.TeamFinishCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnfinishedRacersCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
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
	
	private Long numRaceLaps = 1l;

	private CursorAdapter myRacersCA;
	private CursorAdapter teamsCA;
	private CursorAdapter splitsCA;

	private ListView teams;
	private ListView myRacers;
	private ListView splits;	
	
	//private TabHost tabHost;
	private LinearLayout llSplitButtons;
	
	private long teamInfo_ID;
	private long overallPlacing = 1;
	
	private long currentRaceLap = 1;
	
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
//		tabHost = (TabHost)getView().findViewById(android.R.id.tabhost);  // The activity TabHost
//	    tabHost.setup();	    
//	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
	    
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

//	public void RacerFinished() {
//		try {
//			Log.v(LOG_TAG(), "RacerFinished");
//
//			// Create an unassigned time record
//			long finishTime = System.currentTimeMillis(); // get the endTime from the timer
//			Long race_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1"));
//			
//			ContentValues content = new ContentValues();
//			content.put(RaceLaps.FinishTime, finishTime);
//			content.put(RaceLaps.Race_ID, race_ID);
//
//			RaceLaps.Create(getActivity(), raceResult_ID, teamInfo_ID, lapNumber, finishTime, raceFinishTime, elapsedTime, race_ID)
//			//UnassignedTimes.Create(getActivity(), race_ID, finishTime, null, null, null, null);
//			//getActivity().getContentResolver().insert(UnassignedTimes.CONTENT_URI, content);
//
//		} catch (Exception ex) {
//			Log.e(LOG_TAG(), "RacerFinished failed", ex);
//		}
//	}

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
				teamsCA = new TeamFinishCursorAdapter(getActivity(), null);				

				if (teams != null) {
		        	
					teams.setAdapter(teamsCA);

					teams.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int pos, long teamInfo_ID) {
								overallPlacing++;
								//TextView lblNumFinished = (TextView)((LinearLayout)((LinearLayout)v).getChildAt(1)).getChildAt(1);
								//TextView teamName = ((TextView)((LinearLayout)v).getChildAt(0));
								//String name = (String) teamName.getText();
								//lblNumFinished.setText(name + "9");
								//name = (String) teamName.getText();
								//temp.bringToFront();
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
						
						// Add a button for each split
						for(long i = 0; i < numRaceLaps; i++){
							Button btnSplit = new Button(getActivity());
							LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, (0.9f/((float)numRaceLaps + 1.0f)));
							btnSplit.setPadding(0, 0, 0, 0);
							btnSplit.setTag(i + 1);
							btnSplit.setWidth(0);
							
							if(i == numRaceLaps - 1){
								btnSplit.setText("Finish");
								//btnSplit.setColorFilter(Color.argb(0, 155, 155, 155));
								//btnSplit.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
								//btnSplit.setBackgroundColor(getResources().getColor(R.color.normal_bg_gray));
								//tabHost.addTab(tabHost.newTabSpec("Finish").setIndicator("Finish").setContent(this));
							}else{
								btnSplit.setText("Split " + Long.toString(i + 1));
								//tabHost.addTab(tabHost.newTabSpec("Split" + Integer.toString(i)).setIndicator("Split " + Integer.toString(i)).setContent(this));
							}
							btnSplit.setOnClickListener(this);
							
							llSplitButtons.addView(btnSplit, (int)i, params);
						}
						
						Button btnSplit = new Button(getActivity());
						LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, (0.9f/((float)numRaceLaps + 1.0f)));
						btnSplit.setPadding(0, 0, 0, 0);
						btnSplit.setTag(numRaceLaps + 1);
						btnSplit.setWidth(0);
						
						btnSplit.setText("Done");
						
						btnSplit.setOnClickListener(this);
						
						llSplitButtons.addView(btnSplit, Integer.parseInt(Long.toString(numRaceLaps)), params);
					}
					break;
				case CURRENT_LAP_LOADER_FINISH:					
					cursor.moveToFirst();
					currentRaceLap = cursor.getLong(0);
					
					getActivity().getSupportLoaderManager().destroyLoader(CURRENT_LAP_LOADER_FINISH);
					
					// Restart the loaders
					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_FINISH, null, this);
					getActivity().getSupportLoaderManager().restartLoader(TEAMS_LOADER_FINISH, null, this);
					getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);
					getActivity().getSupportLoaderManager().restartLoader(SPLITS_LOADER_FINISH, null, this);
					break;
				case CURRENT_PLACING_LOADER_FINISH:					
					cursor.moveToFirst();
					overallPlacing = cursor.getLong(0);
					
					getActivity().getSupportLoaderManager().destroyLoader(CURRENT_PLACING_LOADER_FINISH);
					
					// Restart the loaders
					getActivity().getSupportLoaderManager().restartLoader(CURRENT_LAP_LOADER_FINISH, null, this);
					break;
			}
			Log.i(LOG_TAG(),"onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "onLoadFinished error", ex);
		}
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
			long raceLap = Long.parseLong(v.getTag().toString());
			if(raceLap > 0 && currentRaceLap != raceLap){
				currentRaceLap = raceLap;
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
		    		Calculations.CalculateCategoryPlacings(getActivity(), Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1")));
				}
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "btnNewRacerClick failed", ex);
		}
	}

//	public View createTabContent(String tag) {
//		final TextView tv = new TextView(getActivity());
//        tv.setText("Content for tab with tag " + tag);
//        return tv;
//	}
}
