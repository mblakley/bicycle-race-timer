package com.xcracetiming.android.tttimer.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageFormatter {
	private static DropboxAPI<AndroidAuthSession> mDBApi;
	/**
     * Used for dropbox
     */
    final static private String APP_KEY = "6c113yzcd8p714m";
    final static private String APP_SECRET = "j0thz9yz7w1u80z";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
	public static byte[] GetByteArrayFromImage(Bitmap image) {
//		int size = image.getRowBytes() * image.getHeight();
//		ByteBuffer buffer = ByteBuffer.allocate(size);
//		image.copyPixelsToBuffer(buffer);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 0, outputStream);
	    
		return outputStream.toByteArray();//buffer.array();
	}
	
	public static byte[] GetImageBytesFromDropBox(String imageLocation, Context context, ProgressListener listener){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			AccessTokenPair access = new AccessTokenPair(AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_DropBox_Key_Name, null), AppSettings.Instance().ReadValue(context, AppSettings.AppSetting_DropBox_Secret_Name, null));
			mDBApi.getSession().setAccessTokenPair(access);
			
		    mDBApi.getFile(imageLocation, null, outputStream, listener);
		} catch (DropboxException e) {
		    Log.e("ImageFormatter", "Something went wrong while downloading.", e);
		} finally {
		    if (outputStream != null) {
		        try {
		            outputStream.close();
		        } catch (IOException e) {}
		    }
		}
		
		return outputStream.toByteArray();
	}
	
	public static Bitmap GetScaledImageFromBytes(byte[] imageBytes){
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, o);
        
        //The new size we want to scale to
        final int REQUIRED_SIZE=150;

        //Find the correct scale value. It should be the power of 2.
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
		Bitmap b = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, o2);
		return b;
	}
	
	public static Bitmap GetImageFromBytes(byte[] imageBytes){
		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	}
	
	public static Bitmap GetImageFromDropBox(String imageLocation, Context context, ProgressListener listener){
		return GetScaledImageFromBytes(GetImageBytesFromDropBox(imageLocation, context, listener));
	}
}
