package com.xcracetiming.android.tttimer.CursorAdapters;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.DataAccess.AppSettings;
import com.xcracetiming.android.tttimer.DataAccess.Racer;
import com.xcracetiming.android.tttimer.DataAccess.SeriesRaceIndividualResults;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CheckInCursorAdapter extends BaseCursorAdapter {

    public CheckInCursorAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);	
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_checkin, parent, false);
    	}catch(Exception ex){
    		Log.e("CheckInCursorAdapter", "Error in newView()", ex);
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.v("CheckInCursorAdapter", "bindView start");
	    	int firstNameCol = c.getColumnIndex(Racer.FirstName);
	        int lastNameCol = c.getColumnIndex(Racer.LastName);
	
	        String firstName = c.getString(firstNameCol);
	        String lastName = c.getString(lastNameCol);
	        Long racerSeriesInfo_ID = c.getLong(0);
	        Long raceResult_ID;	// TODO: Pass this in as another field on the checkin cursor

	        // Get RaceResult record from RacerSeriesInfo_ID and Race_ID
	        Cursor raceResult = SeriesRaceIndividualResults.Instance().Read(context, new String[]{SeriesRaceIndividualResults.RaceResult_ID}, SeriesRaceIndividualResults.RacerSeriesInfo_ID + "=? AND " + SeriesRaceIndividualResults.Race_ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name), new String[]{Long.toString(racerSeriesInfo_ID)}, null);
	        /**
	         * Next set the name of the entry.
	         */     
	        TextView lblFirstName = (TextView) v.findViewById(R.id.lblFirstName);
	        if (lblFirstName != null) {
	        	lblFirstName.setText(firstName);
	        }
	        
	        TextView lblLastName = (TextView) v.findViewById(R.id.lblLastName);
	        if (lblLastName != null) {
	        	lblLastName.setText(lastName);
	        }

	        Button btnCheckIn = (Button) v.findViewById(R.id.btnCheckIn);
	        if(btnCheckIn != null){
	        	btnCheckIn.setTag(racerSeriesInfo_ID);
	        	
	        	// Check if the racer has previously checked in
	        	if(raceResult != null && raceResult.getCount() > 0){
	        		// They have a race result ID already, so set up the button differently
	        		btnCheckIn.setText("Ready!");
	        		btnCheckIn.setEnabled(false);
	        	}else{
	        		btnCheckIn.setText("Check In");
	        		btnCheckIn.setEnabled(true);
	        	}
	        }
	        
	        if(raceResult != null){
	        	raceResult.close();
	        	raceResult = null;
	        }
	        Log.v("CheckInCursorAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("CheckInCursorAdapter", ex.toString());
		}
    }
}
