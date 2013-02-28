package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetiming.android.tttimer.DataAccess.ContentProviderTable;
import com.xcracetiming.android.tttimer.DataAccess.RaceCategory;
import com.xcracetiming.android.tttimer.DataAccess.RaceRaceCategory;

// BaseColumn contains _id.
public final class RaceRaceCategoryView extends ContentProviderTable implements BaseColumns {

	private static final RaceRaceCategoryView instance = new RaceRaceCategoryView();
    
    public RaceRaceCategoryView() {}
 
    public static RaceRaceCategoryView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(RaceRaceCategory.Instance().getTableName())
    				.LeftJoin(RaceRaceCategory.Instance().getTableName(), RaceCategory.Instance().getTableName(), RaceRaceCategory.RaceCategory_ID, RaceCategory._ID)
    				.toString();
    }
    
    public static String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(RaceRaceCategory.Instance().CONTENT_URI);
    	urisToNotify.add(RaceCategory.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
