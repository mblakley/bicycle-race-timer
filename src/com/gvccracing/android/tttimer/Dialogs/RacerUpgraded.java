package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.RacerSeriesInfo;

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
	private long categoryID;
	private long initCategory;
	
	public RacerUpgraded(long racerClubInfo_ID, long racer_ID, long category, long initCategory){
		this.racerClubInfo_ID = racerClubInfo_ID;
		this.racer_ID = racer_ID;
		this.categoryID = category;
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
		RacerSeriesInfo.Instance().Update(getActivity(), racerClubInfo_ID, null, null, null, null, null, null, null, true, null);
		
		Cursor prevRecord = RacerSeriesInfo.Instance().Read(getActivity(), null, RacerSeriesInfo._ID + "=?", new String[]{Long.toString(racerClubInfo_ID)}, null);
		prevRecord.moveToFirst();
		RacerSeriesInfo.Instance().Create(getActivity(), racer_ID, prevRecord.getString(prevRecord.getColumnIndex(RacerSeriesInfo.SeriesBibNumber)), prevRecord.getLong(prevRecord.getColumnIndex(RacerSeriesInfo.RaceSeries_ID)), 
							 categoryID, prevRecord.getLong(prevRecord.getColumnIndex(RacerSeriesInfo.TTPoints)), 
							 prevRecord.getLong(prevRecord.getColumnIndex(RacerSeriesInfo.RRPoints)), prevRecord.getLong(prevRecord.getColumnIndex(RacerSeriesInfo.PrimePoints)), 
							 prevRecord.getLong(prevRecord.getColumnIndex(RacerSeriesInfo.OnlineRecordID)));
		prevRecord.close();
		prevRecord = null;
	}
	
	public void UpdateRacerCategory(){
		RacerSeriesInfo.Instance().Update(getActivity(), racerClubInfo_ID, null, null, null, categoryID, null, null, null, false, null);
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
