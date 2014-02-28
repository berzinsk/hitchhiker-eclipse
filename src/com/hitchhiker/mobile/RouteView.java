package com.hitchhiker.mobile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hitchhiker.mobile.asynctasks.GetPolyline;
import com.hitchhiker.mobile.asynctasks.GetRouteDetails;
import com.hitchhiker.mobile.objects.Route;
import com.hitchhiker.mobile.tools.API;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RouteView extends Activity {
	
	public API api;
	public Route route;
	public AsyncTask<Void, Void, Void> getRouteDetails;
	public AsyncTask<Void, Void, String> getPoly;
	public ProgressDialog progressDialog;
	private Button joinRoute;
	
	private Double routeFromLat;
	private Double routeFromLng;
	private Double routeToLat;
	private Double routeToLng;
	
	private String polylineResult;

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
				joinDeclineRoute(true);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_view, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void addMap(String result) {
		
		Log.d("JSON FRMO GOOGLE", result);
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		LatLng routeFrom = new LatLng(getRouteFromLat(), getRouteFromLng());
		LatLng routeTo = new LatLng(getRouteToLat(), getRouteToLng());
		
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(routeFrom, 6));
		
		map.addMarker(new MarkerOptions()
				.title("route from")
				.position(routeFrom));
		
		map.addMarker(new MarkerOptions()
				.title("route to")
				.position(routeTo));
		
		try {
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			for (int i = 0; i < list.size() - 1; i++) {
				LatLng src = list.get(i);
				LatLng dest = list.get(i + 1);
				Polyline line = map.addPolyline(new PolylineOptions()
									.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
									.width(2)
									.color(Color.BLUE).geodesic(true));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void updateUI(final Route route) {
		((Hitchhiker) getApplicationContext()).setRoute(route);
		
		setRouteFromLat(route.getLatitudeFrom());
		setRouteFromLng(route.getLongitudeFrom());
		setRouteToLat(route.getLatitudeTo());
		setRouteToLng(route.getLongitudeTo());
		
		TextView routeFrom = (TextView) findViewById(R.id.route_from_view);
//		routeFrom.append(route.getRouteFrom());
		routeFrom.setText(getResources().getString(R.string.from) + route.getRouteFrom());
		
		TextView routeTo = (TextView) findViewById(R.id.route_to_view);
//		routeTo.append(route.getRouteTo());
		routeTo.setText(getResources().getString(R.string.to) + route.getRouteTo());
		
		TextView price = (TextView) findViewById(R.id.price_view);
//		price.append(String.valueOf(route.getPrice()));
		price.setText(getResources().getString(R.string.price_tag) + route.getPrice());
		
		TextView departureTime = (TextView) findViewById(R.id.departure_time_view);
//		departureTime.append(route.getDepartureTime());
		departureTime.setText(getResources().getString(R.string.time) + route.getDepartureTime());
		
		TextView departureDate = (TextView) findViewById(R.id.departure_date_view);
//		departureDate.append(route.getDepartureDate());
		departureDate.setText(getResources().getString(R.string.time) + route.getDepartureTime());
		
		TextView availableSeats = (TextView) findViewById(R.id.seats_view);
//		availableSeats.append(String.valueOf(route.getAvailableSeats()));
		availableSeats.setText(getResources().getString(R.string.seats) + route.getAvailableSeats());
		
		if (route.getPassengers() != null) {
			for (int i = 0; i < route.getPassengers().size(); i++) {
				if (route.getPassengers().get(i).contains(ParseUser.getCurrentUser().getObjectId())) {
					joinRoute.setText(getResources().getString(R.string.decline));
					joinRoute.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							joinDeclineRoute(false);
						}
					});
				}
			}
		}
		
		getPoly = new GetPolyline(this, makeRouteUrl()).execute();
	}
	
	private void joinDeclineRoute(final boolean join) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
		
		query.getInBackground(route.getId(), new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject route, ParseException e) {
				if (e == null) {
					if (join == true) {
						route.add("passengers", ParseUser.getCurrentUser().getObjectId());
						route.increment("availableSeats", -1);
					} else {
						
					}
					
					route.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							getRouteDetails = new GetRouteDetails(RouteView.this).execute();
						}
					});
				}
			}
		});
	}

	public Double getRouteFromLat() {
		return routeFromLat;
	}

	public void setRouteFromLat(Double routeFromLat) {
		this.routeFromLat = routeFromLat;
	}

	public Double getRouteFromLng() {
		return routeFromLng;
	}

	public void setRouteFromLng(Double routeFromLng) {
		this.routeFromLng = routeFromLng;
	}

	public Double getRouteToLat() {
		return routeToLat;
	}

	public void setRouteToLat(Double routeToLat) {
		this.routeToLat = routeToLat;
	}

	public Double getRouteToLng() {
		return routeToLng;
	}

	public void setRouteToLng(Double routeToLng) {
		this.routeToLng = routeToLng;
	}
	
	private String makeRouteUrl() {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");
		urlString.append(getRouteFromLat().toString());
		urlString.append(",");
		urlString.append(getRouteFromLng().toString());
		urlString.append("&destination=");
		urlString.append(getRouteToLat().toString());
		urlString.append(",");
		urlString.append(getRouteToLng().toString());
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		
		return urlString.toString();
	}
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}

	public String getPolylineResult() {
		return polylineResult;
	}

	public void setPolylineResult(String polylineResult) {
		this.polylineResult = polylineResult;
	}

}
