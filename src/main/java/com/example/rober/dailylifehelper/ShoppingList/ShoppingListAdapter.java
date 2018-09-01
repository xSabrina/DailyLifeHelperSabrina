package com.example.rober.dailylifehelper.ShoppingList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rober.dailylifehelper.R;

import java.util.List;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {

    private Context context;
    private List<ShoppingItem> shoppingItems;
    private TextView textViewItemName;
    private TextView textViewItemValue;

    public ShoppingListAdapter(@NonNull Context context, int resource, @NonNull List<ShoppingItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.shoppingItems = objects;
    }

    /*
        creates new Adapter
        @return v returns new View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.shopping_item, null);
        }
        ShoppingItem shoppingItem = shoppingItems.get(position);
        textViewItemName = v.findViewById(R.id.textViewContentName);
        textViewItemValue = v.findViewById(R.id.textViewContentValue);
        textViewItemName.setText(shoppingItem.getShoppingItemName());
        textViewItemValue.setText(shoppingItem.getShoppingItemValue() + " " + shoppingItem.getShoppingItemSpecification());

        return v;
    }
}
