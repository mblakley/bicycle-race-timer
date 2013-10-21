package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

// BaseColumn contains _id.
public final class RacerClubInfo extends ContentProviderTable implements BaseColumns {

    private static final RacerClubInfo instance = new RacerClubInfo();

    public RacerClubInfo() {}

    public static RacerClubInfo Instance() {
        return instance;
    }

    // Table column
    public static final String Racer_ID = "Racer_ID";
    public static final String CheckInID = "CheckInID";
    public static final String Year = "Year";
    public static final String Category = "Category";
    public static final String TTPoints = "TTPoints";
    public static final String RRPoints = "RRPoints";
    public static final String PrimePoints = "PrimePoints";
    public static final String BARPoints = "BARPoints";
    public static final String RacerAge = "RacerAge";
    public static final String GVCCID = "GVCCID";
    public static final String Upgraded = "Upgraded";

    public String getCreate(){
        return "create table " + getTableName()
                + " (" + _ID + " integer primary key autoincrement, "
                + Racer_ID + " integer references " + Racer.Instance().getTableName() + "(" + Racer._ID + ") not null, "
                + CheckInID + " text not null, "
                + Year + " integer not null,"
                + Category + " text not null,"
                + TTPoints + " integer not null,"
                + RRPoints + " integer not null,"
                + PrimePoints + " integer not null,"
                + RacerAge + " integer not null,"
                + GVCCID + " text null,"
                + Upgraded + " integer null"
                + ");";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(RacerClubInfo.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Uri Create(Context context, long racer_ID, String barcode, long year, String category, long ttPoints, long rrPoints, long primePoints, long age, Long gvccID, boolean upgraded) {
        ContentValues content = new ContentValues();
        content.put(RacerClubInfo.Racer_ID, racer_ID);
        content.put(RacerClubInfo.CheckInID, barcode);
        content.put(RacerClubInfo.Year, year);
        content.put(RacerClubInfo.Category, category);
        content.put(RacerClubInfo.TTPoints, 0);
        content.put(RacerClubInfo.RRPoints, 0);
        content.put(RacerClubInfo.PrimePoints, 0);
        content.put(RacerClubInfo.RacerAge, age);
        content.put(RacerClubInfo.GVCCID, gvccID);
        content.put(RacerClubInfo.Upgraded, upgraded ? 1l : 0l);
        return context.getContentResolver().insert(RacerClubInfo.Instance().CONTENT_URI, content);
    }

    public Cursor Read(Context context, String barcode, int year) {
        return context.getContentResolver().query(RacerClubInfo.Instance().CONTENT_URI, new String[]{RacerClubInfo._ID, RacerClubInfo.Racer_ID}, RacerClubInfo.CheckInID + "=? AND " + RacerClubInfo.Year + "=?", new String[]{barcode, Integer.toString(year)}, null);
    }

    public int Update(Context context, long racerClubInfo_ID, Long racer_ID, String checkInID, Long year,
             String category, Long ttPoints, Long rrPoints, Long primePoints, Long racerAge, String gvccID, Boolean upgraded) {
        ContentValues content = new ContentValues();
        if(racer_ID != null)
        {
            content.put(RacerClubInfo.Racer_ID, racer_ID);
        }
        if(checkInID != null)
        {
            content.put(RacerClubInfo.CheckInID, checkInID);
        }
        if(year != null)
        {
            content.put(RacerClubInfo.Year, year);
        }
        if(category != null)
        {
            content.put(RacerClubInfo.Category, category);
        }
        if(ttPoints != null)
        {
            content.put(RacerClubInfo.TTPoints, ttPoints);
        }
        if(rrPoints != null)
        {
            content.put(RacerClubInfo.RRPoints, rrPoints);
        }
        if(primePoints != null)
        {
            content.put(RacerClubInfo.PrimePoints, primePoints);
        }
        if(racerAge != null)
        {
            content.put(RacerClubInfo.RacerAge, racerAge);
        }
        if(gvccID != null)
        {
            content.put(RacerClubInfo.GVCCID, gvccID);
        }
        if(upgraded != null)
        {
            content.put(RacerClubInfo.Upgraded, upgraded ? 1l : 0l);
        }
        int numChanged = context.getContentResolver().update(RacerClubInfo.Instance().CONTENT_URI, content, RacerClubInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)});

        return numChanged;
    }
}
