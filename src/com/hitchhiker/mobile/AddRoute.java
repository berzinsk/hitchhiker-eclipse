package com.hitchhiker.mobile;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.beardedhen.bbutton.BootstrapButton;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class AddRoute extends Activity {
	static final int TIME_DIALOG_ID = 1111;
	
	EditText routeFrom;
	EditText routeTo;
	EditText price;
	Button departureTimeButton;
	BootstrapButton saveButton;
	String departureTime;
	
	private int hour;
    private int minute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_route);
//		Parse.initialize(this, "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK", "2Q2jMF3PlbIgvjuRbfA3aAbj0x9CDqyO3UcOcfCq");
		initializeFields();
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ParseObject route = new ParseObject("Routes");
				route.put("user", ParseUser.getCurrentUser().getUsername());
				route.put("routeFrom", routeFrom.getText().toString());
				route.put("routeTo", routeTo.getText().toString());
				route.put("distance", 104L);
				route.put("departureTime", getDepartureTime());
				route.put("price", Double.valueOf(price.getText().toString()));
				route.put("createdBy", ParseUser.getCurrentUser());
				route.put("availableSeats", 4);
				
				route.saveInBackground();
				
				Intent routeList = new Intent(AddRoute.this, RouteList.class);
				startActivity(routeList);
			}
		});
		
		departureTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
             
            // set time picker as current time
            return new TimePickerDialog(this, timePickerListener, hour, minute,
                    false);
 
        }
        return null;
	}
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        
		 
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            setDepartureTime(String.valueOf(convert(hourOfDay) + ":" + convert(minutes)));
            departureTimeButton.setText(String.valueOf(convert(hourOfDay) + ":" + convert(minutes)));
         }
 
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_route, menu);
		return true;
	}
	
	private void initializeFields() {
		routeFrom = (EditText) findViewById(R.id.driving_from_field);
		routeTo = (EditText) findViewById(R.id.driving_to_field);
		price = (EditText) findViewById(R.id.price_field);
		departureTimeButton = (Button) findViewById(R.id.time_select);
		saveButton = (BootstrapButton) findViewById(R.id.save_button);
	}
	
	private String getDepartureTime() {
		return departureTime;
	}
	
	private String setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
		return this.departureTime;
	}
	
	private static String convert(int c) {
		if (c > 10) {
			return String.valueOf(c);
		} else {
			return "0" + String.valueOf(c);
		}
	}

}
