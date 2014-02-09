package com.dieend.adin.android.library;


import java.util.Map;

// MOCK CLASS
public class ADINAgent {
	private static ADINAgent instance;
	
	private ADINAgent() {}
	private static ADINAgent i() {
		
		return instance;
	}

	public static boolean isInExperiment(String experimentName) {
		return true;
	}

	public static boolean isTreated() {
		return true;
	}
	public static Object getParameter(String experimentName, String variableIdentifier) {
		return null;
	}
	public static void logEvent(String eventName) {
	}
	public static void logEvent(String eventName, Map<String, Object> parameters) {
	}
	public static void logEvent(String eventName, Map<String, Object> parameters, boolean timed) {
	}
	public static void endTimedEvent(String eventName) {
	}
	public static boolean isReady() {
		return true;
	}
}
