package com.example.rober.dailylifehelper.RoomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.rober.dailylifehelper.ToDoList.ToDoItem;

@Dao
public interface ToDoDao {
    /*
        @param toDoItem item to insert
     */
    @Insert
    void insertSingleToDoItem(ToDoItem toDoItem);
    /*
        arrayList don't work because of cursor (doesn't work for arrayLists :( )
        @return allToDoItems() returns array containing all toDos
     */
    @Query("SELECT * FROM todoitem")
    ToDoItem[] allToDoItems();
    /*
        @param toDoItem item to delete
     */
    @Delete
    void deleteTask (ToDoItem toDoItem);

    @Query("DELETE FROM todoitem")
    void nukeToDoItems();
}
