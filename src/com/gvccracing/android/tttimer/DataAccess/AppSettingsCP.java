package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppSettingsCP {
	
    // BaseColumn contains _id.
    public static final class AppSettings implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, AppSettings.class.getSimpleName());

        // Table column
        public static final String AppSettingName = "AppSettingName";
        public static final String AppSettingValue = "AppSettingValue";
        
        // Values
        public static final String AppSetting_DistanceUnits_Name = "DistanceUnits";
        public static final String AppSetting_TemperatureUnits_Name = "TemperatureUnits";
        public static final String AppSetting_RaceID_Name = "Race_ID";
		public static final String AppSetting_AdminMode_Name = "AdminMode";
		public static final String AppSetting_DropBox_Key_Name = "DropBoxKey";
		public static final String AppSetting_DropBox_Secret_Name = "DropBoxSecret";
		public static final String AppSettings_AutoCheckIn_Name = "AutoCheckIn";
		public static final String AppSettings_AutoStartApp_Name = "AutoStartApp";
		public static final String AppSetting_AuthenticatingDropbox_Name = "AuthenticatingDropbox";
		public static final String AppSetting_ResumePreviousState_Name = "ResumePreviousState";
		public static final String AppSetting_TeamID_Name = "Team_ID";
        
        public static String getTableName(){
        	return AppSettings.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + AppSettings.getTableName()
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + AppSettingName + " text not null, " 
        	        + AppSettingValue + " text not null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{AppSettings.CONTENT_URI, CheckInViewInclusive.CONTENT_URI, CheckInViewExclusive.CONTENT_URI};
        }
        
        public static String getParameterSql(String paramName){        	
        	return "(" + SQLiteQueryBuilder.buildQueryString(true, AppSettings.getTableName(), new String[]{AppSettings.AppSettingValue}, 
        										 AppSettings.AppSettingName + "='" + paramName + "'", null, null, AppSettings.AppSettingName, "1") + ")";        
        }

		public static int Update(Context context, String appSettingName, String appSettingValue, boolean addIfNotExist) {
			ContentValues content = new ContentValues();
	        if(appSettingValue != null)
	        {
	        	content.put(AppSettings.AppSettingValue, appSettingValue);
	        }
			int numChanged = context.getContentResolver().update(AppSettings.CONTENT_URI, content, AppSettings.AppSettingName + "=?", new String[]{appSettingName});
			if(addIfNotExist && numChanged < 1){
				content.put(AppSettings.AppSettingName, appSettingName);
				AppSettings.Create(context, content);
				numChanged = 1;
			}
			
			return numChanged;
		}


		private static Uri Create(Context context, ContentValues content) {
	     	return context.getContentResolver().insert(AppSettings.CONTENT_URI, content);
		}

		public static Cursor Read(Context context, String appSettingToRetrieve) {
			return context.getContentResolver().query(AppSettings.CONTENT_URI, new String[]{AppSettings.AppSettingValue, AppSettings._ID}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);  //new String[]{AppSettings.AppSettingValue}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);
		}
		
		public static String ReadValue(Context context, String appSettingToRetrieve, String defaultValue) {
			Cursor temp = AppSettings.Read(context, appSettingToRetrieve);

			String val = defaultValue;
			if(temp != null && temp.getCount() > 0){
				temp.moveToFirst();
				val = temp.getString(0);
			}
			temp.close();
			temp = null;
			return val;
		}
		
		public static int Delete(Context context, String appSettingName){
			return context.getContentResolver().delete(AppSettings.CONTENT_URI, AppSettings.AppSettingName + "=?", new String[]{appSettingName});	
		}
    }
}
