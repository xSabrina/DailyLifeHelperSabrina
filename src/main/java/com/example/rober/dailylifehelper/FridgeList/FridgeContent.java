package com.example.rober.dailylifehelper.FridgeList;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class FridgeContent {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int fridgeContentId;
    private String fridgeContentName;
    private double fridgeContentValue;
    private String fridgeContentSpecification;


    public int getFridgeContentId() {
        return fridgeContentId;
    }

    @NonNull
    public String getFridgeContentName() {
        return fridgeContentName;
    }

    @NonNull
    public double getFridgeContentValue() {
        return fridgeContentValue;
    }

    @NonNull
    public String getFridgeContentSpecification(){
        return fridgeContentSpecification;
    }

    public void setFridgeContentId(@NonNull int fridgeContentId) {
        this.fridgeContentId = fridgeContentId;
    }

    public void setFridgeContentName(@NonNull String fridgeContentName) {
        this.fridgeContentName = fridgeContentName;
    }

    public void setFridgeContentValue(@NonNull double fridgeContentValue) {
        this.fridgeContentValue = fridgeContentValue;
    }

    public void setFridgeContentSpecification(@NonNull String fridgeContentSpecification) {
        this.fridgeContentSpecification = fridgeContentSpecification;
    }

    public FridgeContent(){
    }

    @Override
    public String toString(){
        return fridgeContentName + " : " + fridgeContentValue + " : " + fridgeContentSpecification;
    }
}
