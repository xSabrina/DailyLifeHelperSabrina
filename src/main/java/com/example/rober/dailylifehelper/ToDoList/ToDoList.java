package com.example.rober.dailylifehelper.ToDoList;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;
import com.example.rober.dailylifehelper.RoomDB.MyDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class ToDoList extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ListView toDoListView;

    //mainActivity???
    //doesn't work
    //private final String DB_NAME = getResources().getString(R.string.db_name);
    private MyDatabase db;

    private ToDoListAdapter adapter;

    private String newToDoItemString;
    private ToDoItem toDoItem;
    private ArrayList<ToDoItem> allToDoItems = new ArrayList<>();
    private ToDoItem[] toDoItems = new ToDoItem[100];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        setupDatabase();
        setupListeners();
        initAdapter();
        showAllToDoItems();
    }

    /*
      get all toDos from db
      method enterNewToDoItem for every entry
     */
    private void showAllToDoItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                toDoItems = db.toDoDao().allToDoItems();
                for (ToDoItem toDoItem1 : toDoItems) {
                    enterNewToDoItem(toDoItem1);
                }
            }
        }).start();

    }
    /*
        create new Adapter and init it
     */
    private void initAdapter() {
        adapter = new ToDoListAdapter(this, android.R.layout.simple_list_item_1, allToDoItems);
        toDoListView.setAdapter(adapter);
    }

    private void setupDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, getResources().getString(R.string.db_name)).fallbackToDestructiveMigration().build();
    }

    /*
        listener on button and listView
        create new toDoItem on click
        delete entry on longClick
        new Thread with db-method
        runOnUiThread for UI actions
     */
    private void setupListeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newToDoItemString = editText.getText().toString();
                if (!newToDoItemString.equals("")) {
                    addToDoEntry();
                }else{
                    Toast.makeText(ToDoList.this, "nothing selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteToDoItem(position);
                return false;
            }
        });
    }

    private void addToDoEntry(){
        toDoItem = new ToDoItem();
        String date = getDate();
        toDoItem.setTaskName(newToDoItemString);
        toDoItem.setTaskDate(date);
        new Thread(new Runnable() {
            @Override
            public void run() {

                db.toDoDao().insertSingleToDoItem(toDoItem);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.setText("");
                        enterNewToDoItem(toDoItem);
                    }
                });
            }
        }).start();
    }

    /*
        new Thread with db-method
        runOnUiThread for UI actions
        @param position position to delete in arrayList
     */
    private void deleteToDoItem(final int position) {
        final ToDoItem toDelete = allToDoItems.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.toDoDao().deleteTask(toDelete);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allToDoItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }
    private void sortList(){
        Collections.sort(allToDoItems);
        adapter.notifyDataSetChanged();
    }

    /*
        @param newToDoItem new item to add to arrayList
     */
    private void enterNewToDoItem(ToDoItem newToDoItem) {
        allToDoItems.add(newToDoItem);
        adapter.notifyDataSetChanged();
    }

    /*
        @return  dateFormat.format(calendar.getTime()) returns current date
     */
    private String getDate(){
        /*
        CharSequence date = android.text.format.DateFormat.format("dd-MM-yy hh:mm", new Date().getTime());
        return date.toString();
         */
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return dateFormat.format(calendar.getTime());
    }

    private void setupUI() {
        setContentView(R.layout.list_do_to);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.buttonAddToDo);
        toDoListView = (ListView) findViewById(R.id.toDoView);
    }

    private void clearList() {
        allToDoItems.clear();
        adapter.notifyDataSetChanged();
    }

    /*
        creates menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todolist, menu);
        return true;
    }

    /*
        handles clicks on menu
        @param item clicked menuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Delete:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.toDoDao().nukeToDoItems();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearList();
                            }
                        });
                    }
                }).start();
                Toast.makeText(this, "You nuked the list!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.CloseMe:
                finish();
                return true;
            case R.id.SortMe:
                sortList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        destroys toDoList activity on pause
     */
    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }


}
