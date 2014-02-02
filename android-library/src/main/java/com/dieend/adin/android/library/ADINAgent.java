package com.dieend.adin.android.library;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;


public class ADINAgent {
	private static ADINAgent instance;
	private Context context;
	private MetricsDatabaseHelper dbHelper;
	private Map<String, Long> timedEvent;
	private ADINAgent(Context ctx) {
		context = ctx;
		dbHelper = new MetricsDatabaseHelper(ctx);
		timedEvent = new HashMap<String, Long>();
	}
	public static void onCreate(Context ctx) {
		instance = new ADINAgent(ctx);
	}
	private static ADINAgent i() {
		assert instance != null;
		return instance;
	}
	public static void logEvent(String key) {
		i().save(key, new HashMap<String, String>(), false);
	}
	public static void logEvent(String key, Map<String, String> parameters) {
		i().save(key, parameters, false);
	}
	public static void logEvent(String key, Map<String, String> parameters, boolean timed) {
		i().save(key, parameters, timed);
	}
	public static void endTimedEvent(String key) {
		i().end(key);
	}
	private void end(String key) {
		Long id = timedEvent.remove(key);
		assert id != null;
		dbHelper.update(id, key, System.currentTimeMillis());
	}
	private void save(String key, Map<String, String> params, boolean timed) {
		JSONObject parameters = new JSONObject(params);
		if (timed) {
			Long id = timedEvent.remove(key);
			if (id!=null) {
				dbHelper.update(id, key, System.currentTimeMillis());
			}
			id = dbHelper.add(key, parameters, System.currentTimeMillis(), -1);
			timedEvent.put(key, id);
		}
	}
}
