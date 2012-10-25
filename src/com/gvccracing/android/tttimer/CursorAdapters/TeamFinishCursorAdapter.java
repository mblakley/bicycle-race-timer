package com.gvccracing.android.tttimer.CursorAdapters;

import java.util.Hashtable;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TeamFinishCursorAdapter extends BaseCursorAdapter {	
	
	private class TeamFinishInfo{
		public long teamID;
		public String teamName;
		public long numFinished;
		public long numPoints;
		
		public TeamFinishInfo(long teamID, String teamName, long numFinished, long numPoints){
			this.teamID = teamID;
			this.teamName = teamName;
			this.numFinished = numFinished;
			this.numPoints = numPoints;
		}
		
	}

	Hashtable<Long, TeamFinishInfo> viewLookup = new Hashtable<Long, TeamFinishInfo>();
	Hashtable<Long, Long> initialTeamFinishers = new Hashtable<Long, Long>();
	
    public TeamFinishCursorAdapter (Context context, Cursor c, Hashtable<Long, Long> initialTeamFinishers) {   	    	
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        
        this.initialTeamFinishers = initialTeamFinishers;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_team_finish_info, parent, false);	        
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), "newView", ex);
    	}

        return v;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
    	
    	return super.getView(position, convertView, arg2);
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{	        	
    		int teamIDCol = c.getColumnIndex(TeamInfo._ID);
    		Long teamID = c.getLong(teamIDCol);    
	        
	        if(!viewLookup.containsKey(teamID)){	    		
		        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);    		
		        String teamName = c.getString(teamNameCol);	  
		        
		        long numFinished = 0;
		        if(initialTeamFinishers.containsKey(teamID)){
		        	// look up the number of racers who have already finished
		        	numFinished = initialTeamFinishers.get(teamID);
		        }else{
		        	initialTeamFinishers.put(teamID, 0l);
		        }
		        
	        	viewLookup.put(teamID, new TeamFinishInfo(teamID, teamName, numFinished, 0));
	        	
	        	
	        	// look up the number of points for this team
	        }
	        
	        if(viewLookup.containsKey(teamID)){
	        	TeamFinishInfo finish = viewLookup.get(teamID);
	        	
	        	String teamName = finish.teamName;
	        	Long numFinished = finish.numFinished;
	        	Long numPoints = finish.numPoints;
	        	
	        	TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeamName);				        
		        // Set the team name
		        if (lblTeamName != null) {	        	
		        	lblTeamName.setText(teamName);
		        }
	        	
		        TextView lblNumFinished = (TextView) v.findViewById(R.id.lblNumFinished);				        
		        // Set the number of racers who have finished for this team
		        if (lblNumFinished != null) {	        	
		        	lblNumFinished.setText(Long.toString(numFinished));
		        }
		        
		        TextView lblPoints = (TextView) v.findViewById(R.id.lblPoints);				        
		        // Set the number of current points for this team
		        if (lblPoints != null) {	        	
		        	lblPoints.setText(Long.toString(numPoints));
		        }
	        }
		}catch(Exception ex){
			Log.e(LOG_TAG(), "BindView", ex);
		}
    }
    
	protected String LOG_TAG() {
		return "TeamFinishCursorAdapter";
	}

	public void addToNumFinished(long teamID) {
		TeamFinishInfo teamInfo;
		if(viewLookup.containsKey(teamID)){
			teamInfo = viewLookup.get(teamID);
			teamInfo.numFinished++;		
		} else{
			teamInfo = new TeamFinishInfo(teamID, "", 1, 0);		
		}
		viewLookup.put(teamID, teamInfo);
	}
}

