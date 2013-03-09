package com.xcracetiming.android.tttimer.Utilities;

import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.Utilities.QueryUtilities.SelectBuilder;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;

public final class Loaders {
	// Race Info
	public static final int COURSE_RECORD_LOADER = 0x213;

	public static final int APP_SETTINGS_LOADER_RACEINFO = 0x22;

	public static final int RACE_INFO_LOADER = 0x114;
	
	// Check In
	public static final int TEAM_CHECKIN_LOADER = 0x61;

	public static final int TEAM_START_ORDER_LOADER = 0x95;

	public static final int RACE_INFO_LOADER_CHECKIN = 0x44;

	public static final int APP_SETTINGS_LOADER_CHECKIN = 0x109;

	public static final int CHECKIN_LOADER_CHECKIN = 0x110;

	public static final int START_ORDER_LOADER_CHECKIN = 0x111;
	
	// Dialogs
	public static final int RACE_LOCATIONS_LOADER = 0x01;
	
	// AddRaceView
	public static final int RACE_TYPES_LOADER = 1014;

	public static final int RACE_SERIES_LOADER = 1015;

	public static CursorLoader GetAllCourseNames(FragmentActivity activity) {
		String[] projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = RaceLocation.CourseName;
		return new CursorLoader(activity, RaceLocation.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}
	
	public static CursorLoader GetAllRaceSeriesNames(Context context) {
		String[] projection = new String[]{RaceSeries._ID, RaceSeries.SeriesName};
		String selection = SelectBuilder.Where(RaceSeries.Instance().getColumnName(RaceSeries._ID)).NotEqualsParameter().toString();
		String[] selectionArgs = new String[]{"1"};
		String sortOrder = RaceSeries.SeriesName;
		return new CursorLoader(context, RaceSeries.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	public static CursorLoader GetAllRaceTypeDescriptions(FragmentActivity activity) {
		String[] projection = new String[]{RaceType._ID, RaceType.RaceTypeDescription, RaceType.HasMultipleLaps};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = RaceType._ID;
		return new CursorLoader(activity, RaceType.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	/**
	 * Return a single value for DistanceUnits (ex: mi or km)
	 * 
	 * @param activity - Used to create CursorLoader object
	 * @return AppSettings.AppSettingValue for DistanceUnits - contains mi or km
	 */
	public static CursorLoader GetDistanceUnits(FragmentActivity activity) {
		String[] projection = new String[]{AppSettings.AppSettingValue};
		String selection = SelectBuilder.Where(AppSettings.Instance().getColumnName(AppSettings.AppSettingValue)).EqualsParameter().toString();
		String[] selectionArgs = new String[]{AppSettings.AppSetting_DistanceUnits_Name};
		String sortOrder = null;		
		return new CursorLoader(activity, AppSettings.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
	}
}