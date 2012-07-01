package com.gvccracing.android.tttimer;

import com.gvccracing.android.tttimer.DataAccess.AppSettingsCP.AppSettings;

import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
  
public class BootReceiver extends BroadcastReceiver {  
  
    @Override  
    public void onReceive(Context context, Intent intent) {
    	String val = AppSettings.ReadValue(context, AppSettings.AppSettings_AutoStartApp_Name, "false");
    	if(Boolean.parseBoolean(val)){
	        Intent i = new Intent(context, TTTimerTabsActivity.class);
	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        context.startActivity(i);  
    	}
    }  
}