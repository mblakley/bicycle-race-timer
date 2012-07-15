package com.gvccracing.android.tttimer.Dialogs;

import java.util.HashMap;

import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.LocationImagesCP.LocationImages;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.Utilities.ImageFormatter;

public class AddLocationImages extends BaseDialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	// Used to temporarily store image info while we're editing
	private class DownloadedImage{
		public Long locationImages_ID;
		public Bitmap image;
		public String filename;
		public String revision;
		public boolean keepImage;
		public String notes;
		public Long raceLocation_ID;
		public boolean updated = false;
		
		public DownloadedImage(Bitmap image, String filename, String revision, boolean keepImage, String notes, Long raceLocation_ID, boolean updated){
			this.image = image;
			this.filename = filename;
			this.revision = revision;
			this.keepImage = keepImage;
			this.notes = notes;
			this.raceLocation_ID = raceLocation_ID;
			this.updated = updated;
		}
	}

	public static final String LOG_TAG = AddLocationImages.class.getSimpleName();
	
	private HashMap<String, DownloadedImage> images = new HashMap<String, DownloadedImage>();
	private int selectedImageIndex = 0;
	private DownloadedImage selectedImage;
	
	private Button btnSubmitChanges;
	private ToggleButton toggleKeepImage;
	private ImageButton btnPrevImage;
	private ImageButton btnNextImage;
	private EditText txtNotes;
	private TextView lblFilename;
	private ImageView imgLocationImage;
	private Spinner spinCourseName;
	private SimpleCursorAdapter locationsCA = null;

    /**
     * Used for dropbox
     */
    final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private static final int ALL_RACE_LOCATIONS_LOADER = 1112;
	
	@Override
	protected int GetTitleResourceID() {
		return R.string.GetLocationImagesFromDropbox;
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_download_new_images, container, false);

		btnSubmitChanges = (Button) v.findViewById(R.id.btnSubmitChanges);
		btnSubmitChanges.setOnClickListener(this);		

		btnPrevImage = (ImageButton) v.findViewById(R.id.btnPrevImage);
		btnPrevImage.setOnClickListener(this);		

		btnNextImage = (ImageButton) v.findViewById(R.id.btnNextImage);
		btnNextImage.setOnClickListener(this);

		toggleKeepImage = (ToggleButton) v.findViewById(R.id.toggleKeepImage);
		toggleKeepImage.setOnClickListener(this);
		
		txtNotes = (EditText) v.findViewById(R.id.txtNotes);
		
		lblFilename = (TextView) v.findViewById(R.id.lblFileName);
		
		imgLocationImage = (ImageView) v.findViewById(R.id.imgLocationImage);
		
		spinCourseName = (Spinner) v.findViewById(R.id.spinCourseName);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
		AccessTokenPair access = new AccessTokenPair(AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null));
		mDBApi.getSession().setAccessTokenPair(access);
		
		try {
			String[] columns = new String[] { RaceLocation.CourseName };
            int[] to = new int[] {android.R.id.text1 };
            
			// Create the cursor adapter for the list of race locations
            locationsCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            locationsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
        	spinCourseName.setAdapter(locationsCA);        	
			
			// Get the list of all images from the "LocationImages" folder on dropbox
		    Entry imageFiles = mDBApi.metadata("/LocationImages", 1000, null, true, null);

			// Loop through and fill in the list of images with DownloadedImage objects
		    for(Entry file : imageFiles.contents){
		    	String filename = file.fileName();
		    	String revision = file.rev;
		    	Bitmap image = ImageFormatter.GetImageFromDropBox("/LocationImages/" + filename, getActivity());
		    	Long raceLocation_ID = null;
		    	DownloadedImage imageHolder = new DownloadedImage(image, filename, revision, true, "", raceLocation_ID, false);
		    	images.put(filename, imageHolder);
		    }
		    
    		// Initialize the cursor loader for the races list
    		this.getLoaderManager().restartLoader(ALL_RACE_LOCATIONS_LOADER, null, this);
		} catch (DropboxUnlinkedException e) {
		    // User has unlinked, ask them to link again here.
		    Log.e(LOG_TAG, "User has unlinked.", e);
		} catch (DropboxException e) {
		    Log.e(LOG_TAG, "Something went wrong while uploading.", e);
		}
	}
	
	// Allow the user to scroll through the images and fill in info
	// When scrolling through, update the DownloadedImage object in the array list with the changes
	// When user is done, they click "Save All Changes"
	// All info from each DownloadedImage is saved to the db (or not saved, in the case of discarded images)
	// Discarded images are moved to a different folder in dropbox (?)
	
	@Override
	public void onClick(View v) { 
		try{
			if(v == btnSubmitChanges){
				// Go through each image in the list and save to database (if enabled)
				for(DownloadedImage image : images.values()){
					byte[] imageBytes = ImageFormatter.GetByteArrayFromImage(image.image);
					if(image.updated){
						LocationImages.Update(getActivity(), image.locationImages_ID, imageBytes, image.notes, image.raceLocation_ID, image.filename, image.revision);
					} else {
						LocationImages.Create(getActivity(), imageBytes, image.notes, image.raceLocation_ID, image.filename, image.revision);
					}
				}
			} else if(v == btnPrevImage){
				// Store off the state of the current image
				selectedImage.notes = txtNotes.getText().toString();
				selectedImage.raceLocation_ID = spinCourseName.getSelectedItemId();
				// Go to the previous image in the list
				selectedImageIndex--;
				SetupImage();
			} else if(v == btnNextImage){
				// Store off the state of the current image
				selectedImage.notes = txtNotes.getText().toString();
				selectedImage.raceLocation_ID = spinCourseName.getSelectedItemId();				
				// Go to the next image in the list
				selectedImageIndex++;
				SetupImage();			
			} else if(v == toggleKeepImage){
				// Flip the toggle
				//toggleKeepImage.setChecked(!toggleKeepImage.isChecked());
				selectedImage.keepImage = toggleKeepImage.isChecked();
				SetupToggleButton(selectedImage.keepImage);
			} else{
				super.onClick(v);
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}
	
	private void SetupToggleButton(boolean keepImage) {
		// If keeping image, just leave it alone.  Make sure all controls are enabled.
		if(keepImage){
			txtNotes.setEnabled(true);
		}else{
			// If discarding image, disable all controls.
			txtNotes.setEnabled(false);
		}
	}

	private void SetupImage() {
		// reset the text fields and toggle button state to whatever was in the hash
		selectedImage = images.get(images.keySet().toArray()[selectedImageIndex]);
		// reset the text fields and toggle button state to whatever was in the hash
		txtNotes.setText(selectedImage.notes);
		imgLocationImage.setImageBitmap(selectedImage.image);
		String filename = selectedImage.filename;
		if(selectedImage.updated){
			filename += " - Update";
		}
		lblFilename.setText(filename);
		toggleKeepImage.setChecked(selectedImage.keepImage);
		SetupToggleButton(selectedImage.keepImage);

		boolean prevImageVisible = selectedImageIndex > 0;
		boolean nextImageVisible = selectedImageIndex < images.size() - 1;
		btnPrevImage.setVisibility(prevImageVisible? View.VISIBLE : View.INVISIBLE);
		btnNextImage.setVisibility(nextImageVisible? View.VISIBLE : View.INVISIBLE);
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs;
		String sortOrder;
		switch(id){
			case ALL_RACE_LOCATIONS_LOADER:
				projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceLocation.CourseName;
				loader = new CursorLoader(getActivity(), RaceLocation.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_RACE_LOCATIONS_LOADER:					
					locationsCA.swapCursor(cursor);
					
					String[] fieldsToRetrieve = new String[]{LocationImages._ID, LocationImages.DropboxFilename, LocationImages.DropboxRevision, LocationImages.Notes, LocationImages.RaceLocation_ID};
					
				    Cursor allImagesInDB = LocationImages.Read(getActivity(), fieldsToRetrieve, null, null, null);
					allImagesInDB.moveToFirst();

					// Check the list against the list of current LocationImages in the database (filename, revision), and remove the ones that are in the db already
					while(!allImagesInDB.isAfterLast()){
						String filenameInDB = allImagesInDB.getString(allImagesInDB.getColumnIndex(LocationImages.DropboxFilename));
						// If there is an image with the same filename in the db, but different revision, mark them as "Updated" in the DownloadedImage object
						if(images.containsKey(filenameInDB)){
							// Figure out if the image is the same revision
							DownloadedImage tempImage = images.get(filenameInDB);
							String revisionInDB = allImagesInDB.getString(allImagesInDB.getColumnIndex(LocationImages.DropboxRevision));
							if(tempImage.revision != revisionInDB){
								// Update the downloadedImage
								tempImage.updated = true;
								String notes = allImagesInDB.getString(allImagesInDB.getColumnIndex(LocationImages.Notes));
								tempImage.notes = notes;
								Long raceLocation_ID = allImagesInDB.getLong(allImagesInDB.getColumnIndex(LocationImages.RaceLocation_ID));
								tempImage.raceLocation_ID = raceLocation_ID;
								Long locationImages_ID = allImagesInDB.getLong(allImagesInDB.getColumnIndex(LocationImages._ID));
								tempImage.locationImages_ID = locationImages_ID;
							} else{
								// Otherwise, they are the same, so remove it from the list
								images.remove(filenameInDB);
							}
						}
					}
					
					if(images.size() > 0){
						// Fill in the layout with the image and metadata from the selected image (start with the first)
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
				case ALL_RACE_LOCATIONS_LOADER:
					locationsCA.swapCursor(null);
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
