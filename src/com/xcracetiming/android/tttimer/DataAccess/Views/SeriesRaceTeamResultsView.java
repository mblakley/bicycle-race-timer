package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceTeamResults;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;

// BaseColumn contains _id.
public final class SeriesRaceTeamResultsView extends ContentProviderView {

	private static final SeriesRaceTeamResultsView instance = new SeriesRaceTeamResultsView();
    
    public SeriesRaceTeamResultsView() {}
 
    public static SeriesRaceTeamResultsView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
	    	tableJoin = new TableJoin(SeriesRaceTeamResults.Instance().getTableName()).LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), Race.Instance().getTableName(), SeriesRaceTeamResults.Race_ID, Race._ID)
		    			.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), TeamInfo.Instance().getTableName(), SeriesRaceTeamResults.TeamInfo_ID, TeamInfo._ID)
		    			.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceTeamResults.RaceResult_ID, RaceResults._ID)
		    			.toString();
    	}
    	return tableJoin;
    }

    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(SeriesRaceTeamResultsView.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
