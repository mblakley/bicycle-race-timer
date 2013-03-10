package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceSeriesRaceCategories;

public class SeriesRaceCategoriesView extends ContentProviderView {
	private static final SeriesRaceCategoriesView instance = new SeriesRaceCategoriesView();
    
    public SeriesRaceCategoriesView() {}
 
    public static SeriesRaceCategoriesView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
    		tableJoin = new TableJoin(RaceSeriesRaceCategories.Instance().getTableName())
		    				.LeftJoin(RaceSeriesRaceCategories.Instance().getTableName(), RaceCategory.Instance().getTableName(), RaceSeriesRaceCategories.RaceCategory_ID, RaceCategory._ID)
		    				.toString();
    	}
    	return tableJoin;
    }    
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(RaceSeriesRaceCategories.Instance().CONTENT_URI);
    	urisToNotify.add(RaceCategory.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
