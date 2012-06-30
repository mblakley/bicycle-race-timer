package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditRacerStartOrder extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "EditRacerStartOrder";

	private static final int RACER_START_ORDER_LOADER = 0x145;

	private static final int TEAM_START_ORDER_LOADER = 0x146;

	private static final int RACE_INFO_LOADER = 0x147;

	private static final int APP_SETTINGS_LOADER = 0x148;
	
	private Button btnChangeOrder;
	private long raceResultIDToEdit;
	private EditText txtNewOrder;
	private Long raceTypeID = 0l;
	private Long origStartOrder;
	
	private Loader<Cursor> startOrderLoader = null;
	private Loader<Cursor> teamStartOrderLoader = null;
	
	public EditRacerStartOrder(long raceResultIDToEdit) {
		this.raceResultIDToEdit = raceResultIDToEdit;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_edit_racer_start_order, container, false);

		btnChangeOrder = (Button) v.findViewById(R.id.btnChangeOrder);
		btnChangeOrder.setOnClickListener(this);
		
		txtNewOrder = (EditText) v.findViewById(R.id.txtNewOrder);	
		txtNewOrder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});		
		
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.ReorderRacer;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
		getActivity().getSupportLoaderManager().restartLoader(APP_SETTINGS_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnChangeOrder)
			{
				Log.v(LOG_TAG, "btnChangeOrderClickHandler");
				
				long newStartOrder = Long.parseLong(txtNewOrder.getText().toString());
				// The order has changed, so we need to actually do some updates
				if(origStartOrder != newStartOrder){	
					Cursor allStartOrders = RaceResults.Read(getActivity(), new String[]{RaceResults._ID, RaceResults.StartOrder, RaceResults.StartTimeOffset, RaceResults.StartTime}, RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), null, RaceResults.StartOrder);
					if(origStartOrder > newStartOrder){
						// Notify the user that you can't move a racer up in the order
						Toast.makeText(getActivity(), "Can't move a racer up in the order!", 3000).show();
					} else if(newStartOrder > allStartOrders.getCount()){
						// Notify the user that you can't move a racer past the end
						Toast.makeText(getActivity(), "Can't move a racer past the maximum start order (" + allStartOrders.getCount() + ")!", 3000).show();
					} else {
						// Update all race results that have higher start order than the racer to delete.  Change the start order and start time offset
						if(allStartOrders.getCount() > 0){
							Long startInterval = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_StartInterval_Name, "60"));
							long startOrder = 1;
							allStartOrders.moveToFirst();
							while(!allStartOrders.isAfterLast()){
								Long startTimeOffset = (startInterval * startOrder) * 1000l;
								long updatedStartOrder = startOrder;
								if(startOrder > origStartOrder && startOrder <= newStartOrder){
									updatedStartOrder = startOrder - 1;
									startTimeOffset = (startInterval * updatedStartOrder) * 1000l;
								}
								ContentValues content = new ContentValues();
								content.put(RaceResults.StartOrder, updatedStartOrder);
								content.put(RaceResults.StartTimeOffset, startTimeOffset);
								RaceResults.Update(getActivity(), content, RaceResults._ID + "=?", new String[]{Long.toString(allStartOrders.getLong(allStartOrders.getColumnIndex(RaceResults._ID)))});
								startOrder++;
								allStartOrders.moveToNext();
							}
							Long startTimeOffset = (startInterval * newStartOrder) * 1000l;
							ContentValues content = new ContentValues();
							content.put(RaceResults.StartOrder, newStartOrder);
							content.put(RaceResults.StartTimeOffset, startTimeOffset);
							
							RaceResults.Update(getActivity(), content, RaceResults._ID + "=?", new String[]{Long.toString(raceResultIDToEdit)});
						}

						// Hide the dialog
				    	dismiss();
					}
					allStartOrders.close();
					allStartOrders = null;
				}
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnChangeOrderClickHandler failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		
		switch(id){
			case RACER_START_ORDER_LOADER:
				projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", Racer.LastName, Racer.FirstName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.getTableName() + "." + RaceResults._ID + "=?";
				selectionArgs = new String[]{Long.toString(raceResultIDToEdit)};
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case TEAM_START_ORDER_LOADER:
				projection = new String[]{TeamInfo.getTableName() + "." + TeamInfo._ID + " as _id", TeamInfo.TeamName, RaceResults.StartOrder, RaceResults.StartTimeOffset};
				selection = RaceResults.getTableName() + "." + RaceResults._ID + "=?";
				selectionArgs = new String[]{Long.toString(raceResultIDToEdit)};
				sortOrder = RaceResults.StartOrder;
				loader = new CursorLoader(getActivity(), TeamCheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_INFO_LOADER:
				projection = new String[]{Race.getTableName() + "." + Race._ID + " as _id", Race.RaceType, Race.NumLaps};
				selection = Race.getTableName() + "." + Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), Race.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case APP_SETTINGS_LOADER:
				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
				selection = null;
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACER_START_ORDER_LOADER:	
					cursor.moveToFirst();
					origStartOrder = cursor.getLong(cursor.getColumnIndex(RaceResults.StartOrder));
					txtNewOrder.setText(Long.toString(origStartOrder));
					break;
				case TEAM_START_ORDER_LOADER:
					cursor.moveToFirst();
					origStartOrder = cursor.getLong(cursor.getColumnIndex(RaceResults.StartOrder));
					txtNewOrder.setText(Long.toString(origStartOrder));
					break;
				case RACE_INFO_LOADER:	
					if(cursor!= null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set up the tab based on the race information
						raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						if(getView() != null){
							if(raceTypeID == 1){	
								teamStartOrderLoader = getActivity().getSupportLoaderManager().restartLoader(TEAM_START_ORDER_LOADER, null, this);
							}else {								
								startOrderLoader = getActivity().getSupportLoaderManager().restartLoader(RACER_START_ORDER_LOADER, null, this);
							}
						}
					}
					break;
				case APP_SETTINGS_LOADER:	
					if(startOrderLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(RACER_START_ORDER_LOADER, null, this);
					}
					if( teamStartOrderLoader != null){
						getActivity().getSupportLoaderManager().restartLoader(TEAM_START_ORDER_LOADER, null, this);
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
				case RACER_START_ORDER_LOADER:
					break;
				case TEAM_START_ORDER_LOADER:
					break;
				case RACE_INFO_LOADER:
					// Do nothing...this is only here for consistency					
					break;
				case APP_SETTINGS_LOADER:
					// Do nothing...this is only here for consistency
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
