package com.hitchhiker.mobile.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hitchhiker.mobile.objects.Route;
import com.parse.ParseUser;

import android.app.Activity;
import android.util.Log;

public class API {
	private static final String APPLICATION_ID = "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK";
	private static final String REST_API_KEY = "rKZhqzxRzLRihEKUR42JE0INPiYtRux2OUnK7MnK";
	
	private String version;
	private Activity activity;
	
	private DefaultHttpClient httpclient;
	private HttpGet httpget;
	private HttpPost httppost;
	
	
	/**
	 * API constructor
	 * 
	 * @param Activity activity		Given activity
	 * @param String version		Version of app
	 */
	
	public API(Activity activity, String version) {
		this.activity = activity;
		this.version = version;
	}
	
	public List<Route> getRouteList() {
		
		Route route;
		List<Route> routes = new ArrayList<Route>();
		String url = "https://api.parse.com/1/classes/Routes/";
		
		JSONObject object = null;
		try {
			object = getJSONObject(url);
			
			if (object == null) {
				return null;
			}
			
			if (object != null && object.has("errors")) {
				return null;
			}
			
			JSONArray data = null;
			if (object.has("results")) {
				data = object.getJSONArray("results");
			}
			
			if (data == null) {
				return null;
			}
			
			for (int i = 0; i < data.length(); i++) {
				route = new Route(data.getJSONObject(i).getString("objectId"),
						data.getJSONObject(i).getString("routeFrom"),
						data.getJSONObject(i).getString("routeTo"),
						data.getJSONObject(i).getString("authorId"),
						data.getJSONObject(i).getString("authorName"));
				routes.add(route);
			}
		} catch (Exception e) {
			return null;
		}
		return routes;
	}
	
	public Route getRouteDetails(String id) {
		
		Route route = new Route();
		String url = "https://api.parse.com/1/classes/Routes/" + id;
		
		JSONObject data;
		try {
			data = getJSONObject(url);
			
			if (data == null) {
				return null;
			}
			
			if (data.has("errors")) {
				return null;
			}
			
			if (data.has("authorId")) {
				route.setAuthorId(data.getString("authorId"));
			}
			
			if (data.has("authorName")) {
				route.setAuthorName(data.getString("authorName"));
			}
			
			if (data.has("routeFrom")) {
				route.setRouteFrom(data.getString("routeFrom"));
			}
			
			if (data.has("routeTo")) {
				route.setRouteTo(data.getString("routeTo"));
			}
			
			if (data.has("distance")) {
				route.setDistance(data.getLong("distance"));
			}
			
			if (data.has("price")) {
				route.setPrice(data.getDouble("price"));
			}
			
			if (data.has("departureTime")) {
				route.setDepartureTime(data.getString("departureTime"));
			}
			
			if (data.has("departureDate")) {
				route.setDepartureDate(data.getString("departureDate"));
			}
			
			if (data.has("availableSeats")) {
				route.setAvailableSeats(data.getInt("availableSeats"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return route;
	}
	
	private JSONObject getJSONObject(String url) {
		
		String json = this.getData(url);
		
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			return null;
		}
	}
	
	private JSONObject postJSONObject(String url) {
		
		String json = this.postData(url);
		
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getData(String url) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			// create DefaultHpptClient and HttpGet
			httpclient = new DefaultHttpClient(); 
			httpget = new HttpGet(url);
			
			httpget.setHeader("X-Parse-Application-Id", APPLICATION_ID);
			httpget.setHeader("X-Parse-REST-API-Key", REST_API_KEY);
			
			HttpEntity entity = null;
			try {
				HttpResponse response = httpclient.execute(httpget);
				entity = response.getEntity();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			if (entity != null) {
				InputStream inputstream = null;
				inputstream = entity.getContent();
				
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line + "\n");
					}
					
					reader.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					inputstream.close();
				}
				
				httpclient.getConnectionManager().shutdown();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String result = stringBuilder.toString().trim();
		return result;
	}
	
	/**
	 * Send POST request to given url
	 * 
	 * @param url				Request url
	 * @return String result	Data from url request
	 */
	
	public String postData(String url) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost();
			
			httpget.setHeader("X-Parse-Application-Id", APPLICATION_ID);
			httpget.setHeader("X-Parse-REST-API-Key", REST_API_KEY);
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream inputstream = null;
				inputstream = entity.getContent();
				
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line + "\n");
					}
					reader.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					inputstream.close();
				}
				
				httpclient.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String result = stringBuilder.toString().trim();
		return result;
	}
}