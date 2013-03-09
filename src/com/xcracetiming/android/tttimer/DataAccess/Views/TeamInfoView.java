package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;
import com.xcracetiming.android.tttimer.DataAccess.TeamMembers;

public final class TeamInfoView extends ContentProviderView {

	private static final TeamInfoView instance = new TeamInfoView();
    
    public TeamInfoView() {}
 
    public static TeamInfoView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	return new TableJoin(TeamInfo.Instance().getTableName())
    				.LeftJoin(TeamInfo.Instance().getTableName(), TeamMembers.Instance().getTableName(), TeamInfo._ID, TeamMembers.TeamInfo_ID)
    				.LeftJoin(TeamMembers.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), TeamMembers.RacerSeriesInfo_ID, RacerSeriesInfo._ID)
    				.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
    				.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
    				.toString();
    }    
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
