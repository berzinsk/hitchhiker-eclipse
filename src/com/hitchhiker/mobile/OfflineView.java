package com.hitchhiker.mobile;

import com.hitchhiker.mobile.asynctasks.TestNetwork;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class OfflineView extends Activity {
	
	public AsyncTask<Void, Void, Void> testNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_view);
		
		testNetwork = new TestNetwork(this).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.offline_view, menu);
		return true;
	}

}
