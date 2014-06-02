package com.dieend.example.whichlayouttab;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonClientIdentifier extends ClientIdentifier{

	public JsonClientIdentifier(Context ctx) {
		super(ctx);
	}

	public JSONObject getJsonValue() {
		try {
			if (json == null) {
				json = buildJSON();
			}
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("invalid json value");
		}
	}

	protected JSONObject buildJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("device", device);
		json.put("manufacturer", manufacturer);
		json.put("brand", brand);
		json.put("lang", lang);
		json.put("country", country);
		json.put("uuid", uuid);
		json.put("w", width);
		json.put("h", height);
		return json;
	}
}
