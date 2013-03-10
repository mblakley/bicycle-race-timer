package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;

/**
 * CheckInViewExclusive - Used
 * @author mab
 *
 */
public final class CheckedInRacersView extends ContentProviderView {

	private static final CheckedInRacersView instance = new CheckedInRacersView(); 
	
	public CheckedInRacersView() {}
	
	public static CheckedInRacersView Instance() {		
	     return instance;
	} 
	 
	/**
	 * SeriesRaceIndividualResults join RaceResults on RaceResult_ID join RacerSeriesInfo on RacerSeriesInfo_ID join RacerUSACInfo on RacerUSACInfo_ID join Racer on Racer_ID where SeriesRaceIndividualResults.Race_ID=@RaceID OrderBy StartOrder
	 * 
	 * Joins SeriesRaceIndividualResults, RaceResults, RacerSeriesInfo, RacerUSACInfo, Racer
	 */
	@Override
	public String getTableName(){
		if(tableJoin == ""){
			tableJoin = new TableJoin(SeriesRaceIndividualResults.Instance().getTableName())
							.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceIndividualResults.RaceResult_ID, RaceResults._ID)
							.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), SeriesRaceIndividualResults.RacerSeriesInfo_ID, RacerSeriesInfo._ID)
							.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
							.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
							.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RaceCategory.Instance().getTableName(), RacerSeriesInfo.SeriesRacerCategory_ID, RaceCategory._ID)
							.toString();
		}
		return tableJoin;
	}	 
	 
	@Override
	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
	 	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
	 	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
	 	
	 	return urisToNotify;
	}
 
	public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
		Cursor checkIns = context.getContentResolver().query(CheckedInRacersView.Instance().CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		int numCheckIns = checkIns.getCount();
		if(checkIns != null){
			checkIns.close();
			checkIns = null;
		}
		return numCheckIns;
	}
}
