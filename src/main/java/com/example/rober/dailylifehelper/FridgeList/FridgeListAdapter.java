package com.example.rober.dailylifehelper.FridgeList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rober.dailylifehelper.R;

import java.util.List;

public class FridgeListAdapter extends ArrayAdapter<FridgeContent> {

    private Context context;
    private List<FridgeContent> fridgeContents;
    private TextView textViewContentName;
    private TextView textViewContentValue;

    public FridgeListAdapter (@NonNull Context context, int resource, @NonNull List<FridgeContent> objects){
        super(context, resource, objects);
        this.context = context;
        this.fridgeContents = objects;
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
            v = inflater.inflate(R.layout.fridge_content_item, null);
        }
        FridgeContent fridgeContent = fridgeContents.get(position);
        textViewContentName = v.findViewById(R.id.textViewContentName);
        textViewContentValue = v.findViewById(R.id.textViewContentValue);
        textViewContentName.setText(fridgeContent.getFridgeContentName());
        textViewContentValue.setText(fridgeContent.getFridgeContentValue() + " " + fridgeContent.getFridgeContentSpecification());

        return v;
    }
}
