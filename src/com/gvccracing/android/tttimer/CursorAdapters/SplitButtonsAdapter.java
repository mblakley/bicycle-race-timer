package com.gvccracing.android.tttimer.CursorAdapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.RaceResultsCP.RaceResults;

public class SplitButtonsAdapter extends BaseCursorAdapter {

    public SplitButtonsAdapter (Context context, Cursor c) {
    	super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);	
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
    	View v = null;
    	try{	
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        v = inflater.inflate(R.layout.row_checkin, parent, false);
    	}catch(Exception ex){
    		Log.e("SplitButtonsAdapter", "Error in newView()", ex);
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		Log.i("SplitButtonsAdapter", "bindView start");
	    	int firstNameCol = c.getColumnIndex("FirstName");
	        int lastNameCol = c.getColumnIndex("LastName");
	
	        String firstName = c.getString(firstNameCol);
	        String lastName = c.getString(lastNameCol);
	        Long racerInfo_ID = c.getLong(0);
	        v.setTag(racerInfo_ID);

	        Cursor raceResult = RaceResults.Read(context, new String[]{RaceResults._ID}, RaceResults.RacerClubInfo_ID + "=? AND " + RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name), new String[]{Long.toString(racerInfo_ID)}, null);
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
	        	btnCheckIn.setTag(racerInfo_ID);
	        	
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
	        Log.i("SplitButtonsAdapter", "bindView complete");
		}catch(Exception ex){
			Log.e("SplitButtonsAdapter", ex.toString());
		}
    }
}
