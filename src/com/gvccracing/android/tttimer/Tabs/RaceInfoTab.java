package com.gvccracing.android.tttimer.Tabs;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.AsyncTasks.CreateRaceResultsTask;
import com.gvccracing.android.tttimer.Controls.Timer;
import com.gvccracing.android.tttimer.CursorAdapters.StableSimpleCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoResultsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Dialogs.AddRacerView;
import com.gvccracing.android.tttimer.Dialogs.AdminAuthView;
import com.gvccracing.android.tttimer.Dialogs.AdminMenuView;
import com.gvccracing.android.tttimer.Dialogs.MarshalLocations;
import com.gvccracing.android.tttimer.Dialogs.OtherRaceResults;
import com.gvccracing.android.tttimer.Dialogs.SeriesResultsView;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;
import com.gvccracing.android.tttimer.Utilities.Enums.RaceType;

public class RaceInfoTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	public static final String RaceInfoTabSpecName =  "RaceInfoTab";

	private static final int APP_SETTINGS_LOADER_RACEINFO = 0x22;

	private static final int RACE_INFO_LOADER = 0x114;

	private static final int MEET_TEAMS_LOADER = 23455;

	private static final int MEETS_LOADER = 53453;

	private static final int MEET_RACES_LOADER = 432220;
	
	private TextView raceDate;
	private TextView raceCourseName;
	private TextView raceDistance;
	private String distanceUnit;
	private String distance;
	

	private StableSimpleCursorAdapter teamsCA;
	private StableSimpleCursorAdapter meetsCA;
	private StableSimpleCursorAdapter racesCA;
	
	private Spinner spinMeet;
	private Spinner spinRaceCategory;
	private Spinner spinMyTeam;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_race_info, container, false);
        
        ((Button) view.findViewById(R.id.btnSeriesResults)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnPreviousResults)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnAdminMenu)).setOnClickListener(this);
        
        raceDate = ((TextView) view.findViewById(R.id.raceDate));
        raceCourseName = ((TextView) view.findViewById(R.id.raceCourseName));
        raceDistance = ((TextView) view.findViewById(R.id.raceDistance));
        
        spinMeet = ((Spinner) view.findViewById(R.id.spinMeet));
        spinRaceCategory = ((Spinner) view.findViewById(R.id.spinCurrentRace));
        spinMyTeam = ((Spinner) view.findViewById(R.id.spinMyTeam));
        
        return view;
    }	
	
	private long selectedMeet = 1l;

	private long selectedTeamInfo_ID = 1l;
	
	@Override
	public void onResume() {
		super.onResume(); 
		
		String[] columns = new String[] { RaceLocation.CourseName };
        int[] to = new int[] {android.R.id.text1 };
		
		meetsCA = new StableSimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		meetsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		spinMeet.setAdapter(meetsCA);
		
		spinMeet.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedMeet = id;
		    	
		    	getActivity().getSupportLoaderManager().restartLoader(MEET_TEAMS_LOADER, null, RaceInfoTab.this);
		    }

		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		columns = new String[] { TeamInfo.TeamName };
        to = new int[] {android.R.id.text1 };
		
		teamsCA = new StableSimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		teamsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		spinMyTeam.setAdapter(teamsCA);
		
		spinMyTeam.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedTeamInfo_ID = id;
				//AppSettings.Update(getActivity(), AppSettings.AppSetting_TeamID_Name, Long.toString(selectedTeamInfo_ID), true);
		    	
		    	getActivity().getSupportLoaderManager().restartLoader(MEET_RACES_LOADER, null, RaceInfoTab.this);
		    }

		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		columns = new String[] { "RaceCategory" };
        to = new int[] {android.R.id.text1 };
		
		racesCA = new StableSimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		racesCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
		spinRaceCategory.setAdapter(racesCA);
		
		spinRaceCategory.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	//AppSettings.Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(id), true);		    	

				CreateRaceResultsTask crrt = new CreateRaceResultsTask(getActivity());
				crrt.execute(id, selectedTeamInfo_ID);

				getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, RaceInfoTab.this);
				
				Intent raceHasChanged = new Intent();
				raceHasChanged.setAction(TTTimerTabsActivity.RACE_ID_CHANGED_ACTION);
				raceHasChanged.putExtra(RaceResults.Race_ID, Long.toString(id));
        		getActivity().sendBroadcast(raceHasChanged);
		    }

		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		// Initialize the cursor loader for the meets
		getActivity().getSupportLoaderManager().initLoader(MEETS_LOADER, null, this);

	    //getActivity().getSupportLoaderManager().initLoader(APP_SETTINGS_LOADER_RACEINFO, null, this);
	}

	@Override
	public String TabSpecName() {
		return RaceInfoTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return RaceInfoTabSpecName;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case MEETS_LOADER:
				projection = new String[]{RaceMeet.getTableName() + "." + RaceMeet._ID + " as _id", RaceLocation.CourseName, RaceMeet.RaceMeetDate};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceMeet.RaceMeetDate;
				loader = new CursorLoader(getActivity(), MeetTeamsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case MEET_TEAMS_LOADER:
				projection = new String[]{TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName};
				selection = RaceMeetTeams.getTableName() + "." + RaceMeetTeams.RaceMeet_ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedMeet)};
				sortOrder = TeamInfo.getTableName() + "." + TeamInfo._ID + " DESC";
				loader = new CursorLoader(getActivity(), MeetTeamsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case MEET_RACES_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.Category + "||' '||" + Race.Gender + " as RaceCategory"};
				selection = Race.getTableName() + "." + Race.RaceMeet_ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedMeet)};
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.RaceStartTime, RaceLocation.CourseName, Race.Gender, Race.Category, Race.NumSplits, Race.Distance};
				selection = Race.getTableName() + "." + Race._ID + "=?";// + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = new String[]{AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1")};
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), RaceInfoView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
//			case APP_SETTINGS_LOADER_RACEINFO:
//				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
//				selection = null;
//				sortOrder = null;
//				selectionArgs = null;
//				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
//				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case MEETS_LOADER:
					cursor.moveToFirst();
					selectedMeet = cursor.getLong(cursor.getColumnIndex(RaceMeet._ID));

					meetsCA.swapCursor(cursor);
					
					getActivity().getSupportLoaderManager().restartLoader(MEET_TEAMS_LOADER, null, this);
					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
					break;
				case MEET_TEAMS_LOADER:
					cursor.moveToFirst();

					selectedTeamInfo_ID = cursor.getLong(cursor.getColumnIndex(TeamInfo._ID));
					//AppSettings.Update(getActivity(), AppSettings.AppSetting_TeamID_Name, Long.toString(selectedTeamInfo_ID), true);
					
					teamsCA.swapCursor(cursor);	

					getActivity().getSupportLoaderManager().restartLoader(MEET_RACES_LOADER, null, this);
					break;
				case MEET_RACES_LOADER:
					cursor.moveToFirst();
					long selectedRace_ID = cursor.getLong(cursor.getColumnIndex(Race._ID));
					//AppSettings.Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(selectedRace_ID), true);
					
					racesCA.swapCursor(cursor);										
					
					//CreateRaceResultsTask crrt = new CreateRaceResultsTask(getActivity());
					//crrt.execute(selectedRace_ID, selectedTeamInfo_ID);

					//getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
					break;
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						Long raceDateMS = cursor.getLong(cursor.getColumnIndex(Race.RaceStartTime));
						String courseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
						Float distanceF = cursor.getFloat(cursor.getColumnIndex(Race.Distance));
						
						//llRaceLaps.setVisibility(View.GONE);
						
						Date raceDateTemp = new Date(raceDateMS);
						SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy h:mm");
						raceDate.setText(formatter.format(raceDateTemp).toString());
						raceCourseName.setText(courseName);
						
						raceDistance.setText(Float.toString(distanceF) + " mi");
					}
					break;
//				case APP_SETTINGS_LOADER_RACEINFO:						
//					Integer distanceUnitID = Integer.parseInt(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, "0"));
//					distanceUnit = "mi";
//					switch(distanceUnitID){
//						case 0:
//							distanceUnit = "mi";
//							break;
//						case 1:
//							distanceUnit = "km";
//							break;
//						default:
//							distanceUnit = "mi";
//							break;
//					}
//					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
//					break;
			}
			Log.i(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case MEETS_LOADER:
					meetsCA.swapCursor(null);
					break;
				case MEET_TEAMS_LOADER:
					teamsCA.swapCursor(null);
					break;
				case MEET_RACES_LOADER:
					racesCA.swapCursor(null);
					break;
				case RACE_INFO_LOADER:
					break;
//				case APP_SETTINGS_LOADER_RACEINFO:
//					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}

	public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		switch (v.getId())
		{
			case R.id.btnPreviousResults:
				showChoosePreviousRace();
				break;
			case R.id.btnAdminMenu:
				if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
					AdminMenuView adminMenuDialog = new AdminMenuView();
					adminMenuDialog.show(fm, AdminMenuView.LOG_TAG);
				}else{
					AdminAuthView adminAuthDialog = new AdminAuthView();
			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
				}
				break;
			case R.id.btnSeriesResults:
				SeriesResultsView seriesResultsDialog = new SeriesResultsView();
				seriesResultsDialog.show(fm, SeriesResultsView.LOG_TAG);
				break;
//			case R.id.btnAddRacer:
//				long teamInfo_ID = spinMyTeam.getSelectedItemId();
//				String gender = "M"; // TODO: Get from selected race
//				String category = "Varsity"; // TODO: Get from selected race
//	            AddRacerView addRacer = new AddRacerView(true, teamInfo_ID, gender, category);
//	            addRacer.show(fm, AddRacerView.LOG_TAG);
//				break;
		}
	}

	private void showChoosePreviousRace() {
		OtherRaceResults previousResultsDialog = new OtherRaceResults();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		previousResultsDialog.show(fm, OtherRaceResults.LOG_TAG);
	}
}
