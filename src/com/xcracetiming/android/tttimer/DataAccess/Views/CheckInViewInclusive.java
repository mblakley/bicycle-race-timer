package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;

import android.net.Uri;

public final class CheckInViewInclusive extends ContentProviderView {

	private static final CheckInViewInclusive instance = new CheckInViewInclusive();    
	
    public CheckInViewInclusive() {}
 
    public static CheckInViewInclusive Instance() {
        return instance;
    }
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    		tableJoin = new TableJoin(RacerSeriesInfo.Instance().getTableName())
			.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID) 
			.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RaceCategory.Instance().getTableName(), RacerSeriesInfo.CurrentRaceCategory_ID, RaceCategory._ID)
			.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID).toString();
    	}
    	return tableJoin;
    }    
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}