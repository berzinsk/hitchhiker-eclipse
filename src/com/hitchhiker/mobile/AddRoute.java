package com.hitchhiker.mobile;

import java.util.Calendar;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
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
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AddRoute extends Activity {
	static final int TIME_DIALOG_ID = 1000;
	static final int DATE_DIALOG_ID = 999;
	
	final Context context = this;
	
	TextView seats;
	String departureTime;
	String departureDate;
	
	ImageView addRoteFromImage;
	ImageView addDateImage;
	ImageView addSeatsImage;
	ImageView addNotesImage;
	ImageView addRouteToImage;
	ImageView addTimeImage;
	ImageView addPriceImage;
	ImageView addStopsImage;
	
	TextView routeFromText;
	TextView dateText;
	TextView seatsText;
	TextView notesText;
	TextView routeToText;
	TextView timeText;
	TextView priceText;
	TextView stopsText;
	
	Button postButton;
	Button cancelButton;
	
	private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_route);
//		Parse.initialize(this, "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK", "2Q2jMF3PlbIgvjuRbfA3aAbj0x9CDqyO3UcOcfCq");
		initializeFields();
		
		postButton = (Button) findViewById(R.id.button_post);
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
					route.put("distance", 104L);
					route.put("departureTime", getDepartureTime());
					route.put("departureDate", getDepartureDate());
					route.put("price", Double.valueOf(priceText.getText().toString()));
					route.put("authorId", userFacebookId());
					route.put("authorName", userFacebookName());
					route.put("availableSeats", Integer.parseInt(seatsText.getText().toString()));
					
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
	
	private void imageOnClickListener(ImageView imageView, final String hint, final TextView textView,
			final Boolean numbers, final Boolean map) {
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buildDialog(hint, textView, numbers, map);
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
		
		routeFromText = (TextView) findViewById(R.id.add_from_result);
		dateText = (TextView) findViewById(R.id.add_date_result);
		seatsText = (TextView) findViewById(R.id.add_seats_result);
		notesText = (TextView) findViewById(R.id.add_notes_result);
		routeToText = (TextView) findViewById(R.id.add_to_result);
		timeText = (TextView) findViewById(R.id.add_time_result);
		priceText = (TextView) findViewById(R.id.add_price_result);
		stopsText = (TextView) findViewById(R.id.add_stops_result);
		
		imageOnClickListener(addRoteFromImage, "Add route from", routeFromText, false, true);
		imageOnClickListener(addSeatsImage, "Add seats avaailable", seatsText, true, false);
		imageOnClickListener(addNotesImage, "Add notes", notesText, false, false);
		imageOnClickListener(addRouteToImage, "Add sroute to", routeToText, false, true);
		imageOnClickListener(addPriceImage, "Add price", priceText, true, false);
		imageOnClickListener(addStopsImage, "Add stops if any", stopsText, false, false);
		
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
		if (c > 10) {
			return String.valueOf(c);
		} else {
			return "0" + String.valueOf(c);
		}
	}

}
