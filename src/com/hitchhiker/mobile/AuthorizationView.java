package com.hitchhiker.mobile;

import com.crashlytics.android.Crashlytics;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.bbutton.BootstrapButton;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hitchhiker.mobile.asynctasks.GetTwitterCredentials;
import com.hitchhiker.mobile.tools.API;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;
import com.parse.SignUpCallback;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

public class AuthorizationView extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {
	
	private static final int RC_SIGN_IN = 0;
	
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	
	BootstrapButton facebookLogin;
	BootstrapButton twitterLogin;
	SignInButton googleLogin;
	
	ParseInstallation installation;
	
	public AuthorizationView view = this;
	
	public API api;
	
	AsyncTask<Void, Void, Void> twitterCredentials;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Crashlytics.start(this);
		
		try {
			api = new API(this, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		setContentView(R.layout.authorization);
		ParseAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, AuthorizationView.class);
		installation = ParseInstallation.getCurrentInstallation();
		
		if (testNetwork() == false) {
			startActivity(new Intent(this, OfflineView.class));
		} else {
			SharedPreferences prefs = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE);
			if (prefs.contains("twitterObjectId") || prefs.contains("facebookObjectId") || prefs.contains("googlePlusObjectId")) {
				startActivity(new Intent(AuthorizationView.this, RouteList.class));
			}
			
			facebookLogin = (BootstrapButton) findViewById(R.id.login_facebook);
			twitterLogin = (BootstrapButton) findViewById(R.id.login_twitter);
			googleLogin = (SignInButton) findViewById(R.id.login_google_plus);
			
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
			
			googleLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					googlePlusLogin();
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
	
	private void googlePlusLogin() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API, null)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();
		
		mGoogleApiClient.connect();
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
					installation.put("user", ParseUser.getCurrentUser());
					installation.saveInBackground();
					twitterCredentials = new GetTwitterCredentials(view, ParseTwitterUtils.getTwitter().getScreenName(), user).execute();
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
							userProfile.put("userName", user.getName());
							userProfile.put("userImage", 
									"https://graph.facebook.com/"+user.getId()+"/picture?height=73&type=normal&width=73");
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
	
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}
			
			mIntentInProgress = false;
			
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {
			ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			mConnectionResult = result;
			
			if (mSignInClicked) {
				resolveSignInError();
			}
		}
		
		if (!mIntentInProgress && result.hasResolution()) {
			try {
				mIntentInProgress = true;
				result.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			String personName = currentPerson.getDisplayName();
			String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
			String personPhoto = currentPerson.getImage().getUrl().replace("sz=50", "sz=73");
			
			createParseUser(email, personName, personPhoto);
		}
	}
	
	public void createParseUser(String email, String name, String image) {
		ParseUser newUser = new ParseUser();
		newUser.setUsername(email);
		newUser.setPassword("random");
		
		JSONObject profileData = new JSONObject();
		
		try {
			profileData.put("userName", name);
			profileData.put("userImage", image);
			newUser.put("profile", profileData);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		newUser.signUpInBackground(new SignUpCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					ParseUser user = ParseUser.getCurrentUser();
					Editor editor = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE).edit();
					editor.putString("googlePlusObjectId", user.getObjectId());
					editor.commit();
					installation.put("user", user);
					installation.saveInBackground();
					startActivity(new Intent(AuthorizationView.this, RouteList.class));
				}
			}
		});
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}
}
