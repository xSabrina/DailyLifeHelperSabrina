package com.example.rober.dailylifehelper.ShoppingList;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ShoppingItem {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int shoppingItemId;
    private String shoppingItemName;
    private double shoppingItemValue;
    private String shoppingItemSpecification;

    public int getShoppingItemId(){
        return shoppingItemId;
    }

    @NonNull
    public String getShoppingItemName(){
        return shoppingItemName;
    }

    @NonNull
    public double getShoppingItemValue(){
        return shoppingItemValue;
    }

    @NonNull
    public String getShoppingItemSpecification(){
        return shoppingItemSpecification;
    }

    public void setShoppingItemId(@NonNull int shoppingItemId) {
        this.shoppingItemId = shoppingItemId;
    }

    public void setShoppingItemName(@NonNull String shoppingItemName) {
        this.shoppingItemName = shoppingItemName;
    }

    public void setShoppingItemValue(@NonNull double shoppingItemValue) {
        this.shoppingItemValue = shoppingItemValue;
    }

    public void setShoppingItemSpecification(@NonNull String shoppingItemSpecification) {
        this.shoppingItemSpecification = shoppingItemSpecification;
    }

    public ShoppingItem(){
    }
}
