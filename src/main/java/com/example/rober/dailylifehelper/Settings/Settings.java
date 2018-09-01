package com.example.rober.dailylifehelper.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;

import java.util.List;


public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPrefs;
    /*
        http://www.programmierenlernenhq.de/tutorial-android-settings-preferences-und-einstellungen/
        https://developer.android.com/guide/topics/ui/settings#Custom
        https://stackoverflow.com/questions/23523806/how-do-you-create-preference-activity-and-preference-fragment-on-android
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
    }

    /*
        works
        toDo handle prefs
      */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getResources().getString(R.string.pref_notification))) {
            Boolean notificationPref = sharedPreferences.getBoolean(key, true);
            if (notificationPref){
                Toast.makeText(this, "Notification settings turned on!", Toast.LENGTH_SHORT).show();
                //toDO turn notifications on

            }
            if (!notificationPref){
                Toast.makeText(this, "Notification settings turned off!", Toast.LENGTH_SHORT).show();
                //toDo turn notifications off
            }

        }
        if (key.equals(getResources().getString(R.string.pref_gps))) {
            Boolean gpsPref = sharedPreferences.getBoolean(key, true);
            if (gpsPref) {
                Toast.makeText(this, "GPS settings turned on!", Toast.LENGTH_SHORT).show();
                showGPSAlert();
                //toDo what if user doesn't turn gps on?
                //gps probably turned on
            }
            if (!gpsPref) {
                Toast.makeText(this, "GPS settings turned off!", Toast.LENGTH_SHORT).show();
                //gps turned off
            }
        }
    }

    /*
        https://www.androidauthority.com/get-use-location-data-android-app-625012/
        creates and shows new AlertDialog for gps-settings
     */
    private void showGPSAlert(){
        final AlertDialog.Builder gpsDialog = new AlertDialog.Builder(Settings.this);
        gpsDialog.setTitle("Enable GPS");
        gpsDialog.setMessage("GPS is turned off. \nPlease Enable GPS to use this App perfectly");
        gpsDialog.setPositiveButton("GPS Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsIntent);
            }
        });
        gpsDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Settings.this, "GPS still turned off!", Toast.LENGTH_SHORT).show();
            }
        });
        gpsDialog.show();

    }
    /*

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                showSettingDialog();
            }
        } else {
            showSettingDialog();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
    */


    /*
    //change values?
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        //new try for deprecated version
        //doesn't do what it should
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        //deprecated :(

        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                //won't be used???
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        Toast.makeText(Settings.this, "GPS on", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(Settings.this, REQUEST_CHECK_SETTINGS);
                        }catch (Exception e){
                            Log.d("LOG_TAG", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(Settings.this, "u fucked up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    */


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentString) {
        return SettingsFragment.class.getName().equals(fragmentString);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }


}
