package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;

import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.xcracetiming.android.tttimer.DataAccess.Views.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

// BaseColumn contains _id.
public final class AppSettings extends ContentProviderTable implements BaseColumns {

    private static final AppSettings instance = new AppSettings();
    
    public AppSettings() {}
 
    public static AppSettings Instance() {
        return instance;
    }

    // Table column
    public static final String AppSettingName = "AppSettingName";
    public static final String AppSettingValue = "AppSettingValue";
    
    // Values
    public static final String AppSetting_DistanceUnits_Name = "DistanceUnits";
    public static final String AppSetting_TemperatureUnits_Name = "TemperatureUnits";
    public static final String AppSetting_RaceID_Name = "Race_ID";
	public static final String AppSetting_StartInterval_Name = "StartInterval";
	public static final String AppSetting_AdminMode_Name = "AdminMode";
	public static final String AppSetting_DropBox_Key_Name = "DropBoxKey";
	public static final String AppSetting_DropBox_Secret_Name = "DropBoxSecret";
	public static final String AppSettings_AutoCheckIn_Name = "AutoCheckIn";
	public static final String AppSettings_AutoStartApp_Name = "AutoStartApp";
	public static final String AppSetting_AuthenticatingDropbox_Name = "AuthenticatingDropbox";
	public static final String AppSetting_ResumePreviousState_Name = "ResumePreviousState";
	public static final String AppSetting_RaceSeriesID_Name = "RaceSeries_ID";           
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + AppSettingName + " text not null, " 
    	        + AppSettingValue + " text not null);";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> uriList = super.getAllUrisToNotifyOnChange();
    	uriList.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	uriList.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return uriList;
    }
    
    public String getParameterSql(String paramName){        	
    	return "(" + SQLiteQueryBuilder.buildQueryString(true, AppSettings.Instance().getTableName(), new String[]{AppSettings.AppSettingValue}, 
    										 AppSettings.AppSettingName + "='" + paramName + "'", null, null, AppSettings.AppSettingName, "1") + ")";        
    }

	public int Update(Context context, String appSettingName, String appSettingValue, boolean addIfNotExist) {
		ContentValues content = new ContentValues();
        if(appSettingValue != null)
        {
        	content.put(AppSettings.AppSettingValue, appSettingValue);
        }
        Integer numChanged = 0;
        try{
        	numChanged = context.getContentResolver().update(CONTENT_URI, content, AppSettings.AppSettingName + "=?", new String[]{appSettingName});
        }catch(Exception ex){
        	
        }
		if(addIfNotExist && (numChanged == null || numChanged < 1)){
			content.put(AppSettingName, appSettingName);
			AppSettings.Instance().Create(context, content);
			numChanged = 1;
		}
		
		return numChanged;
	}
	
	public int UpdateLong(Context context, String appSettingName, Long appSettingValue, boolean addIfNotExist) {
		ContentValues content = new ContentValues();
        if(appSettingValue != null)
        {
        	content.put(AppSettings.AppSettingValue, appSettingValue);
        }else{
        	content.putNull(AppSettings.AppSettingValue);
        }
        Integer numChanged = 0;
        try{
        	numChanged = context.getContentResolver().update(CONTENT_URI, content, AppSettings.AppSettingName + "=?", new String[]{appSettingName});
        }catch(Exception ex){
        	
        }
		if(addIfNotExist && (numChanged == null || numChanged < 1)){
			content.put(AppSettingName, appSettingName);
			AppSettings.Instance().Create(context, content);
			numChanged = 1;
		}
		
		return numChanged;
	}

	public Cursor Read(Context context, String appSettingToRetrieve) {
		return context.getContentResolver().query(CONTENT_URI, new String[]{AppSettings.AppSettingValue, AppSettings._ID}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);  //new String[]{AppSettings.AppSettingValue}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);
	}
	
	public String ReadValue(Context context, String appSettingToRetrieve, String defaultValue) {
		String val = defaultValue;
		try{
			Cursor temp = AppSettings.Instance().Read(context, appSettingToRetrieve);

			if(temp != null && temp.getCount() > 0){
				temp.moveToFirst();
				val = temp.getString(0);
			}
			temp.close();
			temp = null;
		}catch(Exception ex){
			Log.e(AppSettings.Instance().getTableName(), "Unexpected error reading " + appSettingToRetrieve, ex);
		}
		return val;
	}
	
	public Long ReadLongValue(Context context, String appSettingToRetrieve, Long defaultValue) {
		Long val = defaultValue;
		try{
			Cursor temp = AppSettings.Instance().Read(context, appSettingToRetrieve);

			if(temp != null && temp.getCount() > 0){
				temp.moveToFirst();
				val = temp.getLong(0);
			}
			temp.close();
			temp = null;
		}catch(Exception ex){
			Log.e(AppSettings.Instance().getTableName(), "Unexpected error reading " + appSettingToRetrieve, ex);
		}
		return val;
	}
	
	public boolean ReadBooleanValue(Context context, String appSettingToRetrieve, String defaultValue) {		
		return Boolean.parseBoolean(ReadValue(context, AppSettings.AppSetting_AdminMode_Name, "false"));
	}
	
	public int Delete(Context context, String appSettingName){
		return context.getContentResolver().delete(AppSettings.Instance().CONTENT_URI, AppSettings.AppSettingName + "=?", new String[]{appSettingName});	
	}
}
