package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public abstract class ContentProviderTable {
	
	public static final String GroupBy = "groupBy";
	public static final String Having = "having";
	public static final String Limit = "limit";
	public static final String Distinct = "distinct";
	
	public final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, getClassName());
	
	public String getTableName(){
    	return this.getClass().getSimpleName(); 
    }
    
    public String getClassName(){
    	return this.getClass().getCanonicalName();
    }
    
    public String getColumnName(String column){
    	return getTableName() + "." + column;
    }
	
	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
		ArrayList<Uri> notifyUris = new ArrayList<Uri>();
		notifyUris.add(CONTENT_URI);
		
		return notifyUris;
	}
	
	public Uri Create(Context context, ContentValues content){
		return context.getContentResolver().insert(CONTENT_URI, content);
	}
	
	public Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder){
		return context.getContentResolver().query(CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
	}
	
	public int Update(Context context, ContentValues content, String selection, String[] selectionArgs){
		return context.getContentResolver().update(CONTENT_URI, content, selection, selectionArgs);
	}
	
	public int Delete(Context context, String selection, String[] selectionArgs){
		return context.getContentResolver().delete(CONTENT_URI, selection, selectionArgs);
	}
}
