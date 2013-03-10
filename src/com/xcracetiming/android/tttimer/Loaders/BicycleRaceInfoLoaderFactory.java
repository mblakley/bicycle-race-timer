package com.xcracetiming.android.tttimer.Loaders;

import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.xcracetiming.android.tttimer.DataAccess.Views.RaceInfoView;
import com.xcracetiming.android.tttimer.Utilities.QueryUtilities.SelectBuilder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;

public class BicycleRaceInfoLoaderFactory implements IRaceInfoTabLoaderFactory {

	/**
	 * Get the race info our of the database for the configured current race ID
	 * 
	 * Warning - This can return multiple rows!  If there's more than 1 RaceWave associated with this race, you'll get the corresponding number of rows.
	 * 
	 * @param activity - Used to create the CursorLoader
	 * @return Race._ID, RaceSeries.SeriesName, Race.RaceDate, Race.RaceLocation_ID, RaceLocation.CourseName, RaceType.HasMultipleLaps, Race.StartInterval, RaceLocation.Distance, RaceLocation.DistanceUnit, RaceWave.NumLaps, RaceType.RaceTypeDescription
	 */
	public CursorLoader GetRaceInfo(FragmentActivity activity) {
		String[] projection = new String[]{Race.Instance().getColumnName(Race._ID), Race.RaceDate, Race.RaceLocation_ID, RaceLocation.CourseName, RaceType.HasMultipleLaps, Race.StartInterval, RaceLocation.Distance, RaceLocation.DistanceUnit, RaceWave.NumLaps, RaceType.RaceTypeDescription, RaceSeries.SeriesName};
		String selection = SelectBuilder.Where(Race.Instance().getColumnName(Race._ID)).Equals(AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name)).toString();
		String[] selectionArgs = null;
		String sortOrder = Race.Instance().getColumnName(Race._ID);
		return new CursorLoader(activity, RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
	}

	/**
	 * Returns a single value for the course record based on the RaceLocation
	 * 
	 * @param activity - Used to create CursorLoader object
	 * @param args - A bundle of arguments containing Race.RaceLocation_ID
	 * @return RaceResults.ElapsedTime
	 */
	public CursorLoader GetCourseRecord(FragmentActivity activity, Bundle args) {
		String[] projection = new String[]{RaceResults.ElapsedTime, Race.RaceDate, Racer.FirstName, Racer.LastName};
		String selection = SelectBuilder.Where(Race.Instance().getColumnName(Race.RaceLocation_ID)).EqualsParameter().And(RaceResults.ElapsedTime).GTE(0).toString();
		String[] selectionArgs = new String[]{Long.toString(args.getLong(Race.RaceLocation_ID))};
		String sortOrder = RaceResults.ElapsedTime;
		return new CursorLoader(activity, RaceInfoResultsView.Instance().CONTENT_URI.buildUpon().appendQueryParameter(ContentProviderTable.Limit, "1").build(), projection, selection, selectionArgs, sortOrder);
	}
}
