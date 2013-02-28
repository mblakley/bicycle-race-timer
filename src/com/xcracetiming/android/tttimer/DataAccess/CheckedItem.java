package com.xcracetiming.android.tttimer.DataAccess;

public class CheckedItem {
	public String Text = "";
	public boolean IsChecked = false;
	public long _ID = -1;
	
	public CheckedItem(){}
	
	public CheckedItem(String text){
		Text = text;
	}
	
	public CheckedItem(String text, long id){
		Text = text;
		_ID = id;
	}
}
