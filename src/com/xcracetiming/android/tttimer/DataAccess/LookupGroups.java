package com.xcracetiming.android.tttimer.DataAccess;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class LookupGroups extends ContentProviderTable implements BaseColumns {
    
    private static final LookupGroups instance = new LookupGroups();
    
    public LookupGroups() {}
 
    public static LookupGroups Instance() {
        return instance;
    }

    // Table column
    public static final String LookupGroup = "LookupGroup";
    public static final String LookupValue = "LookupValue";
    
    // Groups
    public static final String Lookup_Group_Category = "Category";
    public static final String Lookup_Group_Humidity = "Humidity";
    
    public String getTableName(){
    	return LookupGroups.class.getSimpleName();
    }
    
    public String getCreate(){
    	return "create table " + getTableName()
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + LookupGroup + " text not null, "
    	        + LookupValue + " text not null);";
    }

	public int Update(Context context, Long Lookup_ID, String LookupGroup, String LookupValue, boolean addIfNotExist) {
		ContentValues content = new ContentValues();
		if(LookupGroup != null)
        {
        	content.put(LookupGroups.LookupGroup, LookupGroup);
        }
        if(LookupValue != null)
        {
        	content.put(LookupGroups.LookupValue, LookupValue);
        }
		int numChanged = context.getContentResolver().update(LookupGroups.Instance().CONTENT_URI, content, LookupGroups._ID + "=?", new String[]{Long.toString(Lookup_ID)});
		if(addIfNotExist && numChanged < 1){
			LookupGroups.Instance().Create(context, content);
			numChanged = 1;
		}
		
		return numChanged;
	}

	public Cursor Read(Context context, String LookupGroupToRetrieve) {
		return context.getContentResolver().query(LookupGroups.Instance().CONTENT_URI, new String[]{LookupGroups._ID, LookupGroups.LookupGroup, LookupGroups.LookupValue}, LookupGroups.LookupGroup + "=?", new String[]{LookupGroupToRetrieve}, null); 
	}
	
	public List<String> ReadValue(Context context, String LookupGroupToRetrieve) {
		Cursor temp = LookupGroups.Instance().Read(context, LookupGroupToRetrieve);

		List<String> vals = new ArrayList<String>();
		if(temp != null){
			temp.moveToFirst();
			while(!temp.isAfterLast()){
				vals.add(temp.getString(temp.getColumnIndex(LookupGroups.LookupValue)));
			}
		}
		temp.close();
		temp = null;
		return vals;
	}
}