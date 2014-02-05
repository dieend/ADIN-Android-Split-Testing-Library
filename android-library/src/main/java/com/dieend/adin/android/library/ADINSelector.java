package com.dieend.adin.android.library;

import android.content.Context;

final class ADINSelector implements IADINSelector {
	private Context context;
	ADINSelector(Context ctx){
		context = ctx;
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
		return false;
	}
	@Override
	public Object getParameter(String experimentName, String identifier) {
		return null;
	}	
}
