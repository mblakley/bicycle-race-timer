package com.xcracetiming.android.tttimer.DataAccess.Views;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.RacerSeriesInfo;
import com.xcracetiming.android.tttimer.DataAccess.RacerUSACInfo;

public final class RacerInfoView extends ContentProviderView {

	private static final RacerInfoView instance = new RacerInfoView();
    
    public RacerInfoView() {}
 
    public static RacerInfoView Instance() {
        return instance;
    } 
    
    @Override
    public String getTableName(){
    	if(tableJoin == ""){
	    	tableJoin = new TableJoin(Racer.Instance().getTableName())
	    					.LeftJoin(Racer.Instance().getTableName(), RacerUSACInfo.Instance().getTableName(), Racer._ID, RacerUSACInfo.Racer_ID)
	    					.LeftJoin(RacerUSACInfo.Instance().getTableName(), RacerSeriesInfo.Instance().getTableName(), RacerUSACInfo._ID, RacerSeriesInfo.RacerUSACInfo_ID)
	    					.toString();
    	}
    	return tableJoin;
    }
    
    @Override
    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
    	ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();
    	urisToNotify.add(Racer.Instance().CONTENT_URI);
    	urisToNotify.add(RacerSeriesInfo.Instance().CONTENT_URI);
    	
    	return urisToNotify;
    }
    
    public Hashtable<String, Object> getValues(Context context, Long racerClubInfo_ID) {
		Hashtable<String, Object> racerValues = new Hashtable<String, Object>();
		
		Cursor racerCursor = RacerInfoView.Instance().Read(context, null, RacerSeriesInfo.Instance().getTableName() + "." + RacerSeriesInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)}, null);
		if(racerCursor != null && racerCursor.getCount() > 0){
			racerCursor.moveToFirst();
			racerValues.put(RacerSeriesInfo._ID, racerClubInfo_ID);
			racerValues.put(RacerSeriesInfo.SeriesBibNumber, racerCursor.getString(racerCursor.getColumnIndex(RacerSeriesInfo.SeriesBibNumber)));
			racerValues.put(RacerSeriesInfo.RaceSeries_ID, racerCursor.getLong(racerCursor.getColumnIndex(RacerSeriesInfo.RaceSeries_ID)));
			racerValues.put(RacerSeriesInfo.CurrentRaceCategory_ID, racerCursor.getString(racerCursor.getColumnIndex(RacerSeriesInfo.CurrentRaceCategory_ID)));
			racerValues.put(RacerSeriesInfo.TTPoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerSeriesInfo.TTPoints)));
			racerValues.put(RacerSeriesInfo.RRPoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerSeriesInfo.RRPoints)));
			racerValues.put(RacerSeriesInfo.PrimePoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerSeriesInfo.PrimePoints)));
			racerValues.put(RacerSeriesInfo.OnlineRecordID, racerCursor.getLong(racerCursor.getColumnIndex(RacerSeriesInfo.OnlineRecordID)));
			racerValues.put(RacerUSACInfo.Racer_ID, racerCursor.getLong(racerCursor.getColumnIndex(RacerUSACInfo.Racer_ID)));
			racerValues.put(Racer.FirstName, racerCursor.getString(racerCursor.getColumnIndex(Racer.FirstName)));
			racerValues.put(Racer.LastName, racerCursor.getString(racerCursor.getColumnIndex(Racer.LastName)));
			racerValues.put(Racer.BirthDate, racerCursor.getLong(racerCursor.getColumnIndex(Racer.BirthDate)));
			racerValues.put(Racer.PhoneNumber, racerCursor.getLong(racerCursor.getColumnIndex(Racer.PhoneNumber)));
			racerValues.put(Racer.EmergencyContactName, racerCursor.getString(racerCursor.getColumnIndex(Racer.EmergencyContactName)));
			racerValues.put(Racer.EmergencyContactPhoneNumber, racerCursor.getLong(racerCursor.getColumnIndex(Racer.EmergencyContactPhoneNumber)));
		}
		if( racerCursor != null){
			racerCursor.close();
			racerCursor = null;
		}
		
		return racerValues;
	}
}