package com.dieend.adin.hermes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dieend.adin.hermes.tracker.TrackerInterface;

import android.content.Context;

final class DefaultTracker implements TrackerInterface {
	private Map<String, Long> 	timedEvent = new HashMap<String, Long>();
	MetricsDatabaseHelper dbHelper;
	public DefaultTracker(Context ctx) {
		dbHelper = new MetricsDatabaseHelper(ctx);
	}
	@Override
	public void end(String key) {
		Long id = timedEvent.remove(key);
		assert id != null;
		dbHelper.update(id, key, System.currentTimeMillis());
	}
	@Override
	public boolean isReady() {
		return true;
	}
	@Override
	public void save(String eventName, Map<String, Object> params, boolean timed) {
		JSONObject parameters = new JSONObject(params);
		if (timed) {
			Long id = timedEvent.remove(eventName);
			if (id!=null) {
				dbHelper.update(id, eventName, System.currentTimeMillis());
			}
			id = dbHelper.add(eventName, parameters, System.currentTimeMillis(), -1);
			timedEvent.put(eventName, id);
		} else {
			dbHelper.add(eventName, parameters, System.currentTimeMillis(), -1);
		}
	}
}
