package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;

public class AppSettingsView extends BaseWizardPage implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
	public static final String LOG_TAG = "AppSettingsView";

	private static final int TEMPERATURE_UNITS_LOADER = 32;

	private static final int DISTANCE_UNITS_LOADER = 33;

	private static final int AUTO_CHECKIN_LOADER = 66;

	private static final int AUTO_START_APP_LOADER = 67;
	
	private Button btnSaveSettings;
	private Spinner distanceUnits;
	private Spinner temperatureUnits;
	private CheckedTextView autoCheckIn;
	private CheckedTextView autoStartApp;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_settings, container, false);

		btnSaveSettings = (Button) v.findViewById(R.id.btnSaveSettings);
		btnSaveSettings.setOnClickListener(this);	
		  
		distanceUnits = (Spinner) v.findViewById(R.id.spinnerDistanceUnits);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		  getActivity(), R.array.distance_units_array, android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		distanceUnits.setAdapter(adapter);
		
		temperatureUnits = (Spinner) v.findViewById(R.id.spinnerTemperatureUnits);
        adapter = ArrayAdapter.createFromResource(
        		getActivity(), R.array.temperature_units_array, android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		temperatureUnits.setAdapter(adapter);
		
		autoCheckIn = (CheckedTextView) v.findViewById(R.id.chkAutoCheckIn);
		autoCheckIn.setOnClickListener(this);
		
		autoStartApp = (CheckedTextView) v.findViewById(R.id.chkAutoStartApp);
		autoStartApp.setOnClickListener(this);
		
		return v;
	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.AppSettings;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		getActivity().getSupportLoaderManager().initLoader(TEMPERATURE_UNITS_LOADER, null, this);
		getActivity().getSupportLoaderManager().initLoader(DISTANCE_UNITS_LOADER, null, this);
		getActivity().getSupportLoaderManager().initLoader(AUTO_CHECKIN_LOADER, null, this);
		getActivity().getSupportLoaderManager().initLoader(AUTO_START_APP_LOADER, null, this);
	}

	public void onClick(View v) { 
		try{
			if (v == btnSaveSettings)
			{
				Integer distanceUnitID = distanceUnits.getSelectedItemPosition();
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, Integer.toString(distanceUnitID), true);
				
				// Set the race type from the selection
				Integer temperatureUnitID = temperatureUnits.getSelectedItemPosition();
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSetting_TemperatureUnits_Name, Integer.toString(temperatureUnitID), true);
				
				boolean autoCheckInVal = autoCheckIn.isChecked();
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSettings_AutoCheckIn_Name, Boolean.toString(autoCheckInVal), true);
				
				boolean autoStartAppVal = autoStartApp.isChecked();
				AppSettings.Instance().Update(getActivity(), AppSettings.AppSettings_AutoStartApp_Name, Boolean.toString(autoStartAppVal), true);
				
				// Hide the dialog
		    	dismiss();
			} else if( v == autoCheckIn){
				autoCheckIn.setChecked(!autoCheckIn.isChecked());
			} else if( v == autoStartApp){
				autoStartApp.setChecked(!autoStartApp.isChecked());
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnSaveSettings failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		switch(id){
			case TEMPERATURE_UNITS_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSetting_TemperatureUnits_Name};
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, null);
				break;
			case DISTANCE_UNITS_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSetting_DistanceUnits_Name};
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, null);
				break;
			case AUTO_CHECKIN_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSettings_AutoCheckIn_Name};
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, null);
				break;
			case AUTO_START_APP_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSettings_AutoStartApp_Name};
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, null);
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));

			
			switch(loader.getId()){
				case TEMPERATURE_UNITS_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set the selected value of the drop down
						Integer tempUnit = cursor.getInt(cursor.getColumnIndex(AppSettings.AppSettingValue));
						temperatureUnits.setSelection(tempUnit, false);
					}
					break;
				case DISTANCE_UNITS_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						// Set the selected value of the drop down
						Integer distUnit = cursor.getInt(cursor.getColumnIndex(AppSettings.AppSettingValue));
						distanceUnits.setSelection(distUnit, false);
					}
					break;
				case AUTO_CHECKIN_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();						
						boolean autoCheckInVal = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue)));
						autoCheckIn.setChecked(autoCheckInVal);
					}else{
						// The default is "true"
						autoCheckIn.setChecked(true);
					}
					break;
				case AUTO_START_APP_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();						
						boolean autoStartAppVal = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(AppSettings.AppSettingValue)));
						autoStartApp.setChecked(autoStartAppVal);
					}else{
						// The default is "false"
						autoStartApp.setChecked(false);
					}
					break;
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
				case TEMPERATURE_UNITS_LOADER:
					temperatureUnits.setSelection(0, false);
					break;
				case DISTANCE_UNITS_LOADER:
					temperatureUnits.setSelection(0, false);
					break;
				case AUTO_CHECKIN_LOADER:
					break;
				case AUTO_START_APP_LOADER:
					break;
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

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}
