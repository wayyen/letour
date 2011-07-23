package com.twt.xtreme;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RentBikeActivity extends Activity {

	final static String T = "RentBikeActivity";
	TextView tStatus;
	String android_id;
	String slot_id_json;
	TagData tag;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rentbike);
		tStatus = (TextView)findViewById(R.id.slot_status);
		android_id = android.provider.Settings.Secure.getString(getContentResolver(), 
				android.provider.Settings.Secure.ANDROID_ID);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		NdefMessage[] nmsgs = getNdefMessages(getIntent());
		for (NdefMessage nmsg : nmsgs) {
			NdefRecord[] nrecs = nmsg.getRecords();
			for (NdefRecord nrec : nrecs ) {
				slot_id_json = new String(nrec.getPayload(), 3, (nrec.getPayload().length-3));
				Log.d(T,"Got tag content: "+ slot_id_json);
				break;
			}
		}
		Gson g = new Gson();
		tag = g.fromJson(slot_id_json, TagData.class);
		tStatus.setText("You have just tapped slot ID: "+tag.slot_id );
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

	}
	
	public void doPickupBikeAction(View v) {
		RentalRecord rec = new RentalRecord();
		rec.setDeviceId(android_id);
		rec.setPickup_slot_id(tag.slot_id);
		int result = HttpUtil.pickupBike(getApplicationContext(), rec);
		if (result == HttpResult.STATUS_OK) {
			Util.setRentalRecordToSharedPref(getApplicationContext(), rec);
			tStatus.setText("Bike picked up successfully.");
			// start location tracking service
			Log.d(T, "Bike picked up successfuly");
		} else {
			tStatus.setText("No bike available at slot:"+rec.getPickup_slot_id());
			Log.d(T, "No bike available at slot:"+rec.getPickup_slot_id());
		}
	}
	
	public void doDropoffBikeAction(View v) {
		RentalRecord rec = Util.getRentalRecordFromSharedPref(getApplicationContext());
		if (rec != null) {
			rec.setDropoff_slot_id(tag.slot_id);
			int result = HttpUtil.dropOffBike(getApplicationContext(), rec);
			if (result == HttpResult.STATUS_OK) {
				Util.clearRentalRecordFromSharedPref(getApplicationContext());
				tStatus.setText("Bike dropped off successfully.");
				// stop location tracking service
				Log.d(T, "Bike dropped off successfully");
			} else {
				tStatus.setText("Slot " + rec.getDropoff_slot_id()
						+ " occupied.");
				Log.d(T, "Slot " + rec.getDropoff_slot_id() + " occupied.");
			}
		} else {
			tStatus.setText("You do not have bike to drop off");
			Log.d(T, "No bike to drop off");
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
