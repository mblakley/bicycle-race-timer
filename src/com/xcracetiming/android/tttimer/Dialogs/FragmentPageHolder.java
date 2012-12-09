package com.xcracetiming.android.tttimer.Dialogs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentPageHolder extends FragmentPagerAdapter {
	
	FragmentManager manager;
	public FragmentPageHolder(FragmentManager fm) {
	    super(fm);
	    manager = fm;
	}
	
	@Override
	public int getCount() {
	    return manager.getBackStackEntryCount();
	}
	
	@Override
	public Fragment getItem(int position) {
		Fragment f = new Fragment();

        switch (position) {
        case 0:
            f = new AddLocationView();
                    break;
        case 1:
            f = new AddRacerView(false);
                    break;
        case 2:
            f = new AddRaceView(-1);
                    break;
        case 3:
            f = new AddTeamView();
                    break;
        }

        return f;
	}
}
