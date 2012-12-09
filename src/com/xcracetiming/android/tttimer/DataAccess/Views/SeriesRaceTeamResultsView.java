package com.xcracetiming.android.tttimer.DataAccess.Views;

import android.content.Context;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceTeamResults;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;

// BaseColumn contains _id.
public final class SeriesRaceTeamResultsView extends ContentProviderTable implements BaseColumns {

	private static final SeriesRaceTeamResultsView instance = new SeriesRaceTeamResultsView();
    
    public SeriesRaceTeamResultsView() {}
 
    public static SeriesRaceTeamResultsView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(SeriesRaceTeamResults.Instance().getTableName()).LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), Race.Instance().getTableName(), SeriesRaceTeamResults.Race_ID, Race._ID)
    			.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), TeamInfo.Instance().getTableName(), SeriesRaceTeamResults.TeamInfo_ID, TeamInfo._ID)
    			.LeftJoin(SeriesRaceTeamResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceTeamResults.RaceResult_ID, RaceResults._ID)
    			.toString();
    }
    
    public String getCreate(){
    	return "";
    }

	public int ReadCount(Context context, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return 0;
	}
}
