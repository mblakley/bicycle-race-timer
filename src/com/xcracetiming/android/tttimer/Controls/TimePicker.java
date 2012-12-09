package com.xcracetiming.android.tttimer.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xcracetiming.android.tttimer.R;

public class TimePicker extends LinearLayout {
	public static final String LOG_TAG = "TimePicker";
	
	private NumberPicker npHours;
	private NumberPicker npMinutes;
	private NumberPicker npSeconds;
	private NumberPicker npMilliseconds;
	
	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.control_time_picker, this, true);
        
        npHours = (NumberPicker) v.findViewById(R.id.timeHours);
        npMinutes = (NumberPicker) v.findViewById(R.id.timeMinutes);
        npSeconds = (NumberPicker) v.findViewById(R.id.timeSeconds);
        npMilliseconds = (NumberPicker) v.findViewById(R.id.timeMilliseconds);
        
        npHours.setRange(0, 23);
        npMinutes.setRange(0, 59);
        npSeconds.setRange(0, 59);
        npMilliseconds.setRange(0, 9);
        
        npHours.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        npMinutes.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        npSeconds.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        //npMilliseconds.setFormatter(NumberPicker.THREE_DIGIT_FORMATTER);
	}
	
	public void SetTime(long milliseconds){
		long hours = milliseconds/3600000;
		long minutes = (milliseconds - (hours * 3600000))/60000;
		long seconds = (milliseconds - ((hours * 3600000) + (minutes * 60000)))/1000;
		long msTenths = (milliseconds - ((hours * 3600000) + (minutes * 60000) + (seconds * 1000)))/100;
		
		SetTime(hours, minutes, seconds, msTenths);
	}
	
	public void SetTime(long hours, long minutes, long seconds, long msTenths){
		npHours.setCurrent((int)hours);
		npMinutes.setCurrent((int)minutes);
		npSeconds.setCurrent((int)seconds);
		npMilliseconds.setCurrent((int)msTenths);
	}
	
	public long GetTime(){
		return (npHours.getCurrent() * 3600000) + (npMinutes.getCurrent() * 60000) + (npSeconds.getCurrent() * 1000) + npMilliseconds.getCurrent();
	}
}

