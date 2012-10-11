package com.gvccracing.android.tttimer.DataAccess;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.LocationImagesCP.LocationImages;
import com.gvccracing.android.tttimer.DataAccess.LookupGroupsCP.LookupGroups;
import com.gvccracing.android.tttimer.DataAccess.PrimesCP.Primes;
import com.gvccracing.android.tttimer.DataAccess.RaceCP.Race;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.MeetTeamsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoResultsView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.RaceLapsInfoView;
import com.gvccracing.android.tttimer.DataAccess.RaceInfoViewCP.UnassignedTimesView;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.TeamLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceLocationCP.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetCP.RaceMeet;
import com.gvccracing.android.tttimer.DataAccess.RaceMeetTeamsCP.RaceMeetTeams;
import com.gvccracing.android.tttimer.DataAccess.RaceNotesCP.RaceNotes;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsTeamOrRacerViewCP.RaceResultsTeamOrRacerView;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.RacerInfoViewCP.RacerInfoView;
import com.gvccracing.android.tttimer.DataAccess.RacerPreviousResultsViewCP.RacerPreviousResultsView;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamRacesCP.TeamRaces;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TTProvider extends ContentProvider {

	// Database located at /data/data/com.gvccracing.android.tttimer/databases/GVCCRaces
	public static final String PROVIDER_NAME = "com.gvccracing.android.tttimer.DataAccess.TTProvider";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);

    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "GVCCRaces";
    private static final int DATABASE_VERSION = 7;
	
	private DatabaseHelper mDB;

	@Override
	public String getType(Uri arg0) {
		return "TTProvider";
	}

	@Override
	public boolean onCreate() {
		Log.i("TTProvider", "onCreate start");
    	mDB = new DatabaseHelper(getContext());
		Log.i("TTProvider", "onCreate complete");
		return false;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues content) {

		Log.i("TTProvider", "insert start: uri=" + uri.toString());
		Uri resultUri = uri;
		Uri[] notifyUris = new Uri[]{uri};
		if(uri.toString().contains(RacerClubInfo.CONTENT_URI.toString())){
			long racerClubInfo_ID = mDB.getWritableDatabase().insert(RacerClubInfo.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(racerClubInfo_ID));
			
			notifyUris = RacerClubInfo.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceResults.CONTENT_URI.toString())){
			long raceResult_ID = mDB.getWritableDatabase().insert(RaceResults.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(raceResult_ID));
			
			notifyUris = RaceResults.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(Racer.CONTENT_URI.toString())){
			long racer_ID = mDB.getWritableDatabase().insert(Racer.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(racer_ID));
			
			notifyUris = Racer.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(UnassignedTimes.CONTENT_URI.toString())){
			long unassignedTime_ID = mDB.getWritableDatabase().insert(UnassignedTimes.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(unassignedTime_ID));
			
			notifyUris = UnassignedTimes.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(Race.CONTENT_URI.toString())){
			long race_ID = mDB.getWritableDatabase().insert(Race.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(race_ID));
			
			notifyUris = Race.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceLocation.CONTENT_URI.toString())){
			long raceLocation_ID = mDB.getWritableDatabase().insert(RaceLocation.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(raceLocation_ID));
			
			notifyUris = RaceLocation.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(AppSettings.CONTENT_URI.toString())){
			long appSetting_ID = mDB.getWritableDatabase().insert(AppSettings.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(appSetting_ID));
			
			notifyUris = AppSettings.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceNotes.CONTENT_URI.toString())){
			long raceNote_ID = mDB.getWritableDatabase().insert(RaceNotes.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(raceNote_ID));
			
			notifyUris = RaceNotes.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(TeamInfo.CONTENT_URI.toString())){
			long teamInfo_ID = mDB.getWritableDatabase().insert(TeamInfo.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(teamInfo_ID));
			
			notifyUris = TeamInfo.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceMeetTeams.CONTENT_URI.toString())){
			long teamMember_ID = mDB.getWritableDatabase().insert(RaceMeetTeams.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(teamMember_ID));
			
			notifyUris = RaceMeetTeams.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceLaps.CONTENT_URI.toString())){
			long raceLap_ID = mDB.getWritableDatabase().insert(RaceLaps.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(raceLap_ID));
			
			notifyUris = RaceLaps.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LookupGroups.CONTENT_URI.toString())){
			long lookupGroup_ID = mDB.getWritableDatabase().insert(LookupGroups.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(lookupGroup_ID));
			
			notifyUris = LookupGroups.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LocationImages.CONTENT_URI.toString())){
			long locationImage_ID = mDB.getWritableDatabase().insert(LocationImages.getTableName(), null, content);
			
			resultUri = Uri.withAppendedPath(resultUri, Long.toString(locationImage_ID));
			
			notifyUris = LocationImages.getAllUrisToNotifyOnChange();
		} else{
			throw new UnsupportedOperationException("You're an idiot...add the uri " + uri.toString() + " to the TTProvider.insert if/else statement");
		}
		
		if(resultUri != uri){
			// notify all of the uris in the list
			for(Uri notify : notifyUris){
				getContext().getContentResolver().notifyChange(notify, null);
			}
		}
		Log.i("TTProvider", "insert complete: uri=" + uri.toString());
		
		return resultUri;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		Log.i("TTProvider", "query start: uri=" + uri.toString() + " selection=" + selection);
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setDistinct(true);
		if(uri.toString().contains(CheckInViewInclusive.CONTENT_URI.toString())){
			// CheckInViewInclusive
			qBuilder.setTables(CheckInViewInclusive.getTableName());		
			
			Cursor checkInCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			checkInCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return checkInCursor;
		} else if(uri.toString().contains(CheckInViewExclusive.CONTENT_URI.toString())){
			// CheckInViewExclusive
			qBuilder.setTables(CheckInViewExclusive.getTableName());
			String groupBy = null;
			if(!uri.getLastPathSegment().contains("~")){
				if(uri.getLastPathSegment().contains("group by")){
					groupBy = uri.getLastPathSegment().replace("group by", "");
				}
			}
			
			String limit = null;
			if(uri.toString().contains("OnDeck")){
				limit = uri.getLastPathSegment();
			}
		
			Cursor checkInCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, groupBy, null, sortOrder,
													limit);
			checkInCursor.setNotificationUri(getContext().getContentResolver(), CheckInViewExclusive.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection + " count=" + checkInCursor.getCount());
			return checkInCursor;
		} else if(uri.toString().contains(Race.CONTENT_URI.toString())){
			// Race
			qBuilder.setTables(Race.getTableName());
			if(!uri.getLastPathSegment().contains("~")){
				String id = uri.getLastPathSegment();
				selection = Race._ID + " = ?";
				selectionArgs = new String[]{id};
			}
			
			Cursor raceCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);
			raceCursor.setNotificationUri(getContext().getContentResolver(), Race.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceCursor;
		}else if(uri.toString().contains(UnassignedTimes.CONTENT_URI.toString())){
			// UnassignedTimes
			qBuilder.setTables(UnassignedTimes.getTableName());
			
			Cursor raceCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);
			raceCursor.setNotificationUri(getContext().getContentResolver(), UnassignedTimes.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceCursor;
		}else if(uri.toString().contains(RaceResults.CONTENT_URI.toString())){
			// RaceResults
			qBuilder.setTables(RaceResults.getTableName());
			
			Cursor raceResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);
			raceResultsCursor.setNotificationUri(getContext().getContentResolver(), RaceResults.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceResultsCursor;
		}else if(uri.toString().contains(RaceLocation.CONTENT_URI.toString())){
			// Race Location
			qBuilder.setTables(RaceLocation.getTableName());
			
			Cursor raceLocationsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);
			raceLocationsCursor.setNotificationUri(getContext().getContentResolver(), RaceLocation.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceLocationsCursor;
		}else if(uri.toString().contains(RaceInfoView.CONTENT_URI.toString())){
			// RaceInfoView
			qBuilder.setTables(RaceInfoView.getTableName());
			
			Cursor raceinfoCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);
			raceinfoCursor.setNotificationUri(getContext().getContentResolver(), RaceInfoView.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceinfoCursor;
		}else if(uri.toString().contains(RaceInfoResultsView.CONTENT_URI.toString())){
			// RaceInfoResultsView
			qBuilder.setTables(RaceInfoResultsView.getTableName());
			
			Cursor raceinfoResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
					projection, 
					selection, 
					selectionArgs, null, null, sortOrder,
					null);
			
			raceinfoResultsCursor.setNotificationUri(getContext().getContentResolver(), RaceInfoResultsView.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceinfoResultsCursor;
		} else if(uri.toString().contains(AppSettings.CONTENT_URI.toString())){
			// AppSettings
			qBuilder.setTables(AppSettings.getTableName());
			
			Cursor appSettingCursor = qBuilder.query(mDB.getReadableDatabase(),
					projection, 
					selection, 
					selectionArgs, null, null, sortOrder,
					"1");
			
			appSettingCursor.setNotificationUri(getContext().getContentResolver(), AppSettings.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return appSettingCursor;
		} else if(uri.toString().contains(Racer.CONTENT_URI.toString())){
			// Racer
			qBuilder.setTables(Racer.getTableName());
			
			Cursor racerCursor = qBuilder.query(mDB.getReadableDatabase(),
					projection, 
					selection, 
					selectionArgs, null, null, sortOrder,
					null);
			
			racerCursor.setNotificationUri(getContext().getContentResolver(), Racer.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return racerCursor;
		} else if(uri.toString().contains(RacerClubInfo.CONTENT_URI.toString())){
			// Racer
			qBuilder.setTables(RacerClubInfo.getTableName());
			
			Cursor racerClubInfoCursor = qBuilder.query(mDB.getReadableDatabase(),
					projection, 
					selection, 
					selectionArgs, null, null, sortOrder,
					null);
			
			racerClubInfoCursor.setNotificationUri(getContext().getContentResolver(), RacerClubInfo.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return racerClubInfoCursor;
		}  else if(uri.toString().contains(RaceNotes.CONTENT_URI.toString())){
			// RaceNotes
			qBuilder.setTables(RaceNotes.getTableName());
			
			Cursor raceNotesCursor = qBuilder.query(mDB.getReadableDatabase(),
					projection, 
					selection, 
					selectionArgs, null, null, sortOrder,
					null);
			
			raceNotesCursor.setNotificationUri(getContext().getContentResolver(), RaceNotes.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceNotesCursor;
		} else if(uri.toString().contains(RacerPreviousResultsView.CONTENT_URI.toString())){
			// RacerPreviousResultsView
			qBuilder.setTables(RacerPreviousResultsView.getTableName());		
			
			Cursor racerPreviousResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			racerPreviousResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return racerPreviousResultsCursor;
		} else if(uri.toString().contains(TeamInfo.CONTENT_URI.toString())){
			// TeamInfo
			qBuilder.setTables(TeamInfo.getTableName());		
			
			Cursor teamInfoResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			teamInfoResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return teamInfoResultsCursor;
		} else if(uri.toString().contains(RaceMeetTeams.CONTENT_URI.toString())){
			// RaceMeetTeams
			qBuilder.setTables(RaceMeetTeams.getTableName());		
			
			Cursor teamMemberResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			teamMemberResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return teamMemberResultsCursor;
		} else if(uri.toString().contains(RaceLaps.CONTENT_URI.toString())){
			// RaceLaps
			qBuilder.setTables(RaceLaps.getTableName());		
			
			Cursor raceLapsResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			raceLapsResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceLapsResultsCursor;
		} else if(uri.toString().contains(RaceLapsInfoView.CONTENT_URI.toString())){
			// RaceLapsInfoView
			qBuilder.setTables(RaceLapsInfoView.getTableName());		
			
			Cursor raceLapsResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			raceLapsResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceLapsResultsCursor;
		} else if(uri.toString().contains(TeamLaps.CONTENT_URI.toString())){
			// TeamLaps
			qBuilder.setTables(TeamLaps.getTableName());
			String groupBy = null;
			String having = null;
			if(!uri.getLastPathSegment().contains("~")){
				String lastSegment = uri.getLastPathSegment();
				String[] otherParams = lastSegment.split("&");
				if(otherParams.length > 0 && otherParams[0].contains("group by")){
					groupBy = otherParams[0].replace("group by", "").trim();
				}
				if(otherParams.length > 1 && otherParams[1].contains("having")){
					having = otherParams[1].replace("having", "").trim();
				}
			}			
			
			Cursor teamLapsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, groupBy, having, sortOrder,
													null);	
			
			teamLapsCursor.setNotificationUri(getContext().getContentResolver(), TeamLaps.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return teamLapsCursor;
		} else if(uri.toString().contains(RacerInfoView.CONTENT_URI.toString())){
			// RacerInfoView
			qBuilder.setTables(RacerInfoView.getTableName());		
			
			Cursor racerInfoResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			racerInfoResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return racerInfoResultsCursor;
		} else if(uri.toString().contains(RaceResultsTeamOrRacerView.CONTENT_URI.toString())){
			// RaceResultsTeamOrRacerView
			qBuilder.setTables(RaceResultsTeamOrRacerView.getTableName());		
			
			Cursor raceResultsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			raceResultsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceResultsCursor;
		} else if(uri.toString().contains(LookupGroups.CONTENT_URI.toString())){
			// LookupGroups
			qBuilder.setTables(LookupGroups.getTableName());		
			
			Cursor lookupGroupsCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			lookupGroupsCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return lookupGroupsCursor;
		} else if(uri.toString().contains(LocationImages.CONTENT_URI.toString())){
			// LocationImages
			qBuilder.setTables(LocationImages.getTableName());		
			
			Cursor locationImagesCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			locationImagesCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return locationImagesCursor;
		} else if(uri.toString().contains(RaceMeet.CONTENT_URI.toString())){
			// RaceMeet
			qBuilder.setTables(RaceMeet.getTableName());		
			
			Cursor raceMeetCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			raceMeetCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceMeetCursor;
		} else if(uri.toString().contains(MeetTeamsView.CONTENT_URI.toString())){
			// MeetTeamsView
			qBuilder.setTables(MeetTeamsView.getTableName());		
			
			Cursor raceMeetCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, null, null, sortOrder,
													null);	
			
			raceMeetCursor.setNotificationUri(getContext().getContentResolver(), uri);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return raceMeetCursor;
		} else if(uri.toString().contains(UnassignedTimesView.CONTENT_URI.toString())){
			// UnassignedTimesView
			qBuilder.setTables(UnassignedTimesView.getTableName());		
			String groupBy = null;
			if(!uri.getLastPathSegment().contains("~")){
				if(uri.getLastPathSegment().contains("group by")){
					groupBy = uri.getLastPathSegment().replace("group by", "");
				}
			}
			
			String limit = null;
		
			Cursor checkInCursor = qBuilder.query(mDB.getReadableDatabase(),
													projection, 
													selection, 
													selectionArgs, groupBy, null, sortOrder,
													limit);
			checkInCursor.setNotificationUri(getContext().getContentResolver(), UnassignedTimesView.CONTENT_URI);	
			Log.i("TTProvider", "query complete: uri=" + uri.toString() + " selection=" + selection);
			return checkInCursor;
		} else{
			throw new UnsupportedOperationException("You're an idiot...add the uri " + uri.toString() + " to the TTProvider.query if/else statement");
		}		
	}

	@Override
	public int update(Uri uri, ContentValues content, String selection, String[] selectionArgs) {
		Log.i("TTProvider", "update start: uri=" + uri.toString() + " selection=" + selection);
		Uri[] notifyUris = new Uri[]{uri};
		int numChanged = 0;
		if(uri.toString().contains(Race.CONTENT_URI.toString())){
			String race_ID = uri.getLastPathSegment();
			if(race_ID.contains("~")){
				numChanged = mDB.getWritableDatabase().update(Race.getTableName(), content, selection, selectionArgs);
			}else{
				numChanged = mDB.getWritableDatabase().update(Race.getTableName(), content, Race._ID + "=" + race_ID, null);
			}
			
			notifyUris = Race.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceResults.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RaceResults.getTableName(), content, selection, selectionArgs);

			notifyUris = RaceResults.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(AppSettings.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(AppSettings.getTableName(), content, selection, selectionArgs);

			notifyUris = AppSettings.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceNotes.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RaceNotes.getTableName(), content, selection, selectionArgs);

			notifyUris = RaceNotes.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RacerClubInfo.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RacerClubInfo.getTableName(), content, selection, selectionArgs);

			notifyUris = RacerClubInfo.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(TeamInfo.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(TeamInfo.getTableName(), content, selection, selectionArgs);

			notifyUris = TeamInfo.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceMeetTeams.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RaceMeetTeams.getTableName(), content, selection, selectionArgs);

			notifyUris = RaceMeetTeams.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceLaps.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RaceLaps.getTableName(), content, selection, selectionArgs);

			notifyUris = RaceLaps.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(Racer.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(Racer.getTableName(), content, selection, selectionArgs);

			notifyUris = Racer.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(UnassignedTimes.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(UnassignedTimes.getTableName(), content, selection, selectionArgs);

			notifyUris = UnassignedTimes.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceLocation.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(RaceLocation.getTableName(), content, selection, selectionArgs);

			notifyUris = RaceLocation.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LookupGroups.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(LookupGroups.getTableName(), content, selection, selectionArgs);

			notifyUris = LookupGroups.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LocationImages.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().update(LocationImages.getTableName(), content, selection, selectionArgs);

			notifyUris = LocationImages.getAllUrisToNotifyOnChange();
		} else {
			throw new UnsupportedOperationException("You're an idiot...add the uri " + uri.toString() + " to the TTProvider.update if/else statement");
		}
		// notify all of the uris in the list
		for(Uri notify : notifyUris){
			getContext().getContentResolver().notifyChange(notify, null);
		}
		Log.i("TTProvider", "update complete: uri=" + uri.toString() + " selection=" + selection);
		return numChanged;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.i("TTProvider", "update start: uri=" + uri.toString() + " selection=" + selection);
		Uri[] notifyUris = new Uri[]{uri};
		int numChanged = 0;
		if(uri.toString().contains(UnassignedTimes.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(UnassignedTimes.getTableName(), selection, selectionArgs);

			notifyUris = UnassignedTimes.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceResults.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(RaceResults.getTableName(), selection, selectionArgs);

			notifyUris = RaceResults.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceMeetTeams.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(RaceMeetTeams.getTableName(), selection, selectionArgs);

			notifyUris = RaceMeetTeams.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(TeamInfo.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(TeamInfo.getTableName(), selection, selectionArgs);

			notifyUris = TeamInfo.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(RaceLaps.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(RaceLaps.getTableName(), selection, selectionArgs);

			notifyUris = RaceLaps.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LookupGroups.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(LookupGroups.getTableName(), selection, selectionArgs);

			notifyUris = LookupGroups.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(LocationImages.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(LocationImages.getTableName(), selection, selectionArgs);

			notifyUris = LocationImages.getAllUrisToNotifyOnChange();
		} else if(uri.toString().contains(AppSettings.CONTENT_URI.toString())){
			numChanged = mDB.getWritableDatabase().delete(AppSettings.getTableName(), selection, selectionArgs);

			notifyUris = AppSettings.getAllUrisToNotifyOnChange();
		} else{
			throw new UnsupportedOperationException("You're an idiot...add the uri " + uri.toString() + " to the TTProvider.delete if/else statement");
		}
		
		// notify all of the uris in the list
		for(Uri notify : notifyUris){
			getContext().getContentResolver().notifyChange(notify, null);
		}
		Log.i("TTProvider", "update complete: uri=" + uri.toString() + " selection=" + selection);
		return numChanged;
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper 
	{
	    DatabaseHelper(Context context) 
	    {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) 
	    {
	    	Log.w(TAG, "Creating " + DATABASE_NAME );

	        db.execSQL(RaceLocation.getCreate());
	    	db.execSQL(RaceMeet.getCreate());
	    	db.execSQL(Racer.getCreate());
	        db.execSQL(RacerClubInfo.getCreate());
	        db.execSQL(Race.getCreate());
	        db.execSQL(RaceResults.getCreate());
	        db.execSQL(Primes.getCreate());
	        db.execSQL(UnassignedTimes.getCreate());
	        db.execSQL(RaceNotes.getCreate());
	        db.execSQL(TeamInfo.getCreate());
	        db.execSQL(RaceMeetTeams.getCreate());
	        db.execSQL(TeamRaces.getCreate());
	        db.execSQL(AppSettings.getCreate());
	        db.execSQL(RaceLaps.getCreate());
	        db.execSQL(LookupGroups.getCreate());
	        // Initial load of lookup groups
	        db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Dry');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Moderate');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Humid');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Humidity + "', 'Raining');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'Varsity');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'JV');");
    		db.execSQL("INSERT INTO " + LookupGroups.getTableName() + "(" + LookupGroups.LookupGroup + "," + LookupGroups.LookupValue + ") VALUES ('" + LookupGroups.Lookup_Group_Category + "', 'Modified');");
    		db.execSQL(LocationImages.getCreate());		
    	
    		// Create all teams
    		// Arcadia
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Arcadia', 2012, 4, 0, 0, 0, 0)");
			// Athena
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Athena', 2012, 3, 0, 0, 0, 0)");
			// Batavia
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Batavia', 2012, 4, 0, 0, 0, 0)");
			// Brighton
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Brighton', 2012, 2, 0, 0, 0, 0)");
			// Brockport
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Brockport', 2012, 3, 0, 0, 0, 0)");
			// Canandaigua
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Canandaigua', 2012, 2, 0, 0, 0, 0)");
			// Churchville Chili
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Churchville Chili', 2012, 3, 0, 0, 0, 0)");
			// East Irondequoit
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('East Irondequoit', 2012, 4, 0, 0, 0, 0)");
			// East Rochester
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('East Rochester', 2012, 4, 0, 0, 0, 0)");
			// Fairport
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Fairport', 2012, 1, 0, 0, 0, 0)");
			// Gates Chili
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Gates Chili', 2012, 3, 0, 0, 0, 0)");
			// Hilton
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Hilton', 2012, 3, 0, 0, 0, 0)");
			// Honeoye Falls Lima
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Honeoye Falls Lima', 2012, 2, 0, 0, 0, 0)");
			// Irondequoit
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Irondequoit', 2012, 4, 0, 0, 0, 0)");
			// Mendon
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Mendon', 2012, 2, 0, 0, 0, 0)");
			// Odyssey
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Odyssey', 2012, 4, 0, 0, 0, 0)");
			// Olympia
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Olympia', 2012, 4, 0, 0, 0, 0)");
			// Penfield
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Penfield', 2012, 1, 0, 0, 0, 0)");
			// Rush Henrietta
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Rush Henrietta', 2012, 1, 0, 0, 0, 0)");
			// Schroeder
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Schroeder', 2012, 1, 0, 0, 0, 0)");
			// Spencerport
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Spencerport', 2012, 3, 0, 0, 0, 0)");
			// Sutherland
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Sutherland', 2012, 2, 0, 0, 0, 0)");
			// Thomas
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Thomas', 2012, 1, 0, 0, 0, 0)");
			// Victor
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('Victor', 2012, 1, 0, 0, 0, 0)");
			// W. Irondequoit
			db.execSQL("INSERT INTO " + TeamInfo.getTableName() + "(" + TeamInfo.TeamName + "," + TeamInfo.Year + "," + TeamInfo.Division + "," + TeamInfo.DivisionWins + "," + TeamInfo.DivisionLosses + "," + TeamInfo.OverallWins + "," + TeamInfo.OverallLosses + ") VALUES ('W. Irondequoit', 2012, 2, 0, 0, 0, 0)");
						
			// Create a location for the meet
			db.execSQL("INSERT INTO " + RaceLocation.getTableName() + "(" + RaceLocation.CourseName + ") VALUES ('Parma Park')");
			
			// Create a single meet for testing
			db.execSQL("INSERT INTO " + RaceMeet.getTableName() + "(" + RaceMeet.RaceLocation_ID + "," + RaceMeet.RaceMeetDate + ") VALUES (1, 1349209800000)");
			
			// Add teams to the meet
			db.execSQL("INSERT INTO " + RaceMeetTeams.getTableName() + "(" + RaceMeetTeams.RaceMeet_ID + "," + RaceMeetTeams.TeamInfo_ID + ") VALUES (1, 3)");
			db.execSQL("INSERT INTO " + RaceMeetTeams.getTableName() + "(" + RaceMeetTeams.RaceMeet_ID + "," + RaceMeetTeams.TeamInfo_ID + ") VALUES (1, 4)");
			db.execSQL("INSERT INTO " + RaceMeetTeams.getTableName() + "(" + RaceMeetTeams.RaceMeet_ID + "," + RaceMeetTeams.TeamInfo_ID + ") VALUES (1, 12)");
			db.execSQL("INSERT INTO " + RaceMeetTeams.getTableName() + "(" + RaceMeetTeams.RaceMeet_ID + "," + RaceMeetTeams.TeamInfo_ID + ") VALUES (1, 15)");
			db.execSQL("INSERT INTO " + RaceMeetTeams.getTableName() + "(" + RaceMeetTeams.RaceMeet_ID + "," + RaceMeetTeams.TeamInfo_ID + ") VALUES (1, 23)");
			
			
			// Add races to the meet
			//db.execSQL("INSERT INTO " + Race.getTableName() + "(" + Race.Category + "," + Race.Distance + "," + Race.Gender + "," + Race.NumSplits + "," + Race.RaceMeet_ID + "," + Race.RaceStartTime + ") VALUES ('Varsity', 3.1, 'Boys', 3, 1, 1349209800000)");
			db.execSQL("INSERT INTO " + Race.getTableName() + "(" + Race.Category + "," + Race.Distance + "," + Race.Gender + "," + Race.NumSplits + "," + Race.RaceMeet_ID + "," + Race.RaceStartTime + ") VALUES ('Modified', 2, 'Both', 2, 1, 1349209800000)");
			db.execSQL("INSERT INTO " + Race.getTableName() + "(" + Race.Category + "," + Race.Distance + "," + Race.Gender + "," + Race.NumSplits + "," + Race.RaceMeet_ID + "," + Race.RaceStartTime + ") VALUES ('Varsity', 3.1, 'Girls', 3, 1, 1349209800000)");
			
//			// Add a couple of racers
//			db.execSQL("INSERT INTO " + Racer.getTableName() + "(" + Racer.FirstName + "," + Racer.LastName + "," + Racer.Gender + ") VALUES ('Mark', 'Blakley', 'Boys')");
//			db.execSQL("INSERT INTO " + Racer.getTableName() + "(" + Racer.FirstName + "," + Racer.LastName + "," + Racer.Gender + ") VALUES ('Greg', 'Gray', 'Boys')");
//			db.execSQL("INSERT INTO " + Racer.getTableName() + "(" + Racer.FirstName + "," + Racer.LastName + "," + Racer.Gender + ") VALUES ('Perry', 'Pellerino', 'Boys')");
//			
//			// Add their racer club info
//			db.execSQL("INSERT INTO " + RacerClubInfo.getTableName() + "(" + RacerClubInfo.Racer_ID + "," + RacerClubInfo.TeamInfo_ID + "," + RacerClubInfo.Category + "," + RacerClubInfo.Grade + "," + RacerClubInfo.SpeedLevel + ") VALUES (1, 1, 'Varsity', 12, 6)");
//			db.execSQL("INSERT INTO " + RacerClubInfo.getTableName() + "(" + RacerClubInfo.Racer_ID + "," + RacerClubInfo.TeamInfo_ID + "," + RacerClubInfo.Category + "," + RacerClubInfo.Grade + "," + RacerClubInfo.SpeedLevel + ") VALUES (2, 1, 'Varsity', 11, 8)");
//			db.execSQL("INSERT INTO " + RacerClubInfo.getTableName() + "(" + RacerClubInfo.Racer_ID + "," + RacerClubInfo.TeamInfo_ID + "," + RacerClubInfo.Category + "," + RacerClubInfo.Grade + "," + RacerClubInfo.SpeedLevel + ") VALUES (3, 1, 'Varsity', 10, 4)");
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	    int newVersion) 
	    {
	        Log.w(TAG, "Upgrading database from version " + oldVersion 
	                + " to "
	                + newVersion);
	    }
	}
}
