package com.example.jaleesacrawford.findnearme;

import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap google_map;
    double latitude;
    double longitude;
    private int locations_radius = 8000; //5 mile radius
    GoogleApiClient google_Api_Client;
    Location last_location;
    Marker markerCurrentLocation;
    LocationRequest location_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //will verify if google play services is on device
        if (!verifyGooglePlay()) {
            Log.d("onCreate", "google play services is not available");
            finish();
        }
        else {
            Log.d("onCreate","google play services is avaiable");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean verifyGooglePlay() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(status)) {
                googleAPI.getErrorDialog(this, status,
                        0).show();
            }
            return false;
        }
        return true;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
         google_map = googleMap;
        google_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //start google play services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                google_map.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            google_map.setMyLocationEnabled(true);
        }


        Button Hospitalbtn = (Button) findViewById(R.id.Hospitalbtn);
        Hospitalbtn.setOnClickListener(new View.OnClickListener() {
            String Hospital = "hospital";
            @Override
            public void onClick(View v) {
                google_map.clear();
                String url = getUrl(latitude, longitude, Hospital);
                Object[] GetData = new Object[2];
                GetData[0] = google_map;
                GetData[1] = url;
                Log.d("hospitalbtn clicked", url);
                LocationsNearUser getLocationsData = new LocationsNearUser();
                getLocationsData.execute(GetData);
                Toast.makeText(MapsActivity.this,"Locating Hospitals", Toast.LENGTH_LONG).show();
            }
        });


        Button Restaurantbtn = (Button) findViewById(R.id.Restaurantbtn);
        Restaurantbtn.setOnClickListener(new View.OnClickListener() {
            String Restaurant = "restaurant";
            @Override
            public void onClick(View v) {
                google_map.clear();
                String url = getUrl(latitude, longitude, Restaurant);
                Object[] GetData = new Object[2];
                GetData[0] = google_map;
                GetData[1] = url;
                Log.d("restaurantbtn clicked", url);
                LocationsNearUser getLocationsData = new LocationsNearUser();
                getLocationsData.execute(GetData);
                Toast.makeText(MapsActivity.this,"Locating Restaurants", Toast.LENGTH_LONG).show();
            }
        });

        Button Hotelbtn = (Button) findViewById(R.id.Hotelbtn);
        Hotelbtn.setOnClickListener(new View.OnClickListener() {
            String Hotel = "hotel";
            @Override
            public void onClick(View v) {
                google_map.clear();
                String url = getUrl(latitude, longitude, Hotel);
                Object[] GetData = new Object[2];
                GetData[0] = google_map;
                GetData[1] = url;
                Log.d("hotelbtn clicked", url);
                LocationsNearUser getLocationsData = new LocationsNearUser();
                getLocationsData.execute(GetData);
                Toast.makeText(MapsActivity.this,"Locating hotels", Toast.LENGTH_LONG).show();
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        google_Api_Client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        google_Api_Client.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location_request = new LocationRequest();
        location_request.setInterval(1000);
        location_request.setFastestInterval(1000);
        location_request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(google_Api_Client, location_request, this);
        }
    }

    private String getUrl(double latitude, double longitude, String localLocations) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + locations_radius);
        googlePlacesUrl.append("&type=" + localLocations);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "google_api_key_here");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        last_location = location;
        if (markerCurrentLocation != null) {
            markerCurrentLocation.remove();
        }

        //show the current location
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerCurrentLocation = google_map.addMarker(markerOptions);


        google_map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        google_map.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MapsActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();



        if (google_Api_Client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(google_Api_Client, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grant_Permissions) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grant_Permissions.length > 0
                        && grant_Permissions[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (google_Api_Client == null) {
                            buildGoogleApiClient();
                        }
                        google_map.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}