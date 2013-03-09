package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.RaceLaps;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceTeamResults;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;
import com.xcracetiming.android.tttimer.DataAccess.TeamMembers;

// BaseColumn contains _id.
public final class TeamLapsView extends ContentProviderView {

    private static final TeamLapsView instance = new TeamLapsView();
    
    public TeamLapsView() {}
 
    public static TeamLapsView Instance() {
        return instance;
    }
    
    @Override
    public String getTableName(){
    	return new TableJoin(TeamInfo.Instance().getTableName())
    				.LeftJoin(TeamInfo.Instance().getTableName(), SeriesRaceTeamResults.Instance().getTableName(), TeamInfo._ID, SeriesRaceTeamResults.TeamInfo_ID)
    				.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), RaceLaps.Instance().getTableName(), SeriesRaceTeamResults.RaceResult_ID, RaceLaps.RaceResult_ID)
    				.toString();
    }

    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
    	urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
    	urisToNotify.add(TeamMembers.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
