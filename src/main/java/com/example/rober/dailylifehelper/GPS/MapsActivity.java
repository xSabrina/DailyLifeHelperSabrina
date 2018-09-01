package com.example.rober.dailylifehelper.GPS;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;

    private int permissionLocation;

    private double latitude;
    private double longitude;

    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/json?";

    //https://stackoverflow.com/questions/9873190/my-current-location-always-returns-null-how-can-i-fix-this

    //https://google-developer-training.gitbooks.io/android-developer-advanced-course-practicals/unit-4-add-geo-features-to-your-apps/lesson-9-mapping/9-1-p-add-a-google-map-to-your-app/9-1-p-add-a-google-map-to-your-app.html


    //toDo implement onSharedPrefsChangedListener if settings are moved to menu

    //toDo if gps is not enabled location is set in middle of the earth

    //https://o7planning.org/de/10501/anleitung-google-maps-android-api


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        setupLocationManager();
        getLocations();
        new GetPlaces(this).execute();
    }

    private void performSearch(){

    }

    private void setupUI(){
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
        creates new locationManager
     */
    private void setupLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /*
        2 requests useful?
     */
    private void setupLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setNewLocation(location);
                //Log.d("LOG_TAG", "location listener gets new updates");
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
       if gps preference is enabled: checks for permissions, gets first location
       if gps preference is not enabled: finishes activity
    */
    private void getLocations() {
        if (gpsPrefEnabled()) {
            checkPermissions();
            getFirstLocation();
        } else {
            Toast.makeText(MapsActivity.this, "Pls turn GPS in Settings on for Location-updates!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /*
        @param newLocation new Location for latitude and longitude value
     */
    private void setNewLocation(Location newLocation) {
        if (newLocation != null) {
            latitude = newLocation.getLatitude();
            longitude = newLocation.getLongitude();
            //Log.d("LOG_TAG", "" + latitude);
            //Log.d("LOG_TAG", "" + longitude);
        }
    }

    /*
        checks permissions
        if permission not granted it asks for permissions
        if permission granted it creates new locationListener
     */
    private void checkPermissions() {
        permissionLocation = ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(MapsActivity.this,
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
        @return true if gps preference is true
     */
    private boolean gpsPrefEnabled() {
        SharedPreferences gpsPref = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        return gpsPref.getBoolean(getResources().getString(R.string.pref_gps), true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMyLocationAndZoom();
    }

    private void showMyLocationAndZoom(){
        getFirstLocation();
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            Log.d("LOG_TAG", e.toString());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(13)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    class GetPlaces extends AsyncTask<Void, Void, Void>{

        private Context context;
        private ProgressDialog progressDialog;

        public GetPlaces(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Loading");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            findNearPlaces();
            return null;
        }
    }


    //replace atm w/ supermarket specification
    public void findNearPlaces(){
        String[] placeNames;
        PlaceService placeService = new PlaceService(getResources().getString(R.string.google_maps_key));

        List<Place> findPlaces = placeService.findPlaces(latitude, longitude, "atm");

        placeNames = new String[findPlaces.size()];
        for (int i = 0; i < findPlaces.size(); i++){
            Place placeDetail = findPlaces.get(i);
            //exception here
            try {
                placeNames[i] = placeDetail.getName();
                Log.d("LOG_TAG", placeDetail.getName());
            }catch (Exception e){
                Log.d("LOG_TAG", e.toString());
            }

        }
    }


    //toDo doesn't show up
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
