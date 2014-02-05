package com.dieend.adin.android.library;


public interface IADINSelector {
	/**
	 * Whether this client included in a split test with experimentName name.
	 * 
	 * @param experimentName
	 * @return
	 */
	public boolean isInExperiment(String experimentName);
	/**
	 * Whether the selector ready.
	 * @return
	 * @see ADINAgent#isReady()
	 */
	public boolean isReady();
	/**
	 * Whether this client in treatment bucket (not default version).
	 * @return
	 */
	public boolean isTreated();
	/**
	 * Get alternate variable. There is no defined behavior if returned object is invalid.
	 * @param experimentName
	 * @param identifier
	 * @return
	 */
	public Object getParameter(String experimentName, String identifier);
}
