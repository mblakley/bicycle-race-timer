package com.gvccracing.android.tttimer.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class RaceInfoViewCP {

    // BaseColumn contains _id.
    public static final class RaceInfoView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceInfoView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return Race.getTableName() + " JOIN " + RaceLocation.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race.RaceLocation_ID + " = " + RaceLocation.getTableName() + "._ID)";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceInfoView.CONTENT_URI, Race.CONTENT_URI, RaceLocation.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceInfoView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
    
    // BaseColumn contains _id.
    public static final class RaceInfoResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceInfoResultsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return Race.getTableName() + " JOIN " + RaceLocation.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race.RaceLocation_ID + " = " + RaceLocation.getTableName() + "._ID)"
        			+ " JOIN " + RaceResults.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race._ID + " = " + RaceResults.getTableName() + "." + RaceResults.Race_ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceInfoResultsView.CONTENT_URI, Race.CONTENT_URI, RaceResults.CONTENT_URI};
        }
    }
    
    // BaseColumn contains _id.
    public static final class TeamRaceInfoResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamRaceInfoResultsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return Race.getTableName() + " JOIN " + RaceResults.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race._ID + " = " + RaceResults.getTableName() + "." + RaceResults.Race_ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamRaceInfoResultsView.CONTENT_URI, Race.CONTENT_URI, RaceResults.CONTENT_URI};
        }
    }
    
    // BaseColumn contains _id.
    public static final class RaceLapsInfoView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceLapsInfoView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return Race.getTableName() 
        			+ " JOIN " + RaceResults.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race._ID + " = " + RaceResults.getTableName() + "." + RaceResults.Race_ID + ")"
					+ " JOIN " + RaceLaps.getTableName() + 
					" ON (" + RaceLaps.getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.getTableName() + "." + RaceResults._ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceLapsInfoView.CONTENT_URI, Race.CONTENT_URI, RaceResults.CONTENT_URI, RaceLaps.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceLapsInfoView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
