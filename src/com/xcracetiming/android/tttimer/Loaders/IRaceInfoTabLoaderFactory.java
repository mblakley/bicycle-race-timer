package com.xcracetiming.android.tttimer.Loaders;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;

public interface IRaceInfoTabLoaderFactory {

	CursorLoader GetRaceInfo(FragmentActivity activity);

	CursorLoader GetCourseRecord(FragmentActivity activity, Bundle args);

}
