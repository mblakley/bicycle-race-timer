package com.gvccracing.android.tttimer.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerUSACInfoCP.RacerUSACInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembersCP.TeamMembers;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class TeamCheckInViewCP {

    // BaseColumn contains _id.
    public static final class TeamCheckInViewInclusive implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamCheckInViewInclusive.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return TeamInfo.getTableName() 
        			+ " JOIN " + TeamMembers.getTableName() + 
					" ON (" + TeamInfo.getTableName() + "." + TeamInfo._ID + " = " + TeamMembers.getTableName() + "." + TeamMembers.TeamInfo_ID + ")"
					+ " JOIN " + RacerClubInfo.getTableName() + 
					" ON (" + TeamMembers.getTableName() + "." + TeamMembers.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + ")"
					+ " JOIN " + RacerUSACInfo.getTableName() + 
					" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo._ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo.RacerUSACInfo_ID + ")"
					+ " JOIN " + Racer.getTableName() + 
					" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")";        	
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamCheckInViewInclusive.CONTENT_URI, TeamCheckInViewExclusive.CONTENT_URI, TeamInfo.CONTENT_URI, TeamMembers.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamCheckInViewInclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
        
		public static int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			Cursor checkIns = context.getContentResolver().query(TeamCheckInViewInclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
			int numCheckIns = checkIns.getCount();
			if(checkIns != null){
				checkIns.close();
				checkIns = null;
			}
			return numCheckIns;
		}
    }
    
    // BaseColumn contains _id.
    public static final class TeamCheckInViewExclusive implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamCheckInViewExclusive.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	String tableName = TeamInfo.getTableName() 
        			+ " JOIN " + TeamMembers.getTableName() + 
					" ON (" + TeamInfo.getTableName() + "." + TeamInfo._ID + " = " + TeamMembers.getTableName() + "." + TeamMembers.TeamInfo_ID + ")"
					+ " JOIN " + RacerClubInfo.getTableName() + 
					" ON (" + TeamMembers.getTableName() + "." + TeamMembers.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + ")"
					+ " JOIN " + RaceResults.getTableName() + 
					" ON (" + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.getTableName() + "." + TeamInfo._ID + ")"
					+ " JOIN " + RacerClubInfo.getTableName() + 
					" ON (" + TeamMembers.getTableName() + "." + TeamMembers.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + ")"
					+ " JOIN " + RacerUSACInfo.getTableName() + 
					" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo._ID + " = " + RacerClubInfo.getTableName() + "." + RacerClubInfo.RacerUSACInfo_ID + ")"
					+ " JOIN " + Racer.getTableName() + 
					" ON (" + RacerUSACInfo.getTableName() + "." + RacerUSACInfo.Racer_ID + " = " + Racer.getTableName() + "." + Racer._ID + ")";
        	
        	return tableName;
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamCheckInViewExclusive.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamInfo.CONTENT_URI, TeamMembers.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamCheckInViewExclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}

		public static int ReadCount(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			Cursor checkIns = context.getContentResolver().query(TeamCheckInViewExclusive.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
			int numCheckIns = checkIns.getCount();
			if(checkIns != null){
				checkIns.close();
				checkIns = null;
			}
			return numCheckIns;
		}
    }
    
    // BaseColumn contains _id.
    public static final class TeamLapResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, TeamLapResultsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	String tableName = TeamInfo.getTableName() 
					+ " JOIN " + RaceResults.getTableName() + 
					" ON (" + RaceResults.getTableName() + "." + RaceResults.TeamInfo_ID + " = " + TeamInfo.getTableName() + "." + TeamInfo._ID + ")"
					+ " LEFT OUTER JOIN " + RaceLaps.getTableName() + 
					" ON (" + RaceLaps.getTableName() + "." + RaceLaps.RaceResult_ID + " = " + RaceResults.getTableName() + "." + RaceResults._ID + ")";
        	
        	return tableName;
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{TeamLapResultsView.CONTENT_URI, TeamCheckInViewInclusive.CONTENT_URI, TeamInfo.CONTENT_URI, TeamMembers.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(TeamLapResultsView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
