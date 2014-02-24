package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.AddRoute;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetAddress extends AsyncTask<Void, Void, Void> {
	
	private AddRoute view;
	private String address;
	private boolean routeFrom;
	
	private double lat;
	private double lng;
	
	public GetAddress(AddRoute view, String address, boolean routeFrom) {
		this.view = view;
		this.address = address;
		this.routeFrom = routeFrom;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			Log.d("DO IN BACKGROUND", "DO IN BACKGROUND");
			view.api.location(address);
			lat = view.api.getLatitude();
			lng = view.api.getLongitude();
		} catch (Exception e) {
			
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (routeFrom == true) {
			view.setFromLatitude(lat);
			view.setFromLongitude(lng);
		} else {
			view.setToLatitude(lat);
			view.setToLongitude(lng);
		}
		
		super.onPostExecute(result);
	}
}