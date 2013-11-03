package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.RouteList;

import android.os.AsyncTask;

public class GetRouteList extends AsyncTask<Void, Void, Void> {
	
	RouteList view;
	boolean pullToRefresh = false;
	
	public GetRouteList(RouteList view, boolean pullToRefresh) {
		this.view = view;
		this.pullToRefresh = pullToRefresh;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		view.showToast();
	}
	
}