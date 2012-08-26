/**
 * 
 */
package com.gvccracing.android.tttimer.Tabs;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.AsyncTasks.AssignLapTimeTask;
import com.gvccracing.android.tttimer.AsyncTasks.AssignTimeTask;
import com.gvccracing.android.tttimer.CursorAdapters.RacersToFinishCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.TeamsToFinishCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.UnassignedTimeCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.RacersAndGroups;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.TeamLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerFinishGroupCP.RacerFinishGroup;
import com.gvccracing.android.tttimer.DataAccess.RacerGroupCP.RacerGroup;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Dialogs.RemoveUnassignedTime;
import com.gvccracing.android.tttimer.Utilities.Enums;
import com.gvccracing.android.tttimer.Utilities.Enums.RaceType;

/**
 * @author Perry
 * 
 */
public class FinishTab extends BaseTab implements View.OnClickListener,	LoaderManager.LoaderCallbacks<Cursor> {

	public static final String FinishTabSpecName = "Finish";

	private static final int TEAM_FINISH_ORDER_LOADER = 0x34;

	private static final int TEAM_UNASSIGNED_TIMES_LOADER = 0x36;

	private static final int RACE_INFO_LOADER_FINISH = 0x177;

	private static final int UNASSIGNED_TIMES_LOADER_FINISH = 0x115;

	private static final int FINISH_ORDER_LOADER_FINISH = 0x116;

	private static final int RACER_GROUPS_LOADER = 1121;
	
	private ArrayAdapter<String> groupsAdapter;
	
	private Hashtable<Long, String> groupLookup;
	
	private Long raceTypeID = 0l;
	private Long numRaceLaps = 1l;

	private CursorAdapter finishersCA;
	private CursorAdapter unassignedCA;

	private Button btnRacerFinished;
	private Button btnBreakaway;
	private Button btnChase;
	private Button btnPeleton;
	private Button btnOffTheBack;
	private TableLayout tblGroupButtons;
	private LinearLayout llFinishButtons;
	private LinearLayout llFinishLists;
	private ListView finishOrderList;
	private ListView unassignedTimes;
	
	/**
	 * 
	 */
	public FinishTab() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tab_finish, container, false);

		btnRacerFinished = (Button) view.findViewById(R.id.btnRacerFinished);		
		btnRacerFinished.setOnClickListener(this);		

		btnBreakaway = (Button) view.findViewById(R.id.btnBreakaway);		
		btnBreakaway.setOnClickListener(this);

		btnChase = (Button) view.findViewById(R.id.btnChase);		
		btnChase.setOnClickListener(this);

		btnPeleton = (Button) view.findViewById(R.id.btnPeleton);		
		btnPeleton.setOnClickListener(this);

		btnOffTheBack = (Button) view.findViewById(R.id.btnOffTheBack);		
		btnOffTheBack.setOnClickListener(this);
		
		tblGroupButtons = (TableLayout) view.findViewById(R.id.tblGroupButtons);
		
		llFinishButtons = (LinearLayout) view.findViewById(R.id.llFinishButtons);
		llFinishLists = (LinearLayout) view.findViewById(R.id.llFinishLists);
		
		btnRacerFinished.setEnabled(false);

		view.setKeepScreenOn(true);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		unassignedTimes = (ListView) getView().findViewById(R.id.svUnassignedTimes);		
		finishOrderList = (ListView) getView().findViewById(R.id.svRacersToFinish);

		// Initialize the cursor loader for the unassigned times list
		getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_FINISH, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().getSupportLoaderManager().destroyLoader(UNASSIGNED_TIMES_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(RACE_INFO_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(TEAM_UNASSIGNED_TIMES_LOADER);
		getActivity().getSupportLoaderManager().destroyLoader(FINISH_ORDER_LOADER_FINISH);
		getActivity().getSupportLoaderManager().destroyLoader(TEAM_FINISH_ORDER_LOADER);
		getActivity().getSupportLoaderManager().destroyLoader(RACER_GROUPS_LOADER);
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
			case RACER_GROUPS_LOADER:
				projection = new String[] { RacerGroup._ID, RacerGroup.GroupDescription };
				selection = null;
				selectionArgs = null;
				sortOrder = RacerGroup._ID;
				loader = new CursorLoader(getActivity(), RacerGroup.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_UNASSIGNED_TIMES_LOADER:			
			case UNASSIGNED_TIMES_LOADER_FINISH:
				// Create the cursor adapter for the list of unassigned times
				unassignedCA = new UnassignedTimeCursorAdapter(getActivity(), null, getParentActivity().timer.GetStartTime(), numRaceLaps);

				if (unassignedTimes != null) {
		        	
					unassignedTimes.setAdapter(unassignedCA);

					unassignedTimes.setOnItemLongClickListener(new OnItemLongClickListener() {
							public boolean onItemLongClick(AdapterView<?> arg0,	View v, int pos, long id) {
								RemoveUnassignedTime removeUnassignedTimeDialog = new RemoveUnassignedTime(id);
								FragmentManager fm = getActivity().getSupportFragmentManager();
								removeUnassignedTimeDialog.show(fm,	RemoveUnassignedTime.LOG_TAG);
								return false;
							}
						});
				}
				projection = new String[] { UnassignedTimes._ID, UnassignedTimes.FinishTime };
				selection = UnassignedTimes.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + UnassignedTimes.RaceResult_ID + " IS NULL";
				selectionArgs = null;
				sortOrder = UnassignedTimes.FinishTime;
				loader = new CursorLoader(getActivity(), UnassignedTimes.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case FINISH_ORDER_LOADER_FINISH:
				finishersCA = new RacersToFinishCursorAdapter(getActivity(), null, RaceType.valueOf(RaceType.DescriptionFromRaceTypeID(raceTypeID)));
				
		        if( finishOrderList != null){
		        	finishOrderList.setAdapter(finishersCA);
		        	
		        	finishOrderList.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int pos, long raceResult_ID) {
							if(raceTypeID == Enums.RaceType.Criterium.ID()){
								// Add this racer to the selected race group
							}else{
								Cursor unassignedCursor = unassignedCA.getCursor();
								if(unassignedCursor != null && unassignedCursor.getCount() > 0){
									unassignedCursor.moveToPosition(unassignedTimes.getCheckedItemPosition());
									long unassignedID = unassignedCursor.getLong(unassignedCursor.getColumnIndex(UnassignedTimes._ID));
									AssignTimeTask task = new AssignTimeTask(FinishTab.this.getActivity());
									task.execute(new Long[] { unassignedID, raceResult_ID });	
								}
							}
						}
					});
		        }
		        if(raceTypeID == Enums.RaceType.Criterium.ID()){
		        	projection = new String[] {	RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, "MAX(" + RacerFinishGroup.RacerGroup_ID + ") as " + RacerGroup.GroupDescription };
					selection = RaceResults.getTableName() + "." + RaceResults.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NOT NULL" + " AND " + RaceResults.EndTime + " IS NULL AND " + RacerClubInfo.Category + "!=?";
					selectionArgs = new String[]{"G"};
					sortOrder = RaceResults.StartOrder;
					loader = new CursorLoader(getActivity(), Uri.withAppendedPath(RacersAndGroups.CONTENT_URI, "group by " + RaceResults.getTableName() + "." + RaceResults._ID + "," + Racer.LastName + "," + Racer.FirstName), projection, selection, selectionArgs, sortOrder);
		        } else{
		        	projection = new String[] {	RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.StartOrder };
					selection = RaceResults.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NOT NULL" + " AND " + RaceResults.EndTime + " IS NULL AND " + RacerClubInfo.Category + "!=?";
					selectionArgs = new String[]{"G"};
					sortOrder = RaceResults.StartOrder;
					loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
		        }
				break;
			case TEAM_FINISH_ORDER_LOADER:
				finishersCA = new TeamsToFinishCursorAdapter(getActivity(), null);
				
		        if( finishOrderList != null){
		        	finishOrderList.setAdapter(finishersCA);
		        	
		        	finishOrderList.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int pos, long raceResult_ID) {
							try{
								Cursor unassignedCursor = unassignedCA.getCursor();
								unassignedCursor.moveToPosition(unassignedTimes.getCheckedItemPosition());
								long unassignedID = unassignedCursor.getLong(unassignedCursor.getColumnIndex(UnassignedTimes._ID));
								AssignLapTimeTask task = new AssignLapTimeTask(FinishTab.this.getActivity());
								task.execute(new Long[] { unassignedID, raceResult_ID });
							}catch(Exception ex){
								Log.e(LOG_TAG(), "Error assigning time", ex);
							}
						}
					});
		        }
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", TeamInfo.TeamName, RaceResults.StartOrder, "count(" + RaceLaps.getTableName() + "." + RaceLaps._ID + ") as NumLaps"};
				selection = RaceResults.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.StartTime + " IS NOT NULL" + " AND " + RaceResults.EndTime + " IS NULL AND " + TeamInfo.TeamCategory + "!=?";
				selectionArgs = new String[]{"G"};
				sortOrder = "NumLaps ASC," + RaceResults.StartOrder + " ASC";
				loader = new CursorLoader(getActivity(), Uri.withAppendedPath(TeamLaps.CONTENT_URI, "group by " + RaceResults.getTableName() + "." + RaceResults._ID + "," + TeamInfo.TeamName + "," + RaceResults.StartOrder + "&having count(" + RaceLaps.getTableName() + "." + RaceLaps._ID + ") < " + Long.toString(numRaceLaps)), projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_FINISH:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.RaceType, Race.NumLaps};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			Log.i(LOG_TAG(),
					"onLoadFinished start: id="	+ Integer.toString(loader.getId()));
			switch (loader.getId()) {
				case RACER_GROUPS_LOADER:
					if (unassignedTimes != null) {
						groupLookup = new Hashtable<Long, String>();

						if(cursor.getCount() > 0){
							cursor.moveToFirst();
							while(!cursor.isAfterLast()){
								groupLookup.put(cursor.getLong(cursor.getColumnIndex(RacerGroup._ID)), cursor.getString(cursor.getColumnIndex(RacerGroup.GroupDescription)));	
								cursor.moveToNext();
							}
						}
						//groupList.addAll(Arrays.asList(getActivity().getResources().getStringArray(R.array.finish_groups_array)));
						//groupList.add("Prime 1");
						groupsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_finish_group, new ArrayList<String>(groupLookup.values()));
						if(groupsAdapter.getCount() > 0){
							unassignedTimes.setItemChecked(0, true);
						}
						
						unassignedTimes.setAdapter(groupsAdapter);
						
						unassignedTimes.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
								// Clicking one of the finish groups means that group came across the line
								// If it's the first group, add a lap
								// If it's a group after that, calculate the gap
								// If the next lap will be the start of a prime lap, flash a message and "highlight" the prime option (30 seconds after the lap finished).  This will prompt the user to ring the bell for a prime lap
								// If it's a prime lap, disable the rest of the options (breakaway, etc) until enough prime places were selected from the racer list
								// If there's a breakaway, put those racers at the top of the list
//								RemoveUnassignedTime removeUnassignedTimeDialog = new RemoveUnassignedTime(id);
//								FragmentManager fm = getActivity().getSupportFragmentManager();
//								removeUnassignedTimeDialog.show(fm,	RemoveUnassignedTime.LOG_TAG);
							}
						});

						unassignedTimes.setOnItemLongClickListener(new OnItemLongClickListener() {
								public boolean onItemLongClick(AdapterView<?> arg0,	View v, int pos, long id) {
									// Maybe a list of gaps?
//									RemoveUnassignedTime removeUnassignedTimeDialog = new RemoveUnassignedTime(id);
//									FragmentManager fm = getActivity().getSupportFragmentManager();
//									removeUnassignedTimeDialog.show(fm,	RemoveUnassignedTime.LOG_TAG);
									return false;
								}
							});
					}
					break;
				case TEAM_UNASSIGNED_TIMES_LOADER:
				case UNASSIGNED_TIMES_LOADER_FINISH:
					if(cursor.getCount() >= 1){
						unassignedTimes.setItemChecked(0, true);
					}
					cursor.moveToFirst();
					unassignedCA.swapCursor(cursor);
					break;
				case TEAM_FINISH_ORDER_LOADER:
				case FINISH_ORDER_LOADER_FINISH:					
					finishersCA.swapCursor(cursor);
	
					if (getView() != null) {
						if (finishersCA.getCount() > 0) {
							((Button) getView().findViewById(R.id.btnRacerFinished)).setEnabled(true);
						} else {
							((Button) getView().findViewById(R.id.btnRacerFinished)).setEnabled(false);
						}
					}
					break;
				case RACE_INFO_LOADER_FINISH:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						int numLapsCol = cursor.getColumnIndex(Race.NumLaps);
						numRaceLaps = cursor.getLong(numLapsCol);
						if(raceTypeID == Enums.RaceType.TeamTimeTrial.ID()){							
							btnRacerFinished.setText(R.string.LapCompleted);
							tblGroupButtons.setVisibility(View.GONE);
							
							LinearLayout.LayoutParams buttonsParams = (LinearLayout.LayoutParams)llFinishButtons.getLayoutParams();
							buttonsParams.weight = 0.1f;
							llFinishButtons.setLayoutParams(buttonsParams);

							LinearLayout.LayoutParams listsParams = (LinearLayout.LayoutParams)llFinishLists.getLayoutParams();
							listsParams.weight = 0.9f;
							llFinishLists.setLayoutParams(listsParams);
							
							getActivity().getSupportLoaderManager().restartLoader(TEAM_FINISH_ORDER_LOADER, null, this);
							getActivity().getSupportLoaderManager().restartLoader(TEAM_UNASSIGNED_TIMES_LOADER, null, this);
						}else {
							if(raceTypeID == Enums.RaceType.Criterium.ID()){								
								btnRacerFinished.setVisibility(View.GONE);

								tblGroupButtons.setVisibility(View.GONE);
//								
//								LinearLayout.LayoutParams buttonsParams = (LinearLayout.LayoutParams)llFinishButtons.getLayoutParams();
//								buttonsParams.weight = 0.25f;
//								llFinishButtons.setLayoutParams(buttonsParams);
								
								LinearLayout.LayoutParams listsParams = (LinearLayout.LayoutParams)llFinishLists.getLayoutParams();
								listsParams.weight = 1.0f;
								llFinishLists.setLayoutParams(listsParams);								

							    // Initialize the cursor loader for the finish order list
								getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);	
								getActivity().getSupportLoaderManager().restartLoader(RACER_GROUPS_LOADER, null, this);	
							} else {								
								btnRacerFinished.setVisibility(View.VISIBLE);
								btnRacerFinished.setText(R.string.RacerFinished);
								
								tblGroupButtons.setVisibility(View.GONE);
								
								LinearLayout.LayoutParams buttonsParams = (LinearLayout.LayoutParams)llFinishButtons.getLayoutParams();
								buttonsParams.weight = 0.1f;
								llFinishButtons.setLayoutParams(buttonsParams);
								
								LinearLayout.LayoutParams listsParams = (LinearLayout.LayoutParams)llFinishLists.getLayoutParams();
								listsParams.weight = 0.9f;
								llFinishLists.setLayoutParams(listsParams);
								
							    // Initialize the cursor loader for the finish order list
								getActivity().getSupportLoaderManager().restartLoader(FINISH_ORDER_LOADER_FINISH, null, this);								
								getActivity().getSupportLoaderManager().restartLoader(UNASSIGNED_TIMES_LOADER_FINISH, null, this);
							}
						}
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
				case UNASSIGNED_TIMES_LOADER_FINISH:
					unassignedCA.swapCursor(null);
					break;
				case FINISH_ORDER_LOADER_FINISH:
				case TEAM_FINISH_ORDER_LOADER:
					finishersCA.swapCursor(null);
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
			if (v == btnRacerFinished) {
				RacerFinished();
			} else if (v == btnBreakaway) {
				
			} else if (v == btnChase) {
				
			} else if (v == btnPeleton) {
				
			} else if (v == btnOffTheBack) {
				
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "btnNewRacerClick failed", ex);
		}
	}
}
