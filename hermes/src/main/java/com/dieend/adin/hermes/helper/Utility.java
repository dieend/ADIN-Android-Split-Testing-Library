package com.dieend.adin.hermes.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utility {
	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    reader.close();
	    return sb.toString();
	}
}
