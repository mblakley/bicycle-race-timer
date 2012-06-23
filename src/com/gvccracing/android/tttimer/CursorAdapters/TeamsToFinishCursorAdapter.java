package com.gvccracing.android.tttimer.CursorAdapters;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.TeamCheckInViewCP.TeamCheckInViewExclusive;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeamsToFinishCursorAdapter extends BaseCursorAdapter {
	
    public TeamsToFinishCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_team_to_finish, parent, false);

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("TeamsToFinishCursorAdapter", "bindView start");
    		
    		fillData(c, v);
    		
            Log.i("TeamsToFinishCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("TeamsToFinishCursorAdapter", ex.toString());
		}
    }
    
    private void fillData(Cursor c, View v) {
    	int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
        int startOrderCol = c.getColumnIndex(RaceResults.StartOrder);
        
        String teamName = c.getString(teamNameCol);
        int startOrder = c.getInt(startOrderCol);        

        /**
         * Next set the name of the entry.
         */             
        TextView lblName = (TextView) v.findViewById(R.id.lblTeamName);
        if (lblName != null) {
        	lblName.setText(teamName);
        }
        
        TextView lblStartOrder = (TextView) v.findViewById(R.id.lblStartOrder);
        if (lblStartOrder != null) {
        	lblStartOrder.setText(Integer.toString(startOrder));
        } 
        
        TextView lblRacerNames = (TextView) v.findViewById(R.id.lblRacerNames);
        if (lblRacerNames != null) {
            String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", "group_concat(" + Racer.FirstName + "||' '||" + Racer.LastName + ", ',\n') as RacerNames"};
    		String selection = RaceResults.Race_ID	+ "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.getTableName() + "." + RaceResults._ID + "=?";
    		String[] selectionArgs = new String[]{Long.toString(c.getLong(0))};
    		String sortOrder = null;
    		
            Cursor teamRacerNames = getParentActivity().getContentResolver().query(Uri.withAppendedPath(TeamCheckInViewExclusive.CONTENT_URI, "group by " + RaceResults.getTableName() + "." + RaceResults._ID), projection, selection, selectionArgs, sortOrder);
            teamRacerNames.moveToFirst();
            
        	int racerNamesCol = teamRacerNames.getColumnIndex("RacerNames");
            String racerNames = teamRacerNames.getString(racerNamesCol);
            
            teamRacerNames.close();
            teamRacerNames = null;
            
        	lblRacerNames.setText(racerNames);
        } 	
    }
}
