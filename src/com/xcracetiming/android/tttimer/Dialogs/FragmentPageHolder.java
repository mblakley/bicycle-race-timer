package com.xcracetiming.android.tttimer.Dialogs;

import com.xcracetiming.android.tttimer.DataAccess.Race;

import android.os.Bundle;
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
        	Bundle b = new Bundle();
        	b.putLong(Race.RaceSeries_ID, -1);
            f = new AddRaceView();
            f.setArguments(b);
                    break;
        case 3:
            f = new AddTeamView();
                    break;
        }

        return f;
	}
}
