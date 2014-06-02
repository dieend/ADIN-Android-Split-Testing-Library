package com.dieend.example.whichlayouttab;

import java.util.Locale;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class ClientIdentifier {
	private static final String DEVICE_ID = "DEVICE_ID";
	private static final String INSTALLATION = "INSTALLATION";

	@SuppressWarnings("deprecation")
	public ClientIdentifier(Context ctx) {
		uuid = uuidFactory(ctx);
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		if ((android.os.Build.VERSION.SDK_INT >= 13)) {
			display.getSize(size);
			width = size.x;
			height = size.y; 
		} else  { 
			width = display.getWidth(); 
			height = display.getHeight(); 
		} 
		
	}
	private synchronized String uuidFactory(Context context) {
		UUID uuid;
		final SharedPreferences prefs = context.getApplicationContext()
				.getSharedPreferences(INSTALLATION, Context.MODE_PRIVATE);
		final String id = prefs.getString(DEVICE_ID, null);

		if (id != null) {
			uuid = UUID.fromString(id);

		} else {

			UUID newId = UUID.randomUUID();
			uuid = newId;

			// Write the value out to the prefs file
			prefs.edit()
					.putString(DEVICE_ID, newId.toString())
					.commit();

		}

		return uuid.toString();
	}
	protected String device = Build.DEVICE;
	protected String manufacturer = Build.MANUFACTURER;
	protected String brand = Build.BRAND;
	protected String lang = Locale.getDefault().getISO3Language();
	protected String country = Locale.getDefault().getISO3Country();
	protected String uuid;
	protected JSONObject json;
	protected String size;
	protected int width;
	protected int height;
}
