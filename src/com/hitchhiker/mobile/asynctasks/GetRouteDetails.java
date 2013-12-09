package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.RouteView;
import com.hitchhiker.mobile.objects.Route;

import android.os.AsyncTask;

public class GetRouteDetails extends AsyncTask<Void, Void, Void> {
	
	private Route route;
	private RouteView view;
	
	public GetRouteDetails(RouteView view) {
		this.view = view;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			route = view.api.getRouteDetails(view.route.getId());
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	protected void onPostExecute(Void result) {
		view.progressDialog.dismiss();
		view.updateUI(this.route);
	}
	
}