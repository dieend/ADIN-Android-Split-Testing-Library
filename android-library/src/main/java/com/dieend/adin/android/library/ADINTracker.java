package com.dieend.adin.android.library;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

final class ADINTracker implements IADINTracker {
	private Map<String, Long> 	timedEvent = new HashMap<String, Long>();
	MetricsDatabaseHelper dbHelper;
	public ADINTracker(Context ctx) {
		dbHelper = new MetricsDatabaseHelper(ctx);
	}
	@Override
	public void save(String key, Map<String, String> params, boolean timed) {
		JSONObject parameters = new JSONObject(params);
		if (timed) {
			Long id = timedEvent.remove(key);
			if (id!=null) {
				dbHelper.update(id, key, System.currentTimeMillis());
			}
			id = dbHelper.add(key, parameters, System.currentTimeMillis(), -1);
			timedEvent.put(key, id);
		} else {
			dbHelper.add(key, parameters, System.currentTimeMillis(), -1);
		}
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
}
