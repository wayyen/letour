package com.twt.xtreme;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jcouchdb.db.Database;

public class PostPhotoActivity extends Activity {

	
	final static String T = "PostPhotoActivity";
	private Database db;
	
	Intent picIntent;
	File photo_file;
	String title;
	String desc;
	String address;
	Location loc;
	ImageView preview;
	
	EditText field_title;
	EditText field_desc;
	TextView tLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_pic);
		picIntent = this.getIntent();
		field_title = (EditText)findViewById(R.id.textfield_title);
		field_desc = (EditText)findViewById(R.id.textfield_desc);
		preview = (ImageView)findViewById(R.id.image_preview);
		tLocation = (TextView)findViewById(R.id.label_location);
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
		photo_file = new File(picIntent.getStringExtra("photo_path"));
		preview.setImageBitmap(BitmapFactory.decodeFile(photo_file.getAbsolutePath()));
		if (!photo_file.exists())  {
			Toast.makeText(getApplicationContext(), "Unable to read captured photo.", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this, LeTourActivity.class);
			startActivity(i);
		}
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		loc = ( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ?
					locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) :
						locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) );
		if (loc != null) {
			Log.d(T, loc.toString());
			
		} else {
			Log.e(T, "Can't get any location");
		}
		Geocoder g = new Geocoder(this);
		try {
			List<Address> addrlist = g.getFromLocation(loc.getLatitude(),
					loc.getLongitude(), 1);
			for (Address addr : addrlist) {
				Log.d(T, "addr: " + addr.toString());
				address = addr.getAddressLine(0);
			}
		} catch (Exception e) {
			Log.e(T, "exception in geocoding", e);
		}
		
		tLocation.setText("Location: "+address);
	}
	
	public void doPostPhotoAction(View v) {
		title = field_title.getText().toString();
		desc = field_desc.getText().toString();
		

		uploadPhoto();
	}

	private void uploadPhoto() {
		try {
		db = new Database(getText(R.string.couchdb_host).toString(),
				Integer.parseInt(getText(R.string.couchdb_port).toString()),
				getText(R.string.couchdb_db).toString());
		FileInputStream fis = new FileInputStream(photo_file);
		PhotoProperties props = new PhotoProperties();
		props.setDescription("This is a description");
		props.setLatitude(10.0);
		props.setLongtitude(20.0);
		props.setTitle(title);
		props.setUsername("Bob");
		props.setLength(photo_file.length());
		props.setLocation(address);
		props.setEncodingType("image/jpg");
		addPhoto(fis, props);
		} catch ( Exception e ) {
			Log.e(T, "Exception when uploading photo", e);
		}
	}

	private void addPhoto(InputStream photoStream, PhotoProperties props) {
		db.createAttachment(props.getTitle(), null, "default",
				props.getEncodingType(), photoStream, props.getLength());
		Map doc = db.getDocument(Map.class, props.getTitle());
		doc.put("title", props.getTitle());
		doc.put("username", props.getUsername());
		doc.put("longitude", props.getLongitude());
		doc.put("latitude", props.getLatitude());
		doc.put("location", props.getLocation());
		doc.put("description", props.getDescription());
		db.updateDocument(doc);
		Log.d(T, "test");
	}
	
}
