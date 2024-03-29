package com.hitchhiker.mobile;

import com.hitchhiker.mobile.objects.Route;
import com.hitchhiker.mobile.tools.API;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class Hitchhiker extends Application {
	
	private API api;
	private Route route;
	public static final String url = "https://api.parse.com/1/classes/";
	
	@Override
	public void onCreate() {
		Parse.initialize(this, "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK", "2Q2jMF3PlbIgvjuRbfA3aAbj0x9CDqyO3UcOcfCq");
		ParseFacebookUtils.initialize("210982895741034");
		ParseTwitterUtils.initialize("0Tz0BCGj584zNlnWpDJYKw", "MreFPERjh33tO43XoxJKd0wnShW6qb6O00HkReErhvk");
		
		super.onCreate();
	}
	
	public synchronized Hitchhiker setRoute(Route route) {
		this.route = route;
		return this;
	}
	
	public synchronized Hitchhiker setAPI(API api) {
		this.api = api;
		return this;
	}
	
	public synchronized Route getRoute() {
		return this.route;
	}
	
	public synchronized API getAPI() {
		return this.api;
	}
}