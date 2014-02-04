package com.dieend.adin.android.library;


public interface IADINSelector {
	public boolean isInExperiment(String experimentName);
	public boolean isReady();
	public boolean isTreated();
	public Object getParameter(String experimentName, String identifier);
}
