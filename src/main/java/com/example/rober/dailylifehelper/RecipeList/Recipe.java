package com.example.rober.dailylifehelper.RecipeList;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class Recipe implements Comparable<Recipe> {
    @PrimaryKey (autoGenerate = true)
    private int recipeId;

    private String recipeName;
    private String recipeIngredients;
    private String ingredientAmount = "";
    private String ingredientUnit = "";
    private String ingredientName = "";
    private String recipeDescription;
    private String recipeSteps = "";


    public int getRecipeId() {
        return  recipeId;
    }
    @NonNull
    public String getRecipeName(){
        return recipeName;
    }
    @NonNull
    public String getRecipeIngredients(){return recipeIngredients;}
    @NonNull
    public String getIngredientAmount(){return ingredientAmount;}
    @NonNull
    public String getIngredientUnit(){return ingredientUnit;}
    @NonNull
    public String getIngredientName(){return ingredientName;}
    @NonNull
    public String getRecipeDescription() { return recipeDescription;}
    @NonNull
    public String getRecipeSteps(){return recipeSteps;}


    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId; }

    public void setRecipeName(@NonNull String recipeName){
        this.recipeName = recipeName;}

    public void setRecipeIngredients(@NonNull String recipeIngredients){
        this.recipeIngredients = recipeIngredients;}

    public void setIngredientAmount(@NonNull String ingredientAmount){
        this.ingredientAmount += ingredientAmount;}

    public void setIngredientUnit(@NonNull String ingredientUnit){
        this.ingredientUnit += ingredientUnit;}

    public void setIngredientName(@NonNull String ingredientName){
        this.ingredientName += ingredientName;}

    public void setRecipeDescription(@NonNull String recipeDescription){
        this.recipeDescription = recipeDescription;}

    public void setRecipeSteps(@NonNull String recipeSteps){
        this.recipeSteps += recipeSteps;}


    public Recipe(){}

    @Override
    public int compareTo(@NonNull Recipe recipe) {
        return recipeName.compareToIgnoreCase(recipe.getRecipeName());
    }

}
