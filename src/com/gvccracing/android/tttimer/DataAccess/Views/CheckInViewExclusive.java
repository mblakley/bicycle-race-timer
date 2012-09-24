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

//BaseColumn contains _id.
public final class CheckInViewExclusive extends ContentProviderTable implements BaseColumns {

	private static final CheckInViewExclusive instance = new CheckInViewExclusive();
 
	public CheckInViewExclusive() {}
	
	public static CheckInViewExclusive Instance() {
	     return instance;
	} 
	 
	public String getTableName(){
	 	return RacerSeriesInfo.Instance().getTableName() + 
	 			" JOIN " + RacerUSACInfo.Instance().getTableName() + 
				" ON (" + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.RacerUSACInfo_ID + " = " + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo._ID + ")" +
				" JOIN " + Racer.Instance().getTableName() + 
				" ON (" + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")";
	}
	 
	public static String getCreate(){
	 	return "";
	}
	 
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
