package com.kcteam.features.SearchLocation;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Pratim on 3/27/2015.
 */
public class LocationPlcaeId {

	private static final String LOG_TAG = "details";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/details";
	private static final String OUT_JSON = "/json";

	// public static final String API_KEY =
	// "AIzaSyDBbZ0cQXfyrQbESRu3e0d_r18943MXQKk";
	private static final String API_KEY = "AIzaSyCbYMZjnt8T6yivYfIa4_R9oy-L3SIYyrQ";

	/**
	 * This Static Method return an address on the basic of the String passed as
	 * the parameter. the address is generated from the Google.
	 *
	 * @param placeid
	 *            The address String .
	 * @return <b>Google</b> generated Address String.
	 */
	public static Location searchLatLng(String placeid) {

		Location location = new Location("");
		final LatLng currentLat = new LatLng(Double.parseDouble("0"), Double.parseDouble("0"));

		location.setLatitude(currentLat.latitude);
		location.setLongitude(currentLat.longitude);

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);

			sb.append("?key=" + API_KEY);
			sb.append("&placeid=" + URLEncoder.encode(placeid, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return location;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return location;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONObject result = jsonObj.getJSONObject("result");
			JSONObject locationObj = result.getJSONObject("geometry").getJSONObject("location");
			String lat = locationObj.getString("lat");
			String lng = locationObj.getString("lng");

			final LatLng currentlatlng = new LatLng(Double.parseDouble(lat),
					Double.parseDouble(lng));

			location.setLatitude(currentlatlng.latitude);
			location.setLongitude(currentlatlng.longitude);


			// Extract the Place descriptions from the results

			// for (int i = 0; i < predsJsonArray.length(); i++) {
			// resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			// }
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return location;
	}
}
