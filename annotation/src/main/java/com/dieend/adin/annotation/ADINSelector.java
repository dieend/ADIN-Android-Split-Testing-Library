package com.dieend.adin.annotation;

public final class ADINSelector {
	private ADINSelector(){
	}
	int value = 0;
	
	private static ADINSelector instance = null;
	private static ADINSelector instance() {
		if (instance == null) {
			instance = new ADINSelector();
		}
		return instance;
	}
	public static void registerVersion(int i){
		instance().value = i;
	}
	public static int getVersion() {
		return instance().value;
	}
	
}
