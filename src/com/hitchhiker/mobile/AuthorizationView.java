package com.hitchhiker.mobile;

import com.crashlytics.android.Crashlytics;
import java.security.acl.Permission;
import java.util.Arrays;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beardedhen.bbutton.BootstrapButton;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseObject;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class AuthorizationView extends Activity {
	
	BootstrapButton authorize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Crashlytics.start(this);
		setContentView(R.layout.authorization);
		ParseAnalytics.trackAppOpened(getIntent());
		
		SharedPreferences prefs = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE);
		if (prefs.contains("userObjectId")) {
			startActivity(new Intent(AuthorizationView.this, RouteList.class));
		}
		
		authorize = (BootstrapButton) findViewById(R.id.authorization);
		
		authorize.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				facebookLogin();
			}
		});
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
	
	private void facebookLogin() {
		
		ParseFacebookUtils.logIn(Arrays.asList("email", Permissions.User.EMAIL, Permissions.User.ABOUT_ME), this, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d("No useeed", "No useeer");
				} else if (user != null) {
					makeMeRequest();
					Editor editor = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE).edit();
					editor.putString("userObjectId", user.getObjectId());
					editor.commit();
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
							Log.d("Stradaaa", "Stradaaaa");
							userProfile.put("facebookId", user.getId());
							userProfile.put("name", user.getName());
							ParseUser currentUser = ParseUser.getCurrentUser();
							currentUser.put("profile", userProfile);
							currentUser.saveInBackground();
						} catch (Exception e) {
							// TODO: handle exception
						}
//						ParseUser.getCurrentUser().put("fbName", user.getName());
//						ParseUser.getCurrentUser().saveInBackground();
					}
				}
			});
		}
	}
}
