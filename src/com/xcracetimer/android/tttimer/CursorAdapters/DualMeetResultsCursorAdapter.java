package com.xcracetimer.android.tttimer.CursorAdapters;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResults;
import com.xcracetimer.android.tttimer.DataAccess.DualMeetResultsCP.DualMeetResultsView;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DualMeetResultsCursorAdapter extends BaseCursorAdapter {

    public DualMeetResultsCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_dual_meet_results, parent, false);

    	}catch(Exception ex){
    		Log.e("DualMeetResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		setupView(v, c);
		}catch(Exception ex){
			Log.e("DualMeetResultsCursorAdapter", ex.toString());
		}

    }

	public View setupView(View v, Cursor c) {
        int team1NameCol = c.getColumnIndex(DualMeetResultsView.TeamInfo1);
        String team1Name = c.getString(team1NameCol);
//        int team2NameCol = c.getColumnIndex(DualMeetResultsView.TeamInfo2);
//        String team2Name = c.getString(team2NameCol);
        int team1PointsCol = c.getColumnIndex(DualMeetResults.Team1_Points);
        String team1Points = Long.toString(c.getLong(team1PointsCol));
//        int team2PointsCol = c.getColumnIndex(DualMeetResults.Team2_Points);
//        String team2Points = Long.toString(c.getLong(team2PointsCol));
        
        
        TextView lblTeam1Name = (TextView) v.findViewById(R.id.lblTeam1Name);
        TextView lblTeam1Points = (TextView) v.findViewById(R.id.lblTeam1Points);
//        TextView lblTeam2Name = (TextView) v.findViewById(R.id.lblTeam2Name);
//        TextView lblTeam2Points = (TextView) v.findViewById(R.id.lblTeam2Points);
		
        lblTeam1Name.setText(team1Name);
        //lblTeam2Name.setText(team2Name);
        lblTeam1Points.setText(team1Points);
        //lblTeam2Points.setText(team2Points);
        
		return v;
	}
}

