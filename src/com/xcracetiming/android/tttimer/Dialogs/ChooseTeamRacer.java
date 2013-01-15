package com.xcracetiming.android.tttimer.Dialogs;

import java.util.Calendar;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceTeamResultsView;
import com.xcracetiming.android.tttimer.Utilities.RestartLoaderTextWatcher;

import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ChooseTeamRacer extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "ChooseRacer";

	private static final int TEAM_MEMBER_LOADER = 0x48;

	//private static final int APP_SETTINGS_LOADER = 0x89;
	
	public interface ChooseRacerDialogListener {
        void onFinishEditDialog(int racerNum, Long racerClubInfoID);
    }
	
	private Button btnChoose;
	private Button btnRemove;
	private Button btnAddRacer;
	private Spinner spinnerTeamRacer;
	private EditText racerNameSearchText;
	private SimpleCursorAdapter teamRacerCA = null;
	private ChooseRacerDialogListener caller;
	
	private int racerNum;
	
	public ChooseTeamRacer(){};
	
	@Override
	public void setArguments(Bundle args) {
		if(args != null){
			racerNum = args.getInt("RacerNum");
			
			// TODO Figure out how to deal with caller
		}
	}
	
	public ChooseTeamRacer(int racerNum, ChooseRacerDialogListener caller){
		this.racerNum = racerNum;
		this.caller = caller;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_choose_team_racer, container, false);
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.btnChooseRacer;
	}
	
	@Override
	public void onResume(){
		super.onResume();	

		// Create the cursor adapter for the list of racers available for teams
        //teamRacerCA = new SimpleCursorAdapter(getActivity(), null);

		btnChoose = (Button) getView().findViewById(R.id.btnDone);
		btnChoose.setOnClickListener(this);

		btnRemove = (Button) getView().findViewById(R.id.btnRemove);
		btnRemove.setOnClickListener(this);
		
		btnAddRacer = (Button) getView().findViewById(R.id.btnAddRacer);
		btnAddRacer.setOnClickListener(this);

		spinnerTeamRacer = (Spinner) getView().findViewById(R.id.spinnerTeamRacer);
		spinnerTeamRacer.setAdapter(teamRacerCA);
		
        RestartLoaderTextWatcher tw = new RestartLoaderTextWatcher(getLoaderManager(), TEAM_MEMBER_LOADER, this);
        racerNameSearchText = (EditText) getView().findViewById (R.id.txtRacerNameFilter);
		racerNameSearchText.addTextChangedListener(tw);
		
		this.getLoaderManager().restartLoader(TEAM_MEMBER_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnChoose){
				long racerClubInfoID = spinnerTeamRacer.getSelectedItemId();
				// Return input text to activity
	            caller.onFinishEditDialog(racerNum, racerClubInfoID);
	            this.dismiss();
			} else if (v == btnRemove){
				// Return -1 to activity - this signifies a removal/invalid id
	            caller.onFinishEditDialog(racerNum, -1l);
	            this.dismiss();
			} else if (v == btnAddRacer){
	            AddRacerView addRacer = new AddRacerView(false);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				addRacer.show(fm, AddRacerView.LOG_TAG);
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed", ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case TEAM_MEMBER_LOADER:
				projection = new String[]{RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + " as _id", Racer.FirstName + "||' '||" + Racer.LastName + " as RacerName"};
				selection = RacerSeriesInfo.RaceSeries_ID + "=?";
				selectionArgs = new String[]{Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), Long.toString(0l)};
				sortOrder = Racer.LastName;
				String racerNameText = racerNameSearchText.getText().toString();
				if(!racerNameText.equals("")){
					selection += " AND UPPER(" + Racer.LastName + ") GLOB ?";
					selectionArgs = new String[]{ Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), Long.toString(0l), racerNameSearchText.getText().toString().toUpperCase() + "*"};
				}
				loader = new CursorLoader(getActivity(), SeriesRaceTeamResultsView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			//case APP_SETTINGS_LOADER:
				//break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case TEAM_MEMBER_LOADER:
					teamRacerCA = null;
					if(teamRacerCA == null){
						String[] columns = new String[] { "RacerName" };
			            int[] to = new int[] {android.R.id.text1 };
			            
						// Create the cursor adapter for the list of races
			            teamRacerCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, cursor, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			            teamRacerCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
			        	spinnerTeamRacer.setAdapter(teamRacerCA);
					}
					teamRacerCA.swapCursor(cursor);
					break;
				//case APP_SETTINGS_LOADER:	
					//getActivity().getSupportLoaderManager().restartLoader(TEAM_MEMBER_LOADER, null, this);
					//break;
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
				case TEAM_MEMBER_LOADER:
					teamRacerCA.swapCursor(null);
					break;
				//case APP_SETTINGS_LOADER:
					// Do nothing...this is only here for consistency
					//break;
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
}
