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

public class LeTourActivity extends Activity {

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
		tStatus = (TextView) findViewById(R.id.text_status);
		android_id = android.provider.Settings.Secure.getString(getContentResolver(), 
				android.provider.Settings.Secure.ANDROID_ID);

	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

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
