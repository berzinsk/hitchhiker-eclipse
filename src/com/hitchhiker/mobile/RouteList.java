package com.hitchhiker.mobile;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hitchhiker.mobile.asynctasks.GetRouteList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class RouteList extends Activity {
	
	private PullToRefreshListView pullToRefreshView;
	public AsyncTask<Void, Void, Void> getRouteList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_list);
		
		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.list_list);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getRouteList();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_list, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add_route:
				Intent add_route = new Intent(RouteList.this, AddRoute.class);
				startActivity(add_route);
				return true;
				
	    		
			default:
	    		return super.onOptionsItemSelected(item);
		}
	}
	
	private void getRouteList() {
		getRouteList = new GetRouteList(this, true).execute();
	}
	
	public void showToast() {
		pullToRefreshView.onRefreshComplete();
		Toast.makeText(getApplicationContext(), "Refresh kinda works", Toast.LENGTH_LONG).show();
	}

}
