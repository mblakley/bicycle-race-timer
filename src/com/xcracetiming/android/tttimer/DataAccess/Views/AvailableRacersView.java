package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.RaceSeriesRaces;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;

public final class AvailableRacersView extends ContentProviderView {
	private static final AvailableRacersView instance = new AvailableRacersView(); 
	
	public AvailableRacersView() {}
	
	public static AvailableRacersView Instance() {		
	     return instance;
	} 
	 
	/**
	 * RaceSeriesRaces join RacerSeriesInfo on RaceSeries_ID join RacerUSACInfo on RacerUSACInfo_ID join Racer on Racer_ID outer join SeriesRaceIndividualResults on Race_ID and RacerSeriesInfo_ID
	 * 
	 * RaceSeriesRaces, RacerSeriesInfo, RacerUSACInfo, Racer, SeriesRaceIndividualResults
	 */
	@Override
	public String getTableName(){
		if(tableJoin == ""){
			tableJoin = new TableJoin(RaceSeriesRaces.Instance().getTableName())
							.LeftJoin(RaceSeriesRaces.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), RaceSeriesRaces.RaceSeries_ID, RacerSeriesInfo.RaceSeries_ID)
							.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
							.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
							.LeftOuterJoin(RacerSeriesInfo.Instance().getTableName(), SeriesRaceIndividualResults.Instance().getTableName(), RacerSeriesInfo._ID, SeriesRaceIndividualResults.RacerSeriesInfo_ID + " AND " + RaceSeriesRaces.Instance().getTableName() + "." + RaceSeriesRaces.Race_ID +"=" + SeriesRaceIndividualResults.Instance().getTableName() + "." + SeriesRaceIndividualResults.Race_ID)
							.toString();
		}
		return tableJoin;
	}	 
	 
	@Override
	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
	 	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
	 	urisToNotify.add(AvailableRacersView.Instance().CONTENT_URI);
	 	
	 	return urisToNotify;
	}
 
	public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
		Cursor availableRacers = context.getContentResolver().query(AvailableRacersView.Instance().CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		int numRacers = availableRacers.getCount();
		if(availableRacers != null){
			availableRacers.close();
			availableRacers = null;
		}
		return numRacers;
	}
}
