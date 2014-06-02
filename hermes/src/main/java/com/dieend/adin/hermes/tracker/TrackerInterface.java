package com.dieend.adin.hermes.tracker;

import java.util.Map;

import com.dieend.adin.hermes.ADIN;

public interface TrackerInterface {
	/**
	 * Determine how value will be saved.
	 * 
	 * @param eventName
	 * @param params
	 * @param timed
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public void save(String eventName, Map<String, Object> params, boolean timed);
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
	 * @see ADIN#isReady()
	 */
	public boolean isReady();
}
