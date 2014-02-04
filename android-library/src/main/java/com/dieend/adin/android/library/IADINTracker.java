package com.dieend.adin.android.library;

import java.util.Map;

public interface IADINTracker {
	public void save(String key, Map<String, String> params, boolean timed);
	public void end(String key);
	public boolean isReady();
}
