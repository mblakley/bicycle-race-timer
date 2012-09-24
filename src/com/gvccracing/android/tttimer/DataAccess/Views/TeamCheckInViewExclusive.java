package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembers;


//BaseColumn contains _id.
public final class TeamCheckInViewExclusive extends ContentProviderTable implements BaseColumns {

	private static final TeamCheckInViewExclusive instance = new TeamCheckInViewExclusive();
 
	public TeamCheckInViewExclusive() {}
	
	public static TeamCheckInViewExclusive Instance() {
	    return instance;
	} 
 
	public String getTableName() {
		String tableName = TeamInfo.Instance().getTableName() + 
				" JOIN " + TeamMembers.Instance().getTableName() + 
				" ON (" + TeamInfo.Instance().getTableName() + "." + TeamInfo._ID + " = " + TeamMembers.Instance().getTableName() + "." + TeamMembers.TeamInfo_ID + ")" + 
				" JOIN " + RacerSeriesInfo.Instance().getTableName() + 
				" ON (" + TeamMembers.Instance().getTableName() + "." + TeamMembers.RacerSeriesInfo_ID + " = " + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + ")" +				
				" JOIN " + RacerUSACInfo.Instance().getTableName() + 
				" ON ("	+ RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo._ID + " = " + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.RacerUSACInfo_ID + ")" + 
				" JOIN " + Racer.Instance().getTableName() + 
				" ON (" + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")";

		return tableName;
	}

	public String getCreate() {
		return "";
	}
	

	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
 	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
 	urisToNotify.add(TeamCheckInViewInclusive.Instance().CONTENT_URI);
 	urisToNotify.add(TeamCheckInViewExclusive.Instance().CONTENT_URI);
 	urisToNotify.add(TeamInfo.Instance().CONTENT_URI);
 	urisToNotify.add(TeamMembers.Instance().CONTENT_URI);
 	
 	return urisToNotify;
 }

	public int ReadCount(Context context, String[] fieldsToRetrieve,
			String selection, String[] selectionArgs, String sortOrder) {
		Cursor checkIns = context.getContentResolver().query(
				CONTENT_URI, fieldsToRetrieve,
				selection, selectionArgs, sortOrder);
		int numCheckIns = checkIns.getCount();
		if (checkIns != null) {
			checkIns.close();
			checkIns = null;
		}
		return numCheckIns;
	}
}
