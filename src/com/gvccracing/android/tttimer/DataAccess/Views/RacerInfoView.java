package com.gvccracing.android.tttimer.DataAccess.Views;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.ContentProviderTable;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;

// BaseColumn contains _id.
public final class RacerInfoView extends ContentProviderTable implements BaseColumns {

    private static final RacerInfoView instance = new RacerInfoView();

    public RacerInfoView() {}

    public static RacerInfoView Instance() {
        return instance;
    }

    public String getTableName(){
        return Racer.Instance().getTableName() + " JOIN " + RacerClubInfo.Instance().getTableName() +
                " ON (" + RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.Instance().getTableName() + "._ID)";
    }

    public static String getCreate(){
        return "";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(RacerInfoView.Instance().CONTENT_URI);
        urisToNotify.add(Racer.Instance().CONTENT_URI);
        urisToNotify.add(RacerClubInfo.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Hashtable<String, Object> getValues(Context context, Long racerClubInfo_ID) {
        Hashtable<String, Object> racerValues = new Hashtable<String, Object>();

        Cursor racerCursor = RacerInfoView.Instance().Read(context, null, RacerClubInfo.Instance().getTableName() + "." + RacerClubInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)}, null);
        if(racerCursor != null && racerCursor.getCount() > 0){
            racerCursor.moveToFirst();
            racerValues.put(RacerClubInfo._ID, racerClubInfo_ID);
            racerValues.put(RacerClubInfo.Racer_ID, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.CheckInID, racerCursor.getString(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.Year, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.Category, racerCursor.getString(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.TTPoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.RRPoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.PrimePoints, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.RacerAge, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.GVCCID, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(RacerClubInfo.Upgraded, racerCursor.getLong(racerCursor.getColumnIndex(RacerClubInfo.Racer_ID)));
            racerValues.put(Racer.FirstName, racerCursor.getString(racerCursor.getColumnIndex(Racer.FirstName)));
            racerValues.put(Racer.LastName, racerCursor.getString(racerCursor.getColumnIndex(Racer.LastName)));
            racerValues.put(Racer.USACNumber, racerCursor.getLong(racerCursor.getColumnIndex(Racer.USACNumber)));
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