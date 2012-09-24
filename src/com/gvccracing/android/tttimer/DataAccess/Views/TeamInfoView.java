package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembers;

// BaseColumn contains _id.
public final class TeamInfoView extends ContentProviderTable implements BaseColumns {

	private static final TeamInfoView instance = new TeamInfoView();
    
    public TeamInfoView() {}
 
    public static TeamInfoView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(TeamInfo.Instance().getTableName())
    				.LeftJoin(TeamInfo.Instance().getTableName(), TeamMembers.Instance().getTableName(), TeamInfo._ID, TeamMembers.TeamInfo_ID)
    				.LeftJoin(TeamMembers.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), TeamMembers.RacerSeriesInfo_ID, RacerSeriesInfo._ID)
    				.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
    				.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
    				.toString();
    }
    
    public static String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
