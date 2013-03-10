package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;

/**
 * Joins RacerSeriesInfo, RacerUSACInfo, Racer, and RaceCategory
 * 
 * RacerSeriesInfo->RacerUSACInfo_ID
 * RacerUSACInfo->Racer_ID
 * RacerSeriesInfo->CurrentRaceCategory_ID
 * @author mab
 *
 */
public final class RacerSeriesInfoView extends ContentProviderView {

	private static final RacerSeriesInfoView instance = new RacerSeriesInfoView();
    
    public RacerSeriesInfoView() {}
 
    public static RacerSeriesInfoView Instance() {
        return instance;
    } 

    /**
     * Joins RacerSeriesInfo, RacerUSACInfo, Racer, and RaceCategory
     * 
     * RacerSeriesInfo->RacerUSACInfo._ID
     * RacerUSACInfo->Racer._ID
     * RacerSeriesInfo->RaceCategory._ID
     */
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
	    	tableJoin = new TableJoin(RacerSeriesInfo.Instance().getTableName())
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
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
