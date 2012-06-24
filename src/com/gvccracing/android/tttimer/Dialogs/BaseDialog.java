package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager.LayoutParams;

/**
 * @author Mark
 *
 */
public abstract class BaseDialog extends DialogFragment {
	protected static final int RACE_LOCATIONS_LOADER = 0x01;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(R.style.Padded, R.style.CustomDialogTheme);
	}

	@Override
	public void onResume() {
		super.onResume();
		getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}
}
