package com.xcracetiming.android.tttimer.WizardPages;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.Utilities.Loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class AddRaceCategoriesView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceCategoriesView";

	private static final int RACE_CATEGORIES_LOADER = 11116;
	
	private Button btnAddRaceCategory;
	private SimpleCursorAdapter raceCategoriesCA = null;
	private ListView raceCategories;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_race_categories, container, false);

		btnAddRaceCategory = (Button) v.findViewById(R.id.btnAddRaceCategory);
		btnAddRaceCategory.setOnClickListener(this);		
		
		EditText txtRaceCategory = (EditText) v.findViewById(R.id.txtRaceCategory);
		txtRaceCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		        	InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    		mgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
		        }
		    }
		});
		
		raceCategories = (ListView) v.findViewById(R.id.lvRaceCategories);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		String[] columns = new String[] { RaceCategory.FullCategoryName };
        int[] to = new int[] {android.R.id.text1 };
        
		// Create the cursor adapter for the list of races
        raceCategoriesCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        raceCategoriesCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
    	raceCategories.setAdapter(raceCategoriesCA);

		this.getLoaderManager().initLoader(RACE_CATEGORIES_LOADER, null, this);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.AddRaceCategory;
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddRaceCategory){
				// First name
				EditText txtRaceCategory = (EditText) getView().findViewById(R.id.txtRaceCategory);
				String raceCategoryName = txtRaceCategory.getText().toString();
				long raceSeries_ID = Long.parseLong(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_RaceSeriesID_Name, "1"));
		
				RaceCategory.Instance().Create(getActivity(), raceCategoryName, raceCategoryName, raceCategoryName, raceCategoryName, raceCategoryName, raceSeries_ID);
				
				txtRaceCategory.setText("");
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnStartCheckIn failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_CATEGORIES_LOADER:
				projection = new String[]{RaceCategory._ID, RaceCategory.FullCategoryName};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceCategory.FullCategoryName;
				loader = new CursorLoader(getActivity(), RaceCategory.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case RACE_CATEGORIES_LOADER:					
		        	raceCategoriesCA.swapCursor(cursor);
					break;
			}
			Log.v(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case Loaders.RACE_LOCATIONS_LOADER:
					raceCategoriesCA.swapCursor(null);
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	public Bundle Save() {
		// TODO Auto-generated method stub
		return new Bundle();
	}
}
