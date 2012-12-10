/**
 * 
 */
package com.xcracetiming.android.tttimer.Tabs;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.CursorAdapters.CheckInCursorAdapter;
import com.xcracetiming.android.tttimer.CursorAdapters.StartOrderCursorAdapter;
import com.xcracetiming.android.tttimer.CursorAdapters.TeamCheckInCursorAdapter;
import com.xcracetiming.android.tttimer.CursorAdapters.TeamStartOrderCursorAdapter;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceTeamResults;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceWaveInfoView;
import com.xcracetiming.android.tttimer.DataAccess.Views.RacerSeriesInfoView;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceTeamResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.TeamInfoView;
import com.xcracetiming.android.tttimer.Dialogs.AddGhostRacerView;
import com.xcracetiming.android.tttimer.Dialogs.AddRacerView;
import com.xcracetiming.android.tttimer.Dialogs.AddTeamView;
import com.xcracetiming.android.tttimer.Dialogs.EditRacerView;
import com.xcracetiming.android.tttimer.Dialogs.EditTeamView;
import com.xcracetiming.android.tttimer.Dialogs.StartOrderActions;
import com.xcracetiming.android.tttimer.Utilities.Loaders;
import com.xcracetiming.android.tttimer.Utilities.RestartLoaderTextWatcher;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author Perry, Mark
 *
 */
public class CheckInTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	private CursorAdapter checkInsCA;
	private CursorAdapter startOrderCA;
	
	private Button btnAddRacer = null;
	private Button btnAddGhostRacer = null;
	private Button btnAddNewTeam = null;	
	private EditText racerNameSearchText;	
	
	private Loader<Cursor> checkInLoader = null;
	private Loader<Cursor> teamsCheckInLoader = null;
	private Loader<Cursor> startOrderLoader = null;
	private Loader<Cursor> teamStartOrderLoader = null;
	
	private ListView startOrderList;
	private ListView checkInList;
	
	private boolean autoCheckIn = false;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_checkin, container, false);
        
        btnAddRacer = (Button) view.findViewById(R.id.btnAddNewRacer);
		btnAddRacer.setOnClickListener(this);
		
		btnAddNewTeam = (Button) view.findViewById(R.id.btnAddNewTeam);
		btnAddNewTeam.setOnClickListener(this);
		
		btnAddGhostRacer = (Button) view.findViewById(R.id.btnAddGhostRacer);
		btnAddGhostRacer.setOnClickListener(this);
		
        view.setKeepScreenOn(true);
        
        return view;
    }
	
	@Override
	public void onResume() { 
		super.onResume(); 
		
        RestartLoaderTextWatcher tw = new RestartLoaderTextWatcher(getActivity().getSupportLoaderManager(), Loaders.CHECKIN_LOADER_CHECKIN, this);
        
        racerNameSearchText = (EditText) getView().findViewById (R.id.txtRacerNameFilter);
		racerNameSearchText.addTextChangedListener(tw);
		
		startOrderList = (ListView) getView().findViewById(R.id.tblCheckedInRacers);
		checkInList = (ListView) getView().findViewById(R.id.tblFilteredRacers);
		
	    getActivity().getSupportLoaderManager().restartLoader(Loaders.APP_SETTINGS_LOADER_CHECKIN, null, this);
	    getActivity().getSupportLoaderManager().restartLoader(Loaders.RACE_INFO_LOADER_CHECKIN, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.APP_SETTINGS_LOADER_CHECKIN);
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.CHECKIN_LOADER_CHECKIN);
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.TEAM_CHECKIN_LOADER);
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.START_ORDER_LOADER_CHECKIN);
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.TEAM_START_ORDER_LOADER);
		getActivity().getSupportLoaderManager().destroyLoader(Loaders.RACE_INFO_LOADER_CHECKIN);	
	}
	
	private void SetupList(ListView list, CursorAdapter ca, OnItemLongClickListener listener) {	
		if(getView() != null){
	        if( list != null){
	        	list.setAdapter(ca);
	        	
	        	list.setFocusable(true);
	        	list.setClickable(true);
	        	list.setItemsCanFocus(true);
				
	        	list.setOnItemLongClickListener( listener );
	        }
		}
	}	
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		
		switch(id){
			case Loaders.CHECKIN_LOADER_CHECKIN:
				// Create the cursor adapter for the list of checkins
		        checkInsCA = new CheckInCursorAdapter(getActivity(), null);
		        
				SetupList(checkInList, checkInsCA, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id) {
						EditRacerView editRacerDialog = new EditRacerView(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						editRacerDialog.show(fm, EditRacerView.LOG_TAG);
						return false;
					}
	    		});	
				
				projection = new String[]{RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + " as _id", Racer.LastName, Racer.FirstName};
				selection = RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.RaceSeries_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceSeriesID_Name) + " AND " + RacerSeriesInfo.Upgraded + "=? AND " + RaceCategory.FullCategoryName + "!=?";
				selectionArgs = new String[]{Long.toString(0l), "G"};
				sortOrder = Racer.LastName;
				String racerNameText = racerNameSearchText.getText().toString();
				if(!racerNameText.equals("")){
					selection += " AND UPPER(" + Racer.LastName + ") GLOB ?";
					selectionArgs = new String[]{ Long.toString(0l), "G", racerNameSearchText.getText().toString().toUpperCase() + "*"};
				}
				loader = new CursorLoader(getActivity(), RacerSeriesInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.TEAM_CHECKIN_LOADER:
				// Create the cursor adapter for the list of team checkins
		        checkInsCA = new TeamCheckInCursorAdapter(getActivity(), null);

				SetupList(checkInList, checkInsCA, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id) {
						EditTeamView editRacerDialog = new EditTeamView(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						editRacerDialog.show(fm, EditTeamView.LOG_TAG);
						return false;
					}
	    		});
				
				projection = new String[]{TeamInfo.Instance().getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName, "group_concat(" + Racer.FirstName + "||' '||" + Racer.LastName + ", ',\n') as RacerNames"};
				selection = TeamInfo.Instance().getTableName() + "." + TeamInfo.RaceSeries_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceSeriesID_Name) + " AND " + TeamInfo.TeamCategory + "!=?";
				selectionArgs = new String[]{"G"};
				sortOrder = TeamInfo.Instance().getTableName() + "." + TeamInfo.TeamName;
				loader = new CursorLoader(getActivity(), TeamInfoView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.GroupBy, "group by " + TeamInfo.Instance().getTableName() + "." + TeamInfo._ID + "," + TeamInfo.TeamName).build(), projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.START_ORDER_LOADER_CHECKIN:
				// Create the cursor adapter for the start order list
				startOrderCA = new StartOrderCursorAdapter(getActivity(), null);

				SetupList(startOrderList, startOrderCA, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int pos, long id) {
						StartOrderActions startOrderActionsDialog = new StartOrderActions(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						startOrderActionsDialog.show(fm, StartOrderActions.LOG_TAG);
						return false;
					}
	    		});
				projection = new String[]{SeriesRaceIndividualResults.RaceResult_ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = SeriesRaceIndividualResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), SeriesRaceIndividualResultsView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.TEAM_START_ORDER_LOADER:
				// Create the cursor adapter for the start order list
				startOrderCA = new TeamStartOrderCursorAdapter(getActivity(), null);

				SetupList(startOrderList, startOrderCA, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,
							int pos, long id) {
						StartOrderActions startOrderActionsDialog = new StartOrderActions(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						startOrderActionsDialog.show(fm, StartOrderActions.LOG_TAG);
						return false;
					}
	    		});
				
				projection = new String[]{SeriesRaceTeamResults.RaceResult_ID + " as _id", TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset, "group_concat(" + Racer.FirstName + "||' '||" + Racer.LastName + ", ',\n') as RacerNames"};
				selection = SeriesRaceTeamResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), SeriesRaceTeamResultsView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.GroupBy, "group by " + SeriesRaceTeamResults.TeamInfo_ID + "," + TeamInfo.TeamName + "," + RaceResults.StartOrder + "," + RaceResults.StartTimeOffset).build(), projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.RACE_INFO_LOADER_CHECKIN:
				projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceType_ID, RaceType.IsTeamRace, RaceWave.NumLaps};
				selection = Race.Instance().getTableName() + "." + Race._ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.Instance().getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), RaceWaveInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case Loaders.APP_SETTINGS_LOADER_CHECKIN:
				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
				selection = null;
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case Loaders.CHECKIN_LOADER_CHECKIN:	
				case Loaders.TEAM_CHECKIN_LOADER:
					checkInsCA.swapCursor(cursor);
					break;
				case Loaders.START_ORDER_LOADER_CHECKIN:													
					startOrderCA.swapCursor(cursor);
					if(getView() != null){
			    		startOrderList.setSelection(startOrderList.getCount());
					}
					break;
				case Loaders.TEAM_START_ORDER_LOADER:
					startOrderCA.swapCursor(cursor);
					if(getView() != null){
			    		startOrderList.setSelection(startOrderList.getCount());
					}
					break;
				case Loaders.RACE_INFO_LOADER_CHECKIN:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						boolean isTeamRace = cursor.getLong(cursor.getColumnIndex(RaceType.IsTeamRace)) == 1l;
						if(getView() != null){
							LinearLayout llFilters = (LinearLayout) getView().findViewById(R.id.llFilters);
							Button btnAddNewTeam = (Button) getView().findViewById(R.id.btnAddNewTeam);

							if(!isTeamRace){						        							
								btnAddNewTeam.setVisibility(View.GONE);
								btnAddRacer.setVisibility(View.VISIBLE);
								llFilters.setVisibility(View.VISIBLE);

								checkInLoader = getActivity().getSupportLoaderManager().restartLoader(Loaders.CHECKIN_LOADER_CHECKIN, null, this);
								startOrderLoader = getActivity().getSupportLoaderManager().restartLoader(Loaders.START_ORDER_LOADER_CHECKIN, null, this);
							}else {	
								btnAddNewTeam.setVisibility(View.VISIBLE);
								btnAddRacer.setVisibility(View.GONE);
								llFilters.setVisibility(View.GONE);

								teamsCheckInLoader = getActivity().getSupportLoaderManager().restartLoader(Loaders.TEAM_CHECKIN_LOADER, null, this);
								teamStartOrderLoader = getActivity().getSupportLoaderManager().restartLoader(Loaders.TEAM_START_ORDER_LOADER, null, this);
							}
						}
					}
					break;
				case Loaders.APP_SETTINGS_LOADER_CHECKIN:	
					if(checkInLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(Loaders.CHECKIN_LOADER_CHECKIN, null, this);
					}
					if(teamsCheckInLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(Loaders.TEAM_CHECKIN_LOADER, null, this);
					}
					if(startOrderLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(Loaders.START_ORDER_LOADER_CHECKIN, null, this);
					}
					if( teamStartOrderLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(Loaders.TEAM_START_ORDER_LOADER, null, this);
					}
					autoCheckIn = Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSettings_AutoCheckIn_Name, "true"));
					break;
			}
			Log.v(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case Loaders.CHECKIN_LOADER_CHECKIN:
				case Loaders.TEAM_CHECKIN_LOADER:
					checkInsCA.swapCursor(null);
					break;
				case Loaders.START_ORDER_LOADER_CHECKIN:
				case Loaders.TEAM_START_ORDER_LOADER:
					startOrderCA.swapCursor(null);
					break;
				case Loaders.RACE_INFO_LOADER_CHECKIN:
					// Do nothing...this is only here for consistency					
					break;
				case Loaders.APP_SETTINGS_LOADER_CHECKIN:
					// Do nothing...this is only here for consistency
					break;
			}
			Log.v(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}


	public void onClick(View v) {
		try
     	{
 			Log.v(LOG_TAG(), "onClick");
			FragmentManager fm = getActivity().getSupportFragmentManager();
 			if(v == btnAddRacer){
	            AddRacerView addRacer = new AddRacerView(autoCheckIn);
	            addRacer.show(fm, AddRacerView.LOG_TAG);
 			} else if(v == btnAddNewTeam){
				AddTeamView addTeam = new AddTeamView();
				addTeam.show(fm, AddTeamView.LOG_TAG);
 			} else if(v == btnAddGhostRacer){
				AddGhostRacerView addGhost = new AddGhostRacerView();
				addGhost.show(fm, AddGhostRacerView.LOG_TAG);
			}
     	}
     	catch(Exception ex)
     	{
     		Log.e(LOG_TAG(), "onClick failed", ex);
     	}
	}
}