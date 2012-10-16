package com.gvccracing.android.tttimer.CursorAdapters;

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

    public TeamFinishCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = null;
    	try{	
    		v = super.getView(position, convertView, parent);
	        
	        Cursor c = this.getCursor();
	        
	        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);    		
	        String teamName = c.getString(teamNameCol);
	        
	        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeamName);		
	        
	        // Set the team name
	        if (lblTeamName != null) {	        	
	        	lblTeamName.setText(teamName);
	        }
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), ex.toString());
    	}

        return v;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_team_finish_info, parent, false);
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{	        	        
	        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);    		
	        String teamName = c.getString(teamNameCol);
	        
	        TextView lblTeamName = (TextView) v.findViewById(R.id.lblTeamName);		
	        
	        // Set the team name
	        if (lblTeamName != null) {	        	
	        	lblTeamName.setText(teamName);
	        }
		}catch(Exception ex){
			Log.e(LOG_TAG(), ex.toString());
		}
    }
    
	protected String LOG_TAG() {
		return "TeamFinishCursorAdapter";
	}
}

