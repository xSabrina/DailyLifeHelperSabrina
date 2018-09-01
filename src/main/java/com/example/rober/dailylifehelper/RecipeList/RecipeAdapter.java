package com.example.rober.dailylifehelper.RecipeList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.rober.dailylifehelper.R;
import java.util.List;


public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context context;
    private List<Recipe> recipes;

    private TextView recipeName;
    private TextView recipeDescription;
    private TextView amount;
    private TextView unit;
    private TextView ingredientName;
    private TextView recipeSteps;


    public RecipeAdapter(@NonNull Context context, int resource, @NonNull List<Recipe> objects) {
        super(context, resource, objects);
        this.context = context;
        this.recipes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.recipe, null);
        }
        Recipe recipe = recipes.get(position);
        getTextViews(v);
        setTexts(recipe);
        return v;
    }

    private void getTextViews(View v){
        recipeName = v.findViewById(R.id.recipeName);
        recipeDescription = v.findViewById(R.id.recipeDescription);
        amount = v.findViewById(R.id.amount);
        unit = v.findViewById(R.id.unit);
        ingredientName = v.findViewById(R.id.ingredientName);
        recipeSteps = v.findViewById(R.id.recipeSteps);
    }

    private void setTexts(Recipe recipe){
        recipeName.setText(recipe.getRecipeName() + "\n");
        recipeDescription.setText(recipe.getRecipeDescription() + "\n");
        amount.setText(recipe.getIngredientAmount());
        unit.setText(String.valueOf(recipe.getIngredientUnit()));
        ingredientName.setText(recipe.getIngredientName());
        recipeSteps.setText(recipe.getRecipeSteps());
    }
}
