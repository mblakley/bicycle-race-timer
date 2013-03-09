package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;

public final class RaceInfoResultsView extends ContentProviderView {

	private static final RaceInfoResultsView instance = new RaceInfoResultsView();
    
    public RaceInfoResultsView() {}
 
    public static RaceInfoResultsView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    		tableJoin = new TableJoin(Race.Instance().getTableName())
							.LeftJoin(Race.Instance().getTableName(), RaceLocation.Instance().getTableName(), Race.RaceLocation_ID, RaceLocation._ID)
							.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
							.LeftJoin(Race.Instance().getTableName(), SeriesRaceIndividualResults.Instance().getTableName(), Race._ID, SeriesRaceIndividualResults.Race_ID)
							.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceIndividualResults.RaceResult_ID, RaceResults._ID)
							.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
							.toString();
    	}
    	return tableJoin;
    }    
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
