package com.gvccracing.android.tttimer.Controls;

import com.gvccracing.android.tttimer.AsyncTasks.CheckInHandler;

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
        	long racerInfo_ID = (Long)v.getTag();	// Fill in from selected checkin button
        	CheckInRacer(racerInfo_ID);
        	
        	// Change text on Check-in button for this record to "Done" and disable the button
        	((Button)v).setText("Ready!");
        	((Button)v).setEnabled(false);
    	}catch(Exception ex){
    		Log.e(LOG_TAG, "Error in btnCheckIn.onClick", ex);
    	}
	}

	private void CheckInRacer(long racerInfo_ID) {
		CheckInHandler task = new CheckInHandler(context);
		task.execute(new Long[] { racerInfo_ID });		
    }
}
