package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLaps;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceTeamResults;

// BaseColumn contains _id.
public final class RaceLapsInfoView extends ContentProviderTable implements BaseColumns {

	private static final RaceLapsInfoView instance = new RaceLapsInfoView();
    
    public RaceLapsInfoView() {}
 
    public static RaceLapsInfoView Instance() {
        return instance;
    } 
            
    public String getTableName(){
    	return new TableJoin(Race.Instance().getTableName())
			.LeftJoin(Race.Instance().getTableName(), RaceLocation.Instance().getTableName(), Race.RaceLocation_ID, RaceLocation._ID)
			.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
			.LeftJoin(Race.Instance().getTableName(), SeriesRaceTeamResults.Instance().getTableName(), Race._ID, SeriesRaceTeamResults.Race_ID)
			.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceTeamResults.RaceResult_ID, RaceResults._ID)
			.LeftJoin(Race.Instance().getTableName(), RaceLaps.Instance().getTableName(), RaceResults._ID, RaceLaps.RaceResult_ID)
			.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
			.toString();
    }
    
    public String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceResults.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLaps.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
