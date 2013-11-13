package com.hitchhiker.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.beardedhen.bbutton.BootstrapButton;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class AddRoute extends Activity {
	EditText routeFrom;
	EditText routeTo;
	EditText price;
	EditText departureTime;
	BootstrapButton saveButton;

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
				route.put("price", Double.valueOf(price.getText().toString()));
				route.put("departureTime", departureTime.getText().toString());
				route.put("createdBy", ParseUser.getCurrentUser());
				
				route.saveInBackground();
				
				Intent routeList = new Intent(AddRoute.this, RouteList.class);
				startActivity(routeList);
			}
		});
	}

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
		departureTime = (EditText) findViewById(R.id.departure_time_field);
		saveButton = (BootstrapButton) findViewById(R.id.save_button);
	}

}
