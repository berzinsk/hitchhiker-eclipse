package com.hitchhiker.mobile.asynctasks;

import com.hitchhiker.mobile.OfflineView;
import com.hitchhiker.mobile.RouteList;
import com.hitchhiker.mobile.tools.Logger;
import com.hitchhiker.mobile.tools.Network;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

public class TestNetwork extends AsyncTask<Void, Void, Void> {
	
	private Activity activity;
	
	private static final Logger log = new Logger(TestNetwork.class);
	
	public TestNetwork(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		while (true) {
			
			if (isCancelled()) break;
			
			if (!Network.test(activity)) {
				activity.startActivity(new Intent(activity, OfflineView.class));
				break;
			} else if (Network.test(activity) && (activity instanceof OfflineView)) {
				activity.startActivity(new Intent(activity, RouteList.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("TestNetwork Thread.sleep(1000) failed.");
			}
		}
		
		return null;
	}
	
}