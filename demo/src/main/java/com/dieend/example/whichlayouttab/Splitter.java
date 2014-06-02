package com.dieend.example.whichlayouttab;

import java.io.File;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dieend.adin.hermes.splitter.SplitterInterface;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class Splitter implements SplitterInterface{
	Context context;
	JsonClientIdentifier client;
	boolean ready = false;
	File configuration;
	String etag;
	SplitWaiter callback;
	private static final String SPLIT_TEST_DIR = ".splittest";
	private static final String SPLIT_TEST_FILE = "SplitTest.option";

	Splitter(Context ctx, String url, SplitWaiter finished) {
		context = ctx;
		client = new JsonClientIdentifier(ctx);
		File dir = ctx.getApplicationContext().getDir(SPLIT_TEST_DIR, Context.MODE_PRIVATE);
		configuration = new File(dir, SPLIT_TEST_FILE);
		callback = finished;
		new ConfigLoader().execute(url);
	}

	static Random rand = new Random();
	@Override
	public boolean isInExperiment(String arg0) {
		Log.d("EXPERIMENT", "in experiment " + arg0);
		return true; 
	}
	public int bucket(String arg0){
		return rand.nextInt()%2;
	}
	@Override
	public boolean isReady() {
		return ready;
	}
	@Override
	public boolean isTreated(){
		return true;
	}
	@Override
	public Object getParameter(String arg0, String arg1) {
		if (arg0.equals("testExperiment") && arg1.equals("color")) {
			return "#3bbc73";
		} else if (arg0.equals("testExperiment") && arg1.equals("parameter1")) {
			return "#e59400";
		}
		return null;
	}
	
	
	private class ConfigLoader extends AsyncTask<String, Long, File> {
		@Override
		protected File doInBackground(String... urls) {
			try {
			  HttpRequest request = HttpRequest.get(urls[0]);
			  request.acceptGzipEncoding();
			  boolean cached = request.ifNoneMatch(etag).notModified();
			  if (!cached) {
			      int filesize = request.contentLength();
			      if (request.ok()) {
			        request.receive(configuration);
			        
			        etag = request.eTag();
			        publishProgress(configuration.length() / filesize);
			      }
			  } 
			  return configuration;
		    } catch (HttpRequestException exception) {
		      return null;
		    }
		}
		@Override
		protected void onProgressUpdate(Long... params) {
			callback.configProgressed(params[0]);
		}
		@Override
		protected void onPostExecute(File params) {
	         ready = true;
	         if (params == null) {
	        	 Log.d("ADIN", "failed get config");
	         } else {
	        	 // parse JSON
	         }
	         callback.finished();
	     }
	}
}
