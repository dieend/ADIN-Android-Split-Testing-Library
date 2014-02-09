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
	/**
	 * Initializes with context.
	 * 
	 * Currently there will be no specific action with default selector. Default tracker will 
	 * record logged action with ADINAgent to local SQLite Database.
	 * 
	 * @param ctx context
	 */
	public static void onCreate(Context ctx) {
		instance = new ADINAgent(ctx);
	}
	/**
	 * Initializes Agent with context and custom selector.
	 * 
	 * The custom selector provided by user. User can creates custom selector by implementing 
	 * {@link com.dieend.adin.android.library.IADINSelector}.
	 * Currently there is no action with default tracker other than to record to SQLite database.
	 * 
	 * @param ctx
	 * @param selector
	 */
	public static void onCreate(Context ctx, IADINSelector selector) {
		instance = new ADINAgent(ctx, selector);
	}
	/**
	 * Initializes Agent with context and custom tracker.
	 * 
	 * The custom tracker provided by user. User can creates custom selector by implementing 
	 * {@link com.dieend.adin.android.library.IADINTracker}.
	 * Currently there is no action with default selector.
	 * 
	 * @param ctx
	 * @param tracker
	 */
	public static void onCreate(Context ctx, IADINTracker tracker) {
		instance = new ADINAgent(ctx, tracker);
	}

	/**
	 * Initializes Agent with context, custom selector, and custom tracker.
	 * 
	 * The custom selector provided by user. User can creates custom selector by implementing 
	 * {@link com.dieend.adin.android.library.IADINSelector} and creates custom tracker by 
	 * implementing {@link com.dieend.adin.android.library.IADINTracker}. 
	 * 
	 * @param ctx
	 * @param selector
	 * @param tracker
	 */
	public static void onCreate(Context ctx, IADINSelector selector, IADINTracker tracker) {
		instance = new ADINAgent(ctx, selector, tracker);
	}
	
	private static ADINAgent i() {
		if (instance == null) throw new RuntimeException("ADINAgent haven't been initialized.\n"
				+ "Please initialize it with ADINAgent.onCreate()");
		if (!instance.tracker.isReady() && !instance.selector.isReady()) throw 
			new RuntimeException("Tracker or Instance isn't ready. Please wait until it's ready "
					+ "before using.");
		return instance;
	}
	
	/**
	 * Checks whether this client included in experiment named experimentName.
	 * 
	 * This method uses selector that given in onCreate method.
	 * 
	 * @param experimentName
	 * @return boolean true if included in experiment.
	 * 
	 * @see #onCreate(Context, IADINSelector)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see IADINSelector
	 */
	public static boolean isInExperiment(String experimentName) {
		return ADINAgent.i().selector.isInExperiment(experimentName);
	}
	
	/**
	 * Checks whether this client shall use an alternate options. The alternate will depends on
	 * annotation given.
	 * 
	 * This method uses selector that given in onCreate method.
	 * 
	 * @see #onCreate(Context, IADINSelector)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see IADINSelector
	 */
	public static boolean isTreated() {
		return ADINAgent.i().selector.isTreated();
	}
	/**
	 * Get value shall be used for this client with specific experimentName and variableIdentifer.
	 * 
	 * variableIdentifier should be unique among variables within same experiment. This method uses 
	 * selector that given in onCreate method
	 * 
	 * @param experimentName
	 * @param variableIdentifier
	 * @return primitive value and String
	 * @see #onCreate(Context, IADINSelector)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see IADINSelector
	 */
	public static Object getParameter(String experimentName, String variableIdentifier) {
		return ADINAgent.i().selector.getParameter(experimentName, variableIdentifier);
	}
	/**
	 * Log event with provided eventName.
	 * 
	 * This method uses tracker that given in onCreate method
	 * 
	 * @param eventName
	 * 
	 * @see #onCreate(Context, IADINTracker)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public static void logEvent(String eventName) {
		ADINAgent.i().tracker.save(eventName, new HashMap<String, Object>(), false);
	}
	/**
	 * Log event with provided eventName and parameters
	 * 
	 * Parameters are pairs of (String, String) value. This method uses tracker given in onCreate 
	 * method.
	 * 
	 * @param eventName
	 * @param parameters 
	 * @see #onCreate(Context, IADINTracker)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public static void logEvent(String eventName, Map<String, Object> parameters) {
		ADINAgent.i().tracker.save(eventName, parameters, false);
	}
	/**
	 * Log event with provided eventName and parameters and indicator this is a timed event.
	 * 
	 * Parameters are pairs of (String, String) value. This method uses tracker given in onCreate 
	 * method. End this event with endTimedEvent method.
	 * 
	 * @param eventName
	 * @param parameters 
	 * @param timed
	 * @see #endTimedEvent(String)
	 * @see #onCreate(Context, IADINTracker)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public static void logEvent(String eventName, Map<String, Object> parameters, boolean timed) {
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		ADINAgent.i().tracker.save(eventName, parameters, timed);
	}
	/**
	 * End timed event with eventName
	 * 
	 * This method uses tracker given in onCreate method. This will end timed event with eventName.
	 * 
	 * @param eventName
	 * @see #logEvent(String, Map, boolean)
	 * @see #onCreate(Context, IADINTracker)
	 * @see #onCreate(Context, IADINSelector, IADINTracker)
	 * @see <a href="http://support.flurry.com/index.php?title=Analytics/GettingStarted/Events/Android">Flurry Event</a>
	 */
	public static void endTimedEvent(String eventName) {
		ADINAgent.i().tracker.end(eventName);
	}
	
	/**
	 * Check whether selector and tracker ready to be used.
	 * 
	 * User should not call any method that being splittested while this method return false. 
	 * Sometimes selector and tracker needs to be ready before able to be used. Typical operation
	 * that needs to wait
	 *  - network request. selector often contact server to decide whether it is in experiment and 
	 *    the value for variable. You should finish your request before calling any method with 
	 *    alternate.
	 *  - preparing database. This often happened at the start of application.
	 *  
	 * Selector and tracker should only need to be ready once. If there is a need that made selector
	 * or tracker unready, you should restart the application to reduce undesired effects.  
	 * @return readiness of selector and tracker
	 */
	public static boolean isReady() {
		return ADINAgent.instance.selector.isReady() && ADINAgent.instance.tracker.isReady();
	}
}
