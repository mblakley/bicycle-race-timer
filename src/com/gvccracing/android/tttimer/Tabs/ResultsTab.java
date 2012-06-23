package com.gvccracing.android.tttimer.Tabs;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.CursorAdapters.ResultsCursorAdapter;
import com.gvccracing.android.tttimer.CursorAdapters.TeamResultsCursorAdapter;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Dialogs.EditRaceResultView;
import com.gvccracing.android.tttimer.Dialogs.RacerPreviousResults;

public class ResultsTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String ResultsTabSpecName =  "ResultsActivity";

	private static final int TEAM_OVERALL_RESULTS_LOADER = 0x90;

	private static final int RACE_INFO_LOADER_RESULTS = 0x189;

	private static final int OVERALL_RESULTS_LOADER_RESULTS = 0x118;

	private static final int CATEGORY_RESULTS_LOADER_RESULTS = 0x119;

	private Long raceTypeID = 0l;	
	
	private CursorAdapter overallResultsCA;
	private ResultsCursorAdapter categoryResultsCA;
	
	private Loader<Cursor> overallResultsLoader = null;
	private Loader<Cursor> teamOverallResultsLoader = null;
	private Loader<Cursor> categoryResultsLoader = null;
	
	private ListView overallResultsList;
	private ListView categoryResultsList; 
	private TextView lblCategoryResults;
	private LinearLayout llCategoryHeader;
	private LinearLayout llOverallHeader;
	
	/**
	 * 
	 */
	public ResultsTab() {
		super();
	}
	
	 /* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	      
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_results, container, false);
        
        return view;
    }	
	
	@Override
	public void onResume() {
		super.onResume();		  

		overallResultsList = (ListView) getView().findViewById(R.id.svOverallResults);
		categoryResultsList = (ListView) getView().findViewById(R.id.svCategoryResults);
		lblCategoryResults = (TextView) getView().findViewById(R.id.lblCategoryResults);
		llCategoryHeader = (LinearLayout) getView().findViewById(R.id.llCategoryHeader);
		llOverallHeader = (LinearLayout) getView().findViewById(R.id.llOverallHeader);
        
        // Initialize the cursor loader for the overall results list
        getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER_RESULTS, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(getActivity().getSupportLoaderManager().getLoader(RACE_INFO_LOADER_RESULTS) != null){
			getActivity().getSupportLoaderManager().destroyLoader(RACE_INFO_LOADER_RESULTS);
		}
		if(getActivity().getSupportLoaderManager().getLoader(OVERALL_RESULTS_LOADER_RESULTS) != null){
			getActivity().getSupportLoaderManager().destroyLoader(OVERALL_RESULTS_LOADER_RESULTS);
		}
		if(getActivity().getSupportLoaderManager().getLoader(TEAM_OVERALL_RESULTS_LOADER) != null){
			getActivity().getSupportLoaderManager().destroyLoader(TEAM_OVERALL_RESULTS_LOADER);
		}
		if(getActivity().getSupportLoaderManager().getLoader(CATEGORY_RESULTS_LOADER_RESULTS) != null){
			getActivity().getSupportLoaderManager().destroyLoader(CATEGORY_RESULTS_LOADER_RESULTS);
		}
	}

	@Override
	public String TabSpecName() {
		return ResultsTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return ResultsTabSpecName;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case OVERALL_RESULTS_LOADER_RESULTS:
				overallResultsCA = new ResultsCursorAdapter(getActivity(), null, true);
				if(overallResultsList != null){
					overallResultsList.setAdapter(overallResultsCA);
					overallResultsList.setFocusable(false);
					overallResultsList.setClickable(false);
					overallResultsList.setItemsCanFocus(false);
					overallResultsList.setOnItemClickListener(new OnItemClickListener(){
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long id) {
							RacerPreviousResults showPreviousRaceResultsDialog = new RacerPreviousResults(id);
							FragmentManager fm = getActivity().getSupportFragmentManager();
							showPreviousRaceResultsDialog.show(fm, RacerPreviousResults.LOG_TAG);
						}
		    		});
					overallResultsList.setOnItemLongClickListener( new OnItemLongClickListener(){
						public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								int position, long id) {
							if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
								EditRaceResultView editRaceResultDialog = new EditRaceResultView(id);
								FragmentManager fm = getActivity().getSupportFragmentManager();
								editRaceResultDialog.show(fm, EditRaceResultView.LOG_TAG);
							}
							return false;
						}
		    		});
				}
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.ElapsedTime, RacerClubInfo.Category, RaceResults.OverallPlacing, RaceResults.Points};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.ElapsedTime + " IS NOT NULL";
				selectionArgs = null;
				sortOrder = RaceResults.ElapsedTime + "," + Racer.LastName;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_OVERALL_RESULTS_LOADER:
				// Create the cursor adapter for the list of unassigned times
				overallResultsCA = new TeamResultsCursorAdapter(getActivity(), null);
				if (overallResultsList != null) {
					overallResultsList.setAdapter(overallResultsCA);
					overallResultsList.setFocusable(false);
					overallResultsList.setClickable(false);
					overallResultsList.setItemsCanFocus(false);
					overallResultsList.setOnItemClickListener(new OnItemClickListener(){
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long id) {
//							TeamPreviousResults showPreviousRaceResultsDialog = new TeamPreviousResults(id);
//							FragmentManager fm = getActivity().getSupportFragmentManager();
//							showPreviousRaceResultsDialog.show(fm, TeamPreviousResults.LOG_TAG);
						}
		    		});
					overallResultsList.setOnItemLongClickListener( new OnItemLongClickListener(){
						public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								int position, long id) {
							if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
//								EditRaceResultView editRaceResultDialog = new EditRaceResultView(id);
//								FragmentManager fm = getActivity().getSupportFragmentManager();
//								editRaceResultDialog.show(fm, EditRaceResultView.LOG_TAG);
							}
							return false;
						}
		    		});
				}
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", TeamInfo.TeamName, RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " as " + RaceResults.ElapsedTime, RaceResults.OverallPlacing, "group_concat(" + Racer.LastName + ", ', ') as RacerNames"};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + " IS NOT NULL";
				selectionArgs = null;
				sortOrder = RaceResults.ElapsedTime + "," + TeamInfo.TeamName;
				loader = new CursorLoader(getActivity(), Uri.withAppendedPath(TeamCheckInViewExclusive.CONTENT_URI, "group by " + RaceResults.getTableName() + "." + RaceResults._ID + "," + TeamInfo.TeamName + "," + RaceResults.getTableName() + "." + RaceResults.ElapsedTime + "," + RaceResults.OverallPlacing), projection, selection, selectionArgs, sortOrder);
				break;
			case CATEGORY_RESULTS_LOADER_RESULTS:
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.ElapsedTime, RacerClubInfo.Category, RaceResults.CategoryPlacing, RaceResults.Points};
				selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.ElapsedTime + " IS NOT NULL";
				selectionArgs = null;
				sortOrder = RacerClubInfo.Category + "," + RaceResults.ElapsedTime + "," + Racer.LastName;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER_RESULTS:
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
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case TEAM_OVERALL_RESULTS_LOADER:
					Log.i("TEAM_OVERALL_RESULTS_LOADER", "Teams Loaded with " + cursor.getCount() + " records");
					overallResultsCA.swapCursor(cursor);
					break;
				case OVERALL_RESULTS_LOADER_RESULTS:
					Log.i("OVERALL_RESULTS_LOADER_RESULTS", "Loaded with " + cursor.getCount() + " records");
					overallResultsCA.swapCursor(cursor);
					break;
				case CATEGORY_RESULTS_LOADER_RESULTS:						
					categoryResultsCA.swapCursor(cursor);
					break;
				case RACE_INFO_LOADER_RESULTS:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						if(getView() != null){
							if(raceTypeID == 1){								
								if(categoryResultsList != null) {
						        	categoryResultsList.setVisibility(View.GONE);
						        	lblCategoryResults.setVisibility(View.GONE);
						        	llCategoryHeader.setVisibility(View.GONE);
						        	llOverallHeader.setVisibility(View.GONE);
						        }
								
								if( teamOverallResultsLoader == null){
									teamOverallResultsLoader = getActivity().getSupportLoaderManager().initLoader(TEAM_OVERALL_RESULTS_LOADER, null, this);
								} else {
									teamOverallResultsLoader = getActivity().getSupportLoaderManager().restartLoader(TEAM_OVERALL_RESULTS_LOADER, null, this);
								}
							}else {	
								if( overallResultsLoader == null){
									overallResultsLoader = getActivity().getSupportLoaderManager().initLoader(OVERALL_RESULTS_LOADER_RESULTS, null, this);
								} else {
									overallResultsLoader = getActivity().getSupportLoaderManager().restartLoader(OVERALL_RESULTS_LOADER_RESULTS, null, this);
								}

								// Now create a cursor adapter and set it to display using our row
						        categoryResultsCA = new ResultsCursorAdapter(getActivity(), null, false);
								if(categoryResultsList != null) {
						        	categoryResultsList.setAdapter(categoryResultsCA);
						        	categoryResultsList.setVisibility(View.VISIBLE);
						        	lblCategoryResults.setVisibility(View.VISIBLE);
						        	llCategoryHeader.setVisibility(View.VISIBLE);
						        	llOverallHeader.setVisibility(View.VISIBLE);
						        }

								if( categoryResultsLoader == null){
									// Initialize the cursor loader for the category results list
							        categoryResultsLoader = getActivity().getSupportLoaderManager().initLoader(CATEGORY_RESULTS_LOADER_RESULTS, null, this);
								} else {
									categoryResultsLoader = getActivity().getSupportLoaderManager().restartLoader(CATEGORY_RESULTS_LOADER_RESULTS, null, this);
								}						        
							}
						}
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
				case OVERALL_RESULTS_LOADER_RESULTS:
					if(overallResultsCA != null){
						overallResultsCA.swapCursor(null);
					}
					break;
				case CATEGORY_RESULTS_LOADER_RESULTS:
					if(categoryResultsCA != null){
						categoryResultsCA.swapCursor(null);
					}
					break;
				case RACE_INFO_LOADER_RESULTS:
					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}
}
