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
public final class RacerSeriesInfoView extends ContentProviderTable implements BaseColumns {

	private static final RacerSeriesInfoView instance = new RacerSeriesInfoView();
    
    public RacerSeriesInfoView() {}
 
    public static RacerSeriesInfoView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(RacerSeriesInfo.Instance().getTableName())
    				.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
    				.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
    				.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RaceCategory.Instance().getTableName(), RacerSeriesInfo.CurrentRaceCategory_ID, RaceCategory._ID)
    				.toString();
    }
    
    public static String getCreate(){
    	return "";
    }
    
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
}
