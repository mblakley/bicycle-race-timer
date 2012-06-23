package com.gvccracing.android.tttimer.CursorAdapters;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.UnassignedTimesCP.UnassignedTimes;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class UnassignedTimeCursorAdapter extends BaseCursorAdapter {

	protected long raceStartTime = 0;
	protected long numRaceLaps;
    public UnassignedTimeCursorAdapter (Context context, Cursor c, long startTime, long raceLaps) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        raceStartTime = startTime;
        numRaceLaps = raceLaps;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = null;
    	try{	
    		v = super.getView(position, convertView, parent);
	        
	        Cursor c = this.getCursor();
	        
	        int finishTimeCol = c.getColumnIndex(UnassignedTimes.FinishTime);
    		
	        Long finishTime = c.getLong(finishTimeCol);

	        CheckedTextView lblFinishTime = (CheckedTextView) v.findViewById(R.id.text1);
	        
	        ListView parentList = (ListView)parent;
	        int checkedItemPos = parentList.getCheckedItemPosition();
	        int cursorPos = c.getPosition();
	        if(checkedItemPos == cursorPos){
	        	lblFinishTime.setChecked(true);
	        }else{
	        	lblFinishTime.setChecked(false);
	        }
	
	        /**
	         * Next set the name of the entry.
	         */	        
	        if (lblFinishTime != null) {
	        	
//	        	Time startTimeOffset = new Time(finishTime - raceStartTime);
//	        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
//	        	if(finishTime - raceStartTime >= 36000000) {
//	        		formatter = new SimpleDateFormat("HH:mm:ss.S");
//	        	}
//	        	else if(finishTime - raceStartTime >= 3600000) {
//	        		formatter = new SimpleDateFormat("H:mm:ss.S");	
//	        	}
//	        	lblFinishTime.setText(formatter.format(startTimeOffset).toString());
	        	lblFinishTime.setText(TimeFormatter.Format(finishTime - raceStartTime, true, true, true, true, true, false, false, false));
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
	        v = inflater.inflate(R.layout.row_unassigned_time, parent, false);
    	}catch(Exception ex){
    		Log.e(LOG_TAG(), ex.toString());
    	}

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

    	try{
    		int finishTimeCol = c.getColumnIndex(UnassignedTimes.FinishTime);
    		
	        Long finishTime = c.getLong(finishTimeCol);
	
	        /**
	         * Next set the name of the entry.
	         */	        
	        CheckedTextView lblFinishTime = (CheckedTextView) v.findViewById(R.id.text1);
	        if (lblFinishTime != null) {
//	        	Time startTimeOffset = new Time(finishTime - raceStartTime);
//	        	SimpleDateFormat formatter = new SimpleDateFormat("m:ss.SSS");
//	        	if(finishTime - raceStartTime >= 36000000) {
//	        		formatter = new SimpleDateFormat("HH:mm:ss.S");
//	        	}
//	        	else if(finishTime - raceStartTime >= 3600000) {
//	        		formatter = new SimpleDateFormat("H:mm:ss.S");	
//	        	}
//	        	lblFinishTime.setText(formatter.format(startTimeOffset).toString());
	        	lblFinishTime.setText(TimeFormatter.Format(finishTime - raceStartTime, true, true, true, true, true, false, false, false));
	        }
	        
//	        String[] projection = new String[]{RaceResults.getTableName() + "." + RaceResults._ID + " as _id", RaceResults.StartOrder, Racer.FirstName, Racer.LastName};
//			String selection = RaceResults.Race_ID + "=" + AppSettings.getParameterSql(AppSettings.AppSetting_RaceID_Name) + " AND " + RaceResults.StartTime + " IS NOT NULL" + " AND " + RaceResults.EndTime + " IS NULL";
//			String[] selectionArgs = null;
//			String sortOrder = RaceResults.StartOrder;
//			Cursor unfinished = context.getContentResolver().query(CheckInViewExclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
//			getParentActivity().startManagingCursor(unfinished);  // OK, this is ugly and deprecated, but I'm being tricky here!
//			
//			Spinner spinAssignNumber = (Spinner) v.findViewById(R.id.spinnerAssignNumber);
//	        
//	        UnfinishedRacersCursorAdapter finishersCA = new UnfinishedRacersCursorAdapter(context, unfinished);
//            spinAssignNumber.setAdapter(finishersCA);
//            
//	        Button btnAssign = (Button) v.findViewById(R.id.btnAssign);
//	        if(btnAssign != null){
//	        	btnAssign.setTag(unassignedTime_ID);
//	        }
		}catch(Exception ex){
			Log.e(LOG_TAG(), ex.toString());
		}
    }
    
	protected String LOG_TAG() {
		return "UnassignedTimeCursorAdapter";
	}
}

