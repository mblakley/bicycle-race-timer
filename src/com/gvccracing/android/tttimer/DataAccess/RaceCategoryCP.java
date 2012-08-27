package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class RaceCategoryCP {
	// BaseColumn contains _id.
    public static final class RaceCategory implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceCategory.class.getSimpleName() + "~");

        // Table column
        public static final String FullCategoryName = "RaceCategoryName";
        public static final String Category = "Category";
        public static final String Gender = "Gender";
        public static final String CategoryClass = "CategoryClass";
        public static final String Age = "Age";
        
        public static String getTableName(){
        	return RaceCategory.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + RaceCategory.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "
        	        + FullCategoryName + " text not null, " 
        	        + Category + " text null, "
        	        + Gender + " text not null,"
        	        + CategoryClass + " text not null,"
        	        + Age + " text null);";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceCategory.CONTENT_URI};
        }

		public static Uri Create(Context context, String fullCategoryName, String category, String gender, String categoryClass, String age) {
			ContentValues content = new ContentValues();
			content.put(RaceCategory.FullCategoryName, fullCategoryName);
			content.put(RaceCategory.Category, category);
			content.put(RaceCategory.Gender, gender);
			content.put(RaceCategory.CategoryClass, categoryClass);
			content.put(RaceCategory.Age, age);

	     	return context.getContentResolver().insert(RaceCategory.CONTENT_URI, content);
		}

		public static int Update(Context context, String where, String[] selectionArgs, String fullCategoryName, String category, String gender, String categoryClass, String age) {
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
			return context.getContentResolver().update(RaceCategory.CONTENT_URI, content, where, selectionArgs);
		}

		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceCategory.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
