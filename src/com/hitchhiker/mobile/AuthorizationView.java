package com.hitchhiker.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

public class AuthorizationView extends Activity {
	
	Button authorize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authorization);
//		Parse.initialize(this, "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK", "2Q2jMF3PlbIgvjuRbfA3aAbj0x9CDqyO3UcOcfCq");
		authorize = (Button) findViewById(R.id.authorization);
		
//		String user = "Karlis Berzins";
//		String routeFrom = "Lielvarde";
//		String routeTo = "Valmiera";
//		Long distance = 104L;
//		Double price = 2.50;
//		String departureTime = "13:35";
//		
//		ParseObject route = new ParseObject("Routes");
//		route.put("user", user);
//		route.put("routeFrom", routeFrom);
//		route.put("routeTo", routeTo);
//		route.put("distance", distance);
//		route.put("price", price);
//		route.put("departureTime", departureTime);
//		route.saveInBackground();
		
		authorize.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent routeList = new Intent(AuthorizationView.this, RouteList.class);
				startActivity(routeList);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authorization, menu);
		return true;
	}

}
