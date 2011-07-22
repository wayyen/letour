package com.twt.xtreme;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
	
	String android_id;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bAction = (Button) findViewById(R.id.btn_action);
		bLocationAction = (Button) findViewById(R.id.btn_location_action);
		tStatus = (TextView) findViewById(R.id.text_status);
		android_id = android.provider.Settings.Secure.getString(getContentResolver(), 
				android.provider.Settings.Secure.ANDROID_ID);
		Log.d(T, "Android ID: "+android_id);
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
		    @Override
			public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      Log.d(T, "location update: " + location.toString());
		    }

		    @Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    @Override
			public void onProviderEnabled(String provider) {}

		    @Override
			public void onProviderDisabled(String provider) {}
		  };
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Register the listener with the Location Manager to receive location updates	
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		Criteria c = new Criteria();
		// locationManager.requestSingleUpdate(c, locationListener, null);
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
		Geocoder g = new Geocoder(this);
		try {
		List<Address> addrlist = g.getFromLocation(loc.getLatitude(), loc.getLongitude(), 5);
		for(Address addr : addrlist) {
			Log.d(T, "addr: "+addr.toString());
		}
		} catch (Exception e) {
			Log.e(T, "exception in geocoding", e);
		}
		
	}
	
	public void doPickupBikeAction(View v) {
		RentalRecord rec = new RentalRecord();
		rec.setDeviceId(android_id);
		rec.setPickup_slot_id("1"); // rec.setSlotId(slot_id); TODO: use NFC to get the slot id
		int result = HttpUtil.pickupBike(getApplicationContext(), rec);
		if (result == HttpResult.STATUS_OK) {
			Util.setRentalRecordToSharedPref(getApplicationContext(), rec);
			tStatus.setText("Bike picked up successfully.");
			Log.d(T, "Bike picked up successfuly");
		} else {
			tStatus.setText("No bike available at slot:"+rec.getPickup_slot_id());
			Log.d(T, "No bike available at slot:"+rec.getPickup_slot_id());
		}
	}
	
	public void doDropOffBikeAction(View v) {
		RentalRecord rec = Util.getRentalRecordFromSharedPref(getApplicationContext());
		rec.setDropoff_slot_id("2");
		int result = HttpUtil.dropOffBike(getApplicationContext(), rec);
		if (result == HttpResult.STATUS_OK) {
			Util.clearRentalRecordFromSharedPref(getApplicationContext());
			tStatus.setText("Bike dropped off successfully.");
			Log.d(T, "Bike dropped off successfully");
		} else {
			tStatus.setText("Slot "+rec.getDropoff_slot_id()+" occupied.");
			Log.d(T, "Slot "+rec.getDropoff_slot_id()+" occupied.");
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

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		NdefMessage[] nmsgs = getNdefMessages(intent);
		for (NdefMessage nmsg : nmsgs) {
			Log.d(T,"Got tag content: "+nmsg.toString());
		}
	}
	
	NdefMessage[] getNdefMessages(Intent intent) {
	    // Parse the intent
	    NdefMessage[] msgs = null;
	    Log.d(T, "NDEF discovered!");
	    String action = intent.getAction();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        if (rawMsgs != null) {
	            msgs = new NdefMessage[rawMsgs.length];
	            for (int i = 0; i < rawMsgs.length; i++) {
	                msgs[i] = (NdefMessage) rawMsgs[i];
	            }
	        }
	        else {
	        // Unknown tag type
	            byte[] empty = new byte[] {};
	            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
	            NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
	            msgs = new NdefMessage[] {msg};
	        }
	    }        
	    else {
	        Log.e(T, "Unknown intent " + intent);
	        finish();
	    }
	    return msgs;
	}
	
}
