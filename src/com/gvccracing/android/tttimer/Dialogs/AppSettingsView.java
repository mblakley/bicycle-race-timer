package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;

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
import android.widget.Spinner;
import android.widget.TextView;

public class AppSettingsView extends BaseDialog implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
	public static final String LOG_TAG = "AppSettingsView";

	private static final int TEMPERATURE_UNITS_LOADER = 32;

	private static final int DISTANCE_UNITS_LOADER = 33;
	
	private Button btnSaveSettings;
	private Spinner distanceUnits;
	private Spinner temperatureUnits;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_settings, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.AppSettings);
		titleView.setTextAppearance(getActivity(), R.style.Large);

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
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		getActivity().getSupportLoaderManager().initLoader(TEMPERATURE_UNITS_LOADER, null, this);
		getActivity().getSupportLoaderManager().initLoader(DISTANCE_UNITS_LOADER, null, this);
	}

	public void onClick(View v) { 
		try{
			if (v == btnSaveSettings)
			{
				Integer distanceUnitID = distanceUnits.getSelectedItemPosition();
				AppSettings.Update(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, Integer.toString(distanceUnitID), true);
				
				// Set the race type from the selection
				Integer temperatureUnitID = temperatureUnits.getSelectedItemPosition();
				AppSettings.Update(getActivity(), AppSettings.AppSetting_TemperatureUnits_Name, Integer.toString(temperatureUnitID), true);
				
				// Hide the dialog
		    	dismiss();
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnSaveSettings failed",ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		switch(id){
			case TEMPERATURE_UNITS_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSetting_TemperatureUnits_Name};
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, null);
				break;
			case DISTANCE_UNITS_LOADER:
				projection = new String[]{AppSettings._ID, AppSettings.AppSettingValue};
				selection = AppSettings.AppSettingName + "=?";
				selectionArgs = new String[]{AppSettings.AppSetting_DistanceUnits_Name};
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, null);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));

			if(cursor != null && cursor.getCount() > 0){
				cursor.moveToFirst();
				switch(loader.getId()){
					case TEMPERATURE_UNITS_LOADER:
						// Set the selected value of the drop down
						Integer tempUnit = cursor.getInt(cursor.getColumnIndex(AppSettings.AppSettingValue));
	//					ArrayAdapter<Object> myAdap = (ArrayAdapter<Object>) temperatureUnits.getAdapter(); //cast to an ArrayAdapter
	//
	//					int tempPos = myAdap.getPosition(tempUnit);
						temperatureUnits.setSelection(tempUnit, false);
						break;
					case DISTANCE_UNITS_LOADER:
						// Set the selected value of the drop down
						Integer distUnit = cursor.getInt(cursor.getColumnIndex(AppSettings.AppSettingValue));
	//					ArrayAdapter<Object> distAdap = (ArrayAdapter<Object>) temperatureUnits.getAdapter(); //cast to an ArrayAdapter
	//
	//					int distPos = distAdap.getPosition(distUnit);
						distanceUnits.setSelection(distUnit, false);
						break;
				}
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
				case TEMPERATURE_UNITS_LOADER:
					temperatureUnits.setSelection(0, false);
					break;
				case DISTANCE_UNITS_LOADER:
					temperatureUnits.setSelection(0, false);
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
