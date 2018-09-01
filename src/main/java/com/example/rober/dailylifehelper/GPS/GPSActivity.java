package com.example.rober.dailylifehelper.GPS;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;

import java.util.ArrayList;
import java.util.List;

public class GPSActivity extends AppCompatActivity {

    private TextView longitudeTextView, latitudeTextView;
    private LocationManager locationManager;

    private int permissionLocation;

    private double latitude;
    private double longitude;

    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    //https://stackoverflow.com/questions/9873190/my-current-location-always-returns-null-how-can-i-fix-this
    //works

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        setupLocationManager();
        getLocations();

    }

    /*
        if gps preference is enabled: checks for permissions, gets first location
        if gps preference is not enabled: finishes activity
     */
    private void getLocations() {
        if (gpsPrefEnabled()) {
            checkPermissions();
            getFirstLocation();
            setEditTexts();
        } else {
            Toast.makeText(GPSActivity.this, "Pls turn GPS in Settings on for Location-updates!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_gps);
        latitudeTextView = (TextView) findViewById(R.id.textViewLatitude);
        longitudeTextView = (TextView) findViewById(R.id.textViewLongitude);
    }

    /*
        creates new locationManager
     */
    private void setupLocationManager() {
        String service = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(service);
    }

    /*
        checks permissions
        if permission not granted it asks for permissions
        if permission granted it creates new locationListener
     */
    private void checkPermissions() {
        permissionLocation = ContextCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(GPSActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

            }
        } else {
            setupLocationListener();
        }
    }

    /*
        gets the best location
     */
    private void getFirstLocation() {
        try {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location possibleBestLocation = locationManager.getLastKnownLocation(provider);
                if (possibleBestLocation == null) {
                    continue;
                }
                if (bestLocation == null || possibleBestLocation.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = possibleBestLocation;
                }
            }
            if (bestLocation != null) {
                latitude = bestLocation.getLatitude();
                longitude = bestLocation.getLongitude();
            } else {
                Log.d("LOG_TAG", "bestLocation : null");
            }
        } catch (SecurityException e) {
            Log.d("LOG_TAG", e.toString());
        }
    }

    /*
        @param newLocation new Location for latitude and longitude value
     */
    private void setNewLocation(Location newLocation) {
        if (newLocation != null) {
            latitude = newLocation.getLatitude();
            longitude = newLocation.getLongitude();
            Log.d("LOG_TAG", "" + latitude);
            Log.d("LOG_TAG", "" + longitude);
        }
    }

    private void setEditTexts() {
        latitudeTextView.setText("Latitude : " + latitude);
        longitudeTextView.setText("Longitude : " + longitude);
    }

    /*
        2 requests useful?
     */
    private void setupLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setNewLocation(location);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setEditTexts();
                    }
                });
                Log.d("LOG_TAG", "location listener gets new updates");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            //change values?
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
            //when gps is enabled in prefs but only course location in device
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);

        } catch (SecurityException e) {
            Log.d("LOG_TAG", e.toString());
        }

    }

    /*
        @return true if gps preference is true
     */
    private boolean gpsPrefEnabled() {
        SharedPreferences gpsPref = PreferenceManager.getDefaultSharedPreferences(GPSActivity.this);
        return gpsPref.getBoolean(getResources().getString(R.string.pref_gps), true);
    }

}
