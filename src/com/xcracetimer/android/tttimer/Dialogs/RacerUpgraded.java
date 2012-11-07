package com.xcracetimer.android.tttimer.Dialogs;

import com.xcracetimer.android.tttimer.R;
import com.xcracetimer.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RacerUpgraded extends BaseDialog implements OnClickListener {
	
	public static final String LOG_TAG = "RacerUpgraded";
	
	private Button btnRacerUpgraded;
	private Button btnIncorrectCategory;
	private TextView txtRacerUpgrade;
	
	private Long racerClubInfo_ID;
	private Long racer_ID;
	private String category;
	private String initCategory;
	
	public RacerUpgraded(long racerClubInfo_ID, long racer_ID, String category, String initCategory){
		this.racerClubInfo_ID = racerClubInfo_ID;
		this.racer_ID = racer_ID;
		this.category = category;
		this.initCategory = initCategory;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_racer_upgraded, container, false);
		
		txtRacerUpgrade = (TextView) v.findViewById(R.id.txtMessage);
		txtRacerUpgrade.setText("Did this racer just upgrade from category " + initCategory + ", or was their inital category incorrect?");

		btnRacerUpgraded = (Button) v.findViewById(R.id.btnRacerUpgraded);
		btnRacerUpgraded.setOnClickListener(this);
		
		btnIncorrectCategory = (Button) v.findViewById(R.id.btnIncorrectCategory);
		btnIncorrectCategory.setOnClickListener(this);
				
		return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.RacerUpgrade;
	}
	
	public void UpgradeRacerCategory(){
		RacerClubInfo.Update(getActivity(), racerClubInfo_ID, null, null, null, null, null);
		
		Cursor prevRecord = RacerClubInfo.Read(getActivity(), null, RacerClubInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)}, null);
		prevRecord.moveToFirst();
		long grade = 12l;
		float speedLevel = 1.0f;
		RacerClubInfo.Create(getActivity(), racer_ID, prevRecord.getLong(prevRecord.getColumnIndex(RacerClubInfo.TeamInfo_ID)), prevRecord.getString(prevRecord.getColumnIndex(RacerClubInfo.Category)), 
							 grade, speedLevel);
		prevRecord.close();
		prevRecord = null;
	}
	
	public void UpdateRacerCategory(){
		RacerClubInfo.Update(getActivity(), racerClubInfo_ID, null, null, null, null, null);
	}
	
	public void onClick(View v) {
		if(v == btnRacerUpgraded){
			UpgradeRacerCategory();
			dismiss();
		} else if (v == btnIncorrectCategory){
			UpdateRacerCategory();
			dismiss();
		} else {
			super.onClick(v);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}
