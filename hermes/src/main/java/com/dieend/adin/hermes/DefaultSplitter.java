package com.dieend.adin.hermes;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dieend.adin.hermes.helper.Utility;
import com.dieend.adin.hermes.identity.JsonClientIdentifier;
import com.dieend.adin.hermes.splitter.SplitWaiter;
import com.dieend.adin.hermes.splitter.SplitterInterface;
import com.github.kevinsawicki.http.HttpRequest;

public class DefaultSplitter implements SplitterInterface {

	JsonClientIdentifier client;
	boolean ready = false;
	File configuration;
	SplitWaiter callback;
	Map<String, Boolean> active = new HashMap<String, Boolean>();
	Map<String, Integer> bucket = new HashMap<String, Integer>();
	Map<String, JSONObject> params = new HashMap<String, JSONObject>();

	private static final String SPLIT_TEST_DIR = ".splittest";
	private static final String SPLIT_TEST_FILE = "SplitTest.option";

	DefaultSplitter(Context ctx, String url, SplitWaiter finished) {
		client = new JsonClientIdentifier(ctx);
		File dir = ctx.getApplicationContext().getDir(SPLIT_TEST_DIR,
				Context.MODE_PRIVATE);
		configuration = new File(dir, SPLIT_TEST_FILE);
		callback = finished;
		if (configuration.exists()) {
			try {
				String json = Utility
						.convertStreamToString(new FileInputStream(
								configuration));
				insertData(json);
				
			} catch (Exception e) {
				configuration.delete();
				e.printStackTrace();
			}
		}
		new ConfigLoader().execute(getConfigUrl(url));
	}

	private void insertData(String json) throws JSONException {
		JSONArray arr = new JSONArray(json);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			active.put(obj.getString("experimentName"),
					obj.getBoolean("active"));
			params.put(obj.getString("experimentName"),
					obj.getJSONObject("params"));
			bucket.put(obj.getString("experimentName"),
					obj.getInt("bucket"));

		}
	}
	static Random rand = new Random();

	@Override
	public boolean isInExperiment(String experimentName) {
		if (configuration.exists()) {
			Boolean data = active.get(experimentName);
			return data == null ? false : data;
		} else {
			return false;
		}
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public Object getParameter(String experimentName, String param) {
		try {
			return params.get(experimentName).get(param);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class ConfigLoader extends AsyncTask<String, Long, File> {
		@Override
		protected File doInBackground(String... urls) {
			Log.d("ConfigLoader",urls[0]);
			try {
				HttpRequest request = HttpRequest.get(urls[0]);
				request.acceptGzipEncoding();
				
				int filesize = request.contentLength();
				if (request.ok()) {
					Log.d("ConfigLoader", "A");
					request.receive(configuration);
					Log.d("ConfigLoader", "here");
					publishProgress(configuration.length() / filesize);
				}
			
				return configuration;
			} catch (Exception exception) {
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Long... params) {
			callback.configProgressed(params[0]);
		}

		@Override
		protected void onPostExecute(File jsonFile) {
			if (jsonFile != null) {
				try {
					String json = Utility
							.convertStreamToString(new FileInputStream(jsonFile));
					Log.d("ConfigLoader", json);
					insertData(json);
				} catch (Exception e) {
					e.printStackTrace();
					jsonFile.delete();
				}
			}
			ready = true;
			callback.finished();
		}
	}

	@Override
	public int bucket(String experimentName) {
		return bucket.get(experimentName);
	}

	protected String getConfigUrl(String baseUrl) {
		return baseUrl + "/config/" + client.getUUID();
	}

	@Override
	public boolean isTreated() {
		return false;
	}
}
