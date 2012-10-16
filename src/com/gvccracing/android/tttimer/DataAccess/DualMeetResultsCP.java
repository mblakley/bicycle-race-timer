package com.gvccracing.android.tttimer.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

public class DualMeetResultsCP {

    // BaseColumn contains _id.
    public static final class DualMeetResults implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, DualMeetResults.class.getSimpleName() + "~");
        
        // Table column
        public static final String Race_ID = "Race_ID";
        public static final String Team1_TeamInfo_ID = "Team1_TeamInfo_ID";
        public static final String Team2_TeamInfo_ID = "Team2_TeamInfo_ID";
        public static final String Team1_Points = "Team1_Points";
        public static final String Team2_Points = "Team2_Points";
        public static final String Winning_TeamInfo_ID = "Winning_TeamInfo_ID";
        
        public static String getTableName(){
        	return DualMeetResults.class.getSimpleName();
        }
        
        public static String getCreate(){
        	return "create table " + DualMeetResults.getTableName() 
        	        + " (" + _ID + " integer primary key autoincrement, "        	        
        	        + Race_ID + " integer references " + Race.getTableName() + "(" + Race._ID + ") not null, "
        	        + Team1_TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, "
        	        + Team2_TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") not null, "
        	        + Team1_Points + " integer not null,"
        	        + Team2_Points + " integer not null,"
        	        + Winning_TeamInfo_ID + " integer references " + TeamInfo.getTableName() + "(" + TeamInfo._ID + ") null"
        	        + ");";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{DualMeetResults.CONTENT_URI};
        }

		public static Uri Create(Context context,
				long race_ID, long team1_TeamInfo_ID, long team2_TeamInfo_ID,
				Integer team1_Points, Integer team2_Points, boolean winning_TeamInfo_ID) {
			ContentValues content = new ContentValues();
	     	content.put(DualMeetResults.Race_ID, race_ID);
	     	content.put(DualMeetResults.Team1_TeamInfo_ID, team1_TeamInfo_ID);
	     	content.put(DualMeetResults.Team2_TeamInfo_ID, team2_TeamInfo_ID);
	     	content.put(DualMeetResults.Team1_Points, team1_Points);
	     	content.put(DualMeetResults.Team2_Points, team2_Points);
	     	content.put(DualMeetResults.Winning_TeamInfo_ID, winning_TeamInfo_ID);
	     	return context.getContentResolver().insert(DualMeetResults.CONTENT_URI, content);
		}
		
		public static Uri Create(Context context, ContentValues content) {
	     	return context.getContentResolver().insert(DualMeetResults.CONTENT_URI, content);
		}
		
		public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(DualMeetResults.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static int Update(Context context, ContentValues content, String selection, String[] selectionArgs) {
			return context.getContentResolver().update(DualMeetResults.CONTENT_URI, content, selection, selectionArgs);
		}
		
		public static int Update(Context context, ContentValues content, String selection, String[] selectionArgs, boolean addIfNotExist) {			
			int numChanged = context.getContentResolver().update(DualMeetResults.CONTENT_URI, content, selection, selectionArgs);
			if(addIfNotExist && numChanged < 1){
				DualMeetResults.Create(context, content);
				numChanged = 1;
			}
			
			return numChanged;
		}
		
		public static int Delete(Context context, String selection, String[] selectionArgs) {
			return context.getContentResolver().delete(DualMeetResults.CONTENT_URI, selection, selectionArgs);
		}
    }
    
    // BaseColumn contains _id.
    public static final class DualMeetResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, DualMeetResultsView.class.getSimpleName() + "~");
        
        public static final String TeamInfo1 = "TeamInfo1";
        public static final String TeamInfo2 = "TeamInfo2";
        public static final String WinningTeamInfo = "WinningTeamInfo";
        
        public static String getTableName(){
        	return DualMeetResults.getTableName() + 
        			" JOIN " + TeamInfo.getTableName() + " " + DualMeetResultsView.TeamInfo1 +
    				" ON (" + DualMeetResults.getTableName() + "." + DualMeetResults.Team1_TeamInfo_ID + " = " + DualMeetResultsView.TeamInfo1 + "." + TeamInfo._ID + ")" +
        			" JOIN " + TeamInfo.getTableName() + " " + DualMeetResultsView.TeamInfo2 +
    				" ON (" + DualMeetResults.getTableName() + "." + DualMeetResults.Team2_TeamInfo_ID + " = " + DualMeetResultsView.TeamInfo2 + "." + TeamInfo._ID + ")" +
    				" JOIN " + Race.getTableName() + 
    				" ON (" + Race.getTableName() + "." + Race._ID + " = " + DualMeetResults.getTableName() + "." + DualMeetResults.Race_ID + ")";// +
    				//" LEFT OUTER JOIN " + TeamInfo.getTableName() + " " + DualMeetResultsView.WinningTeamInfo +
    				//" ON (" + DualMeetResults.getTableName() + "." + DualMeetResults.Winning_TeamInfo_ID + " = " + TeamInfo.getTableName() + "." + TeamInfo._ID + ")";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{DualMeetResultsView.CONTENT_URI};
        }

        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(DualMeetResultsView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }    
}
