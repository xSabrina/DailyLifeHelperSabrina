package com.example.rober.dailylifehelper.GPS;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlaceService {

    private String apiKey;


    public PlaceService (String apiKey){
        this.apiKey = apiKey;
    }

    public List<Place> findPlaces (double latitude, double longitude, String placeSpecification){

        String urlString = makeUrl(latitude, longitude, placeSpecification);

        try {
            String json = getJSON(urlString);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++){
                    Place place = Place.jsonToPlace((JSONObject) jsonArray.get(i));
                    arrayList.add(place);
            }
            return arrayList;
        }catch (Exception e){
            Log.d("LOG_TAG", e.toString());
        }
        return null;
    }

    private String makeUrl(double latitude, double longitude, String placeSpecification){
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");

        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=1000");
        urlString.append("&types=" + placeSpecification);
        urlString.append("&sensor=false&key=" + apiKey);

        return urlString.toString();
    }

    private String getJSON (String url){
        return getUrlContents(url);
    }

    private String getUrlContents(String urlString){
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }catch (Exception e){
            Log.d("LOG_TAG", e.toString());
        }

        return content.toString();
    }

}
