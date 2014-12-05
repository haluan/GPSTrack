package com.example.gpstrack;

import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
	private TextView latituteField;
	private TextView longitudeField;
	private LocationManager locationManager;
	private String provider;
	private SQLiteDatabase mydatabase;
	private com.example.gpstrack.Location loc;
	private long lengthcontinuality;
	
	
	/** Called when the activity is first created. */

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    lengthcontinuality = 60000;
	    setContentView(R.layout.activity_main);
	    latituteField = (TextView) findViewById(R.id.TextView02);
	    longitudeField = (TextView) findViewById(R.id.TextView04);
	    mydatabase = openOrCreateDatabase("location.db",MODE_PRIVATE,null);
	    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS location(id integer primary key autoincrement, latitude real, longitude real, username text);");
	    
	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	      latituteField.setText("Location not available");
	      longitudeField.setText("Location not available");
	    }
	    Toast.makeText(this, "MASUK PERTAMA", 9).show();
	    Time time = new Time(Time.getCurrentTimezone());
	    time.setToNow();
	    Log.v("TIME", time.format("%d/%m/%Y - %k:%M:%S"));
	    
	    Handler handler = new Handler();
	    Timer myTimer = new Timer();
	    
	    myTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				taskPostHandler();
			}
		}, 0, lengthcontinuality);
	  }

	  /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    Location locationA = new Location("point A");
	    Location locationB = new Location("point B");
	    locationA.setLatitude(80.4232);
	    locationA.setLongitude(152.084);
	    locationB.setLatitude(88.4232);
	    locationB.setLongitude(111.084);
	    
	    
	    
	    float distance = (locationA.distanceTo(locationB)/1000);
	    Log.d("Distance", "jarak :"+distance);
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    Toast.makeText(this, "MASUK RESUME", 9).show();
	    Cursor resultSet = mydatabase.rawQuery("Select * from location",null);
	    if(resultSet.getCount()>0){
	    	resultSet.moveToFirst();
		    for(int i = 0; i<resultSet.getCount();i++){
		    	try{
		    		Log.v("Tracker onresume", "id: "+resultSet.getString(0)+" lat: "+resultSet.getString(1)+" long: "+resultSet.getString(2)+" username: "+resultSet.getString(3));
			    	resultSet.moveToNext();
		    	}catch(Exception ex){
		    		Log.d("break onResume", "Breakout");
		    		break;
		    	}
		    }
	    }
	    	
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    Toast.makeText(this, "MASUK PAUSE", 9).show();
	  }
	  
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		float lat = (float) (location.getLatitude());
		float lng = (float) (location.getLongitude());
	    latituteField.setText(String.valueOf(lat));
	    longitudeField.setText(String.valueOf(lng));
	    Toast.makeText(this, "Lokasi Berubah", 9).show();
	    mydatabase.execSQL("CREATE TABLE IF NOT EXISTS location(id integer primary key autoincrement, latitude real, longitude real, username text);");
	    
	    mydatabase.execSQL("INSERT INTO location(latitude, longitude, username) VALUES("+(float)lat+","+ (float) lng+", 'test');");
	    Toast.makeText(this, "Lokasi Tersimpan", 9).show();
	   
	}
	
	private  boolean isConnectedToServer(String url, int timeout) {
	    try{
	        URL myUrl = new URL(url);
	        URLConnection connection = myUrl.openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.connect();
	        return true;
	    } catch (Exception e) {
	        // Handle your exceptions
	        return false;
	    }
	}
	
	
	private void taskPostHandler(){
		 	mydatabase.execSQL("CREATE TABLE IF NOT EXISTS location(id integer primary key autoincrement, latitude real, longitude real, username text);");
		 	final Cursor resultSet = mydatabase.rawQuery("Select * from location",null);
		    	
		    	new Thread(new Runnable() {
					@Override
					public void run() {
						if(isConnectedToServer("http://192.168.1.117:3002/locations", 20000)){
							Log.d("CON IN", "CON TO SEVER");
							if(resultSet.getCount()>0){
								resultSet.moveToFirst();
								 for(int i = 0; i<resultSet.getCount();i++){
								    try{
								    	Log.v("Tracker onRunnable", "id: "+resultSet.getString(0)+" lat: "+resultSet.getString(1)+" long: "+resultSet.getString(2)+" username: "+resultSet.getString(3));
								    	loc = new com.example.gpstrack.Location();
								    	loc.setLatitude(resultSet.getString(1));
								    	loc.setLongitude(resultSet.getString(2));
										// TODO Auto-generated method stub
										new HttpManaged().postEvents(loc);
										resultSet.moveToNext();
										Log.v("Tracker", "id: "+resultSet.getString(0)+" lat: "+resultSet.getString(1)+" long: "+resultSet.getString(2)+" username: "+resultSet.getString(3));
								    }catch(Exception ex){
								    	Log.d("break runable", "Breakout");
								    	break;
								    }
								}
								 resultSet.moveToLast();
								 Log.d("DATA TERAKHIR", "id :"+resultSet.getString(0)+" lat: "+resultSet.getString(1)+" long: "+resultSet.getString(2)+" username: "+resultSet.getString(3));
								 mydatabase.execSQL("DROP TABLE IF EXISTS " + "location");
								 Log.v("Table Activity", "Dihapus");
							}
					    }else{
					    	lengthcontinuality = 30000;
					    	Log.d("CON OUT", "NO CON TO SEVER");
					    }
					}
				}).start();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
