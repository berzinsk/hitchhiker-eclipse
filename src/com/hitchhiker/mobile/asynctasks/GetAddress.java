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
	private String city;
	private String street;
	private String streetNumber = null;
	
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
			city = view.api.getCity();
			street = view.api.getStreet();
			streetNumber = view.api.getStreetNumber();
		} catch (Exception e) {
			
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (routeFrom == true) {
			view.setFromLatitude(lat);
			view.setFromLongitude(lng);
			view.setFromCity(city);
			if (streetNumber != null) {
				view.setLocationFrom(street + " " + streetNumber);
			} else {
				view.setLocationFrom(street);
			}
			
		} else {
			view.setToLatitude(lat);
			view.setToLongitude(lng);
			view.setToCity(city);
			if (streetNumber != null) {
				view.setLocationTo(street + " " + streetNumber);
			} else {
				view.setLocationTo(street);
			}
		}
		
		super.onPostExecute(result);
	}
}