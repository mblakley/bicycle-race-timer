package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;

/**
 * CheckInViewExclusive - Used
 * @author mab
 *
 */
public final class CheckInViewExclusive extends ContentProviderView {

	private static final CheckInViewExclusive instance = new CheckInViewExclusive(); 
	
	public CheckInViewExclusive() {}
	
	public static CheckInViewExclusive Instance() {		
	     return instance;
	} 
	 
	/**
	 * Joins RacerSeriesInfo, RacerUSACInfo, Racer
	 */
	@Override
	public String getTableName(){
		if(tableJoin == ""){
			tableJoin = new TableJoin(RacerSeriesInfo.Instance().getTableName())
							.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
							.LeftJoin(RacerSeriesInfo.Instance().getTableName(), Racer.Instance().getTableName(), Racer._ID, RacerUSACInfo.Racer_ID)
							.toString();
		}
		return tableJoin;
	}	 
	 
	@Override
	public ArrayList<Uri> getAllUrisToNotifyOnChange(){
	 	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
	 	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
	 	
	 	return urisToNotify;
	}
 
	public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
		Cursor checkIns = context.getContentResolver().query(CheckInViewExclusive.Instance().CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		int numCheckIns = checkIns.getCount();
		if(checkIns != null){
			checkIns.close();
			checkIns = null;
		}
		return numCheckIns;
	}
}
