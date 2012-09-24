package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfo;

// BaseColumn contains _id.
public final class RacerPreviousResultsView extends ContentProviderTable implements BaseColumns {

	private static final RacerPreviousResultsView instance = new RacerPreviousResultsView();
    
    public RacerPreviousResultsView() {}
 
    public static RacerPreviousResultsView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return RacerUSACInfo.Instance().getTableName() + 
    			" JOIN " + Racer.Instance().getTableName() +
				" ON (" + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "." + Racer._ID + ")" +
    			" JOIN " + RacerSeriesInfo.Instance().getTableName() + 
				" ON (" + RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo.RacerUSACInfo_ID + " = " + RacerUSACInfo.Instance().getTableName() + "." + RacerUSACInfo._ID + ")" +
				" JOIN " + RaceLocation.Instance().getTableName() + 
				" ON (" + RaceLocation.Instance().getTableName() + "." + RaceLocation._ID + " = " + Race.Instance().getTableName() + "." + Race.RaceLocation_ID + ")";			
    }
    
    public String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RaceResults.Instance().CONTENT_URI);
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
    	urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
