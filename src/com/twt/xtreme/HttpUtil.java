package com.twt.xtreme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

public class HttpUtil {
	private static final String TAG = "HttpUtil";
	
	public static int pickupBike(Context ctx, RentalRecord rec ) {
		int resp_code = -1;
		HttpResult result = null;
		try {
			
			DefaultHttpClient c = new DefaultHttpClient(getTimeoutParams());
			HttpPost req = new HttpPost(ctx.getText(R.string.pickup_bike_url).toString());
			req.setHeader("Content-Type", "application/json; charset=UTF-8");
			Gson gson = new Gson();
			String poststr = gson.toJson(rec);
			StringEntity se = new StringEntity(poststr);
			
			req.setEntity(se);
			HttpResponse resp = c.execute(req);
			String result_json = convertStreamToString(resp.getEntity().getContent());
			resp_code = resp.getStatusLine().getStatusCode();
			result = gson.fromJson(result_json, HttpResult.class);
			Log.d(TAG, "HTTP Post Result: "+resp_code+" Entity Result: " + result);
		} catch ( Exception e ) {
			Log.e(TAG, "Exception when posting pickupBike HTTP", e);
		}
		return (result != null ? result.getSuccess() : -1);
		
	}

	public static int dropOffBike(Context ctx, RentalRecord rec ) {
		int resp_code = -1;
		HttpResult result = null;
		try {
			
			DefaultHttpClient c = new DefaultHttpClient(getTimeoutParams());
			HttpPost req = new HttpPost(ctx.getText(R.string.dropoff_bike_url).toString());
			req.setHeader("Content-Type", "application/json; charset=UTF-8");
			Gson gson = new Gson();
			String poststr = gson.toJson(rec);
			StringEntity se = new StringEntity(poststr);
			
			req.setEntity(se);
			HttpResponse resp = c.execute(req);
			String result_json = convertStreamToString(resp.getEntity().getContent());
			resp_code = resp.getStatusLine().getStatusCode();
			result = gson.fromJson(result_json, HttpResult.class);
			Log.d(TAG, "HTTP Post Result: "+resp_code+" Entity Result: " + result);
		} catch ( Exception e ) {
			Log.e(TAG, "Exception posting dropOffBike HTTP", e);
		}
		return (result != null ? result.getSuccess() : -1);
	}
	
	public static int updateLocationTrack(Context ctx, RentalLocation rl) {
		int resp_code = -1;
		HttpResult result = null;
		try {
			
			DefaultHttpClient c = new DefaultHttpClient(getTimeoutParams());
			HttpPost req = new HttpPost(ctx.getText(R.string.tracking_url).toString());
			req.setHeader("Content-Type", "application/json; charset=UTF-8");
			Gson gson = new Gson();
			String poststr = gson.toJson(rl);
			StringEntity se = new StringEntity(poststr);
			
			req.setEntity(se);
			HttpResponse resp = c.execute(req);
			String result_json = convertStreamToString(resp.getEntity().getContent());
			resp_code = resp.getStatusLine().getStatusCode();
			result = gson.fromJson(result_json, HttpResult.class);
			Log.d(TAG, "HTTP Post Result: "+resp_code+" Entity Result: " + result);
		} catch ( Exception e ) {
			Log.e(TAG, "Exception when posting updateLocationTrack HTTP", e);
		}
		return (result != null ? result.getSuccess() : -1);
	}
	
	public static HttpParams getTimeoutParams() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		return httpParameters;
	}

	   public static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }

}

