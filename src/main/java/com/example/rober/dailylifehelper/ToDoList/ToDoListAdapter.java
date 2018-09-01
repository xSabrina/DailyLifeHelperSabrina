package com.example.rober.dailylifehelper.ToDoList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rober.dailylifehelper.R;

import java.util.List;

public class ToDoListAdapter extends ArrayAdapter<ToDoItem> {

    private Context context;
    private List<ToDoItem> toDoItems;
    private TextView taskNameText;
    private TextView taskDateText;

    public ToDoListAdapter(@NonNull Context context, int resource, @NonNull List<ToDoItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.toDoItems = objects;
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
            v = inflater.inflate(R.layout.todo_item, null);
        }
        ToDoItem toDoItem = toDoItems.get(position);
        taskNameText = v.findViewById(R.id.textViewName);
        taskDateText = v.findViewById(R.id.textViewDate);
        taskNameText.setText(toDoItem.getTaskName());
        taskDateText.setText(toDoItem.getTaskDate());

        return v;
    }
}
