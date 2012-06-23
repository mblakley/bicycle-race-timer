package com.gvccracing.android.tttimer.Dialogs;

import java.util.ArrayList;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.DataAccess.CheckInViewCP.CheckInViewInclusive;
import com.gvccracing.android.tttimer.DataAccess.RacerCP.Racer;
import com.gvccracing.android.tttimer.DataAccess.RacerClubInfoCP.RacerClubInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamInfoCP.TeamInfo;
import com.gvccracing.android.tttimer.DataAccess.TeamMembersCP.TeamMembers;
import com.gvccracing.android.tttimer.Dialogs.ChooseTeamRacer.ChooseRacerDialogListener;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddTeamView extends BaseDialog implements View.OnClickListener, ChooseRacerDialogListener, LoaderManager.LoaderCallbacks<Cursor> {
	public static final String LOG_TAG = "AddTeamView";
	
	private Button btnAddNewTeam;
	
	private ArrayList<Long> teamRacerIDs = new ArrayList<Long>();
	
	/**
     * This is a special intent action that means a new team was added.
     */
    public static final String TEAM_NAME_ADDED_ACTION = "com.gvccracing.android.tttimer.TEAM_NAME_ADDED";

	private static final int TEAM_MEMBERS_LOADER = 0x76;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_add_team, container, false);
		TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
		titleView.setText(R.string.AddNewTeam);
		titleView.setTextAppearance(getActivity(), R.style.Large);

		btnAddNewTeam = (Button) v.findViewById(R.id.btnAddNewTeam);
		btnAddNewTeam.setOnClickListener(this);
		
		((Button) v.findViewById(R.id.btnEditRacer1)).setOnClickListener(this);
		((Button) v.findViewById(R.id.btnEditRacer2)).setOnClickListener(this);
		((Button) v.findViewById(R.id.btnEditRacer3)).setOnClickListener(this);
		((Button) v.findViewById(R.id.btnEditRacer4)).setOnClickListener(this);
		((Button) v.findViewById(R.id.btnEditRacer5)).setOnClickListener(this);
		
		return v;
	}

	@Override
	public void onResume(){
		super.onResume();
		
		teamRacerIDs = new ArrayList<Long>();
		// pre-fill the list
		teamRacerIDs.add(-1l);
		teamRacerIDs.add(-1l);
		teamRacerIDs.add(-1l);
		teamRacerIDs.add(-1l);
		teamRacerIDs.add(-1l);
		
		this.getLoaderManager().initLoader(TEAM_MEMBERS_LOADER, null, this);
	}
	
	public void onClick(View v) { 
		try{
			if (v == btnAddNewTeam){
				// Save team name to db					
	 			EditText txtTeamName = (EditText) getView().findViewById(R.id.txtTeamName);
	    		String teamName = txtTeamName.getText().toString();
	    		if(teamName.length() <= 0) {
	    			Toast.makeText(getActivity(), "Please enter a team name", Toast.LENGTH_LONG).show();
	    		} else {
		    		// TODO: Add TeamCategory
		    		Uri resultUri = TeamInfo.Create(getActivity(), teamName, "");
		    		// Get the teamID from the uri
	    			long team_ID = Long.parseLong(resultUri.getLastPathSegment());
	    			
	    			// Broadcast that team names changed
	    			Intent teamNamesChanged = new Intent();
	    			teamNamesChanged.setAction(TEAM_NAME_ADDED_ACTION);
	    			teamNamesChanged.putExtra(TEAM_NAME_ADDED_ACTION, team_ID);
	    			getActivity().sendBroadcast(teamNamesChanged);
	    			
	    			// Add all of the team members to the newly created team
	    			for(int teamMemberCount = 0; teamMemberCount < teamRacerIDs.size(); teamMemberCount++){
	    				if(teamRacerIDs.get(teamMemberCount) >= 0){
	    					TeamMembers.Update(getActivity(), team_ID, teamRacerIDs.get(teamMemberCount), teamMemberCount, true);
	    				}else{
	    					// If the ID is negative, delete this team member
	    					TeamMembers.Delete(getActivity(), team_ID, teamMemberCount);
	    				}
	    			}
	    			    			
	     			// Hide the dialog
	     			dismiss();
	    		}
			} else if(v.getId() == R.id.btnEditRacer1){
				showChooseTeamRacerDialog(0);
			} else if(v.getId() == R.id.btnEditRacer2){
				showChooseTeamRacerDialog(1);				
			} else if(v.getId() == R.id.btnEditRacer3){
				showChooseTeamRacerDialog(2);				
			} else if(v.getId() == R.id.btnEditRacer4){
				showChooseTeamRacerDialog(3);				
			} else if(v.getId() == R.id.btnEditRacer5){
				showChooseTeamRacerDialog(4);			
			}
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	private void showChooseTeamRacerDialog(int racerNumber) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
        ChooseTeamRacer editNameDialog = new ChooseTeamRacer(racerNumber, this);
        editNameDialog.show(fm, ChooseTeamRacer.LOG_TAG);
	}

	public void onFinishEditDialog(int racerNum, Long racerClubInfo_ID) {
		teamRacerIDs.set(racerNum, racerClubInfo_ID);
		
		getActivity().getSupportLoaderManager().restartLoader(TEAM_MEMBERS_LOADER, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.i(LOG_TAG, "onCreateLoader start: id=" + Integer.toString(id));
		CursorLoader loader = null;
		String[] projection;
		String selection;
		String[] selectionArgs = null;
		String sortOrder;
		switch(id){
			case TEAM_MEMBERS_LOADER:
				projection = new String[]{RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + " as _id", Racer.LastName, Racer.FirstName};
				selection = RacerClubInfo.getTableName() + "." + RacerClubInfo._ID + " in " + getTeamRacerList();
				sortOrder = null;
				loader = new CursorLoader(getActivity(), CheckInViewInclusive.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
				break;
		}
		Log.i(LOG_TAG, "onCreateLoader complete: id=" + Integer.toString(id));
		return loader;
	}

	private String getTeamRacerList() {
		String teamRacerList = "(";
		
		for(int i = 0; i < teamRacerIDs.size(); i++){
			teamRacerList += Long.toString(teamRacerIDs.get(i));
			if(i+1 < teamRacerIDs.size()){
				teamRacerList += ",";
			}
		}
		
		teamRacerList += ")";
		return teamRacerList;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			Log.i(LOG_TAG, "onLoadFinished start: id=" + Integer.toString(loader.getId()));			
			switch(loader.getId()){
				case TEAM_MEMBERS_LOADER:	
					if( cursor != null && cursor.getCount() > 0){
						TextView editRacer;
						cursor.moveToFirst();
						
						do{
							Long cursorRacerClubInfoID = cursor.getLong(cursor.getColumnIndex(RacerClubInfo._ID));
							int arrayPosition = 0;
							for(int i = 0; i < teamRacerIDs.size(); i++){
								if(teamRacerIDs.get(i).equals(cursorRacerClubInfoID)){
									arrayPosition = i;
									break;
								}
							}
							if(getView() != null){
								switch(arrayPosition){
									case 0:
										editRacer = (TextView)getView().findViewById(R.id.lblRacer1Name);
										break;
									case 1:
										editRacer = (TextView)getView().findViewById(R.id.lblRacer2Name);
										break;
									case 2:
										editRacer = (TextView)getView().findViewById(R.id.lblRacer3Name);
										break;
									case 3:
										editRacer = (TextView)getView().findViewById(R.id.lblRacer4Name);
										break;
									case 4:
										editRacer = (TextView)getView().findViewById(R.id.lblRacer5Name);
										break;
									default:
										editRacer = null;
										break;
								}
	
								if( editRacer != null){
									if(arrayPosition >= 0 && teamRacerIDs.size() >= arrayPosition + 1 && teamRacerIDs.get(arrayPosition) >= 0){
										String firstName = cursor.getString(cursor.getColumnIndex(Racer.FirstName));
										String lastName = cursor.getString(cursor.getColumnIndex(Racer.LastName));
										// This racer should be added/updated
										editRacer.setText(firstName + " " + lastName);
									}else{
										// This racer should be removed
										editRacer.setText("");
									}
								}
							}
						}while(cursor.moveToNext());
					}
					break;
			}
			Log.i(LOG_TAG, "onLoadFinished complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoadFinished error", ex); 
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		try{
			Log.i(LOG_TAG, "onLoaderReset start: id=" + Integer.toString(loader.getId()));
			switch(loader.getId()){
				case TEAM_MEMBERS_LOADER:
					break;
			}
			Log.i(LOG_TAG, "onLoaderReset complete: id=" + Integer.toString(loader.getId()));
		}catch(Exception ex){
			Log.e(LOG_TAG, "onLoaderReset error", ex); 
		}
	}
}
