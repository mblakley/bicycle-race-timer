package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeriesRaces;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;

/**
 * RaceInfoView - Useful for retrieving info about the race
 * Joins Race, RaceSeries, RaceLocation, RaceType, and outer join on RaceWave (for laps)
 * 
 * Race->RaceSeries_ID - One RaceSeries per Race
 * Race->RaceLocation_ID - One RaceLocation per Race
 * Race->RaceType_ID - One RaceType per Race
 * RaceWave->Race_ID (outer) - Multiple RaceWaves per Race, but waves are optional
 * @author mab
 *
 */
public final class RaceInfoView extends ContentProviderView {

	private static final RaceInfoView instance = new RaceInfoView();
    
	/**
	 * Joins Race, RaceSeries, RaceLocation, RaceType, and outer join on RaceWave (for laps)
	 * 
	 * Race->RaceSeries_ID (outer)
	 * Race->RaceLocation_ID
	 * Race->RaceType_ID
	 * Race->RaceWave_ID (outer)
	 */
    public RaceInfoView() {}
 
    public static RaceInfoView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){    		
    		tableJoin = new TableJoin(Race.Instance().getTableName())
							.LeftJoin(Race.Instance().getTableName(), RaceLocation.Instance().getTableName(), Race.RaceLocation_ID, RaceLocation._ID)
							.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
							.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
							.LeftOuterJoin(Race.Instance().getTableName(), RaceSeriesRaces.Instance().getTableName(), Race._ID, RaceSeriesRaces.Race_ID)
							.LeftOuterJoin(RaceSeriesRaces.Instance().getTableName(), RaceSeries.Instance().getTableName(), RaceSeriesRaces.RaceSeries_ID, RaceSeries._ID)
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
