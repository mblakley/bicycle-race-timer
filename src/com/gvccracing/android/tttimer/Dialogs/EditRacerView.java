package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.SeriesRaceIndividualResultsView;

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
import android.widget.Toast;

public class EditRacerView extends AddRacerView implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	private static final int RACER_INFO_LOADER = 0x88;
	private Long racerClubInfo_ID;
	private Long racer_ID;
	private long initCategory;
	public EditRacerView(long racerClubInfo_ID) {
		super(false);
		
		this.racerClubInfo_ID = racerClubInfo_ID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);	

		btnAddRacer.setText(getResources().getString(R.string.SaveChanges));
		
		return v;
	}
	
	@Override
	protected int GetTitleResourceID() {
		return R.string.EditRacerInfo;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		this.getLoaderManager().initLoader(RACER_INFO_LOADER, null, this);		
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddRacer)
			{
				Log.v(LOG_TAG, "btnEditRacerClickHandler");
				
				// First name
				String firstName = txtFirstName.getText().toString();
				// Last name
				String lastName = txtLastName.getText().toString();
				// Category
				long category = spinCategory.getSelectedItemId();	
				// USACNumber
				String usacNumber = txtUSACNumber.getText().toString();
		
				if(firstName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a first name", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(lastName.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a last name", Toast.LENGTH_LONG).show();
					return;
				}

				if(usacNumber.trim().equals("")){
					Toast.makeText(getActivity(), "Please enter a USAC license number", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(UpdateRacer(firstName, lastName, category)){
					// Hide the dialog
			    	dismiss();
					
					// Set the textboxes in the dialog to an empty string
					txtFirstName.setText("");
					txtLastName.setText("");
					txtUSACNumber.setText("");
				}
			} else {
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "btnEditRacerClickHandler failed",ex);
		}
	}

	private boolean UpdateRacer(String firstName, String lastName, long category) {		
		Racer.Instance().Update(getActivity(), racer_ID, firstName, lastName, null, null, null, null);
		
		// The category has changed.  Figure out if the racer upgraded, or if the initial value was incorrect
		if(category != initCategory){
			// Ask the question
			RacerUpgraded racerUpgraded = new RacerUpgraded(racerClubInfo_ID, racer_ID, category, initCategory);
			FragmentManager fm = getActivity().getSupportFragmentManager();
			racerUpgraded.show(fm, RacerUpgraded.LOG_TAG);
		}		
		return true;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder = null;
		switch(id){
			case RACER_INFO_LOADER:
				projection = new String[]{RacerSeriesInfo.RacerUSACInfo_ID, Racer.LastName, Racer.FirstName, RacerSeriesInfo.CurrentRaceCategory_ID};
				selection = RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + "=?";
				selectionArgs = new String[]{Long.toString(racerClubInfo_ID)};
				loader = new CursorLoader(getActivity(), SeriesRaceIndividualResultsView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACER_INFO_LOADER:	
					cursor.moveToFirst();
					racer_ID = cursor.getLong(cursor.getColumnIndex(RacerSeriesInfo.RacerUSACInfo_ID)); 
					
					txtFirstName.setText(cursor.getString(cursor.getColumnIndex(Racer.FirstName)));
					txtLastName.setText(cursor.getString(cursor.getColumnIndex(Racer.LastName)));
					SetCategorySelectionByValue(cursor.getLong(cursor.getColumnIndex(RacerSeriesInfo.CurrentRaceCategory_ID)));
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	private void SetCategorySelectionByValue(long racerCategory) {
		initCategory = racerCategory;
		
		for (int i = 0; i < spinCategory.getCount(); i++) {
		    long catID = spinCategory.getItemIdAtPosition(i);
		    if (racerCategory == catID) {
		    	spinCategory.setSelection(i);
		    	break;
		    }
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case RACER_INFO_LOADER:
					// Do nothing
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
