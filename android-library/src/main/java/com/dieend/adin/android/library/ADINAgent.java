package com.dieend.adin.android.library;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;


public class ADINAgent {
	private static ADINAgent instance;
	private Context context;
	private IADINSelector selector;
	private IADINTracker tracker;
	
	private ADINAgent(Context ctx) {
		context = ctx;
		selector = new ADINSelector(ctx);
		tracker = new ADINTracker(ctx);
	}
	private ADINAgent(Context ctx, IADINSelector slctr) {
		context = ctx;
		selector = slctr;
		tracker = new ADINTracker(ctx);
	}
	private ADINAgent(Context ctx, IADINTracker trck) {
		context = ctx;
		tracker = trck;
		selector = new ADINSelector(ctx);
	}
	private ADINAgent(Context ctx, IADINSelector slctr, IADINTracker trck) {
		context = ctx;
		selector = slctr;
		tracker = trck;
	}
	
	public static void onCreate(Context ctx) {
		instance = new ADINAgent(ctx);
	}
	public static void onCreate(Context ctx, IADINSelector selector) {
		instance = new ADINAgent(ctx, selector);
	}
	public static void onCreate(Context ctx, IADINTracker tracker) {
		instance = new ADINAgent(ctx, tracker);
	}
	public static void onCreate(Context ctx, IADINSelector selector, IADINTracker tracker) {
		instance = new ADINAgent(ctx, selector, tracker);
	}
	public static boolean isInExperiment(String experimentName) {
		return ADINAgent.i().selector.isInExperiment(experimentName);
	}
	private static ADINAgent i() {
		if (instance == null) throw new RuntimeException("ADINAgent haven't been initialized.\n"
				+ "Please initialize it with ADINAgent.onCreate()");
		if (!instance.tracker.isReady() && !instance.selector.isReady()) throw 
			new RuntimeException("Tracker or Instance isn't ready. Please wait until it's ready "
					+ "before using.");
		return instance;
	}
	public static void logEvent(String key) {
		ADINAgent.i().tracker.save(key, new HashMap<String, String>(), false);
	}
	public static void logEvent(String key, Map<String, String> parameters) {
		ADINAgent.i().tracker.save(key, parameters, false);
	}
	public static void logEvent(String key, Map<String, String> parameters, boolean timed) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		ADINAgent.i().tracker.save(key, parameters, timed);
	}
	public static void endTimedEvent(String key) {
		ADINAgent.i().tracker.end(key);
	}
	
	public static boolean isReady() {
		return ADINAgent.instance.selector.isReady() && ADINAgent.instance.tracker.isReady();
	}
}
