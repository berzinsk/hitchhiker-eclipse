package com.hitchhiker.mobile;

import java.util.Calendar;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beardedhen.bbutton.BootstrapButton;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class AddRoute extends Activity {
	static final int TIME_DIALOG_ID = 1000;
	static final int DATE_DIALOG_ID = 999;
	
	final Context context = this;
	
	TextView routeFrom;
	TextView routeTo;
	TextView price;
	TextView seats;
	String departureTime;
	String departureDate;
	ImageView addDateImage;
	ImageView addRoteFromImage;
	ImageView addRouteToImage;
	TextView routeFromText;
	TextView routeToText;
	
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
		
//		saveButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				ParseObject route = new ParseObject("Routes");
//				route.put("routeFrom", routeFrom.getText().toString());
//				route.put("routeTo", routeTo.getText().toString());
//				route.put("distance", 104L);
//				route.put("departureTime", getDepartureTime());
//				route.put("departureDate", getDepartureDate());
//				route.put("price", Double.valueOf(price.getText().toString()));
//				route.put("authorId", userFacebookId());
//				route.put("authorName", userFacebookName());
//				route.put("availableSeats", Integer.parseInt(seats.getText().toString()));
//				
//				route.saveInBackground();
//				
//				Intent routeList = new Intent(AddRoute.this, RouteList.class);
//				startActivity(routeList);
//			}
//		});
		
//		departureDateButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				showDialog(DATE_DIALOG_ID);
//			}
//		});
		
//		departureTimeButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				showDialog(TIME_DIALOG_ID);
//			}
//		});
		
		addRoteFromImage = (ImageView) findViewById(R.id.add_from_image);
		addRouteToImage = (ImageView) findViewById(R.id.add_to_image);
		routeFromText = (TextView) findViewById(R.id.add_from_result);
		routeToText = (TextView) findViewById(R.id.add_to_result);
		addRoteFromImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buildDialog("Add route from", routeFromText, false);
			}
		});
		
		addRouteToImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buildDialog("Add route to", routeToText, false);
			}
		});
		
		addDateImage = (ImageView) findViewById(R.id.add_date_image);
		addDateImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}
	
	private void buildDialog(String hint, final TextView textView, boolean numbers) {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.custom_dialog);
		
		final EditText input = (EditText) dialog.findViewById(R.id.dialog_edit_text);
		input.setHint(hint);
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
	
	private void buildAlertDialog(String title, final TextView textView, boolean numbers) {
		AlertDialog.Builder alert = new AlertDialog.Builder(AddRoute.this);
		
		alert.setTitle(title);
		
		final EditText input = new EditText(AddRoute.this);
		alert.setView(input);
		
		alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				textView.setText(input.getText().toString());
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		alert.show();
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
//            departureTimeButton.setText(String.valueOf(convert(hourOfDay) + ":" + convert(minutes)));
         }
 
    };
    
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setDepartureDate(String.valueOf(dayOfMonth + "." + monthOfYear + "." + year));
//			departureDateButton.setText(String.valueOf(dayOfMonth + "." + monthOfYear + "." + year));
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_route, menu);
		return true;
	}
	
	private void initializeFields() {
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
