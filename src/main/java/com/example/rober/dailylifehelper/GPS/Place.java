package com.example.rober.dailylifehelper.GPS;

import android.util.Log;

import org.json.JSONObject;

public class Place {

    private String name;
    private double latitude;
    private double longitude;

    //https://stackoverflow.com/questions/10443652/customize-the-android-maps-places/10444127#10444127

    //double to Double?

    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    static Place jsonToPlace (JSONObject jsonPlace){
        try {
            Place result = new Place();
            JSONObject geometry = (JSONObject) jsonPlace.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((double) location.get("lat"));
            result.setLongitude((double) location.get("lng"));
            result.setName(jsonPlace.getString("name"));

        }catch (Exception e){
            Log.d("LOG_TAG", e.toString());
        }

        return null;
    }
}
