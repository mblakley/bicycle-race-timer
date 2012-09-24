package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfo;

// BaseColumn contains _id.
public final class RacerClubInfoView extends ContentProviderTable implements BaseColumns {

	private static final RacerClubInfoView instance = new RacerClubInfoView();
    
    public RacerClubInfoView() {}
 
    public static RacerClubInfoView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return RacerSeriesInfo.Instance().getTableName() + 
    			" JOIN " + RacerUSACInfo.Instance().getTableName() + 
				" ON (" + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.RacerUSACInfo_ID + " = " + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo._ID + ")" +
    			" JOIN " + RaceCategory.Instance().getTableName() + 
				" ON (" + RaceCategory.Instance().getTableName() + "." + RaceCategory._ID + " = " + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.CurrentRaceCategory_ID + ")" +
    			" JOIN " + Racer.Instance().getTableName() + 
				" ON (" + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")";// +
    			//" LEFT OUTER JOIN " + RaceResults.getTableName() + 
				//" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + " = " + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + ")";
    }
    
    public String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
