package com.hitchhiker.mobile;

import com.hitchhiker.mobile.asynctasks.GetRouteDetails;
import com.hitchhiker.mobile.objects.Route;
import com.hitchhiker.mobile.tools.API;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RouteView extends Activity {
	
	public API api;
	public Route route;
	public AsyncTask<Void, Void, Void> getRouteDetails;
	public ProgressDialog progressDialog;
	private Button joinRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try {
			api = new API(this, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_view);
		
		progressDialog = ProgressDialog.show(RouteView.this, null, getResources().getString(R.string.loading), true);
		
		route = ((Hitchhiker) this.getApplicationContext()).getRoute();
		
		getRouteDetails = new GetRouteDetails(this).execute();
		
		joinRoute = (Button) findViewById(R.id.join);
		joinRoute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				joinRoute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_view, menu);
		return true;
	}
	
	public void updateUI(Route route) {
		((Hitchhiker) getApplicationContext()).setRoute(route);
		
		TextView routeFrom = (TextView) findViewById(R.id.route_from_view);
		routeFrom.append(route.getRouteFrom());
		
		TextView routeTo = (TextView) findViewById(R.id.route_to_view);
		routeTo.append(route.getRouteTo());
		
		TextView price = (TextView) findViewById(R.id.price_view);
		price.append(String.valueOf(route.getPrice()));
		
		TextView departureTime = (TextView) findViewById(R.id.departure_time_view);
		departureTime.append(route.getDepartureTime());
		
		TextView departureDate = (TextView) findViewById(R.id.departure_date_view);
		departureDate.append(route.getDepartureDate());
		
		TextView availableSeats = (TextView) findViewById(R.id.seats_view);
		availableSeats.append(String.valueOf(route.getAvailableSeats()));
	}
	
	private void joinRoute() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
		
		query.getInBackground(route.getId(), new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject route, ParseException e) {
				if (e == null) {
					
				}
			}
		});
	}

}
