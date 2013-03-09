package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceRaceCategory;

public final class RaceRaceCategoryView extends ContentProviderView {

	private static final RaceRaceCategoryView instance = new RaceRaceCategoryView();
    
    public RaceRaceCategoryView() {}
 
    public static RaceRaceCategoryView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    		tableJoin = new TableJoin(RaceRaceCategory.Instance().getTableName())
		    				.LeftJoin(RaceRaceCategory.Instance().getTableName(), RaceCategory.Instance().getTableName(), RaceRaceCategory.RaceCategory_ID, RaceCategory._ID)
		    				.toString();
    	}
    	return tableJoin;
    }    
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(RaceRaceCategory.Instance().CONTENT_URI);
    	urisToNotify.add(RaceCategory.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
