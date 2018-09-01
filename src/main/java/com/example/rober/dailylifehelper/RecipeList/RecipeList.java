package com.example.rober.dailylifehelper.RecipeList;


import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.example.rober.dailylifehelper.R;
import com.example.rober.dailylifehelper.RoomDB.MyDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class RecipeList extends AppCompatActivity {

    private List<Recipe> allRecipesList;
    private RecipeAdapter adapter;
    private ListView recipeListView;

    private final String DB_NAME = String.valueOf(R.string.db_name);
    private MyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setupUI();
        setupDatabase();
        execAsync();
        getAllRecipes();
        initAdapter();
    }

    /*
        sets up the ui for the recipeList
     */

    private void setupUI(){
        setContentView(R.layout.list_recipe);
        recipeListView = findViewById(R.id.recipeView);
    }

    /*
        sets up the database for getting recipes
     */
    private void setupDatabase(){
        database =  Room.databaseBuilder(this, MyDatabase.class, DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        adapter = new RecipeAdapter(this, android.R.layout.simple_list_item_1, allRecipesList);
    }

    /*
        executes asyncTask to download recipes in background
     */
    private void execAsync(){
        if(database.recipeDao().allRecipes().length == 0) {
            new RecipeAsyncTask(this).execute();
        }
    }

    /*
        getting the recipes for the ui from database
     */
    private void getAllRecipes(){
        Recipe[] allRecipes = database.recipeDao().allRecipes();
        allRecipesList = new ArrayList (Arrays.asList(allRecipes));
    }

    /*
        puts adapter on the listview to fill it up with recipes from database
     */
    private void initAdapter() {
        adapter = new RecipeAdapter(this, android.R.layout.simple_list_item_1, allRecipesList);
        recipeListView.setAdapter(adapter);
        recipeListView.isShown();
    }

    /*
        creates menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipelist, menu);
        return true;
    }


    /*
        handles clicks on menu
        @param item clicked menuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.DeleteRecipes:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        database.recipeDao().nukeRecipes();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearRecipes();
                            }
                        });
                    }
                }).start();
                Toast.makeText(this, "You nuked the recipelist!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.CloseRecipes:
                finish();
                return true;
            case R.id.SortRecipes:
                sortRecipes();
                return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }

    /*
        clears recipeList
     */
    private void clearRecipes(){
        allRecipesList.clear();
        adapter.notifyDataSetChanged();
    }

    /*
        sorts recipes inside listView
     */
    private void sortRecipes(){
        Collections.sort(allRecipesList);
        adapter.notifyDataSetChanged();
    }

    /*
        destroys recipeList activity on pause
     */
    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

}
