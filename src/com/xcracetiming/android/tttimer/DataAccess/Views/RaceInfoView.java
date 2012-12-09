package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceLocation;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeries;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;

// BaseColumn contains _id.
public final class RaceInfoView extends ContentProviderTable implements BaseColumns {

	private static final RaceInfoView instance = new RaceInfoView();
    
    public RaceInfoView() {}
 
    public static RaceInfoView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(Race.Instance().getTableName())
    				.LeftJoin(Race.Instance().getTableName(), RaceSeries.Instance().getTableName(), Race.RaceSeries_ID, RaceSeries._ID)
    				.LeftJoin(Race.Instance().getTableName(), RaceLocation.Instance().getTableName(), Race.RaceLocation_ID, RaceLocation._ID)
    				.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
    				.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
    				.toString();
    }
    
    public static String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceLocation.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
