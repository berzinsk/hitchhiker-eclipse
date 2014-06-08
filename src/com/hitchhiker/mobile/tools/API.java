package com.hitchhiker.mobile.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.fo;
import com.hitchhiker.mobile.objects.Route;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.entity.mime.content.StringBody;

import android.app.Activity;
import android.util.Log;

public class API {
	private static final String APPLICATION_ID = "IfqZO5qsBYS8vsGh0XwqKbpuhndnIihhrOhgVTxK";
	private static final String REST_API_KEY = "rKZhqzxRzLRihEKUR42JE0INPiYtRux2OUnK7MnK";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	
	private static final String API_KEY = "AIzaSyC93AujG3dwtXQbXWoq4QTigb6xHN1vsLc";
	
	private String version;
	private Activity activity;
	
	private DefaultHttpClient httpclient;
	private HttpGet httpget;
	private HttpPost httppost;
	
	private double lat;
	private double lng;
	private String city;
	private String streetNumber;
	private String street;
	private String twitterName;
	private String twitterImage;
	
	
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
	
	public API() {
	}
	
	public double location(String address) {
		setStreet(null);
		setStreetNumber(null);
		setCity(null);
		
		String formatedAddress = address.replace(" ", "%20");
		String formatedAddress2 = formatedAddress.replaceAll("\"|\"", "");
		
		String url = "http://maps.google.com/maps/api/geocode/json?address=" + formatedAddress2 + "&sensor=false";
		Log.d("Addresss calll", url);
		JSONObject object;
		try {
			object = getJSONObject(url, false);
			
			JSONArray data = null;
			if (object.has("results")) {
				data = object.getJSONArray("results");
			}
			
			for (int i = 0; i < data.length(); i++) {
				JSONObject location = data.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
				setLatitude(location.getDouble("lat"));
				setLongitude(location.getDouble("lng"));
				
				JSONArray city = data.getJSONObject(i).getJSONArray("address_components");
				for (int j = 0; j < city.length(); j++) {
					Log.d("TYPES", city.getString(j));
					JSONObject types = city.getJSONObject(j);
					
					if (types.getString("types").contains("locality")) {
						setCity(city.getJSONObject(j).getString("short_name"));
						Log.d("LOCALITY", city.getJSONObject(j).getString("short_name"));
					}
					
					if (types.getString("types").contains("street_number")) {
						setStreetNumber(city.getJSONObject(j).getString("short_name"));
					}
					
					if (types.getString("types").contains("route")) {
						setStreet(city.getJSONObject(j).getString("short_name"));
						Log.d("contains route", "contains route");
					} else if (types.getString("types").contains("establishment")) {
						setStreet(city.getJSONObject(j).getString("short_name"));
						Log.d("contains establishment", "contains establishment");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getCity(String address) {
		
		return null;
	}
	
	public List<Route> getRouteList() {
		
		Route route;
		List<Route> routes = new ArrayList<Route>();
		String url = "https://api.parse.com/1/classes/Routes?order=-createdAt";
		
		JSONObject object = null;
		try {
			object = getJSONObject(url, false);
			
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
						data.getJSONObject(i).getString("cityFrom"),
						data.getJSONObject(i).getString("cityTo"),
						data.getJSONObject(i).getString("userProfileImage"),
						data.getJSONObject(i).getString("authorName"));
				routes.add(route);
			}
		} catch (Exception e) {
			return null;
		}
		return routes;
	}
	
	public void getTwitterCredentials(String screenName) {
		String url = "https://api.twitter.com/1.1/users/show.json?screen_name=" + screenName;
		
		JSONObject data;
		
		try {
			data = getJSONObject(url, true);
			
			if (data.has("name")) {
				setTwitterName(data.getString("name"));
			}
			
			if (data.has("profile_image_url_https")) {
				setTwitterImage(data.getString("profile_image_url_https"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Route getRouteDetails(String id) {
		
		Route route = new Route();
		String url = "https://api.parse.com/1/classes/Routes/" + id;
		
		JSONObject data;
		try {
			data = getJSONObject(url, false);
			
			if (data == null) {
				return null;
			}
			
			if (data.has("errors")) {
				return null;
			}
			
			Log.d("Route detail JSON DATA RECEIVED", data.toString());
			
			if (data.has("user")) {
				route.setUserId(data.getJSONObject("user").getString("objectId"));
			}
			
			if (data.has("authorName")) {
				route.setAuthorName(data.getString("authorName"));
			}
			
			if (data.has("routeFrom")) {
				route.setRouteFrom(data.getString("locationFrom"));
			}
			
			if (data.has("routeTo")) {
				route.setRouteTo(data.getString("locationTo"));
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
			
			if (data.has("latFrom")) {
				route.setLatitudeFrom(data.getDouble("latFrom"));
			}
			
			if (data.has("latTo")) {
				route.setLatitudeTo(data.getDouble("latTo"));
			}
			
			if (data.has("lngFrom")) {
				route.setLongitudeFrom(data.getDouble("lngFrom"));
			}
			
			if (data.has("lngTo")) {
				route.setLongitudeTo(data.getDouble("lngTo"));
			}
			
			List<String> paidPassengers = new ArrayList<String>();
			JSONArray paidObject = null;
			if (data.has("paidPassengers")) {
				paidObject = data.getJSONArray("paidPassengers");
				Log.d("has paid passengers", "has paid passengers");
				for (int i = 0; i < paidObject.length(); i++) {
					paidPassengers.add(paidObject.get(i).toString());
				}
				if (paidPassengers != null) {
					Log.d("irrrrrrrrrrrrr", "irrrrrrrrrr");
					route.setPaidPassengers(paidPassengers);
				}
			}
			
			List<String> passengers = new ArrayList<String>();
			JSONArray object = null;
			if (data.has("passengers")) {
				object = data.getJSONArray("passengers");
				for (int i = 0; i < object.length(); i++) {
					passengers.add(object.get(i).toString());
				}
				if (passengers != null) {
					route.setPassengers(passengers);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return route;
	}
	
	private JSONObject getJSONObject(String url, boolean twitter) {
		String json = null;
		if (twitter) {
			json = this.getTwitterInfo(url);
		} else {
			json = this.getData(url);
		}
		
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
	
	public String getPolyData(String url) {
	
		Log.d("URRRLLLLL", url);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			// create DefaultHpptClient and HttpGet
			httpclient = new DefaultHttpClient(); 
			httpget = new HttpGet(url);
			
			HttpEntity entity = null;
			try {
				HttpResponse response = httpclient.execute(httpget);
				entity = response.getEntity();
			} catch (Exception e) {
				e.printStackTrace();
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
					e.printStackTrace();
				} finally {
					inputstream.close();
				}
				
				httpclient.getConnectionManager().shutdown();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = stringBuilder.toString().trim();
//		Log.d("RESULT FROM GOOGLE", result);
		return result;
	}
	
	public String getTwitterInfo(String url) {
		StringBuilder stringBuilder = new StringBuilder();
		Log.d("URLLLL", url);
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet verifyGet = new HttpGet(url);
			ParseTwitterUtils.getTwitter().signRequest(verifyGet);
			HttpEntity entity = null;
			try {
				HttpResponse response = client.execute(verifyGet);
				entity = response.getEntity();
			} catch (Exception e) {
				e.printStackTrace();
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
					e.printStackTrace();
				} finally {
					inputstream.close();
				}
				
				client.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = stringBuilder.toString().trim();
		Log.d("GET DATAAAA RETURNED", result);
		return result;
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
				e.printStackTrace();
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
					e.printStackTrace();
				} finally {
					inputstream.close();
				}
				
				httpclient.getConnectionManager().shutdown();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = stringBuilder.toString().trim();
		Log.d("GET DATAAAA RETURNED", result);
		return result;
	}
	
	/**
	 * Send POST request to given url
	 * 
	 * @param url				Request url
	 * @return String result	Data from url request
	 */
	
	private String postData(String url) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost();
			
			httppost.setHeader("X-Parse-Application-Id", APPLICATION_ID);
			httppost.setHeader("X-Parse-REST-API-Key", REST_API_KEY);
			
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
					e.printStackTrace();
				} finally {
					inputstream.close();
				}
				
				httpclient.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = stringBuilder.toString().trim();
		return result;
	}
	
	public ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;
		
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + API_KEY);
			sb.append("&components=country:lv");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			
			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			
			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		try {
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
			
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}

	public double getLatitude() {
		return lat;
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public double getLongitude() {
		return lng;
	}

	public void setLongitude(double lng) {
		this.lng = lng;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTwitterName() {
		return twitterName;
	}

	public void setTwitterName(String twitterName) {
		this.twitterName = twitterName;
	}

	public String getTwitterImage() {
		return twitterImage;
	}

	public void setTwitterImage(String twitterImage) {
		this.twitterImage = twitterImage;
	}
}