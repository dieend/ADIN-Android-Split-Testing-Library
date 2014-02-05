package com.dieend.adin.android.library;

import java.util.Map;

public interface IADINTracker {
	/**
	 * Determine how value will be saved.
	 * 
	 * @param eventName
	 * @param params
	 * @param timed
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public void save(String eventName, Map<String, String> params, boolean timed);
	/**
	 * End timed event
	 * 
	 * @param eventName
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public void end(String eventName);
	/**
	 * Whether the tracker is ready
	 * @return
	 * @see ADINAgent#isReady()
	 */
	public boolean isReady();
}
