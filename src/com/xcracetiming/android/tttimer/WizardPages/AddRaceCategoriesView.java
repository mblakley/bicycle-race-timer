package com.xcracetiming.android.tttimer.WizardPages;

import java.util.ArrayList;
import java.util.List;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.Utilities.Loaders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;


public class AddRaceCategoriesView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceCategoriesView";

	private static final int RACE_CATEGORIES_LOADER = 11116;
	
	private ArrayAdapter<String> raceCategoriesAdapter = null;
	private List<String> raceCategories = new ArrayList<String>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_add_race_categories, container, false);		
	}
	
	@Override
	protected void addListeners() {
		getButton(R.id.btnAddRaceCategory).setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		String[] columns = new String[] { RaceCategory.FullCategoryName };
        int[] to = new int[] {android.R.id.text1 };
        
		// Create the cursor adapter for the list of categories
        raceCategoriesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.control_simple_spinner, raceCategories);
        raceCategoriesAdapter.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
    	getListView(R.id.lvRaceCategories).setAdapter(raceCategoriesAdapter);

		this.getLoaderManager().initLoader(RACE_CATEGORIES_LOADER, null, this);
	}
	
	@Override 
	public int GetTitleResourceID() {
		return R.string.AddRaceCategory;
	}
	
	public void onClick(View v) { 
		try{
			if (v.getId() == R.id.btnAddRaceCategory){
				// Category Name
				String raceCategoryName = getEditText(R.id.txtRaceCategory).getText().toString();
				
				if(!raceCategories.contains(raceCategoryName)){
					// Only add the new category if it's not already in the list
					raceCategories.add(raceCategoryName);
				}else{
					// If it's in the list already, make sure it's checked
					// TODO: Make sure the item is checked
				}
				getEditText(R.id.txtRaceCategory).setText("");
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnAddRaceCategory failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
	
	private void SetupList(ListView list, ArrayAdapter<String> ca, OnItemLongClickListener listener) {	
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
				
				SetupList(getListView(R.id.lvRaceCategories), raceCategoriesAdapter, new OnItemLongClickListener(){
					public boolean onItemLongClick(AdapterView<?> arg0, View v,	int pos, long id) {
						EditRacerView editRacerDialog = new EditRacerView(id);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						editRacerDialog.show(fm, EditRacerView.LOG_TAG);
						return false;
					}
	    		});
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
					// TODO: Go through each of the loaded categories and add them to the array
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
					raceCategoriesAdapter.clear();
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
