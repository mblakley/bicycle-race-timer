package com.gvccracing.android.tttimer.Tabs;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceNotesCP.RaceNotes;
import com.gvccracing.android.tttimer.Utilities.UploadToDropBox;

public class OtherTab extends BaseTab implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	public static final String OtherTabSpecName =  "OtherTab";
	private static final int RACE_NOTES_LOADER = 0x176;
	private static final int APP_SETTINGS_LOADER_OTHER = 0x117;
	
	private Spinner spinHumidity;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = null;
		try{
	        // Inflate the layout for this fragment
	        view = inflater.inflate(R.layout.tab_other, container, false);
	        
	        ((Button) view.findViewById(R.id.btnSaveNotesResults)).setOnClickListener(this);
	        ((Button) view.findViewById(R.id.btnSubmitAllResults)).setOnClickListener(this);

	        spinHumidity = (Spinner) view.findViewById(R.id.spinnerHumidity);			
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onCreateView error", ex); 
		}
        return view;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		getActivity(), R.array.humidity_array, android.R.layout.simple_spinner_item );
 		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
 		spinHumidity.setAdapter(adapter);
		
		getActivity().getSupportLoaderManager().initLoader(RACE_NOTES_LOADER, null, this);
		getActivity().getSupportLoaderManager().initLoader(APP_SETTINGS_LOADER_OTHER, null, this);
	}
	
	@Override
	public String TabSpecName() {
		return OtherTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return OtherTabSpecName;
	} 

	public void SaveNotes(){
		try{
			Log.v(LOG_TAG(), "SaveNotes");

			Long race_ID = Long.parseLong(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_RaceID_Name, "-1"));
	     	// Save weather and notes info	 			
 			EditText txtWeatherNotes = (EditText) getView().findViewById(R.id.txtWeatherNotes);
    		String weatherNotes = txtWeatherNotes.getText().toString();

 			EditText txtOtherNotes = (EditText) getView().findViewById(R.id.txtOtherNotes);
    		String otherNotes = txtOtherNotes.getText().toString();

 			EditText txtTemperature = (EditText) getView().findViewById(R.id.txtTemperature);
    		Integer temperature = Integer.parseInt(txtTemperature.getText().toString());

 			EditText txtWindSpeed = (EditText) getView().findViewById(R.id.txtWindSpeed);
    		Integer windSpeed = Integer.parseInt(txtWindSpeed.getText().toString());

 			EditText txtWindDirection = (EditText) getView().findViewById(R.id.txtWindDirection);
    		String windDirection = txtWindDirection.getText().toString();

 			Spinner spinHumidity = (Spinner) getView().findViewById(R.id.spinnerHumidity);
    		Integer humidity = getHumidityID(spinHumidity.getSelectedItem().toString());
    		
    		// If raceNotes already exists, do an update
    		RaceNotes.Update(getActivity(), race_ID, weatherNotes, temperature, windSpeed, windDirection, humidity, otherNotes, true);
		} catch(Exception ex){Log.e(LOG_TAG(), "SaveNotes failed", ex);}
	}
	
	private Integer getHumidityID(String humidityDescription) {
		Integer humidityID = null;
		if(humidityDescription == "Dry")
		{
			humidityID = 1;
		}else if(humidityDescription == "Moderate"){	
			humidityID = 2;
		}else if(humidityDescription == "Humid"){
			humidityID = 3;
		}else if(humidityDescription == "Raining"){
			humidityID = 4;
		}
		
		return humidityID;
	}
	
	public void onClick(View v) {
		try {
			if (v.getId() == R.id.btnSaveNotesResults) {
		        if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
		        	SaveNotes();
		        }else{
		        	Toast.makeText(getActivity(), "Unable to save race notes.  Please login as administrator", Toast.LENGTH_LONG).show();
		        }
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG(), "OtherTab.onClick failed", ex);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case RACE_NOTES_LOADER:
				projection = new String[]{RaceNotes.WeatherNotes, RaceNotes.Temperature, RaceNotes.WindSpeed, RaceNotes.WindDirection, RaceNotes.Humidity, RaceNotes.OtherNotes};
				selection = RaceNotes.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceNotes.Race_ID;
				loader = new CursorLoader(getActivity(), RaceNotes.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case APP_SETTINGS_LOADER_OTHER:
				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
				selection = null;
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_NOTES_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						try{
						String weatherNotes = cursor.getString(cursor.getColumnIndex(RaceNotes.WeatherNotes));
						Integer temperature = cursor.getInt(cursor.getColumnIndex(RaceNotes.Temperature));
						Integer windSpeed = cursor.getInt(cursor.getColumnIndex(RaceNotes.WindSpeed));
						String windDirection = cursor.getString(cursor.getColumnIndex(RaceNotes.WindDirection));
						Integer humidity = cursor.getInt(cursor.getColumnIndex(RaceNotes.Humidity));
						String otherNotes = cursor.getString(cursor.getColumnIndex(RaceNotes.OtherNotes));
						
						EditText txtWeatherNotes = (EditText) getView().findViewById(R.id.txtWeatherNotes);
			    		txtWeatherNotes.setText(weatherNotes);

			 			EditText txtOtherNotes = (EditText) getView().findViewById(R.id.txtOtherNotes);
			    		txtOtherNotes.setText(otherNotes);

			 			EditText txtTemperature = (EditText) getView().findViewById(R.id.txtTemperature);
			    		txtTemperature.setText(temperature.toString());

			 			EditText txtWindSpeed = (EditText) getView().findViewById(R.id.txtWindSpeed);
			    		txtWindSpeed.setText(windSpeed.toString());

			 			EditText txtWindDirection = (EditText) getView().findViewById(R.id.txtWindDirection);
			    		txtWindDirection.setText(windDirection);

			 			Spinner spinHumidity = (Spinner) getView().findViewById(R.id.spinnerHumidity);
			    		spinHumidity.setSelection(humidity);
						}catch(Exception ex){
							Log.e(LOG_TAG(), "RACE_NOTES_LOADER error", ex); 
						}
					}
					break;
				case APP_SETTINGS_LOADER_OTHER:
					if(getView() != null){
						if(Boolean.parseBoolean(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
				        	((Button) getView().findViewById(R.id.btnSaveNotesResults)).setVisibility(View.VISIBLE);
				        	((Button) getView().findViewById(R.id.btnSubmitAllResults)).setVisibility(View.VISIBLE);
				        }else{
				        	((Button) getView().findViewById(R.id.btnSaveNotesResults)).setVisibility(View.GONE);
				        	((Button) getView().findViewById(R.id.btnSubmitAllResults)).setVisibility(View.GONE);
				        }
					}
					//getActivity().getSupportLoaderManager().restartLoader(RACE_NOTES_LOADER, null, this);
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
				case RACE_NOTES_LOADER:
					break;
				case APP_SETTINGS_LOADER_OTHER:
					// Do nothing...this is only here for consistency
					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}
}
