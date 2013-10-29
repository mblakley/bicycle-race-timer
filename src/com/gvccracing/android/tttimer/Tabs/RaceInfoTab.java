package com.gvccracing.android.tttimer.Tabs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gvccracing.android.tttimer.DataAccess.RaceLocation;
import com.gvccracing.android.tttimer.DataAccess.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceInfoView;
import com.gvccracing.android.tttimer.DataAccess.Views.RacerInfoView;
import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.AppSettings;
import com.gvccracing.android.tttimer.DataAccess.Race;
import com.gvccracing.android.tttimer.DataAccess.Views.RaceInfoResultsView;
import com.gvccracing.android.tttimer.DataAccess.RaceResults;
import com.gvccracing.android.tttimer.Dialogs.AdminAuthView;
import com.gvccracing.android.tttimer.Dialogs.AdminMenuView;
import com.gvccracing.android.tttimer.Dialogs.MarshalLocations;
import com.gvccracing.android.tttimer.Dialogs.OtherRaceResults;
import com.gvccracing.android.tttimer.Dialogs.SeriesResultsView;
import com.gvccracing.android.tttimer.Utilities.TimeFormatter;
import com.gvccracing.android.tttimer.Utilities.Enums.RaceType;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RaceInfoTab extends BaseTab implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	public static final String RaceInfoTabSpecName =  "RaceInfoTab";
	
	private static final int COURSE_RECORD_LOADER = 0x213;

	private static final int APP_SETTINGS_LOADER_RACEINFO = 0x22;

	private static final int RACE_INFO_LOADER = 0x114;
	
	private TextView raceDate;
	private TextView raceCourseName;
	private TextView raceType;
	private TextView raceStartInterval;
	private TextView raceDistance;
	private TextView courseRecord;
	private TextView raceLaps;
	private LinearLayout llRaceLaps;
	private String distanceUnit;
	private String distance;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_race_info, container, false);
        
        ((Button) view.findViewById(R.id.btnMarshalLocations)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnSeriesResults)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnPreviousResults)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnAdminMenu)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btnDownloadRacers)).setOnClickListener(this);
        
        raceDate = ((TextView) view.findViewById(R.id.raceDate));
        raceCourseName = ((TextView) view.findViewById(R.id.raceCourseName));
        raceType = ((TextView) view.findViewById(R.id.raceType));
        raceStartInterval = ((TextView) view.findViewById(R.id.raceStartInterval));
        raceDistance = ((TextView) view.findViewById(R.id.raceDistance));
        courseRecord = ((TextView) view.findViewById(R.id.courseRecord));
        raceLaps = ((TextView) view.findViewById(R.id.raceLaps));
        llRaceLaps = ((LinearLayout) view.findViewById(R.id.llRaceLaps));
        
        return view;
    }	
	
	@Override
	public void onResume() {
		super.onResume(); 
		// Initialize the cursor loader for the race info
		getActivity().getSupportLoaderManager().initLoader(RACE_INFO_LOADER, null, this);

	    getActivity().getSupportLoaderManager().initLoader(APP_SETTINGS_LOADER_RACEINFO, null, this);
	    
	    getActivity().getSupportLoaderManager().initLoader(COURSE_RECORD_LOADER, null, this);
	}

	@Override
	public String TabSpecName() {
		return RaceInfoTabSpecName;
	}

	@Override
	protected String LOG_TAG() {
		return RaceInfoTabSpecName;
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		Log.i(LOG_TAG(), "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case RACE_INFO_LOADER:
				projection = new String[]{Race.Instance().getTableName() + "." + Race._ID + " as _id", Race.RaceDate, RaceLocation.CourseName, Race.RaceType, Race.StartInterval, RaceLocation.Distance, Race.NumLaps};
				selection = Race.Instance().getTableName() + "." + Race._ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name);
				selectionArgs = null;
				sortOrder = Race.Instance().getTableName() + "." + Race._ID;
				loader = new CursorLoader(getActivity(), RaceInfoView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case APP_SETTINGS_LOADER_RACEINFO:
				projection = new String[]{AppSettings.AppSettingName, AppSettings.AppSettingValue};
				selection = null;
				sortOrder = null;
				selectionArgs = null;
				loader = new CursorLoader(getActivity(), AppSettings.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
			case COURSE_RECORD_LOADER:
				projection = new String[]{RaceResults.Instance().getTableName() + "." + RaceResults._ID + " as _id", RaceResults.ElapsedTime};
				selection = Race.Instance().getTableName() + "." + Race.RaceLocation_ID + " in (" +
							SQLiteQueryBuilder.buildQueryString(true, RaceInfoView.Instance().getTableName(), new String[]{Race.RaceLocation_ID},
																Race.Instance().getTableName() + "." + Race._ID + "=" + AppSettings.Instance().getParameterSql(AppSettings.AppSetting_RaceID_Name), null, null, Race.Instance().getTableName() + "." + Race._ID, "1") + ")";
				selectionArgs = null;
				sortOrder = RaceResults.Instance().getTableName() + "." + RaceResults.ElapsedTime;
				loader = new CursorLoader(getActivity(), RaceInfoResultsView.Instance().CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG(), "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG(), "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					cursor.moveToFirst();
					if(cursor.getCount() > 0){
						Long raceDateMS = cursor.getLong(cursor.getColumnIndex(Race.RaceDate));
						String courseName = cursor.getString(cursor.getColumnIndex(RaceLocation.CourseName));
						long raceTypeID = cursor.getLong(cursor.getColumnIndex(Race.RaceType));
						String raceTypeName = RaceType.DescriptionFromRaceTypeID(raceTypeID);
						String startIntervalText = Long.toString(cursor.getLong(cursor.getColumnIndex(Race.StartInterval)));
						long numRaceLaps = cursor.getLong(cursor.getColumnIndex(Race.NumLaps));
						distance = Float.toString(cursor.getFloat(cursor.getColumnIndex(RaceLocation.Distance)) * (float)numRaceLaps);	
						
						if(distance == null){
							distance = "";
						}
						
						if(raceTypeID == 1){							
							raceLaps.setText(Long.toString(numRaceLaps));
							llRaceLaps.setVisibility(View.VISIBLE);
						}else{
							llRaceLaps.setVisibility(View.GONE);
						}
						
						Date raceDateTemp = new Date(raceDateMS);
						SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy");
						raceDate.setText(formatter.format(raceDateTemp).toString());
						raceCourseName.setText(courseName);
						raceType.setText(raceTypeName);
						raceStartInterval.setText(startIntervalText);
						SetDistance();
					}
					break;
				case APP_SETTINGS_LOADER_RACEINFO:	
					getActivity().getSupportLoaderManager().restartLoader(COURSE_RECORD_LOADER, null, this);
					
					Integer distanceUnitID = Integer.parseInt(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_DistanceUnits_Name, "0"));
					distanceUnit = "mi";
					switch(distanceUnitID){
						case 0:
							distanceUnit = "mi";
							break;
						case 1:
							distanceUnit = "km";
							break;
						default:
							distanceUnit = "mi";
							break;
					}
					getActivity().getSupportLoaderManager().restartLoader(RACE_INFO_LOADER, null, this);
					break;	
				case COURSE_RECORD_LOADER:
					if(cursor != null && cursor.getCount() > 0){
						cursor.moveToFirst();
						long elapsedTime = cursor.getLong(cursor.getColumnIndex(RaceResults.ElapsedTime));
						if (courseRecord != null) {
				        	courseRecord.setText(TimeFormatter.Format(elapsedTime, true, true, true, true, true, false, false, false));
				        }
					}
					break;
			}
			Log.i(LOG_TAG(), "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoadFinished error", ex); 
		}
	}

	private void SetDistance()
	{
		if (distance != "" && distanceUnit == "")
		{
			raceDistance.setText(distance);
		}
		else if (distanceUnit != "" && distance != "" && !distance.contains(" "))
		{
			distance += " " + distanceUnit;
			raceDistance.setText(distance);
		}
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG(), "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case RACE_INFO_LOADER:
					break;
				case APP_SETTINGS_LOADER_RACEINFO:
					break;
				case COURSE_RECORD_LOADER:
					break;
			}
			Log.i(LOG_TAG(), "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG(), "onLoaderReset error", ex); 
		}
	}

    public void postData(){
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.gvccracing.com/?page_id=2525&pass=com.gvccracing.android.tttimer");

        try {
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            InputStream is = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);

            int current = 0;

            while((current = bis.read()) != -1){
                baf.append((byte)current);
            }

            /* Convert the Bytes read to a String. */
            String text = new String(baf.toByteArray());

            JSONObject mainJson = new JSONObject(text);
            JSONArray jsonArray = mainJson.getJSONArray("members");

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);

                String firstName = json.getString("fname");
                String lastName = json.getString("lname");
                String licenseStr = json.getString("license");
                Integer license = 0;
                try{
                    license = Integer.parseInt(licenseStr);
                } catch(Exception ex){
                    Log.e(LOG_TAG(), "Unable to parse license string");
                }
                long age = json.getLong("age");
                String categoryStr = json.getString("category");
                Integer category = 5;
                try{
                    category = Integer.parseInt(categoryStr);
                } catch(Exception ex){
                    Log.e(LOG_TAG(), "Unable to parse category string");
                }
                String phone = json.getString("phone");
                long phoneNumber = 0;
                try{
                    phoneNumber = Long.parseLong(phone.replace("-", "").replace("(", "").replace(")", "").replace(" ", "").replace(".", "").replace("*", ""));
                }catch(Exception e){
                    Log.e(LOG_TAG(), "Unable to parse phone number");
                }
                String gender = json.getString("gender");
                String econtact = json.getString("econtact");
                String econtactPhone = json.getString("econtact_phone");
                long eContactPhoneNumber = 0;
                try{
                    eContactPhoneNumber = Long.parseLong(econtactPhone.replace("-", "").replace("(", "").replace(")", "").replace(" ", "").replace(".", "").replace("*", ""));
                }catch(Exception e){
                    Log.e(LOG_TAG(), "Unable to parse econtact phone number");
                }
                Long member_id = json.getLong("member_id");

                String gvccCategory;
                switch(category){
                    case 1:
                    case 2:
                    case 3:
                        gvccCategory = "A";
                        break;
                    case 4:
                        gvccCategory = "B4";
                        break;
                    case 5:
                        gvccCategory = "B5";
                        break;
                    default:
                        gvccCategory = "B5";
                        break;
                }

                Log.w(LOG_TAG(), lastName);
                Cursor racerInfo = Racer.Instance().Read(getActivity(), new String[]{Racer._ID, Racer.FirstName, Racer.LastName, Racer.USACNumber, Racer.PhoneNumber, Racer.EmergencyContactName, Racer.EmergencyContactPhoneNumber}, Racer.USACNumber + "=?", new String[]{license.toString()}, null);
                if(racerInfo.getCount() > 0){
                    racerInfo.moveToFirst();
                    Long racerID = racerInfo.getLong(racerInfo.getColumnIndex(Racer._ID));
                    Racer.Instance().Update(getActivity(), racerID, firstName, lastName, license, 0l, phoneNumber, econtact, eContactPhoneNumber, gender);
                    Cursor racerClubInfo = RacerClubInfo.Instance().Read(getActivity(), new String[]{RacerClubInfo._ID, RacerClubInfo.GVCCID, RacerClubInfo.RacerAge, RacerClubInfo.Category}, RacerClubInfo.Racer_ID + "=? AND " + RacerClubInfo.Year + "=? AND " + RacerClubInfo.Upgraded + "=?", new String[]{racerID.toString(), "2013", "0"}, null);
                    if(racerClubInfo.getCount() > 0){
                        racerClubInfo.moveToFirst();
                        long racerClubInfoID = racerClubInfo.getLong(racerClubInfo.getColumnIndex(RacerClubInfo._ID));
                        String rciCategory = racerClubInfo.getString(racerClubInfo.getColumnIndex(RacerClubInfo.Category));

                        boolean upgraded = gvccCategory != rciCategory;
                        if(upgraded){
                            RacerClubInfo.Instance().Update(getActivity(), racerClubInfoID, null, null, null, null, null, null, null, null, null, upgraded);
                            RacerClubInfo.Instance().Create(getActivity(), racerID, null, 2013, gvccCategory, 0, 0, 0, age, member_id, false);
                        } else{
                            RacerClubInfo.Instance().Update(getActivity(), racerClubInfoID, null, null, null, null, null, null, null, age, member_id, upgraded);
                        }

                    }else{
                        RacerClubInfo.Instance().Create(getActivity(), racerID, null, 2013, gvccCategory, 0, 0, 0, age, member_id, false);
                    }
                    if(racerClubInfo != null){
                        racerClubInfo.close();
                    }
                }else{
                    // TODO: Better birth date
                    Uri resultUri = Racer.Instance().Create(getActivity(), firstName, lastName, license, 0l, phoneNumber, econtact, eContactPhoneNumber, gender);
                    long racerID = Long.parseLong(resultUri.getLastPathSegment());
                    RacerClubInfo.Instance().Create(getActivity(), racerID, null, 2013, gvccCategory, 0, 0, 0, age, member_id, false);
                }
                if(racerInfo != null){
                    racerInfo.close();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG(), e.getMessage());
        }
    }

	public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		switch (v.getId())
		{
            case R.id.btnDownloadRacers:
                postData();
                break;
			case R.id.btnMarshalLocations:
				showMarshalLocations(v);
				break;
			case R.id.btnPreviousResults:
				showChoosePreviousRace();
				break;
			case R.id.btnAdminMenu:
				if(Boolean.parseBoolean(AppSettings.Instance().ReadValue(getActivity(), AppSettings.AppSetting_AdminMode_Name, "false"))){
					AdminMenuView adminMenuDialog = new AdminMenuView();
					adminMenuDialog.show(fm, AdminMenuView.LOG_TAG);
				}else{
					AdminAuthView adminAuthDialog = new AdminAuthView();
			        adminAuthDialog.show(fm, AdminAuthView.LOG_TAG);
				}
				break;
			case R.id.btnSeriesResults:
				SeriesResultsView seriesResultsDialog = new SeriesResultsView();
				seriesResultsDialog.show(fm, SeriesResultsView.LOG_TAG);
				break;
		}
	}

	private void showChoosePreviousRace() {
		OtherRaceResults previousResultsDialog = new OtherRaceResults();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		previousResultsDialog.show(fm, OtherRaceResults.LOG_TAG);
	}

	private void showMarshalLocations(View v) {
		MarshalLocations marshalLocationsDialog = new MarshalLocations();
		FragmentManager fm = getParentActivity().getSupportFragmentManager();
		marshalLocationsDialog.show(fm, MarshalLocations.LOG_TAG);
	}
}
