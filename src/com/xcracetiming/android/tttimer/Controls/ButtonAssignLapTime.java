package com.xcracetiming.android.tttimer.Controls;

import com.xcracetiming.android.tttimer.R;
import com.xcracetiming.android.tttimer.AsyncTasks.AssignLapTimeTask;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;

public class ButtonAssignLapTime extends Button implements View.OnClickListener{

	public String LOG_TAG = ButtonAssignLapTime.class.getSimpleName();
	
	public ButtonAssignLapTime(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnClickListener(this);
	}

	public void onClick(View view) {
		try{
			Log.v(LOG_TAG, "onClick");

			int unassignedTime_ID = (Integer)view.getTag();
			Spinner startOrderText = (Spinner) ((View)(view.getParent())).findViewById(R.id.spinnerAssignNumber);
        	long raceResult_ID = startOrderText.getSelectedItemId();
        	
        	AssignTimeToLap(unassignedTime_ID, raceResult_ID);        	
    	}catch(Exception ex){Log.e(LOG_TAG, "onClick failed:", ex);}
	}

	private void AssignTimeToLap(long unassignedTime_ID, long raceResult_ID) {		
		AssignLapTimeTask task = new AssignLapTimeTask(getContext());
		task.execute(new Long[] { unassignedTime_ID, raceResult_ID });		
    }
}
