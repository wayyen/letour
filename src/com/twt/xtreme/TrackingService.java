package com.twt.xtreme;

import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

public class TrackingService extends Service {

	final static String T = "TrackingService";
	
	private boolean running = true;
	private Context ctx;
	// private DBProvider dbp;
	private SharedPreferences pref;
	private Random rand;
	private PowerManager.WakeLock wl;
	LocationManager locationManager;
	LocationListener locationListener;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wl.release();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, T);
		wl.setReferenceCounted(false);
		wl.acquire();
		return START_STICKY;
	}
	
	private void setupLocationManager() {
		
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    @Override
			public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      Log.d(T, "location update: " + location.toString());
		      RentalRecord r = Util.getRentalRecordFromSharedPref(getApplicationContext());
		      if (r != null ) {
		    	  RentalLocation rl = new RentalLocation();
		    	  rl.setDevice_id(r.getDeviceId());
		    	  rl.setLatitude(location.getLatitude());
		    	  rl.setLongitude(location.getLongitude());
		    	  rl.setAccuracy(location.getAccuracy());
		    	  HttpUtil.updateLocationTrack(getApplicationContext(), rl);
		      }
		    }

		    @Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    @Override
			public void onProviderEnabled(String provider) {}

		    @Override
			public void onProviderDisabled(String provider) {}
		  };
	}
	
	private void startLocationTracking() {
		// Register the listener with the Location Manager to receive location updates	
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
		// Criteria c = new Criteria();
		// locationManager.requestSingleUpdate(c, locationListener, null);
	}
	
	/*
	public void doLocationAction(View v) {

		Location loc = ( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ?
					locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) :
						locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) );
		if (loc != null) {
			Log.d(T, loc.toString());
			tStatus.setText(String.format("Lat: %f, Long: %f, Accuracy: %f",
					loc.getLatitude(), loc.getLongitude(), loc.getAccuracy()));
		} else {
			Log.e(T, "Can't get any location");
		}
		Geocoder g = new Geocoder(this);
		try {
			List<Address> addrlist = g.getFromLocation(loc.getLatitude(),
					loc.getLongitude(), 5);
			for (Address addr : addrlist) {
				Log.d(T, "addr: " + addr.toString());
			}
		} catch (Exception e) {
			Log.e(T, "exception in geocoding", e);
		}
		
	}
	*/
}
