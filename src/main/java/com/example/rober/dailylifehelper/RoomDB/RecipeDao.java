package com.example.rober.dailylifehelper.RoomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.rober.dailylifehelper.RecipeList.Recipe;

@Dao
public interface RecipeDao {
    /*
        @param recipe recipe to insert
     */
    @Insert
    void insertSingleRecipe(Recipe recipe);
    /*
        arrayList don't work because of cursor (doesn't work for arrayLists :( )
        @return allRecipes() returns array containing all recipes
     */
    @Query("SELECT * FROM recipe")
    Recipe[] allRecipes();
    @Query("DELETE FROM recipe")
    void nukeRecipes();
}
