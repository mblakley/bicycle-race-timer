package com.xcracetiming.android.tttimer.Tabs;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.CursorAdapters.StableSimpleCursorAdapter;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.LookupGroups;
import com.xcracetiming.android.tttimer.DataAccess.RaceNotes;

public class OtherTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String OtherTabSpecName =  "OtherTab";
	private static final int RACE_NOTES_LOADER = 0x176;
	private static final int APP_SETTINGS_LOADER_OTHER = 0x117;
	private static final int ALL_HUMIDITY_LOADER = 1110;
	
	private Spinner spinHumidity;
	private StableSimpleCursorAdapter humidityCA;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = null;
		try{
	        // Inflate the layout for this fragment
	        view = inflater.inflate(R.layout.tab_other, container, false);

	        spinHumidity = (Spinner) view.findViewById(R.id.spinnerHumidity);			
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onCreateView error", ex); 
		}
        return view;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		String[] columns = new String[] { LookupGroups.LookupValue };
        int[] to = new int[] {android.R.id.text1 };
        
		// Create the cursor adapter for the list of races
        humidityCA = new StableSimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        humidityCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
        spinHumidity.setAdapter(humidityCA);
		
        getActivity().getSupportLoaderManager().initLoader(ALL_HUMIDITY_LOADER, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		SaveNotes();
		getActivity().getSupportLoaderManager().destroyLoader(APP_SETTINGS_LOADER_OTHER);
		getActivity().getSupportLoaderManager().destroyLoader(ALL_HUMIDITY_LOADER);
		getActivity().getSupportLoaderManager().destroyLoader(RACE_NOTES_LOADER);
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

			Long race_ID = AppSettings.Instance().ReadLongValue(getActivity(), AppSettings.AppSetting_RaceID_Name, null);
	     	// Save weather and notes info	 			
 			EditText txtWeatherNotes = (EditText) getView().findViewById(R.id.txtWeatherNotes);
    		String weatherNotes = txtWeatherNotes.getText().toString();

 			EditText txtOtherNotes = (EditText) getView().findViewById(R.id.txtOtherNotes);
    		String otherNotes = txtOtherNotes.getText().toString();

 			EditText txtTemperature = (EditText) getView().findViewById(R.id.txtTemperature);
    		Integer temperature = null;
    		if(txtTemperature.getText().length() > 0){
    			temperature = Integer.parseInt(txtTemperature.getText().toString());
    		}

 			EditText txtWindSpeed = (EditText) getView().findViewById(R.id.txtWindSpeed);
 			Integer windSpeed = null;
 			if(txtWindSpeed.getText().length() > 0){
 				windSpeed = Integer.parseInt(txtWindSpeed.getText().toString());
 			}

 			EditText txtWindDirection = (EditText) getView().findViewById(R.id.txtWindDirection);
    		String windDirection = txtWindDirection.getText().toString();

 			Spinner spinHumidity = (Spinner) getView().findViewById(R.id.spinnerHumidity);
    		Long humidity = spinHumidity.getSelectedItemId();
    		
    		// If raceNotes already exists, do an update
    		RaceNotes.Instance().Update(getActivity(), race_ID, weatherNotes, temperature, windSpeed, windDirection, humidity, otherNotes, true);
		} catch(Exception ex){Log.e(LOG_TAG(), "SaveNotes failed", ex);}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.v(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case ALL_HUMIDITY_LOADER:
				projection = new String[]{LookupGroups._ID, LookupGroups.LookupGroup, LookupGroups.LookupValue};
				selection = LookupGroups.LookupGroup + "='" + LookupGroups.Lookup_Group_Humidity + "'";
				selectionArgs = null;
				sortOrder = LookupGroups.LookupValue;
				loader = new CursorLoader(getActivity(), LookupGroups.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_NOTES_LOADER:
				projection = new String[]{RaceNotes.WeatherNotes, RaceNotes.Temperature, RaceNotes.WindSpeed, RaceNotes.WindDirection, RaceNotes.Humidity, RaceNotes.OtherNotes};
				selection = RaceNotes.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = RaceNotes.Race_ID;
				loader = new CursorLoader(getActivity(), RaceNotes.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case APP_SETTINGS_LOADER_OTHER:
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
				case ALL_HUMIDITY_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						humidityCA.swapCursor(cursor);
					}
					getActivity().getSupportLoaderManager().restartLoader(RACE_NOTES_LOADER, null, this);
					getActivity().getSupportLoaderManager().restartLoader(APP_SETTINGS_LOADER_OTHER, null, this);
					break;
				case RACE_NOTES_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						try{
						String weatherNotes = cursor.getString(cursor.getColumnIndex(RaceNotes.WeatherNotes));
						Integer temperature = cursor.getInt(cursor.getColumnIndex(RaceNotes.Temperature));
						Integer windSpeed = cursor.getInt(cursor.getColumnIndex(RaceNotes.WindSpeed));
						String windDirection = cursor.getString(cursor.getColumnIndex(RaceNotes.WindDirection));
						Long humidity = cursor.getLong(cursor.getColumnIndex(RaceNotes.Humidity));
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
			 			for(int count = 0; count < spinHumidity.getCount(); count++){
			 				long id = spinHumidity.getItemIdAtPosition(count);
			 				if(id == humidity){
			 					spinHumidity.setSelection(count);
			 					break;
			 				}
			 			}
						}catch(Exception ex){
							Log.e(LOG_TAG(), "RACE_NOTES_LOADER error", ex); 
						}
					}
					break;
				case APP_SETTINGS_LOADER_OTHER:
					getActivity().getSupportLoaderManager().restartLoader(RACE_NOTES_LOADER, null, this);
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
				case RACE_NOTES_LOADER:
					break;
				case APP_SETTINGS_LOADER_OTHER:
					// Do nothing...this is only here for consistency
					break;
			}
			Log.v(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}
}
