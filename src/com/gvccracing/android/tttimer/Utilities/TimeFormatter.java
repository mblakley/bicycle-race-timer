package com.gvccracing.android.tttimer.Utilities;

public class TimeFormatter {
	public static String Format(long timeInMillseconds, boolean showTensHours, boolean showHours, boolean showMinutes, boolean showSeconds, 
								boolean showTenths, boolean showHundredths, boolean showThousandths, boolean showIfZero){
		String formattedTime = "";
		long secs = (long)(timeInMillseconds/1000);
 		long mins = (long)(secs/60);
 		long hrs = (long)(mins/60);

 		if(showTensHours){
	     	/* Convert the hours to String and format the String */
	
	     	String hours=String.valueOf(hrs);
	     	if(hrs == 0){
	     		hours = "0" + hours;
	     	}
	     	if(hrs <10 && hrs > 0){
	     		hours = "0"+hours;
	     	}
	     	if(hrs > 0 || showIfZero){
	     		formattedTime += hours;
	     	}
 		} else if(showHours){
 			/* Convert the hours to String and format the String */
 			
	     	String hours=String.valueOf(hrs);
	     	if(hrs == 0){
	     		hours = "0";
	     	}
	     	if(hrs > 0 || showIfZero){
	     		formattedTime += hours;
	     	}
 		}

 		if(showTensHours || showHours || showMinutes || showSeconds){
 			if(showIfZero || hrs > 0){
 				formattedTime += ":";
 			}
	 		/* Convert the minutes to String and format the String */
	
	     	mins = mins % 60;
	 		String minutes=String.valueOf(mins);
	     	if(mins == 0){
	     		minutes = "00";
	     	}
	     	if(mins <10 && mins > 0){
	     		minutes = "0"+minutes;
	     	}
	     	if(hrs > 0 || mins > 0 || showSeconds || showIfZero){
	     		formattedTime += minutes;
	     	}
 		}
     	
 		if(showTensHours || showHours || showMinutes || showSeconds){
 			// Always add minutes if we're showing seconds, so need to add the ":"
			formattedTime += ":";
 			
	 		/* Convert the seconds to String
	 		 * and format to ensure it has
	 		 * a leading zero when required
	 		 */
	 		secs = secs % 60;
	 		String seconds=String.valueOf(secs);
	     	if(secs == 0){
	     		seconds = "00";
	     	}
	     	if(secs <10 && secs > 0){
	     		seconds = "0"+seconds;
	     	}
     		formattedTime += seconds;
 		}

		if(showThousandths){
			if(showIfZero || secs > 0 || showSeconds){
				formattedTime += ".";
			}
	     	String milliseconds = String.valueOf((long)timeInMillseconds);
	     	if(milliseconds.length()==2){
	     		milliseconds = "0"+milliseconds;
	     	}
	       	if(milliseconds.length()<=1){
	     		milliseconds = "00" + milliseconds;
	     	}
	 		milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length());

	     	formattedTime += milliseconds;
		} else if(showHundredths){
 			formattedTime += ".";
			String milliseconds = String.valueOf((long)timeInMillseconds);
	     	if(milliseconds.length()==2){
	     		milliseconds = "0"+milliseconds;
	     	}
	     	if(milliseconds.length()<=1){
	     		milliseconds = "00" + milliseconds;
	     	}
			milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-1);
			
	     	formattedTime += milliseconds;
		} else if(showTenths){
 			formattedTime += ".";
			String milliseconds = String.valueOf((long)timeInMillseconds);
			if(milliseconds.length()==2){
	     		milliseconds = "0"+milliseconds;
	     	}
	       	if(milliseconds.length()<=1){
	     		milliseconds = "00" + milliseconds;
	     	}
			milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);
			
	     	formattedTime += milliseconds;
		}
 		
 		return formattedTime;
	}
}
