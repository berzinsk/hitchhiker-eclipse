package com.hitchhiker.mobile.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.facebook.widget.ProfilePictureView;
import com.hitchhiker.mobile.R;
import com.hitchhiker.mobile.asynctasks.GetUserPicture;
import com.hitchhiker.mobile.objects.Route;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
			tag.authorPicture = (ImageView) view.findViewById(R.id.row_profilePicture);
			tag.authorName = (TextView) view.findViewById(R.id.row_name);
			Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/CharisSILI.ttf");
			tag.authorName.setTypeface(font);
			tag.routeFrom = (TextView) view.findViewById(R.id.row_routeFrom);
			tag.routeFrom.setTypeface(font);
			tag.routeTo = (TextView) view.findViewById(R.id.row_routeTo);
			tag.routeTo.setTypeface(font);
			view.setTag(tag);
		} else {
			tag = (Tag) view.getTag();
		}
		
		// Should add to check device screen to display proper size profile picture
		new GetUserPicture(tag.authorPicture).execute("http://graph.facebook.com/"+routes.get(index).getAuthorId()+"/picture?height=100&type=normal&width=100");
		tag.authorName.setText(routes.get(index).getAuthorName());
		tag.routeFrom.setText(routes.get(index).getRouteFrom());
		tag.routeTo.setText(routes.get(index).getRouteTo());
		return view;
	}
	
	private static class Tag {
		ImageView authorPicture;
		TextView authorName;
		TextView routeFrom;
		TextView routeTo;
	}
	
}