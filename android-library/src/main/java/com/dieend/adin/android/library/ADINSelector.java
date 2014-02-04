package com.dieend.adin.android.library;

import android.content.Context;

final class ADINSelector implements IADINSelector {
	private Context context;
	ADINSelector(Context ctx){
		context = ctx;
	}
	int value = 0;
	
	public void registerVersion(int i){
		value = i;
	}
	public int getVersion() {
		return value;
	}
	@Override
	public boolean isInExperiment(String experimentName) {
		return false;
	}
	@Override
	public boolean isReady() {
		return true;
	}
	@Override
	public boolean isTreated() {
		return true;
	}
	@Override
	public Object getParameter(String experimentName, String identifier) {
		// TODO Auto-generated method stub
		return null;
	}	
}
