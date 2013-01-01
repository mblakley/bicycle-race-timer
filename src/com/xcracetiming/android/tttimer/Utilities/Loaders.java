package com.xcracetiming.android.tttimer.Utilities;

import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoView;
import com.xcracetiming.android.tttimer.Utilities.QueryUtilities.SelectBuilder;

import android.os.Bundle;
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

	public static CursorLoader GetAllCourseNames(FragmentActivity activity) {
		String[] projection = new String[]{RaceLocation._ID, RaceLocation.CourseName};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = RaceLocation.CourseName;
		return new CursorLoader(activity, RaceLocation.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	public static CursorLoader GetAllRaceTypeDescriptions(FragmentActivity activity) {
		String[] projection = new String[]{RaceType._ID, RaceType.RaceTypeDescription, RaceType.HasMultipleLaps};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = RaceType._ID;
		return new CursorLoader(activity, RaceType.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}
	
	public static CursorLoader GetRaceInfo(FragmentActivity activity){
		String[] projection = new String[]{Race.Instance().getColumnName(Race._ID), RaceSeries.SeriesName, Race.RaceDate, Race.RaceLocation_ID, RaceLocation.CourseName, RaceType.HasMultipleLaps, Race.StartInterval, RaceLocation.Distance, RaceLocation.DistanceUnit, RaceWave.NumLaps, RaceType.RaceTypeDescription};
		String selection = SelectBuilder.Where(Race.Instance().getColumnName(Race._ID)).Equals(AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name)).toString();
		String[] selectionArgs = null;
		String sortOrder = Race.Instance().getColumnName(Race._ID);
		return new CursorLoader(activity, RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	public static CursorLoader GetDistanceUnits(FragmentActivity activity) {
		String[] projection = new String[]{AppSettings.AppSettingValue};
		String selection = SelectBuilder.Where(AppSettings.Instance().getColumnName(AppSettings.AppSettingValue)).EqualsParameter().toString();
		String[] selectionArgs = new String[]{AppSettings.AppSetting_DistanceUnits_Name};
		String sortOrder = null;		
		return new CursorLoader(activity, AppSettings.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
	}

	public static CursorLoader GetCourseRecord(FragmentActivity activity, Bundle args) {
		String[] projection = new String[]{RaceResults.ElapsedTime};
		String selection = SelectBuilder.Where(Race.Instance().getColumnName(Race.RaceLocation_ID)).EqualsParameter().And(RaceResults.ElapsedTime).EqualsParameter().And(RaceResults.ElapsedTime).GTE(0).toString();
		String[] selectionArgs = new String[]{Long.toString(args.getLong(Race.RaceLocation_ID))};
		String sortOrder = RaceResults.ElapsedTime;
		return new CursorLoader(activity, RaceInfoResultsView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
	}
}