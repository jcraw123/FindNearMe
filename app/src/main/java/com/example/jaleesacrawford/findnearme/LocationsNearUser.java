package com.example.jaleesacrawford.findnearme;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;


public class LocationsNearUser extends AsyncTask<Object, String, String> {
    String url;
    String placesInfo;
    GoogleMap google_map;

    @Override
    protected String doInBackground(Object... args) {
        try {
            google_map = (GoogleMap) args[0];
            url = (String) args[1];
            GooglePlacesUrl placesUrl = new GooglePlacesUrl();
            placesInfo = placesUrl.getUrl(url);
        } catch (Exception e) {
            Log.d("Exception occured", e.toString());
        }
        return placesInfo;
    }

    @Override
    protected void onPostExecute(String result) {
        List<HashMap<String, String>> localLocationList = null;
            GetLocationData parseLocationData = new GetLocationData();
            localLocationList =  parseLocationData.parse(result);
            displayLocalLocations(localLocationList);
    }

    private void displayLocalLocations(List<HashMap<String, String>> locationsList) {
        for (int i = 0; i < locationsList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = locationsList.get(i);

            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            google_map.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            google_map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            google_map.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }
}
