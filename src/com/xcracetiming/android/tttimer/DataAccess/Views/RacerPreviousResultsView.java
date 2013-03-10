package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;

// BaseColumn contains _id.
public final class RacerPreviousResultsView extends ContentProviderView {

	private static final RacerPreviousResultsView instance = new RacerPreviousResultsView();
    
    public RacerPreviousResultsView() {}
 
    public static RacerPreviousResultsView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    	tableJoin = new TableJoin(RacerUSACInfo.Instance().getTableName())
    					.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
    					.LeftJoin(RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo._ID, RacerSeriesInfo.RacerUSACInfo_ID)
    					.LeftJoin(Race.Instance().getTableName(), RaceLocation.Instance().getTableName(), Race.RaceLocation_ID, RaceLocation._ID)
    					.toString();
    	}
    	return tableJoin;
    }
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RaceResults.Instance().CONTENT_URI);
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckedInRacersView.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
