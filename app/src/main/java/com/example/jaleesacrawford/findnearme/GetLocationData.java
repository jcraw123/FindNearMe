package com.example.jaleesacrawford.findnearme;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GetLocationData {
    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("parse", "error parsing data");
            e.printStackTrace();
        }
        return getLocations(jsonArray);
    }

    private List<HashMap<String, String>> getLocations(JSONArray jsonArray) {
        int locationsCount = jsonArray.length();
        List<HashMap<String, String>> locationsList = new ArrayList<>();
        HashMap<String, String> locationMap = null;

        for (int i = 0; i < locationsCount; i++) {
            try {
                locationMap = getLocation((JSONObject) jsonArray.get(i));
                locationsList.add(locationMap);

            } catch (JSONException e) {
                Log.d("getLocations", "Error in Adding locations");
                e.printStackTrace();
            }
        }
        return locationsList;
    }

    private HashMap<String, String> getLocation(JSONObject googlePlaceJson) {
        HashMap<String, String> locationsMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";


        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            locationsMap.put("place_name", placeName);
            locationsMap.put("vicinity", vicinity);
            locationsMap.put("lat", latitude);
            locationsMap.put("lng", longitude);
            locationsMap.put("reference", reference);
        } catch (JSONException e) {
            Log.d("getLocation", "Error");
            e.printStackTrace();
        }
        return locationsMap;
    }
}
