package com.hitchhiker.mobile;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hitchhiker.mobile.adapters.RouteListAdapter;
import com.hitchhiker.mobile.asynctasks.GetRouteList;
import com.hitchhiker.mobile.objects.Route;
import com.hitchhiker.mobile.tools.API;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class RouteList extends Activity {
	
	public API api;
	
	private PullToRefreshListView pullToRefreshView;
	public AsyncTask<Void, Void, Void> getRouteList;
	public List<Route> routes = new ArrayList<Route>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			api = new API(this, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
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
	
	public void updateUi(List<Route> routes) {
		Log.d("Points", String.valueOf(routes));
		pullToRefreshView.onRefreshComplete();
		if (routes == null) {
			findViewById(R.id.list_list).setVisibility(View.GONE);
		} else {
			findViewById(R.id.list_list).setVisibility(View.VISIBLE);
		}
		
		final PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.list_list);
		list.setAdapter(new RouteListAdapter(this, getApplicationContext(), routes));
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				chooseRoute(position);
			}
		});
	}
	
	public void chooseRoute(int position) {
		position--;
		Route route = routes.get(position);
		((Hitchhiker) getApplication()).setRoute(routes.get(position));
		startActivity(new Intent(RouteList.this, RouteView.class));
	}

}
