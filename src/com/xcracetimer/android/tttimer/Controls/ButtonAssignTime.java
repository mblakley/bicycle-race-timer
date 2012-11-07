package com.xcracetimer.android.tttimer.Controls;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.AsyncTasks.AssignTimeTask;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;

public class ButtonAssignTime extends Button implements View.OnClickListener{

	public String LOG_TAG = ButtonAssignTime.class.getSimpleName();
	
	public ButtonAssignTime(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnClickListener(this);
	}

	public void onClick(View view) {
		try{
			Log.v(LOG_TAG, "onClick");

			int unassignedTime_ID = (Integer)view.getTag();
			Spinner startOrderText = (Spinner) ((View)(view.getParent())).findViewById(R.id.spinnerAssignNumber);
        	long raceResult_ID = startOrderText.getSelectedItemId();
        	
        	AssignTimeToRacer(unassignedTime_ID, raceResult_ID);        	
    	}catch(Exception ex){Log.e(LOG_TAG, "onClick failed:", ex);}
	}

	private void AssignTimeToRacer(long unassignedTime_ID, long raceResult_ID) throws Exception {		
		AssignTimeTask task = new AssignTimeTask(getContext());
		// TODO: Fill in all parameters correctly!
		throw new Exception("ButtonAssignTime.AssignTimeToRacer did nothing!");
		//task.execute(new Long[] { unassignedTime_ID, raceResult_ID });		
    }
}
