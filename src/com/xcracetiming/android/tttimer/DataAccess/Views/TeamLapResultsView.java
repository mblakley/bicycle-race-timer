package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.RaceLaps;
import com.xcracetiming.android.tttimer.DataAccess.RaceResults;
import com.xcracetiming.android.tttimer.DataAccess.TeamInfo;
import com.xcracetiming.android.tttimer.DataAccess.TeamMembers;

public final class TeamLapResultsView extends ContentProviderView {

	private static final TeamLapResultsView instance = new TeamLapResultsView();
 
	public TeamLapResultsView() {}

	public static TeamLapResultsView Instance() {
		return instance;
	} 
 
	@Override
	public String getTableName() {
		String tableName = TeamInfo.Instance().getTableName() + " JOIN "
				+ RaceResults.Instance().getTableName() + " ON ("
				+ RaceResults.Instance().getTableName() + "."
				+ "." + TeamInfo._ID + ")" + " LEFT OUTER JOIN "
				+ RaceLaps.Instance().getTableName() + " ON ("
				+ RaceLaps.Instance().getTableName() + "." + RaceLaps.RaceResult_ID
				+ " = " + RaceResults.Instance().getTableName() + "."
				+ RaceResults._ID + ")";

		return tableName;
	}

	@Override
	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
	 	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
	 	urisToNotify.add(TeamLapResultsView.Instance().CONTENT_URI);
	 	urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
	 	urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
	 	urisToNotify.add(TeamMembers.Instance().CONTENT_URI);
	 	
	 	return urisToNotify;
	}
}
