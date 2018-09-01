package com.example.rober.dailylifehelper.RoomDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.rober.dailylifehelper.FridgeList.FridgeContent;
import com.example.rober.dailylifehelper.RecipeList.Recipe;
import com.example.rober.dailylifehelper.ShoppingList.ShoppingItem;
import com.example.rober.dailylifehelper.ToDoList.ToDoItem;

@Database(entities = {ToDoItem.class, Recipe.class, FridgeContent.class, ShoppingItem.class}, version = 8, exportSchema = true)
public abstract class MyDatabase extends RoomDatabase{
    public abstract ToDoDao toDoDao();
    public abstract RecipeDao recipeDao();
    public abstract FridgeContentDao fridgeContentDao();
    public abstract ShoppingDao shoppingDao();
}
