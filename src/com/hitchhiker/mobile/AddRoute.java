package com.hitchhiker.mobile;

import java.util.Calendar;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.hitchhiker.mobile.adapters.PlacesAutoCompleteAdapter;
import com.hitchhiker.mobile.asynctasks.GetAddress;
import com.hitchhiker.mobile.tools.API;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AddRoute extends Activity {
	static final int TIME_DIALOG_ID = 1000;
	static final int DATE_DIALOG_ID = 999;
	
	final Context context = this;
	
	public API api;
	
	TextView seats;
	String departureTime;
	String departureDate;
	
	private ImageView addRoteFromImage;
	private ImageView addDateImage;
	private ImageView addSeatsImage;
	private ImageView addNotesImage;
	private ImageView addRouteToImage;
	private ImageView addTimeImage;
	private ImageView addPriceImage;
	private ImageView addStopsImage;
	
	private AutoCompleteTextView routeFromText;
	private TextView dateText;
	private EditText seatsText;
	private EditText notesText;
	private AutoCompleteTextView routeToText;
	private TextView timeText;
	private EditText priceText;
	private EditText stopsText;
	
	Button postButton;
	Button cancelButton;
	
	private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;
    
    private double fromLat;
    private double fromLng;
    private double toLat;
    private double toLng;
    private String fromCity;
    private String toCity;
    private String locationFrom;
    private String locationTo;
    
    public AsyncTask<Void, Void, Void> getAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			api = new API(this, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		setContentView(R.layout.add_route);
//		Parse.initialize(this, "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK", "2Q2jMF3PlbIgvjuRbfA3aAbj0x9CDqyO3UcOcfCq");
		initializeFields();
		
		postButton = (Button) findViewById(R.id.button_post);
		final SharedPreferences prefs = getSharedPreferences("com.hitchhiker.mobile", Context.MODE_PRIVATE);
		postButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (routeFromText.getText().toString() == null || routeToText.getText().toString() == null
						|| getDepartureTime() == null || getDepartureDate() == null
						|| priceText.getText().toString() == null || seatsText.getText().toString() == null) {
					Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show();
				} else {
					ParseObject route = new ParseObject("Routes");
					route.put("routeFrom", routeFromText.getText().toString());
					route.put("routeTo", routeToText.getText().toString());
					route.put("departureTime", getDepartureTime());
					route.put("departureDate", getDepartureDate());
					route.put("price", Double.valueOf(priceText.getText().toString()));
					route.put("availableSeats", Integer.parseInt(seatsText.getText().toString()));
					route.put("latFrom", getFromLatitude());
					route.put("lngFrom", getFromLongitude());
					route.put("latTo", getToLatitude());
					route.put("lngTo", getToLongitude());
					route.put("cityFrom", getFromCity());
					route.put("cityTo", getToCity());
					route.put("locationFrom", getLocationFrom());
					route.put("locationTo", getLocationTo());
					route.put("user", ParseUser.getCurrentUser());
					
					if (prefs.contains("facebookObjectId")) {
						route.put("authorName", userFacebookName());
						route.put("userProfileImage", 
								"https://graph.facebook.com/"+userFacebookId()+"/picture?height=73&type=normal&width=73");
					} else if (prefs.contains("twitterObjectId")) {
						route.put("authorName", userTwitterName());
						route.put("userProfileImage", userTwitterImage());
					}
					
					route.saveInBackground();
					
					Intent routeList = new Intent(AddRoute.this, RouteList.class);
					startActivity(routeList);
				}
			}
		});
		
		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		addDateImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		addTimeImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);	
			}
		});
	}
	
	private void buildDialog(String hint, final TextView textView, boolean numbers, boolean map) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.custom_dialog);
		
		if (map == true) {
			
		}
		
		final EditText input = (EditText) dialog.findViewById(R.id.dialog_edit_text);
		input.setHint(hint);
		
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		if (numbers == true) {
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		
		input.requestFocus();
		
		Button dialogButtonAdd = (Button) dialog.findViewById(R.id.dialog_button_add);
		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);
		
		dialogButtonAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textView.setText(input.getText().toString());
				dialog.dismiss();
			}
		});
		
		dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	private String userFacebookId() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					return userProfile.getString("facebookId").toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}
	
	private String userFacebookName() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("name") != null) {
					return userProfile.getString("name").toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}
	
	private String userTwitterName() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("twitterName") != null) {
					return userProfile.getString("twitterName").toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}
	
	private String userTwitterImage() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("twitterImage") != null) {
					return userProfile.getString("twitterImage").toString();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
        switch (id) {
        case TIME_DIALOG_ID:
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(this, timePickerListener, hour, minute,
                    false);
            
        case DATE_DIALOG_ID:
        	year = c.get(Calendar.YEAR);
        	month = c.get(Calendar.MONTH);
        	day = c.get(Calendar.DAY_OF_MONTH);
        	return new DatePickerDialog(this, datePickerListener, year, month, day);
 
        }
        return null;
	}
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        
		 
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            setDepartureTime(String.valueOf(convert(hourOfDay) + ":" + convert(minutes)));
            timeText.setText(String.valueOf(convert(hourOfDay)) + ":" + convert(minutes));
         }
 
    };
    
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setDepartureDate(String.valueOf(dayOfMonth + "." + convert((monthOfYear + 1)) + "." + year));
			dateText.setText(String.valueOf(dayOfMonth + "." + convert((monthOfYear + 1)) + "." + year));
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_route, menu);
		return true;
	}
	
	private void initializeFields() {
		
		addRoteFromImage = (ImageView) findViewById(R.id.add_from_image);
		addDateImage = (ImageView) findViewById(R.id.add_date_image);
		addSeatsImage = (ImageView) findViewById(R.id.add_seats_image);
		addNotesImage = (ImageView) findViewById(R.id.add_notes_image);
		addRouteToImage = (ImageView) findViewById(R.id.add_to_image);
		addTimeImage = (ImageView) findViewById(R.id.add_time_image);
		addPriceImage = (ImageView) findViewById(R.id.add_price_image);
		addStopsImage = (ImageView) findViewById(R.id.add_stops_image);
		
		dateText = (TextView) findViewById(R.id.add_date_result);
		timeText = (TextView) findViewById(R.id.add_time_result);
		priceText = (EditText) findViewById(R.id.price_edittext);
		seatsText = (EditText) findViewById(R.id.seats_eidttext);
		
		routeFromText = (AutoCompleteTextView) findViewById(R.id.route_from_edittext);
		routeFromText.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		routeFromText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				getAddress = new GetAddress(AddRoute.this, str, true).execute();
				
		        
			}
		});
		
		routeToText = (AutoCompleteTextView) findViewById(R.id.route_to_edittext);
		routeToText.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		routeToText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				getAddress = new GetAddress(AddRoute.this, str, false).execute();
			}
		});
		
	}
	
	private String getDepartureTime() {
		return departureTime;
	}
	
	private String getDepartureDate() {
		return departureDate;
	}
	
	private String setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
		return this.departureTime;
	}
	
	private String setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
		return this.departureDate;
	}
	
	private static String convert(int c) {
		if (c > 9) {
			return String.valueOf(c);
		} else {
			return "0" + String.valueOf(c);
		}
	}

	public double getFromLatitude() {
		return fromLat;
	}

	public void setFromLatitude(double lat) {
		this.fromLat = lat;
	}

	public double getFromLongitude() {
		return fromLng;
	}

	public void setFromLongitude(double lng) {
		this.fromLng = lng;
	}

	public double getToLatitude() {
		return toLat;
	}

	public void setToLatitude(double toLat) {
		this.toLat = toLat;
	}

	public double getToLongitude() {
		return toLng;
	}

	public void setToLongitude(double toLng) {
		this.toLng = toLng;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public String getLocationFrom() {
		return locationFrom;
	}

	public void setLocationFrom(String locationFrom) {
		this.locationFrom = locationFrom;
	}

	public String getLocationTo() {
		return locationTo;
	}

	public void setLocationTo(String locationTo) {
		this.locationTo = locationTo;
	}

}
