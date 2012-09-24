package com.gvccracing.android.tttimer.DataAccess.Views;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceCategory;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfo;
import com.gvccracing.android.tttimer.DataAccess.SeriesRaceIndividualResults;

// BaseColumn contains _id.
public final class SeriesRaceIndividualResultsView extends ContentProviderTable implements BaseColumns {

	private static final SeriesRaceIndividualResultsView instance = new SeriesRaceIndividualResultsView();
    
    public SeriesRaceIndividualResultsView() {}
 
    public static SeriesRaceIndividualResultsView Instance() {
        return instance;
    } 
    
    public String getTableName(){
    	return new TableJoin(SeriesRaceIndividualResults.Instance().getTableName()).LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), Race.Instance().getTableName(), SeriesRaceIndividualResults.Race_ID, Race._ID)
    			.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), SeriesRaceIndividualResults.RacerSeriesInfo_ID, RacerSeriesInfo._ID)
    			.LeftJoin(RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.RacerUSACInfo_ID, RacerUSACInfo._ID)
    			.LeftJoin(RacerUSACInfo.Instance().getTableName(), Racer.Instance().getTableName(), RacerUSACInfo.Racer_ID, Racer._ID)
    			.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RaceResults.Instance().getTableName(), SeriesRaceIndividualResults.RaceResult_ID, RaceResults._ID)
    			.LeftJoin(SeriesRaceIndividualResults.Instance().getTableName(), RaceCategory.Instance().getTableName(), SeriesRaceIndividualResults.RaceCategory_ID, RaceCategory._ID)
    			.toString();
    }
    
    public String getCreate(){
    	return "";
    }
    
    public int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
		Cursor checkIns = context.getContentResolver().query(CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		int numCheckIns = checkIns.getCount();
		if(checkIns != null){
			checkIns.close();
			checkIns = null;
		}
		return numCheckIns;
	}
}
