package com.hitchhiker.mobile.asynctasks;

import java.util.ArrayList;
import java.util.List;

import com.hitchhiker.mobile.RouteList;
import com.hitchhiker.mobile.objects.Route;

import android.os.AsyncTask;
import android.util.Log;

public class GetRouteList extends AsyncTask<Void, Void, Void> {
	
	RouteList view;
	boolean pullToRefresh = false;
	String data;
	List<Route> routes = new ArrayList<Route>();
	
	public GetRouteList(RouteList view, boolean pullToRefresh) {
		this.view = view;
		this.pullToRefresh = pullToRefresh;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			view.routes = view.api.getRouteList();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		view.updateUi(view.routes);
	}
	
}