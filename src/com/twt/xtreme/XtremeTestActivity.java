package com.twt.xtreme;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class XtremeTestActivity extends Activity {

	final static String T = "XtremeActivity";
	final static int TAKE_PICTURE = 100;

	Button bAction;
	Button bLocationAction;
	TextView tStatus;
	LocationManager locationManager;
	LocationListener locationListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bAction = (Button) findViewById(R.id.btn_action);
		bLocationAction = (Button) findViewById(R.id.btn_location_action);
		tStatus = (TextView) findViewById(R.id.text_status);
		
		
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      Log.d(T, "location update: " + location.toString());
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Register the listener with the Location Manager to receive location updates	
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		Criteria c = new Criteria();
		locationManager.requestSingleUpdate(c, locationListener, null);
	}


	public void doAction(View v) {
		File file = new File(Environment.getExternalStorageDirectory() +"/"+
				"xtremetest.jpg");
		Log.d(T, "pic file: " + file.getAbsolutePath());
		Uri outputFileUri = Uri.fromFile(file);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, TAKE_PICTURE);
		
	}
	
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
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:
			switch (resultCode) {
			case Activity.RESULT_OK:
				Log.d(T, "picture saved");
				break;
			case Activity.RESULT_CANCELED:
				Log.d(T, "picture captured cancelled.");
				break;
			}
			
			break;
		}
	}

	
}
