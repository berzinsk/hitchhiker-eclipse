package com.hitchhiker.mobile;

import com.crashlytics.android.Crashlytics;
import java.util.Arrays;

import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;

import com.beardedhen.bbutton.BootstrapButton;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

public class AuthorizationView extends Activity {
	
	BootstrapButton facebookLogin;
	BootstrapButton twitterLogin;
	ParseInstallation installation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Crashlytics.start(this);
		setContentView(R.layout.authorization);
		ParseAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, AuthorizationView.class);
		installation = ParseInstallation.getCurrentInstallation();
		
		if (testNetwork() == false) {
			startActivity(new Intent(this, OfflineView.class));
		} else {
			SharedPreferences prefs = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE);
			if (prefs.contains("twitterObjectId") || prefs.contains("facebookObjectId")) {
				startActivity(new Intent(AuthorizationView.this, RouteList.class));
			}
			
			facebookLogin = (BootstrapButton) findViewById(R.id.login_facebook);
			twitterLogin = (BootstrapButton) findViewById(R.id.login_twitter);
			
			twitterLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					twitterLogin();
				}
			});
			
			facebookLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					facebookLogin();
				}
			});
		}
	}
	
	public boolean testNetwork() {
		NetworkInfo info = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		
		if (info == null || !info.isConnected()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authorization, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	private void twitterLogin() {
		ParseTwitterUtils.logIn(this, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					
				} else {
					Editor editor = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE).edit();
					editor.putString("twitterObjectId", user.getObjectId());
					editor.commit();
					startActivity(new Intent(AuthorizationView.this, RouteList.class));
				}
			}
		});
	}
	
	private void facebookLogin() {
		
		ParseFacebookUtils.logIn(Arrays.asList("email", Permissions.User.EMAIL, Permissions.User.ABOUT_ME), this, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					
				} else if (user != null) {
					makeMeRequest();
					Editor editor = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE).edit();
					editor.putString("facebookObjectId", user.getObjectId());
					editor.commit();
					installation.put("user", ParseUser.getCurrentUser());
					installation.saveInBackground();
					startActivity(new Intent(AuthorizationView.this, RouteList.class));
				}
			}
		});
	}
	
	public void makeMeRequest() {
		if (ParseFacebookUtils.getSession().isOpened()) {
			Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						JSONObject userProfile = new JSONObject();
						
						try {
							userProfile.put("facebookId", user.getId());
							userProfile.put("name", user.getName());
							ParseUser currentUser = ParseUser.getCurrentUser();
							currentUser.put("profile", userProfile);
							currentUser.saveInBackground();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			});
		}
	}
}
