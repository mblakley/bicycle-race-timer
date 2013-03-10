package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;

// BaseColumn contains _id.
public final class SeriesRaceIndividualResultsView extends ContentProviderView {

	private static final SeriesRaceIndividualResultsView instance = new SeriesRaceIndividualResultsView();
    
    public SeriesRaceIndividualResultsView() {}
 
    public static SeriesRaceIndividualResultsView Instance() {
        return instance;
    } 
    
    /**
     * Joins SeriesRaceIndividualResults, Race, RacerSeriesInfo, RacerUSACInfo, Racer, RaceResults, RaceCategory tables
     */
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    		tableJoin = new TableJoin(SeriesRaceIndividualResults.Instance().getTableName())
    						.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), Race.Instance().getTableName(), SeriesRaceIndividualResults.Race_ID, Race._ID)
			    			.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), SeriesRaceIndividualResults.RacerSeriesInfo_ID, RacerSeriesInfo._ID)
			    			.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
			    			.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
			    			.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceIndividualResults.RaceResult_ID, RaceResults._ID)
			    			.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RaceCategory.Instance().getTableName(), RacerSeriesInfo.SeriesRacerCategory_ID, RaceCategory._ID)
			    			.toString();
    	}
    	return tableJoin;
    }
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(SeriesRaceIndividualResultsView.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
    
    public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
		Cursor checkIns = context.getContentResolver().query(CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		int numCheckIns = checkIns.getCount();
		if(checkIns != null){
			checkIns.close();
			checkIns = null;
		}
		return numCheckIns;
	}
}
