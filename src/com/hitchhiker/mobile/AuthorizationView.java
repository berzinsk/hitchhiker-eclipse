package com.hitchhiker.mobile;

import java.security.acl.Permission;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.beardedhen.bbutton.BootstrapButton;
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
		setContentView(R.layout.authorization);
		ParseAnalytics.trackAppOpened(getIntent());
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
		
		ParseFacebookUtils.logIn(Arrays.asList("email", Permissions.User.EMAIL), this, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d("No useeed", "No useeer");
				} else if (user.isNew()) {
					user.add("email", user.getEmail());
					startActivity(new Intent(AuthorizationView.this, RouteList.class));
				} else {
					startActivity(new Intent(AuthorizationView.this, RouteList.class));
				}
			}
		});
		
//		ParseFacebookUtils.logIn(this, new LogInCallback() {
//			
//			@Override
//			public void done(ParseUser user, ParseException err) {
//				if (user == null) {
//					Log.d("No useeed", "No useeer");
//				} else if (user.isNew()) {
//					startActivity(new Intent(AuthorizationView.this, RouteList.class));
//				} else {
//					startActivity(new Intent(AuthorizationView.this, RouteList.class));
//				}
//			}
//		});
	}
}
