package com.hitchhiker.mobile.asynctasks;

import org.json.JSONObject;

import com.hitchhiker.mobile.AuthorizationView;
import com.parse.ParseUser;

import android.os.AsyncTask;

public class GetTwitterCredentials extends AsyncTask<Void, Void, Void> {
	
	private AuthorizationView view;
	private String screenName;
	private ParseUser user;
	
	public GetTwitterCredentials(AuthorizationView view, String screenName, ParseUser user) {
		this.view = view;
		this.screenName = screenName;
		this.user = user;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			view.api.getTwitterCredentials(screenName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		JSONObject userProfile = new JSONObject();
		try {
			userProfile.put("twitterName", view.api.getTwitterName());
			userProfile.put("twitterImage", view.api.getTwitterImage());
			user.put("profile", userProfile);
			user.saveInBackground();
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onPostExecute(result);
	}
	
}