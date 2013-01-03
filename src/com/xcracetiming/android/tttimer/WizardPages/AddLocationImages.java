package com.xcracetiming.android.tttimer.WizardPages;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.LocationImages;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.Utilities.ImageFormatter;

public class AddLocationImages extends BaseWizardPage implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

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
	private ArrayList<DownloadImageTask> tasks = new ArrayList<DownloadImageTask>();
	private int selectedImageIndex = 0;
	private DownloadedImage selectedImage;
	
	private Button btnSubmitChanges;
	private Button btnRetry;
	private ToggleButton toggleKeepImage;
	private ImageButton btnPrevImage;
	private ImageButton btnNextImage;
	private EditText txtNotes;
	private TextView lblFilename;
	private TextView lblCompare;
	private ImageView imgLocationImage;
	private Spinner spinCourseName;
	private ProgressBar progressImageLoad;
	private ProgressBar progressImageCompare;
	private LinearLayout llProgress;
	private LinearLayout llViewImages;
	private LinearLayout llAllProgress;
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
		
		btnRetry = (Button) v.findViewById(R.id.btnRetry);
		btnRetry.setOnClickListener(this);

		toggleKeepImage = (ToggleButton) v.findViewById(R.id.toggleKeepImage);
		toggleKeepImage.setOnClickListener(this);
		
		txtNotes = (EditText) v.findViewById(R.id.txtNotes);
		
		lblFilename = (TextView) v.findViewById(R.id.lblFileName);
		
		imgLocationImage = (ImageView) v.findViewById(R.id.imgLocationImage);
		
		spinCourseName = (Spinner) v.findViewById(R.id.spinCourseName);
		
		progressImageLoad = (ProgressBar) v.findViewById(R.id.progressImageLoad);
		progressImageLoad.setMax(100);
		
		progressImageCompare = (ProgressBar) v.findViewById(R.id.progressImageCompare);
		
		lblCompare = (TextView) v.findViewById(R.id.lblCompare);
		
		llProgress = (LinearLayout) v.findViewById(R.id.llProgress);
		
		llViewImages = (LinearLayout) v.findViewById(R.id.llViewImages);
		
		llAllProgress = (LinearLayout) v.findViewById(R.id.llAllProgress);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		//getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		String[] columns = new String[] { RaceLocation.CourseName };
        int[] to = new int[] {android.R.id.text1 };
        
		// Create the cursor adapter for the list of race locations
        locationsCA = new SimpleCursorAdapter(getActivity(), R.layout.control_simple_spinner, null, columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        locationsCA.setDropDownViewResource( R.layout.control_simple_spinner_dropdown );
    	spinCourseName.setAdapter(locationsCA);

		// Initialize the cursor loader for the locations list
		this.getLoaderManager().restartLoader(ALL_RACE_LOCATIONS_LOADER, null, this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		this.getLoaderManager().destroyLoader(ALL_RACE_LOCATIONS_LOADER);
		
		for(DownloadImageTask task : tasks){
			task.cancel(true);
		}
		tasks.clear();
	}
	
	// Allow the user to scroll through the images and fill in info
	// When scrolling through, update the DownloadedImage object in the array list with the changes
	// When user is done, they click "Save All Changes"
	// All info from each DownloadedImage is saved to the db (or not saved, in the case of discarded images)
	// Discarded images are moved to a different folder in dropbox (?)
	
	@Override
	public void onClick(View v) { 
		try{
			if(v == btnRetry){
				progressImageCompare.setProgress(0);
				progressImageCompare.setVisibility(View.VISIBLE);
				lblCompare.setText(R.string.RetrievingDropboxImageList);
				btnRetry.setVisibility(View.GONE);
				
				// Initialize the cursor loader for the races list
				this.getLoaderManager().restartLoader(ALL_RACE_LOCATIONS_LOADER, null, this);				
			}else if(v == btnSubmitChanges){
				// Store off the state of the current image
				selectedImage.notes = txtNotes.getText().toString();
				selectedImage.raceLocation_ID = spinCourseName.getSelectedItemId();
				// Go through each image in the list and save to database (if enabled)
				for(DownloadedImage image : images.values()){
					if(image.keepImage){
						byte[] imageBytes = ImageFormatter.GetByteArrayFromImage(image.image);
						if(image.updated){
							LocationImages.Instance().Update(getActivity(), image.locationImages_ID, imageBytes, image.notes, image.raceLocation_ID, image.filename, image.revision);
						} else {
							String notes = image.notes;
							Long id = image.raceLocation_ID;
							String filename = image.filename;
							String rev = image.revision;
							if(notes == null){
								Log.e("Loop", "notes is null");
							}
							if(id == null){
								Log.e("Loop", "id is null");
							}
							if(filename == null){
								Log.e("Loop", "filename is null");
							}
							if(rev == null){
								Log.e("Loop", "rev is null");
							}
							LocationImages.Instance().Create(getActivity(), imageBytes, image.notes, image.raceLocation_ID, image.filename, image.revision);
//							String id = create.getLastPathSegment();
//							Cursor afterInsert = LocationImages.Read(getActivity(), new String[]{LocationImages.Image}, LocationImages._ID + "=" + id, null, null);
//							afterInsert.moveToFirst();
//							byte[] afterBytes = afterInsert.getBlob(0);
//							if(afterBytes != imageBytes){
//								Log.e("Wrong", "found a difference after insert");
//							}
						}
					}
				}
				dismiss();
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
		if(selectedImage.image != null){
			imgLocationImage.setVisibility(View.VISIBLE);
			llProgress.setVisibility(View.GONE);
			imgLocationImage.setImageBitmap(selectedImage.image);
		}else{
			imgLocationImage.setVisibility(View.GONE);
			llProgress.setVisibility(View.VISIBLE);	
			// Cancel the async task, since we're about to get the image synchronously
			DownloadImageTask task = tasks.get(selectedImageIndex);
			if(!task.running && !task.isComplete()){
				progressImageLoad.setProgress(0);
				task.execute();
			}else{
				progressImageLoad.setProgress(task.progress);
			}
		}
		String filename = selectedImage.filename;
		if(selectedImage.updated){
			filename += " - Update";
		}
		lblFilename.setText(filename);
		if(selectedImage.raceLocation_ID != null){
			SetRaceLocationSelectionByID(spinCourseName, selectedImage.raceLocation_ID);
		}
		toggleKeepImage.setChecked(selectedImage.keepImage);
		SetupToggleButton(selectedImage.keepImage);

		boolean prevImageVisible = selectedImageIndex > 0;
		boolean nextImageVisible = selectedImageIndex < images.size() - 1;
		btnPrevImage.setVisibility(prevImageVisible? View.VISIBLE : View.INVISIBLE);
		btnNextImage.setVisibility(nextImageVisible? View.VISIBLE : View.INVISIBLE);
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Integer, Void> {	
		private int imageIndex;
		public int progress = 0;
		public boolean running = false;
		private boolean complete = false;
		
		ProgressListener dropboxFileProgressListener = new ProgressListener(){
            @Override
            public void onProgress(long bytesSent, long total) {
                progress = (int) ((bytesSent * 100)/total); 
                if(progress > 5){
                	publishProgress(new Integer[]{progress});
                }
            }
		};
			
		public boolean isComplete(){
			return complete;
		}
		
		public DownloadImageTask(Integer index){
			imageIndex = index;
		}
		
		@Override
		protected Void doInBackground(Void... notUsed) {
			running = true;
            publishProgress(new Integer[]{5});
			DownloadedImage img = images.get(images.keySet().toArray()[imageIndex]);
			img.image = ImageFormatter.GetImageFromDropBox("/LocationImages/" + img.filename, getActivity(), dropboxFileProgressListener);
			complete = true;
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progressParam) {
			if(imageIndex == selectedImageIndex){
				super.onProgressUpdate(progressParam);
				progressImageLoad.setProgress(progressParam[0]);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			running = false;
			if(progress == 100){
				complete = false;
			}
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			if(imageIndex == selectedImageIndex){
				DownloadedImage img = images.get(images.keySet().toArray()[imageIndex]);
				imgLocationImage.setVisibility(View.VISIBLE);
				llProgress.setVisibility(View.GONE);
				imgLocationImage.setImageBitmap(img.image);
				
				for(DownloadImageTask task : tasks){
					if(!task.running && !task.isComplete()){
						task.running = true;
						task.execute();
					}
				}
			}
			running = false;
		}
	}
	
	private class GetDropboxImagesTask extends AsyncTask<Void, Integer, Void> {
		private boolean retrievingImageList = false;
		private boolean comparingImageLists = false;
		private int progressMax;
		
		@Override
		protected void onProgressUpdate(Integer... progressParam) {
			super.onProgressUpdate(progressParam);
			if(retrievingImageList){
			    progressImageCompare.setMax(progressMax);
				lblCompare.setText(R.string.RetrievingDropboxImageList);
				retrievingImageList = false;
			}else if(comparingImageLists){
			    progressImageCompare.setMax(progressMax);
				lblCompare.setText(R.string.ComparingImageLists);				
				comparingImageLists = false;
			}
		    progressImageCompare.setProgress(progressParam[0]);
		}
		
		@Override
		protected Void doInBackground(Void... notUsed) {
			retrievingImageList = true;
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			
			AccessTokenPair access = new AccessTokenPair(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DropBox_Secret_Name, null));
			mDBApi.getSession().setAccessTokenPair(access);

		    int progress = 0;
			try {
				// Get the list of all images from the "LocationImages" folder on dropbox
			    Entry imageFiles = mDBApi.metadata("/LocationImages", 1000, null, true, null);

				// Loop through and fill in the list of images with DownloadedImage objects
			    for(Entry file : imageFiles.contents){
			    	String filename = file.fileName();
			    	String revision = file.rev;
			    	Bitmap image = null;
			    	Long raceLocation_ID = spinCourseName.getSelectedItemId();;
			    	DownloadedImage imageHolder = new DownloadedImage(image, filename, revision, true, "", raceLocation_ID, false);
			    	images.put(filename, imageHolder);
			    	publishProgress(new Integer[]{progress++});
			    }
			    
			} catch (DropboxUnlinkedException e) {
			    // User has unlinked, ask them to link again here.
			    Log.e(LOG_TAG, "User has unlinked.", e);
			} catch (DropboxException e) {
			    Log.e(LOG_TAG, "Something went wrong while uploading.", e);
			}
			
			// Get the image list from the database, and compare the lists
			comparingImageLists = true;
			
			String[] fieldsToRetrieve = new String[]{LocationImages._ID, LocationImages.DropboxFilename, LocationImages.DropboxRevision, LocationImages.Notes, LocationImages.RaceLocation_ID};
			
		    Cursor allImagesInDB = LocationImages.Instance().Read(getActivity(), fieldsToRetrieve, null, null, null);
		    progress = 0;
		    progressMax = allImagesInDB.getCount();
			allImagesInDB.moveToFirst();

			// Check the list against the list of current LocationImages in the database (filename, revision), and remove the ones that are in the db already
			while(!allImagesInDB.isAfterLast()){
				String filenameInDB = allImagesInDB.getString(allImagesInDB.getColumnIndex(LocationImages.DropboxFilename));
				// If there is an image with the same filename in the db, but different revision, mark them as "Updated" in the DownloadedImage object
				if(images.containsKey(filenameInDB)){
					// Figure out if the image is the same revision
					DownloadedImage tempImage = images.get(filenameInDB);
					String revisionInDB = allImagesInDB.getString(allImagesInDB.getColumnIndex(LocationImages.DropboxRevision));
					if(!tempImage.revision.equals(revisionInDB)){
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
		    	publishProgress(new Integer[]{progress++});
				allImagesInDB.moveToNext();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void notUsed) {
			if(images.size() > 0){	
				llAllProgress.setVisibility(View.GONE);
				llViewImages.setVisibility(View.VISIBLE);
				for(int index = 0; index < images.size(); index++){
					DownloadImageTask getImage = new DownloadImageTask(index);
					tasks.add(getImage);
				}

				// Fill in the layout with the image and metadata from the selected image (start with the first)
				SetupImage();
			}else{
				lblCompare.setText(R.string.NoNewImagesFound);
				progressImageCompare.setVisibility(View.GONE);
				btnRetry.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void SetRaceLocationSelectionByID(Spinner raceLocation, long raceCourseName_ID) {
		for (int i = 0; i < raceLocation.getCount(); i++) {
		    Long location_ID = raceLocation.getItemIdAtPosition(i);
		    if (location_ID == raceCourseName_ID) {
		    	raceLocation.setSelection(i);
		    	break;
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
			case ALL_RACE_LOCATIONS_LOADER:
				projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
				selection = null;
				selectionArgs = null;
				sortOrder = RaceLocation.CourseName;
				loader = new CursorLoader(getActivity(), RaceLocation.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.v(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.v(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case ALL_RACE_LOCATIONS_LOADER:					
					locationsCA.swapCursor(cursor);
					
					GetDropboxImagesTask getAllImagesTask = new GetDropboxImagesTask();
					getAllImagesTask.execute();
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
				case ALL_RACE_LOCATIONS_LOADER:
					locationsCA.swapCursor(null);
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
