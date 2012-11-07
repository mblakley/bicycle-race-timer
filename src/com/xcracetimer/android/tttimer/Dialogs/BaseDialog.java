package com.xcracetimer.android.tttimer.Dialogs;

import com.xcracetimer.android.tttimer.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.WindowManager.LayoutParams;

/**
 * @author Mark
 *
 */
public abstract class BaseDialog extends DialogFragment implements View.OnClickListener {
	protected static final int RACE_LOCATIONS_LOADER = 0x01;
	
	protected ImageButton btnBack;
	
	private boolean showCancelButton = true;
	
	protected abstract int GetTitleResourceID();
	
	protected abstract String LOG_TAG();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogTheme);
	}
	
	protected void ShowCancelButton(boolean show) {
		showCancelButton = show;
		if(btnBack != null){
			if(showCancelButton){
				btnBack.setVisibility(View.VISIBLE);
			}else{
				btnBack.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if(btnBack == null){
			View title = LayoutInflater.from(getActivity()).inflate(R.layout.title_with_back, (ViewGroup)getView(), false);
			ViewGroup dialog = (ViewGroup)getView().findViewById(R.id.dialogContainer);
			dialog.addView(title, 0);
			
			getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			btnBack = (ImageButton)getView().findViewById(R.id.btnBaseDialogClose1);
			btnBack.setOnClickListener(this);
		}
		if(showCancelButton){
			btnBack.setVisibility(View.VISIBLE);
		}else{
			btnBack.setVisibility(View.GONE);
		}

		((TextView)(getView().findViewById(R.id.title))).setText(GetTitleResourceID());
	}

	public void onClick(View v) {
		if (v == btnBack){
			dismiss();
		}
	}
}
