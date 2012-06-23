package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamLapResultsView;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class TeamUnassignedTimeCursorAdapter extends UnassignedTimeCursorAdapter {

    public TeamUnassignedTimeCursorAdapter (Context context, Cursor c, long startTime, long numRaceLaps) {
        super(context, c, startTime, numRaceLaps);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_team_unassigned_time, parent, false);
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, final Context context, Cursor c) {

    	try{
    		int finishTimeCol = c.getColumnIndex(UnassignedTimes.FinishTime);
    		
	        Long finishTime = c.getLong(finishTimeCol);
	        int unassignedTime_ID = c.getInt(0);
	
	        /**
	         * Next set the name of the entry.
	         */	        
	        TextView lblFinishTime = (TextView) v.findViewById(R.id.lblFinishTime);
	        if (lblFinishTime != null) {
	        	Time startTimeOffset = new Time(finishTime - raceStartTime);
	        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
	        	if(finishTime - raceStartTime >= 36000000) {
	        		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
	        	}
	        	else if(finishTime - raceStartTime >= 3600000) {
	        		formatter = new SimpleDateFormat("H:mm:ss.SSS");	
	        	}
	        	lblFinishTime.setText(formatter.format(startTimeOffset).toString());
	        }
	        
	        String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.StartOrder, TeamInfo.TeamName, "COUNT(" + RaceLaps.getTableName() + "." + RaceLaps._ID + ") as LapsCompleted"};
			String selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults.StartTime + " IS NOT NULL" + " AND " + RaceResults.EndTime + " IS NULL";
			String[] selectionArgs = null;
			String sortOrder = "LapsCompleted, " + RaceResults.StartOrder;
			Cursor unfinished = context.getContentResolver().query(Uri.withAppendedPath(TeamLapResultsView.CONTENT_URI, "group by " + RaceResults.getTableName() + "." + RaceResults._ID + "," + RaceResults.StartOrder + "," + TeamInfo.TeamName), projection, selection, selectionArgs, sortOrder);
			getParentActivity().startManagingCursor(unfinished);  // OK, this is ugly and deprecated, but I'm being tricky here!
			
			Spinner spinAssignNumber = (Spinner) v.findViewById(R.id.spinnerAssignNumber);
	        
	        TeamUnfinishedRacersCursorAdapter finishers = new TeamUnfinishedRacersCursorAdapter(context, unfinished, numRaceLaps);
            spinAssignNumber.setAdapter(finishers);
            
	        Button btnAssign = (Button) v.findViewById(R.id.btnAssign);
	        if(btnAssign != null){
	        	btnAssign.setTag(unassignedTime_ID);
	        }
		}catch(Exception ex){
			Log.e(LOG_TAG(), ex.toString());
		}
    }
    
	protected String LOG_TAG() {
		return "TeamUnassignedTimeCursorAdapter";
	}
}

