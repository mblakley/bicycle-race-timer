package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

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

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + AppSettingName + " text not null, "
                + AppSettingValue + " text not null);";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
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
        int numChanged = context.getContentResolver().update(AppSettings.Instance().CONTENT_URI, content, AppSettings.AppSettingName + "=?", new String[]{appSettingName});
        if(addIfNotExist && numChanged < 1){
            content.put(AppSettings.AppSettingName, appSettingName);
            AppSettings.Instance().Create(context, content);
            numChanged = 1;
        }

        return numChanged;
    }

    public Cursor Read(Context context, String appSettingToRetrieve) {
        return context.getContentResolver().query(AppSettings.Instance().CONTENT_URI, new String[]{AppSettings.AppSettingValue, AppSettings._ID}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);  //new String[]{AppSettings.AppSettingValue}, AppSettings.AppSettingName + "=?", new String[]{appSettingToRetrieve}, null);
    }

    public String ReadValue(Context context, String appSettingToRetrieve, String defaultValue) {
        Cursor temp = AppSettings.Instance().Read(context, appSettingToRetrieve);

        String val = defaultValue;
        if(temp != null && temp.getCount() > 0){
            temp.moveToFirst();
            val = temp.getString(0);

            temp.close();
        }
        temp = null;
        return val;
    }

    public int Delete(Context context, String appSettingName){
        return context.getContentResolver().delete(AppSettings.Instance().CONTENT_URI, AppSettings.AppSettingName + "=?", new String[]{appSettingName});
    }
}
