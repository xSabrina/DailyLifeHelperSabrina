package com.example.rober.dailylifehelper.RoomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.rober.dailylifehelper.ShoppingList.ShoppingItem;

@Dao
public interface ShoppingDao {

    /*
        @param shoppingItem item to insert in db
     */
    @Insert
    void insertSingleShoppingItem(ShoppingItem shoppingItem);

    /*
        get all shoppingItems
        @return arrayList w/ all shoppingItems
     */
    @Query("SELECT * FROM shoppingitem")
    ShoppingItem[] allShoppingItems();

    /*
        delete all FridgeContents
     */
    @Query("DELETE FROM shoppingitem")
    void nukeShoppingItems();

    @Delete
    void deleteItem(ShoppingItem shoppingItem);
}
