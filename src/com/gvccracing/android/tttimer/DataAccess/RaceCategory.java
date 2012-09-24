package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

// BaseColumn contains _id.
public final class RaceCategory extends ContentProviderTable implements BaseColumns {
    
    private static final RaceCategory instance = new RaceCategory();
    
    public RaceCategory() {}
 
    public static RaceCategory Instance() {
        return instance;
    }

    // Table column
    public static final String FullCategoryName = "RaceCategoryName";
    public static final String Category = "Category";
    public static final String Gender = "Gender";
    public static final String CategoryClass = "CategoryClass";
    public static final String Age = "Age";
    public static final String RaceSeries_ID = "RaceSeries_ID";        
    
    public String getCreate(){
    	return "create table " + RaceCategory.Instance().getTableName() 
    	        + " (" + _ID + " integer primary key autoincrement, "
    	        + FullCategoryName + " text not null, " 
    	        + Category + " text null, "
    	        + Gender + " text not null,"
    	        + CategoryClass + " text not null,"
    	        + Age + " text null,"
    	        + RaceSeries_ID + " integer references " + RaceSeries.Instance().getTableName() + "(" + RaceSeries._ID + ") not null"
    	        + ");";
    }

    /**
     * Create a new RaceCategory record
     * @param context - The Context used to get the contentResolver that we call insert on
     * @param fullCategoryName - The full category name (ex: Masters 45+)
     * @param category - ex: Cat 4-5
     * @param gender - Men, Women, or Both
     * @param categoryClass - Masters, Elite, or Juniors
     * @param age - 35+, 45+, Blank, etc
     * @return The URI containing the id of the newly added record
     */
	public Uri Create(Context context, String fullCategoryName, String category, String gender, String categoryClass, String age, long raceSeries_ID) {
		ContentValues content = new ContentValues();
		content.put(RaceCategory.FullCategoryName, fullCategoryName);
		content.put(RaceCategory.Category, category);
		content.put(RaceCategory.Gender, gender);
		content.put(RaceCategory.CategoryClass, categoryClass);
		content.put(RaceCategory.Age, age);
		content.put(RaceCategory.RaceSeries_ID, raceSeries_ID);

     	return context.getContentResolver().insert(CONTENT_URI, content);
	}

	public int Update(Context context, String where, String[] selectionArgs, String fullCategoryName, String category, String gender, String categoryClass, String age, Long raceSeries_ID) {
		ContentValues content = new ContentValues();
		if(fullCategoryName != null)
        {
			content.put(RaceCategory.FullCategoryName, fullCategoryName);
        }
        if(category != null)
        {
        	content.put(RaceCategory.Category, category);
        }
        if(gender != null)
        {
        	content.put(RaceCategory.Gender, gender);
        }
        if(categoryClass != null)
        {
        	content.put(RaceCategory.CategoryClass, categoryClass);
        }
        if(age != null)
        {
        	content.put(RaceCategory.Age, age);
        }
        if(raceSeries_ID != null)
        {
        	content.put(RaceCategory.RaceSeries_ID, raceSeries_ID);
        }
		return context.getContentResolver().update(RaceCategory.Instance().CONTENT_URI, content, where, selectionArgs);
	}
}
