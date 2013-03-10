package com.xcracetiming.android.tttimer.WizardPages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.CursorAdapters.CheckedItemArrayAdapter;
import com.xcracetiming.android.tttimer.DataAccess.CheckedItem;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeriesRaceCategories;
import com.xcracetiming.android.tttimer.DataAccess.Views.SeriesRaceCategoriesView;
import com.xcracetiming.android.tttimer.Utilities.QueryUtilities.SelectBuilder;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddRaceCategoriesView extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddRaceCategoriesView";

	// TODO: Move this
	private static final int ALL_RACE_CATEGORIES_LOADER = 11116;
	
	private CheckedItemArrayAdapter raceCategoriesAdapter = null;
	private Map<String, CheckedItem> raceCategories = new HashMap<String, CheckedItem>();
	private List<String> newRaceCategories = new ArrayList<String>();
	private List<Long> selectedRaceCategories = new ArrayList<Long>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_add_race_categories, container, false);		
	}
	
	// TODO: WARNING - This page should display all categories for each of the race series' for the given race_ID.  You can have multiple series associated with a race.
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		// TODO: Set newRaceCategories and selectedRaceCategories from bundle
		//newRaceCategories.addAll(args.getStringArray(RaceCategory.FullCategoryName));
	}
	
	@Override
	protected void addListeners() {
		getButton(R.id.btnAddRaceCategory).setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();		
        
		CheckedItem[] items = new CheckedItem[3];
		items[0] = new CheckedItem("A");
		items[1] = new CheckedItem("B4");
		items[2] = new CheckedItem("B5");
		
		newRaceCategories.add("A");
		newRaceCategories.add("B4");
		newRaceCategories.add("B5");
		
		// Create the array adapter for the list of categories
        raceCategoriesAdapter = new CheckedItemArrayAdapter(getActivity(), R.id.text, items);//(CheckedItem[])raceCategories.keySet().toArray());        
    	getListView(R.id.lvRaceCategories).setAdapter(raceCategoriesAdapter);

    	Bundle args = getArguments();
    	if(!getArguments().containsKey(RaceSeries.Instance().getTableName()+RaceSeries._ID)){
    		args.putLong(RaceSeries.Instance().getTableName()+RaceSeries._ID, -1l);
    	}
		//this.getLoaderManager().initLoader(ALL_RACE_CATEGORIES_LOADER, args, this);
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
				
				if(raceCategories.get(raceCategoryName) == null){
					// Only add the new category if it's not already in the list
					raceCategories.put(raceCategoryName, new CheckedItem(raceCategoryName));
					newRaceCategories.add(raceCategoryName);
				} 
				
				// Make sure it's checked
				// If we found a race category with the same name already in the list, this will select it (it didn't need to be added as a new category)
				raceCategories.get(raceCategoryName).IsChecked = true;
				
				getEditText(R.id.txtRaceCategory).setText("");
				
				raceCategoriesAdapter.notifyDataSetChanged();
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
	
//	private void SetupList(ListView list, ArrayAdapter<String> ca, OnItemLongClickListener listener) {	
//		if(getView() != null){
//	        if( list != null){
//	        	list.setAdapter(ca);
//	        	
//	        	list.setFocusable(true);
//	        	list.setClickable(true);
//	        	list.setItemsCanFocus(true);
//				
//	        	list.setOnItemLongClickListener( listener );
//	        }
//		}
//	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.v(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case ALL_RACE_CATEGORIES_LOADER:
				projection = new String[]{RaceCategory._ID, RaceCategory.FullCategoryName};
				selection = SelectBuilder.Where(RaceCategory.Instance().getColumnName(RaceSeriesRaceCategories.RaceSeries_ID)).EqualsParameter().toString();
				selectionArgs = new String[]{Long.toString(this.getArguments().getLong(RaceSeries.Instance().getTableName()+RaceSeries._ID))};
				sortOrder = RaceCategory.FullCategoryName;
				loader = new CursorLoader(getActivity(), SeriesRaceCategoriesView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case ALL_RACE_CATEGORIES_LOADER:
					// Go through each of the loaded categories and add them to the array
					cursor.moveToFirst();
					while(cursor.moveToNext()){
						String catName = cursor.getString(cursor.getColumnIndex(RaceCategory.FullCategoryName));
						if(raceCategories.get(catName) == null){
							long category_ID = cursor.getLong(cursor.getColumnIndex(RaceCategory._ID));
							// Create a category in the list
							raceCategories.put(catName, new CheckedItem(catName, category_ID));						
						}
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
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_RACE_CATEGORIES_LOADER:
					raceCategoriesAdapter.clear();
					break;
			}
			Log.v(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}

	public Bundle Save() {
		Bundle b = getArguments();
		
		String[] newCats = new String[3];
		newCats[0] = "A";
		newCats[1] = "B4";
		newCats[2] = "B5";
		
		if(b == null){
			b = new Bundle();
		}
		
		b.putStringArray("NewRaceCategories", newCats);//(String[])newRaceCategories.toArray());
		b.putLongArray("SelectedRaceCategories", convertLongs(selectedRaceCategories));
		
		return b;
	}
	
	public long[] convertLongs(List<Long> longs)
	{
	    long[] ret = new long[longs.size()];
	    Iterator<Long> iterator = longs.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
}
