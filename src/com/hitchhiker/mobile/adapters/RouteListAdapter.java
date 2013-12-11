package com.hitchhiker.mobile.adapters;

import java.util.List;

import com.facebook.widget.ProfilePictureView;
import com.hitchhiker.mobile.R;
import com.hitchhiker.mobile.objects.Route;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RouteListAdapter extends BaseAdapter {
	
	Activity activity;
	List<Route> routes;
	Context context;
	
	// the constructor
	public RouteListAdapter(Activity activity, Context context, List<Route> routes) {
		super();
		
		this.activity = activity;
		this.routes = routes;
		this.context = context;
	}

	@Override
	public int getCount() {
		return routes.size();
	}

	@Override
	public Object getItem(int index) {
		return routes.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 3;
	}

	@Override
	public View getView(int index, View view, ViewGroup viewgroup) {
		Tag tag;
		LayoutInflater inflater = activity.getLayoutInflater();
		
		if (view == null) {
			tag = new Tag();
			view = inflater.inflate(R.layout.row_01, null);
			tag.authorPicture = (ProfilePictureView) view.findViewById(R.id.row_profilePicture);
			tag.authorName = (TextView) view.findViewById(R.id.row_name);
			tag.routeFrom = (TextView) view.findViewById(R.id.row_routeFrom);
			tag.routeTo = (TextView) view.findViewById(R.id.row_routeTo);
			view.setTag(tag);
		} else {
			tag = (Tag) view.getTag();
		}
		
		tag.authorPicture.setProfileId(routes.get(index).getAuthorId());
		tag.authorName.setText(routes.get(index).getAuthorName());
		tag.routeFrom.setText(routes.get(index).getRouteFrom());
		tag.routeTo.setText(routes.get(index).getRouteTo());
		return view;
	}
	
	private static class Tag {
		ProfilePictureView authorPicture;
		TextView authorName;
		TextView routeFrom;
		TextView routeTo;
	}
	
}