package com.xcracetimer.android.tttimer.Controls;

import com.xcracetimer.android.tttimer.AsyncTasks.CheckInHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ButtonCheckIn extends Button implements View.OnClickListener{

	public String LOG_TAG = ButtonCheckIn.class.getSimpleName();
	
	private Context context = null;
	
	public ButtonCheckIn(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		
		setOnClickListener(this);
	}

	public void onClick(View v) {
		try{
			Log.v(LOG_TAG, "btnCheckInClick");
			// Create a RaceResults record with the selected RacerInfo_ID,
        	long raceResult_ID = (Long)v.getTag();	// Fill in from selected checkin button
        	CheckInRacer(raceResult_ID);
        	
        	// Change text on Check-in button for this record to "Done" and disable the button
        	((Button)v).setText("Check In");        	
    	}catch(Exception ex){
    		Log.e(LOG_TAG, "Error in btnCheckIn.onClick", ex);
    	}
	}

	private void CheckInRacer(long raceResult_ID) {
		CheckInHandler task = new CheckInHandler(context);
		task.execute(new Long[] { raceResult_ID });		
    }
}
