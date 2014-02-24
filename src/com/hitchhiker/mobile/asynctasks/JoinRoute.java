package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.RouteView;
import com.hitchhiker.mobile.objects.Route;

import android.os.AsyncTask;

public class JoinRoute extends AsyncTask<Void, Void, Void> {
	
	private Route route;
	private RouteView view;
	private String userId;
	
	public JoinRoute(RouteView view, String userId) {
		this.view = view;
		this.userId = userId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			view.api.joinRoute(view.route.getId(), "sample");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	protected void onPostExecute(Void result) {
		
	}
	
}