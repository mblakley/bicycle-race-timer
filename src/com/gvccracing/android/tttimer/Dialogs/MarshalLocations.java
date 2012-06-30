package com.gvccracing.android.tttimer.Dialogs;

import com.gvccracing.android.tttimer.R;
import com.gvccracing.android.tttimer.Utilities.ImageAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class MarshalLocations extends BaseDialog implements View.OnClickListener {
	public static final String LOG_TAG = "MarshalLocations";
	
	ImageView imageView = null;
	TextView marshalLocationDescription = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_marshal_locations, container, false);
	    super.onCreate(savedInstanceState);

	    Gallery gallery = (Gallery) v.findViewById(R.id.gallery);
	    gallery.setAdapter(new ImageAdapter(this.getActivity()));
	    
	    imageView = (ImageView) v.findViewById(R.id.ImageView01);
	    
	    marshalLocationDescription = (TextView) v.findViewById(R.id.marshalLocationDescription);
	    
	    gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,long id) {
				imageView.setImageResource(ImageAdapter.ImageIds[position]);
				marshalLocationDescription.setText("Description of position " + position);
			}
	    });
	    
	    imageView.setImageResource(ImageAdapter.ImageIds[0]);
	    marshalLocationDescription.setText("Start out");
	    
	    return v;
	}
	
	@Override 
	protected int GetTitleResourceID() {
		return R.string.MarshalLocation;
	}

	public void onClick(View v) { 
		try{
			//The cancel button was pressed
			super.onClick(v);			
		}
		catch(Exception ex){
			Log.e(LOG_TAG, "onClick failed",ex);
		}
	}

	@Override
	protected String LOG_TAG() {
		return LOG_TAG;
	}
}