package com.xcracetimer.android.tttimer.Dialogs;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.xcracetimer.android.tttimer.DataAccess.LocationImagesCP.LocationImages;
import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.xcracetimer.android.tttimer.Utilities.ImageFormatter;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MarshalLocations extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "MarshalLocations";

	private static final int RACE_LOCATION_IMAGES_LOADER = 1113;

	private static final int RACE_INFO_LOADER = 1114;
	
	private Cursor raceLocationImages;
	private int selectedImageIndex = 0;
	private long raceLocation_ID = 0;
	
	ImageView imgLocationImage;
	TextView lblLocationNotes;
	ImageButton btnPrevImage;
	ImageButton btnNextImage;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_marshal_locations, container, false);
	    super.onCreate(savedInstanceState);
	    
	    imgLocationImage = (ImageView) v.findViewById(R.id.imgLocationImage);
	    
	    lblLocationNotes = (TextView) v.findViewById(R.id.lblLocationNotes);
	    
	    btnPrevImage = (ImageButton) v.findViewById(R.id.btnPrevImage);
		btnPrevImage.setOnClickListener(this);		

		btnNextImage = (ImageButton) v.findViewById(R.id.btnNextImage);
		btnNextImage.setOnClickListener(this);
	    
	    return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// Initialize the cursor loader for the races list
		this.getLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.MarshalLocation;
	}

	public void onClick(View v) { 
		try{
			if(v == btnPrevImage){
				// Go to the previous image in the list
				selectedImageIndex--;
				SetupImage();
			} else if(v == btnNextImage){
				// Go to the next image in the list
				selectedImageIndex++;
				SetupImage();		
			} else{
				//The cancel button was pressed
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
	
	private void SetupImage(){
		if(raceLocationImages.getCount() > 0){
			raceLocationImages.moveToPosition(selectedImageIndex);
			imgLocationImage.setVisibility(View.VISIBLE);
			byte[] imageBytes = raceLocationImages.getBlob(raceLocationImages.getColumnIndex(LocationImages.Image));
			Bitmap imageBmp = ImageFormatter.GetScaledImageFromBytes(imageBytes);
			imgLocationImage.setImageBitmap(imageBmp);
			lblLocationNotes.setText(raceLocationImages.getString(raceLocationImages.getColumnIndex(LocationImages.Notes)));
			
			boolean prevImageVisible = selectedImageIndex > 0;
			boolean nextImageVisible = selectedImageIndex < raceLocationImages.getCount() - 1;
			btnPrevImage.setVisibility(prevImageVisible? View.VISIBLE : View.INVISIBLE);
			btnNextImage.setVisibility(nextImageVisible? View.VISIBLE : View.INVISIBLE);
		} else{
			imgLocationImage.setVisibility(View.GONE);
			lblLocationNotes.setText(R.string.NoMarshalLocationImages);
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case RACE_INFO_LOADER:
				projection = new String[]{RaceMeet._ID, RaceMeet.RaceLocation_ID};
				selection = Race._ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race._ID;
				loader = new CursorLoader(getActivity(), RaceMeet.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case RACE_LOCATION_IMAGES_LOADER:
				projection = new String[]{LocationImages._ID, LocationImages.Notes, LocationImages.Image, LocationImages.RaceLocation_ID};
				selection = LocationImages.RaceLocation_ID + "=?" ;
				selectionArgs = new String[]{Long.toString(raceLocation_ID)};
				sortOrder = LocationImages.Notes + " DESC";
				loader = new CursorLoader(getActivity(), LocationImages.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					raceLocation_ID = cursor.getLong(cursor.getColumnIndex(RaceMeet.RaceLocation_ID));
					this.getLoaderManager().restartLoader(RACE_LOCATION_IMAGES_LOADER, null, this);
					break;
				case RACE_LOCATION_IMAGES_LOADER:					
					raceLocationImages = cursor;
					if(raceLocationImages.getCount() > 0){
						selectedImageIndex = 0;
						SetupImage();
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
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					break;
				case RACE_LOCATION_IMAGES_LOADER:
					raceLocationImages = null;
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}