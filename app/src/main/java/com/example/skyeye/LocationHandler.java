package com.example.skyeye;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;
import java.util.Locale;

public class LocationHandler {

    // Constants
    private static final String TAG = "LocationHandler";

    // References
    private Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Constructor
    public LocationHandler(Activity activity, FusedLocationProviderClient fusedLocationClient) {
        this.activity = activity;
        this.fusedLocationClient = fusedLocationClient;
    }

    // Get the current location
    public void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        ((MainActivity) activity).updateAddressUI(reverseGeocode(lat, lon));
                        ((MainActivity) activity).updateLocationUI(lat, lon);
                        ((MainActivity) activity).weatherFetcher.fetchWeatherData(lat, lon);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // Reverse geocode to get the address from latitude and longitude
    private String reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            } else {
                return "Address not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to get address";
        }
    }
}
