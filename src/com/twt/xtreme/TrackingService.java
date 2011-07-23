package com.twt.xtreme;

import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class TrackingService extends Service {

	final static String T = "TrackingService";
	
	private boolean running = true;
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
		locationManager.removeUpdates(locationListener);
		wl.release();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, T);
		wl.setReferenceCounted(false);
		wl.acquire();
		setupLocationManager();
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

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
		// Criteria c = new Criteria();
		// locationManager.requestSingleUpdate(c, locationListener, null);
	}

}
