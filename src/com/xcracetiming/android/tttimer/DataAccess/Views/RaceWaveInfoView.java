package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Race;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceType;
import com.xcracetiming.android.tttimer.DataAccess.RaceWave;

public final class RaceWaveInfoView extends ContentProviderView {

	private static final RaceWaveInfoView instance = new RaceWaveInfoView();
    
    public RaceWaveInfoView() {}
 
    public static RaceWaveInfoView Instance() {
        return instance;
    } 

    @Override
    public String getTableName(){
    	if(tableJoin == ""){
	    	tableJoin = new TableJoin(Race.Instance().getTableName())
		    				.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
		    				.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
		    				.LeftOuterJoin(RaceWave.Instance().getTableName(), RaceCategory.Instance().getTableName(), RaceWave.RaceCategory_ID, RaceCategory._ID)
		    				.toString();
    	}
    	return tableJoin;
    }
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceType.Instance().CONTENT_URI);
    	urisToNotify.add(RaceWave.Instance().CONTENT_URI);
    	urisToNotify.add(RaceCategory.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
