package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RaceType;
import com.gvccracing.android.tttimer.DataAccess.RaceWave;

// BaseColumn contains _id.
public final class RaceWaveInfoView extends ContentProviderTable implements BaseColumns {

	private static final RaceWaveInfoView instance = new RaceWaveInfoView();
    
    public RaceWaveInfoView() {}
 
    public static RaceWaveInfoView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(Race.Instance().getTableName())
    				.LeftJoin(Race.Instance().getTableName(), RaceType.Instance().getTableName(), Race.RaceType_ID, RaceType._ID)
    				.LeftOuterJoin(Race.Instance().getTableName(), RaceWave.Instance().getTableName(), Race._ID, RaceWave.Race_ID)
    				.LeftOuterJoin(RaceWave.Instance().getTableName(), RaceCategory.Instance().getTableName(), RaceWave.RaceCategory_ID, RaceCategory._ID)
    				.toString();
    }
    
    public static String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Race.Instance().CONTENT_URI);
    	urisToNotify.add(RaceType.Instance().CONTENT_URI);
    	urisToNotify.add(RaceWave.Instance().CONTENT_URI);
    	urisToNotify.add(RaceCategory.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
