package com.example.rober.dailylifehelper.RecipeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.example.rober.dailylifehelper.R;
import com.example.rober.dailylifehelper.RoomDB.MyDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class RecipeAsyncTask extends AsyncTask<String, Integer, Void> {

    private Context context;
    private ProgressDialog progressDialog;

    private String recipeUrl = "https://raw.githubusercontent.com/LeaVerou/forkgasm/master/recipes.json";
    private HttpURLConnection httpURLConnection;
    private int response;

    private final int READTIMEOUT = 15000;
    private final int CONNECTIONTIMEOUT = 10000;
    private final int MAXBYTE = 8192;
    private final int PROGRESSBARMAX = 100;

    private StringBuilder stringBuilder;

    private final String DB_NAME = String.valueOf(R.string.db_name);
    private MyDatabase database;

    //debug
    private final String LOG_TAG = "RecipeAsync-TAG";


    public RecipeAsyncTask(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setupDatabase();
        setupProgressDialog(progressDialog);
    }

    @Override
    protected Void doInBackground(String... URL) {
        JSONObject jsonObject = getJSONFromUrl(recipeUrl);
        addRecipesToDb(jsonObject);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        if (response != HttpURLConnection.HTTP_OK) {
            Toast.makeText(context, "Network connection failed", Toast.LENGTH_LONG).show();
        }
    }

    private void setupDatabase() {
        database =  Room.databaseBuilder(context, MyDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
    }

    private void setupProgressDialog(ProgressDialog progressDialog) {
        progressDialog.setIcon(R.drawable.ic_download);
        progressDialog.setTitle("Downloading recipes...");
        progressDialog.setMessage("This may take a few moments.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    private JSONObject getJSONFromUrl(String urlString) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(urlString);
            setupHttpUrlConnection(url);
            if (response == HttpURLConnection.HTTP_OK) {
                readData(url);
                getStringsFromURL(url);
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(LOG_TAG, "Error:" + response);
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void setupHttpUrlConnection(URL url) {
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
            httpURLConnection.setReadTimeout(READTIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECTIONTIMEOUT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            response = httpURLConnection.getResponseCode();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(URL url) {
        try {
            InputStream input;
            input = new BufferedInputStream(url.openStream());
            int fileLength = httpURLConnection.getContentLength();
            Log.d(LOG_TAG, "Filelength: " + fileLength);
            byte data[] = new byte[MAXBYTE];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) total / (fileLength / PROGRESSBARMAX));
                Log.d(LOG_TAG, "PUBLISH: " + total);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getStringsFromURL(URL url) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addRecipesToDb(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("recipe");
            for (int i = 0; i < jsonArray.length(); i++) {
                Recipe recipe = new Recipe();
                JSONObject recipeJsonObject = jsonArray.getJSONObject(i);
                setRecipeNameAndDescription(recipeJsonObject, recipe);
                JSONArray ingredientJsonArray = recipeJsonObject.optJSONArray("ingredient");
                for (int j = 0; j < ingredientJsonArray.length(); j++) {
                    JSONObject ingredientJsonObject = ingredientJsonArray.optJSONObject(j);
                    setIngredients(ingredientJsonObject, recipe);
                }
                //doesnt work - whenever you have to scroll the inner list because its too long for one screen, outer listview stops being created
                JSONArray ingredientGroupJsonArray = recipeJsonObject.optJSONArray("ingredientGroup");
                for (int k = 0; k < ingredientGroupJsonArray.length(); k++) {
                    JSONObject ingredientGroupJsonObject = ingredientGroupJsonArray.optJSONObject(k);
                    JSONArray innerIngredientArray = ingredientGroupJsonObject.optJSONArray("ingredient");
                    for(int l = 0; l < innerIngredientArray.length(); l ++){
                        JSONObject innerIngJsonObject = innerIngredientArray.optJSONObject(l);
                        setIngredients(innerIngJsonObject,recipe);
                    }
                }
                JSONArray stepJsonArray = recipeJsonObject.getJSONArray("step");
                for (int m = 0; m < stepJsonArray.length(); m++) {
                    JSONObject stepJsonObject = stepJsonArray.getJSONObject(m);
                    recipe.setRecipeSteps(stepJsonObject.getString("description") + "\n");
                }
                database.recipeDao().insertSingleRecipe(recipe);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "addJSONsToDb ex: " + e);
        }
    }

    private void setRecipeNameAndDescription(JSONObject jsonObject, Recipe recipe){
        try {
            recipe.setRecipeName(jsonObject.getString("name"));
            recipe.setRecipeDescription(jsonObject.getString("description") + "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setIngredients(JSONObject jsonObject, Recipe recipe){
        recipe.setIngredientAmount(jsonObject.optString("amount") + "\n");
        recipe.setIngredientUnit(jsonObject.optString("unit") + "\n");
        recipe.setIngredientName(jsonObject.optString("name") + "\n");
    }
}

