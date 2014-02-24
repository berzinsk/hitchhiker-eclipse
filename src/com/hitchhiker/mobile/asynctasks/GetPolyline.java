package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.RouteView;

import android.os.AsyncTask;
import android.util.Log;

public class GetPolyline extends AsyncTask<Void, Void, String> {
	
	private RouteView view;
	private String json;
	private String url;
	
	public GetPolyline(RouteView view, String url) {
		this.view = view;
		this.url = url;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			json = view.api.getPolyData(url);
			return json;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result != null) {
			view.addMap(result);
		}
	}
}