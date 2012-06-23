package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RaceLapsCP.RaceLaps;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TeamResultsCursorAdapter extends BaseCursorAdapter {


    private LayoutInflater inflater;
    public TeamResultsCursorAdapter (Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        v = inflater.inflate(R.layout.row_team_result, parent, false);
    	}catch(Exception ex){
    		Log.e("TeamResultsCursorAdapter", ex.toString());
    	}

        return v;
    }

    @Override
	public void bindView(View v, Context context, Cursor c) {
    	try{
    		Log.i("TeamResultsCursorAdapter", "Position=" + Integer.toString(c.getPosition()));
    		
			int raceResultIDCol = c.getColumnIndex(RaceResults._ID);
	    	int placingCol = c.getColumnIndex(RaceResults.OverallPlacing);
	        int elapsedTimeCol = c.getColumnIndex(RaceResults.ElapsedTime);
	        int teamNameCol = c.getColumnIndex(TeamInfo.TeamName);
	        int racerNamesCol = c.getColumnIndex("RacerNames");
	        
	        Long raceResult_ID = c.getLong(raceResultIDCol);
	        Integer placing = c.getInt(placingCol);
	        Long elapsedTime = c.getLong(elapsedTimeCol);
	        String teamName = c.getString(teamNameCol);
	        String racerNames = c.getString(racerNamesCol);	    
	
	        /**
	         * Next set the name of the entry.
	         */	
	        TextView lblPlacing = (TextView) v.findViewById(R.id.lblPlacing);
	        if (lblPlacing != null) {
	        	lblPlacing.setText(placing.toString());
	        }
	        
	        TextView lblElapsedTime = (TextView) v.findViewById(R.id.lblElapsedTime);
	        if (lblElapsedTime != null) {
	        	if(elapsedTime == Long.MAX_VALUE){
	        		lblElapsedTime.setText("Total - DNF");
	        	} else if(elapsedTime == Long.MAX_VALUE - 1){
	        		lblElapsedTime.setText("Total -DNS");
	        	} else{
//		        	Time elapsed = new Time(elapsedTime);
//		        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
//		        	if(elapsedTime >= 36000000) {
//		        		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
//		        	}
//		        	else if(elapsedTime >= 3600000) {
//		        		formatter = new SimpleDateFormat("H:mm:ss.SSS");	
//		        	}
//		        	lblElapsedTime.setText("Total - " + formatter.format(elapsed).toString());
		        	lblElapsedTime.setText("Total - " + TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
	        	}
	        }
	        
	        TextView lblName = (TextView) v.findViewById(R.id.lblTeamName);
	        if (lblName != null) {
	        	lblName.setText(teamName);
	        }
	        
	        // Next the list of racerNames
	        TextView lblRacerNames = (TextView) v.findViewById(R.id.lblRacerNames);
	        if (lblRacerNames != null) {
	        	lblRacerNames.setText("(" + racerNames + ")");
	        }
	        // Set up laps
	        String[] projection = new String[]{RaceLaps.getTableName() + "." + RaceLaps._ID + " as _id", RaceLaps.LapNumber, RaceLaps.ElapsedTime};
			String selection = RaceLaps.RaceResult_ID + "=?";
			String[] selectionArgs = new String[]{Long.toString(raceResult_ID)};
			String sortOrder = RaceLaps.LapNumber;
			Cursor laps = context.getContentResolver().query(RaceLaps.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

			LinearLayout lapContainer = (LinearLayout) v.findViewById(R.id.llLapContainer);
        	lapContainer.removeAllViews();
			if(laps != null && laps.getCount() > 0){
				laps.moveToFirst();
				do{
					// Get the lap info
					Long lapNumber = laps.getLong(laps.getColumnIndex(RaceLaps.LapNumber));
					Long lapElapsedTime = laps.getLong(laps.getColumnIndex(RaceLaps.ElapsedTime));
					
					// Inflate a lap row
			        View lapView = inflater.inflate(R.layout.row_lap_time, lapContainer, false);
			        String lapViewIDString = Long.toString(lapNumber) + "99" + Long.toString(raceResult_ID);
			        int lapViewID = Integer.parseInt(lapViewIDString);
			        lapView.setId(lapViewID);
			        // Set the lap time
					TextView lblLapTime = ((TextView)lapView.findViewById(R.id.lblLapTime));
					if(lapElapsedTime == Long.MAX_VALUE){
						lblLapTime.setText("DNF");
		        	} else if(lapElapsedTime == Long.MAX_VALUE - 1){
		        		lblLapTime.setText("DNS");
		        	} else{
//			        	Time elapsed = new Time(lapElapsedTime);
//			        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
//			        	if(elapsedTime >= 36000000) {
//			        		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
//			        	}
//			        	else if(elapsedTime >= 3600000) {
//			        		formatter = new SimpleDateFormat("H:mm:ss.SSS");	
//			        	}
//			        	lblLapTime.setText(formatter.format(elapsed).toString());
			        	lblLapTime.setText(TimeFormatter.Format(lapElapsedTime, true, true, true, true, true, false, false, false));
		        	}
					// Set lap number
			        ((TextView)lapView.findViewById(R.id.lblLapNumber)).setText("Lap " + Long.toString(lapNumber) + " -");
			        if(lapContainer.findViewById(lapViewID) == null){
			        	lapContainer.addView(lapView);
			        }
				} while(laps.moveToNext());
				
				laps.close();
				laps = null;
			}
		}catch(Exception ex){
			Log.e("TeamResultsCursorAdapter", ex.toString());
		}
	}
}

