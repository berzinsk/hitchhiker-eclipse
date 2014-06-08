package com.hitchhiker.mobile;

import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.hitchhiker.mobile.adapters.PassengerListAdapter;
import com.hitchhiker.mobile.asynctasks.GetPolyline;
import com.hitchhiker.mobile.asynctasks.GetRouteDetails;
import com.hitchhiker.mobile.objects.Route;
import com.hitchhiker.mobile.tools.API;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPayment;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RouteView extends Activity {
	private static final int PAYPAL_CODE = 9911;
	
	public API api;
	public Route route;
	public AsyncTask<Void, Void, Void> getRouteDetails;
	public AsyncTask<Void, Void, String> getPoly;
	public ProgressDialog progressDialog;
	private Button joinRoute;
	private Button buttonPay;
	
	private Double routeFromLat;
	private Double routeFromLng;
	private Double routeToLat;
	private Double routeToLng;
	private String routeUserId;
	private Double finalPrice;
	private String paypalAccountEmail;
	private ArrayList<String> paidPassengersList;
	
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
		initPaypalLibrary();
		
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
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		LatLng routeFrom = new LatLng(getRouteFromLat(), getRouteFromLng());
		LatLng routeTo = new LatLng(getRouteToLat(), getRouteToLng());
		
//		map.setMyLocationEnabled(true);
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
		setRouteUserId(route.getUserId());
		
		buttonPay = (Button) findViewById(R.id.button_pay);
		if (getRouteUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
			Log.d("ir", "ir");
			buttonPay.setVisibility(4);
		} else {
			buttonPay.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getPaypalAccount();
				}
			});
		}
		
		if (getRouteUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
			
			if (route.getPaidPassengers() != null && !route.getPaidPassengers().isEmpty()) {
				joinRoute.setText("Paid passengers");
				joinRoute.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						buildDialog(route);
					}
				});
			} else {
				joinRoute.setVisibility(4);
			}
			
		} else {
			if (route.getPassengers() != null) {
				if (!route.getPassengers().isEmpty()) {
					for (int i = 0; i < route.getPassengers().size(); i++) {
						if (route.getPassengers().get(i).contains(ParseUser.getCurrentUser().getObjectId())) {
							joinRoute.setText(getResources().getString(R.string.decline));
							joinRoute.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									joinDeclineRoute(false);
								}
							});
						} else {
							joinRoute.setText(getResources().getString(R.string.join));
							joinRoute.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									joinDeclineRoute(true);
								}
							});
						}
					}
				} else {
					joinRoute.setText(getResources().getString(R.string.join));
					joinRoute.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							joinDeclineRoute(true);
						}
					});
				}
				
			}
		}
		
		TextView routeFrom = (TextView) findViewById(R.id.route_from_view);
		routeFrom.setText(getResources().getString(R.string.from) + route.getRouteFrom());
		
		TextView routeTo = (TextView) findViewById(R.id.route_to_view);
		routeTo.setText(getResources().getString(R.string.to) + route.getRouteTo());
		
		TextView price = (TextView) findViewById(R.id.price_view);
		finalPrice = route.getPrice();
		price.setText(getResources().getString(R.string.price_tag) + route.getPrice());
		
		TextView departureTime = (TextView) findViewById(R.id.departure_time_view);
		departureTime.setText(getResources().getString(R.string.time) + route.getDepartureTime());
		
		TextView departureDate = (TextView) findViewById(R.id.departure_date_view);
		departureDate.setText(getResources().getString(R.string.date) + route.getDepartureDate());
		
		TextView availableSeats = (TextView) findViewById(R.id.seats_view);
		availableSeats.setText(getResources().getString(R.string.seats) + route.getAvailableSeats());
		
		getPoly = new GetPolyline(this, makeRouteUrl()).execute();
	}
	
	private void buildDialog(final Route route) {
		final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.custom_dialog);
		
		final ListView paidCustomers = (ListView) dialog.findViewById(R.id.parid_customer_list);
	    final ArrayList<String> sample = new ArrayList<String>();
	    for (int i = 0; i < route.getPaidPassengers().size(); i++) {
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			final int index = i;
			
			query.getInBackground(route.getPaidPassengers().get(i), new GetCallback<ParseUser>() {

				@Override
				public void done(ParseUser object, ParseException error) {
					JSONObject routeAuthor = object.getJSONObject("profile");
					try {
						if (routeAuthor.getString("userName") != null) {
							String name = routeAuthor.getString("userName").toString();
							sample.add(name);
							Log.d("passssengeer", String.valueOf(route.getPaidPassengers().size()));
							Log.d("passenger nameees", routeAuthor.getString("userName").toString());
							Log.d("tiek ari te", "tiek ari te" + String.valueOf(index));
							if (index == (route.getPaidPassengers().size() - 2)) {
								PassengerListAdapter passengerList = new PassengerListAdapter(RouteView.this, android.R.layout.simple_list_item_1, sample);
							    paidCustomers.setAdapter(passengerList);
								
								dialog.show();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			
			
		}
	}
	
	
	
	private void addPaidPassengers() {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
		query.getInBackground(route.getId(), new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject route, ParseException error) {
				if (error == null) {
					route.add("paidPassengers", ParseUser.getCurrentUser().getObjectId());
					route.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		});
	}
	
	private void joinDeclineRoute(final boolean join) {
		
		progressDialog = ProgressDialog.show(RouteView.this, null, getResources().getString(R.string.loading), true);
		progressDialog.setCancelable(true);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
		
		query.getInBackground(route.getId(), new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject route, ParseException e) {
				if (e == null) {
					if (join) {
						route.add("passengers", ParseUser.getCurrentUser().getObjectId());
						route.put("hasPassengers", true);
						route.increment("availableSeats", -1);
					} else {
						List<String> passengers = route.getList("passengers");
						for (int i = 0; i < passengers.size(); i++) {
							if (passengers.get(i).equals(ParseUser.getCurrentUser().getObjectId())) {
								passengers.remove(i);
							}
						}
						route.put("passengers", passengers);
						route.increment("availableSeats");
						if (passengers.isEmpty()) {
							route.put("hasPassengers", false);
						}
					}
					
					route.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							if (join) {
								sendPush(getRouteUserId(), true);
							} else {
								sendPush(getRouteUserId(), false);
							}
							
							progressDialog.dismiss();
							getRouteDetails = new GetRouteDetails(RouteView.this).execute();
						}
					});
				}
			}
		});
	}
	
	private void getPaypalAccount() {
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.getInBackground(getRouteUserId(), new GetCallback<ParseUser>() {

			@Override
			public void done(ParseUser object, ParseException error) {
				if (object.getString("paypalAccount") != null) {
					Log.d("useer email", object.getString("paypalAccount"));
					setPaypalAccountEmail(object.getString("paypalAccount"));
					createPayment(object.getString("paypalAccount"));
				} else {
					Log.d("noooo", "no paypal account");
				}
			}
		});
		Log.d("straadaaa", "straadaaaa");
//		Log.d("paypaaaal", getPaypalAccountEmail());
	}
	
	private void createPayment(String email) {
		PayPalPayment payment = new PayPalPayment();
		payment.setCurrencyType("EUR");
		payment.setRecipient(email);
		payment.setSubtotal(new BigDecimal(finalPrice));
		payment.setPaymentType(PayPal.PAY_TYPE_SIMPLE);
		
		Intent checkoutIntent = PayPal.getInstance().checkout(payment, this);
		startActivityForResult(checkoutIntent, PAYPAL_CODE);
	}
	
	private void sendPush(String userId, boolean join) {
		ParseUser currentUser = ParseUser.getCurrentUser();
		String name = null;
		try {
			name = currentUser.getJSONObject("profile").getString("userName");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo("user", ParseObject.createWithoutData("_User", userId));
		
		ParsePush push = new ParsePush();
		push.setQuery(pushQuery);
		
		if (join && name != null) {
			push.setMessage(name + " just joined your route.");
		} else if (!join && name != null) {
			push.setMessage(name + " just abandoned the route.");
		}
		
		push.sendInBackground();
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
	
	private void initPaypalLibrary() {
		PayPal pp = PayPal.getInstance();
		
		if (pp == null) {
			pp = PayPal.initWithAppID(this, "APP-80W284485P519543T", PayPal.ENV_SANDBOX);
			pp.setLanguage("en_US");
			pp.setFeesPayer(PayPal.FEEPAYER_SENDER);
			pp.setShippingEnabled(false);
			pp.setDynamicAmountCalculationEnabled(false);
		}
	}

	public String getPolylineResult() {
		return polylineResult;
	}

	public void setPolylineResult(String polylineResult) {
		this.polylineResult = polylineResult;
	}

	public String getRouteUserId() {
		return routeUserId;
	}

	public void setRouteUserId(String routeUserId) {
		this.routeUserId = routeUserId;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PAYPAL_CODE) {
			Log.d("success", "success");
			addPaidPassengers();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void setPaypalAccountEmail(String paypalAccountEmail) {
		this.paypalAccountEmail = paypalAccountEmail;
	}
	
	public String getPaypalAccountEmail() {
		return this.paypalAccountEmail;
	}

	public ArrayList<String> getPaidPassengersList() {
		return paidPassengersList;
	}

	public void setPaidPassengersList(ArrayList<String> paidPassengersList) {
		this.paidPassengersList = paidPassengersList;
	}
}
