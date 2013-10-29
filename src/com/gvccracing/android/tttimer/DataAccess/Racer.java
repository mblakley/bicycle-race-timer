package com.gvccracing.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.Hashtable;

import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.Views.CheckInViewInclusive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class Racer extends ContentProviderTable implements BaseColumns {

    private static final Racer instance = new Racer();

    public Racer() {}

    public static Racer Instance() {
        return instance;
    }

    // Table column
    public static final String FirstName = "FirstName";
    public static final String LastName = "LastName";
    public static final String Gender = "Gender";
    public static final String USACNumber = "USACNumber";
    public static final String BirthDate = "BirthDate";
    public static final String PhoneNumber = "PhoneNumber";
    public static final String EmergencyContactName = "EmergencyContactName";
    public static final String EmergencyContactPhoneNumber = "EmergencyContactPhoneNumber";

    public String getCreate(){
        return "create table " + getTableName()
        + " (" + _ID + " integer primary key autoincrement, "
        + FirstName + " text not null, "
        + LastName + " text not null,"
        + Gender + " text null,"
        + USACNumber + " integer not null,"
        + BirthDate + " integer null,"
        + PhoneNumber + " integer null,"
        + EmergencyContactName + " text null,"
        + EmergencyContactPhoneNumber + " integer null"
        + ");";
    }

    public ArrayList<Uri> getAllUrisToNotifyOnChange(){
        ArrayList<Uri> urisToNotify = super.getAllUrisToNotifyOnChange();

        urisToNotify.add(Racer.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewInclusive.Instance().CONTENT_URI);
        urisToNotify.add(CheckInViewExclusive.Instance().CONTENT_URI);

        return urisToNotify;
    }

    public Uri Create(Context context, String firstName, String lastName, int usacNumber,
                            long birthDate, long phoneNumber, String emergencyContactName, long emergencyContactPhoneNumber, String gender) {
        ContentValues content = new ContentValues();
        content.put(Racer.FirstName, firstName);
        content.put(Racer.LastName, lastName);
        content.put(Racer.Gender, gender);
        content.put(Racer.USACNumber, usacNumber);
        content.put(Racer.BirthDate, birthDate);
        content.put(Racer.PhoneNumber, phoneNumber);
        content.put(Racer.EmergencyContactName, emergencyContactName);
        content.put(Racer.EmergencyContactPhoneNumber, emergencyContactPhoneNumber);
        return context.getContentResolver().insert(Racer.Instance().CONTENT_URI, content);
    }

    public Hashtable<String, Object> getValues(Context context, Long racer_ID) {
        Hashtable<String, Object> racerValues = new Hashtable<String, Object>();

        Cursor racerCursor = Racer.Instance().Read(context, null, Racer._ID + "=?", new String[]{Long.toString(racer_ID)}, null);
        if(racerCursor != null && racerCursor.getCount() > 0){
            racerCursor.moveToFirst();
            racerValues.put(Racer._ID, racer_ID);
            racerValues.put(Racer.FirstName, racerCursor.getString(racerCursor.getColumnIndex(Racer.FirstName)));
            racerValues.put(Racer.LastName, racerCursor.getString(racerCursor.getColumnIndex(Racer.LastName)));
            racerValues.put(Racer.Gender, racerCursor.getString(racerCursor.getColumnIndex(Racer.Gender)));
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

    public int Update(Context context, long racer_ID, String firstName, String lastName, Integer usacNumber, Long birthDate,
                             Long phoneNumber, String emergencyContactName, Long emergencyContactPhoneNumber, String gender) {
        ContentValues content = new ContentValues();
        if(firstName != null)
        {
            content.put(Racer.FirstName, firstName);
        }
        if(lastName != null)
        {
            content.put(Racer.LastName, lastName);
        }
        if(gender != null)
        {
            content.put(Racer.Gender, gender);
        }
        if(usacNumber != null)
        {
            content.put(Racer.USACNumber, usacNumber);
        }
        if(birthDate != null)
        {
            content.put(Racer.BirthDate, birthDate);
        }
        if(phoneNumber != null)
        {
            content.put(Racer.PhoneNumber, phoneNumber);
        }
        if(emergencyContactName != null)
        {
            content.put(Racer.EmergencyContactName, emergencyContactName);
        }
        if(emergencyContactPhoneNumber != null)
        {
            content.put(Racer.EmergencyContactPhoneNumber, emergencyContactPhoneNumber);
        }
        int numChanged = context.getContentResolver().update(Racer.Instance().CONTENT_URI, content, Racer._ID + "=?", new String[]{Long.toString(racer_ID)});

        return numChanged;
    }
}