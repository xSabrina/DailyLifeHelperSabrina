package com.example.rober.dailylifehelper.ShoppingList;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;
import com.example.rober.dailylifehelper.RoomDB.MyDatabase;

import java.util.ArrayList;

public class ShoppingList extends AppCompatActivity {


    private Button generateShoppingListButton;
    private ListView shoppingView;

    private ShoppingItem[] shoppingItems = new ShoppingItem[100];
    private ArrayList<ShoppingItem> allShoppingItems = new ArrayList<>();

    private ShoppingListAdapter adapter;

    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setupUI();
        setupDatabase();
        initAdapter();
        setupListeners();
        showAllShoppingItems();
    }

    private void setupUI(){
        setContentView(R.layout.list_shopping);
        generateShoppingListButton = (Button) findViewById(R.id.buttonAddShopping);
        shoppingView = (ListView) findViewById(R.id.shoppingView);
    }

    private void setupDatabase(){
        db = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, getResources().getString(R.string.db_name)).fallbackToDestructiveMigration().build();
    }

    private void initAdapter(){
        adapter = new ShoppingListAdapter(this, android.R.layout.simple_list_item_1, allShoppingItems);
        shoppingView.setAdapter(adapter);
    }

    private void setupListeners(){
        generateShoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateItems();
            }
        });
        shoppingView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteShoppingItem(position);
                return false;
            }
        });
    }

    //missing
    private void generateItems(){
        Toast.makeText(this, "generating list...", Toast.LENGTH_SHORT).show();
    }

    /*
        @param position position in array to remove
     */
    private void deleteShoppingItem(final int position) {
        final ShoppingItem toDelete = allShoppingItems.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.shoppingDao().deleteItem(toDelete);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allShoppingItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }


    private void showAllShoppingItems(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                shoppingItems = db.shoppingDao().allShoppingItems();
                for (ShoppingItem shoppingItem1 : shoppingItems){
                    enterNewShoppingItem(shoppingItem1);
                }
            }
        }).start();
    }

    /*
        @param newShoppingItem new item to add to arrayList
     */
    private void enterNewShoppingItem(ShoppingItem newShoppingItem) {
        allShoppingItems.add(newShoppingItem);
        adapter.notifyDataSetChanged();
    }

    /*
       creates menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shoppinglist, menu);
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
                        db.shoppingDao().nukeShoppingItems();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                          //      clearList();
                            }
                        });
                    }
                }).start();
                Toast.makeText(this, "You nuked the list!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.CloseMe:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
