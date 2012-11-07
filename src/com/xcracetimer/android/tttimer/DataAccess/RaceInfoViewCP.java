package com.xcracetimer.android.tttimer.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.xcracetimer.android.tttimer.DataAccess.RaceCP.Race;
import com.xcracetimer.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.xcracetimer.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.xcracetimer.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.xcracetimer.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.xcracetimer.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.xcracetimer.android.tttimer.DataAccess.RacerCP.Racer;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.xcracetimer.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.xcracetimer.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

public class RaceInfoViewCP {

    // BaseColumn contains _id.
    public static final class RaceInfoView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceInfoView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceMeet.getTableName() 
        			+ " JOIN " + Race.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race.RaceMeet_ID + " = " + RaceMeet.getTableName() + "._ID)" 
					+ " JOIN " + RaceLocation.getTableName() + 
					" ON (" + RaceMeet.getTableName() + "." + RaceMeet.RaceLocation_ID + " = " + RaceLocation.getTableName() + "._ID)";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{RaceInfoView.CONTENT_URI, Race.CONTENT_URI, RaceMeet.CONTENT_URI, RaceLocation.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(RaceInfoView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }

    // BaseColumn contains _id.
    public static final class MeetTeamsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, MeetTeamsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceMeetTeams.getTableName() 
        			+ " JOIN " + RaceMeet.getTableName() + 
					" ON (" + RaceMeetTeams.getTableName() + "." + RaceMeetTeams.RaceMeet_ID + " = " + RaceMeet.getTableName() + "._ID)"
					+ " JOIN " + RaceLocation.getTableName() + 
					" ON (" + RaceMeet.getTableName() + "." + RaceMeet.RaceLocation_ID + " = " + RaceLocation.getTableName() + "._ID)"
					+ " JOIN " + TeamInfo.getTableName() + 
					" ON (" + RaceMeetTeams.getTableName() + "." + RaceMeetTeams.TeamInfo_ID + " = " + TeamInfo.getTableName() + "._ID)";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{MeetTeamsView.CONTENT_URI, Race.CONTENT_URI, RaceMeet.CONTENT_URI, TeamInfo.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(MeetTeamsView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }

    
    // BaseColumn contains _id.
    public static final class RaceInfoResultsView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, RaceInfoResultsView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return RaceMeet.getTableName() + " JOIN " + RaceLocation.getTableName() + 
					" ON (" + RaceMeet.getTableName() + "." + RaceMeet.RaceLocation_ID + " = " + RaceLocation.getTableName() + "._ID)"
					+ " JOIN " + Race.getTableName() + 
					" ON (" + Race.getTableName() + "." + Race.RaceMeet_ID + " = " + RaceMeet.getTableName() + "._ID)"
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
    
 // BaseColumn contains _id.
    public static final class UnassignedTimesView implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(TTProvider.CONTENT_URI, UnassignedTimesView.class.getSimpleName() + "~");
        
        public static String getTableName(){
        	return UnassignedTimes.getTableName() 
        			+ " JOIN " + Race.getTableName() + 
					" ON (" + UnassignedTimes.getTableName() + "." + UnassignedTimes.Race_ID + " = " + Race.getTableName() + "._ID)"
        			+ " JOIN " + TeamInfo.getTableName() + 
					" ON (" + UnassignedTimes.getTableName() + "." + UnassignedTimes.TeamInfo_ID + " = " + TeamInfo.getTableName() + "._ID)"
					+ " LEFT OUTER JOIN " + RaceResults.getTableName() + 
					" ON (" + UnassignedTimes.getTableName() + "." + UnassignedTimes.RaceResult_ID + " = " + RaceResults.getTableName() + "._ID)"
        			+ " LEFT OUTER JOIN " + RacerClubInfo.getTableName() + 
    				" ON (" + RaceResults.getTableName() + "." + RaceResults.RacerClubInfo_ID + " = " + RacerClubInfo.getTableName() + "._ID)"
    				+ " LEFT OUTER JOIN " + Racer.getTableName() + 
    				" ON (" + RacerClubInfo.getTableName() + "." + RacerClubInfo.Racer_ID + " = " + Racer.getTableName() + "._ID)";
        }
        
        public static String getCreate(){
        	return "";
        }
        
        public static Uri[] getAllUrisToNotifyOnChange(){
        	return new Uri[]{UnassignedTimesView.CONTENT_URI, UnassignedTimes.CONTENT_URI};
        }
        
        public static Cursor Read(Context context, String[] fieldsToRetrieve, String selection, String[] selectionArgs, String sortOrder) {
			return context.getContentResolver().query(UnassignedTimesView.CONTENT_URI, fieldsToRetrieve, selection, selectionArgs, sortOrder);
		}
    }
}
