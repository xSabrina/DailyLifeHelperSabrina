package com.example.rober.dailylifehelper.ToDoList;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ToDoItem implements Comparable<ToDoItem> {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private String taskName;
    private String taskDate;

    @NonNull
    public String getTaskName() {
        return taskName;
    }

    public int getTaskId() {
        return taskId;
    }

    @NonNull
    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskName(@NonNull String taskName) {
        this.taskName = taskName;
    }


    public void setTaskId(@NonNull int taskId) {
        this.taskId = taskId;
    }

    public void setTaskDate(@NonNull String taskDate) {
        this.taskDate = taskDate;
    }


    public ToDoItem() {
    }

    @Override
    public String toString() {
        return taskName + " : " + taskDate;
    }

    /*
        method for comparing
     */
    @Override
    public int compareTo(@NonNull ToDoItem o) {
        //doesn't work right
        int result = taskName.compareToIgnoreCase(o.getTaskName());
        if (result == 0){
            result = taskDate.compareToIgnoreCase(o.getTaskDate());
        }
        return result;
    }
}
