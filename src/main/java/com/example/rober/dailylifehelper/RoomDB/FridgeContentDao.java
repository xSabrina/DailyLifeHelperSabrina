package com.example.rober.dailylifehelper.RoomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.rober.dailylifehelper.FridgeList.FridgeContent;

@Dao
public interface FridgeContentDao {

    /*
        @param fridgeContent fridgeContent to insert
     */
    @Insert
    void insertSingleFridgeContent(FridgeContent fridgeContent);
    /*
        get all FridgeContents
        @return arrayList w/ all fridgeContents
     */
    @Query("SELECT * FROM fridgeContent")
    FridgeContent[] allFridgeContents();

    /*
        delete all FridgeContents
     */
    @Query("DELETE FROM fridgeContent")
    void nukeFridgeContents();

    /*
        @param wantedFridgeContentName name to search in db
        @return stringArray w/ matching fridgeContentName
     */
    @Query("SELECT fridgeContentName FROM fridgeContent WHERE fridgeContentName = :wantedFridgeContentName")
    String[] fetchFridgeContentNameByName(String wantedFridgeContentName);


    /*
        @param existingFridgeContentName name to search in db
        @return fridgeContent w/ matching name
     */
    @Query("SELECT * FROM fridgecontent WHERE fridgeContentName = :existingFridgeContentName")
    FridgeContent fetchExistingFridgeContent (String existingFridgeContentName);


    /*
        @param existingFridgeContentName fridgeContents w/ matching name to delete
     */
    @Query("DELETE FROM fridgecontent WHERE fridgeContentName = :existingFridgeContentName")
    void deleteFridgeContentItemByName (String existingFridgeContentName);

    /*
        @param fridgeContent fridgeContent to delete
     */
    @Delete
    void deleteFridgeContent(FridgeContent fridgeContent);
}
