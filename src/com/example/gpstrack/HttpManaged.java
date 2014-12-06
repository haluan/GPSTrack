package com.example.gpstrack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class HttpManaged {
	
	public void postEvents(Location location)
	{
		DefaultHttpClient client = new DefaultHttpClient();
		
		/** FOR LOCAL DEV   HttpPost post = new HttpPost("http://192.168.0.186:3000/events"); //works with and without "/create" on the end */
		HttpPost post = new HttpPost("http://192.168.1.41:3002/locations");
	    JSONObject holder = new JSONObject();
	    JSONObject eventObj = new JSONObject();
	    
	    try {	
	    	eventObj.put("latitude", location.getLatitude());
		    eventObj.put("longitude", location.getLongitude());
		    eventObj.put("date", location.getDate());
		    
		    holder.put("location", eventObj);
		    
		    Log.e("Event JSON", "Event JSON = "+ holder);
		    
	    	StringEntity se = new StringEntity(holder.toString());
	    	post.setEntity(se);
	    	post.setHeader("Accept", "application/json");
	    	post.setHeader("Content-type","application/json");
	 	
	    	
	    } catch (UnsupportedEncodingException e) {
	    	Log.v("Error",""+e);
	        e.printStackTrace();
	    } catch (JSONException js) {
	    	js.printStackTrace();
	    }

	    HttpResponse response = null;
	    
	    try {
	        response = client.execute(post);   
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	        Log.v("ClientProtocol",""+e);
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.v("IO",""+e);
	    }

	    HttpEntity entity = response.getEntity();
	    
	    if (entity != null) {
	        try {
	            entity.consumeContent();
	        } catch (IOException e) {
	        	Log.v("IO E",""+e);
	            e.printStackTrace();
	        }
	    }
	    
	    Log.v("Terkirim", "BERHASIL DIKIRIM");
	  
	}

}
