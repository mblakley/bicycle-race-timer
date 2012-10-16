package com.gvccracing.android.tttimer.Tabs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.TTTimerTabsActivity;
import com.gvccracing.android.tttimer.AsyncTasks.CreateRaceResultsTask;
import com.gvccracing.android.tttimer.CursorAdapters.SingleStringCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Dialogs.AdminAuthView;
import com.gvccracing.android.tttimer.Dialogs.AdminMenuView;
import com.gvccracing.android.tttimer.Dialogs.OtherRaceResults;
import com.gvccracing.android.tttimer.Dialogs.SeriesResultsView;

public class RaceInfoTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	public static final String RaceInfoTabSpecName =  "RaceInfoTab";

	private static final int RACE_INFO_LOADER = 0x114;

	private static final int MEET_TEAMS_LOADER = 23455;

	private static final int MEETS_LOADER = 53453;

	private static final int MEET_RACES_LOADER = 432220;

	private static final int CURRENT_MEET_LOADER = 524352;

	private static final int CURRENT_MEET_TEAM_LOADER = 23623;

	private static final int CURRENT_MEET_RACE_LOADER = 73673;
	
	private TextView raceDate;
	private TextView raceCourseName;
	private TextView raceDistance;
	

	private SingleStringCursorAdapter teamsCA;
	private SingleStringCursorAdapter meetsCA;
	private SingleStringCursorAdapter racesCA;
	
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
		
		
		spinMeet.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedMeet = id;
		    	
		    	AppSettings.Update(getActivity(), AppSettings.AppSetting_RaceMeet_ID_Name, Long.toString(selectedMeet), true);
		    	
		    	getActivity().getSupportLoaderManager().restartLoader(MEET_TEAMS_LOADER, null, RaceInfoTab.this);
		    }

		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});
		
		
		spinMyTeam.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	selectedTeamInfo_ID = id;
				AppSettings.Update(getActivity(), AppSettings.AppSetting_TeamID_Name, Long.toString(selectedTeamInfo_ID), true);
		    	
		    	getActivity().getSupportLoaderManager().restartLoader(MEET_RACES_LOADER, null, RaceInfoTab.this);
		    }

		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});		
		
		
		spinRaceCategory.setOnItemSelectedListener(new OnItemSelectedListener() {		    
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	AppSettings.Update(getActivity(), AppSettings.AppSetting_RaceID_Name, Long.toString(id), true);		    	

		    	Hashtable<String, Object> raceValues = Race.getValues(getActivity(), id);
		    	String category = raceValues.get(Race.Category).toString();
		    	String gender = raceValues.get(Race.Gender).toString();
		    	
				CreateRaceResultsTask crrt = new CreateRaceResultsTask(getActivity());
				crrt.execute(id, selectedTeamInfo_ID, category, gender);

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
		getActivity().getSupportLoaderManager().restartLoader(MEETS_LOADER, null, this);
		
		//selectedTeamInfo_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_TeamID_Name, Long.toString(selectedTeamInfo_ID)));
		

	    //getActivity().getSupportLoaderManager().initLoader(APP_SETTINGS_LOADER_RACEINFO, null, this);
	}

	private int GetPositionByID(CursorAdapter ca, long selected_ID) {
		
		for(int position = 0; position < ca.getCount(); position++){
			if(ca.getItemId(position) == selected_ID){
				return position;
			}
		}
		return -1;
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
			case CURRENT_MEET_LOADER:
				projection = new String[]{AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "='" + AppSettings.AppSetting_RaceMeet_ID_Name + "'";
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case MEET_TEAMS_LOADER:
				projection = new String[]{TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName};
				selection = RaceMeetTeams.getTableName() + "." + RaceMeetTeams.RaceMeet_ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedMeet)};
				sortOrder = TeamInfo.getTableName() + "." + TeamInfo._ID + " DESC";
				loader = new CursorLoader(getActivity(), MeetTeamsView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_MEET_TEAM_LOADER:
				projection = new String[]{AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "='" + AppSettings.AppSetting_TeamID_Name + "'";
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case MEET_RACES_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.Category + "||' '||" + Race.Gender + " as RaceCategory"};
				selection = Race.getTableName() + "." + Race.RaceMeet_ID + "=?";
				selectionArgs = new String[]{Long.toString(selectedMeet)};
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case CURRENT_MEET_RACE_LOADER:
				projection = new String[]{AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "='" + AppSettings.AppSetting_RaceID_Name + "'";
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.RaceStartTime, RaceLocation.CourseName, Race.Gender, Race.Category, Race.NumSplits, Race.Distance};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), RaceInfoView.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case MEETS_LOADER:
					if(meetsCA == null){
						if(cursor.getCount() > 0){
							meetsCA = new SingleStringCursorAdapter(getActivity(), cursor);
							spinMeet.setAdapter(meetsCA);
						} else{
							Log.i("Binding shit", "Not binding anything");
						}
					} else{
						spinMeet.setAdapter(meetsCA);
						meetsCA.swapCursor(cursor);
					}
					
					getActivity().getSupportLoaderManager().restartLoader(CURRENT_MEET_LOADER, null, this);
					break;
				case CURRENT_MEET_LOADER:
					int meetIDPosition = 0;
					// If there's a meet_ID in the appSettings table select that
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						
						long meet_ID = Long.parseLong(cursor.getString(0));
						
						if(meet_ID > 0){
							meetIDPosition = GetPositionByID(meetsCA, meet_ID);
						}
						spinMeet.setSelection(meetIDPosition);
						
					} 
					if(cursor != null){
						cursor.close();
						cursor = null;
					}
					
					// After the new meet has been selected, restart the teams loader
					getActivity().getSupportLoaderManager().restartLoader(MEET_TEAMS_LOADER, null, this);					
					break;
				case MEET_TEAMS_LOADER:		
					if(teamsCA == null){
						if(cursor.getCount() > 0){
							teamsCA = new SingleStringCursorAdapter(getActivity(), cursor);
							spinMyTeam.setAdapter(teamsCA);
						} else{
							Log.i("Binding shit", "Not binding anything");
						}
					} else{
						spinMyTeam.setAdapter(teamsCA);
						teamsCA.swapCursor(cursor);
					}

					getActivity().getSupportLoaderManager().restartLoader(CURRENT_MEET_TEAM_LOADER, null, this);
					break;
				case CURRENT_MEET_TEAM_LOADER:
					int teamIDPosition = 0;
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// If there's a team_ID in the appSettings table select that
						long team_ID = Long.parseLong(cursor.getString(0));
						
						if(team_ID > 0){
							teamIDPosition = GetPositionByID(teamsCA, team_ID);
						}	
						spinMyTeam.setSelection(teamIDPosition);
					} 
					if(cursor != null){
						cursor.close();
						cursor = null;
					}					
					
					// After the new team has been selected, restart the races loader
					getActivity().getSupportLoaderManager().restartLoader(MEET_RACES_LOADER, null, this);
					break;
				case MEET_RACES_LOADER:	
					if(racesCA == null){
						if(cursor.getCount() > 0){
							racesCA = new SingleStringCursorAdapter(getActivity(), cursor);
							spinRaceCategory.setAdapter(racesCA);
						} else{
							Log.i("Binding shit", "Not binding anything");
						}
					} else{
						spinRaceCategory.setAdapter(racesCA);
						racesCA.swapCursor(cursor);
					}														

					getActivity().getSupportLoaderManager().restartLoader(CURRENT_MEET_RACE_LOADER, null, this);
					break;
				case CURRENT_MEET_RACE_LOADER:
					int raceIDPosition = 0;
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// If there's a race_ID in the appSettings table select that
						long race_ID = Long.parseLong(cursor.getString(0));
						
						if(race_ID > 0){
							raceIDPosition = GetPositionByID(racesCA, race_ID);
						}
						spinRaceCategory.setSelection(raceIDPosition);	
					} 			
					if(cursor != null){
						cursor.close();
						cursor = null;
					}				
					
					// After the new team has been selected, restart the races loader
					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
					break;	
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						Long raceDateMS = cursor.getLong(cursor.getColumnIndex(Race.RaceStartTime));
						String courseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
						Float distanceF = cursor.getFloat(cursor.getColumnIndex(Race.Distance));						
						
						Date raceDateTemp = new Date(raceDateMS);
						SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy h:mm");
						raceDate.setText(formatter.format(raceDateTemp).toString());
						raceCourseName.setText(courseName);
						
						raceDistance.setText(Float.toString(distanceF) + " mi");
					}
					if(cursor != null){
						cursor.close();
						cursor = null;
					}
					break;
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
		}
	}

	private void showChoosePreviousRace() {
		OtherRaceResults previousResultsDialog = new OtherRaceResults();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		previousResultsDialog.show(fm, OtherRaceResults.LOG_TAG);
	}
}
