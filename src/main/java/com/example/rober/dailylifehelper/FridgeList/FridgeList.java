package com.example.rober.dailylifehelper.FridgeList;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rober.dailylifehelper.R;
import com.example.rober.dailylifehelper.RoomDB.MyDatabase;

import java.util.ArrayList;

public class FridgeList extends AppCompatActivity {

    private EditText editTextValue;
    private Button addButton;
    private Spinner spinnerCategory;
    private Spinner spinnerContent;
    private ListView listViewFridge;

    private ArrayAdapter<CharSequence> adapterSpinnerContent;

    private String fridgeEntryName;
    private String fridgeEntrySpecification;
    private String[] existingFridgeContentName = new String[100];

    private String newExistingFridgeContentName;

    private double fridgeContentValue;
    private FridgeContent fridgeContent;
    private FridgeContent modifiedFridgeContent;

    private FridgeContent existingFridgeContent;

    private FridgeListAdapter adapter;

    private static final int MILK_PRODUCT_ID = 0;
    private static final int MEAT_ID = 1;
    private static final int HERB_ID = 2;
    private static final int VEGETABLE_ID = 3;
    private static final int LIQUID_ID = 4;

    private ArrayList<FridgeContent> allFridgeContents = new ArrayList<>();
    private FridgeContent[] fridgeContents = new FridgeContent[100];


    //toDo create database in MainActivity ?
    private MyDatabase db;

    /*
        does't work --> because of methods ?
        private final String DB_NAME = getResources().getString(R.string.db_name);
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        setupDatabase();
        setupCategorySpinner();
        setupListeners();
        initAdapter();
        showAllFridgeContents();
    }

    private void setupUI() {
        setContentView(R.layout.list_fridge);
        editTextValue = (EditText) findViewById(R.id.editTextValueFridge);
        addButton = (Button) findViewById(R.id.buttonFridge);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerContent = (Spinner) findViewById(R.id.spinnerContent);
        listViewFridge = (ListView) findViewById(R.id.listViewFridge);
    }

    /*
        creates database
     */
    private void setupDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, getResources().getString(R.string.db_name)).fallbackToDestructiveMigration().build();
    }

    /*
        create Adapter for categorySpinner
        add adapter to spinner
        https://www.youtube.com/watch?v=on_OrrX7Nw4
     */
    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapterSpinnerCategory = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinnerCategory);

    }

    private void setupContentSpinner(int resId){
        adapterSpinnerContent = ArrayAdapter.createFromResource(FridgeList.this, resId, android.R.layout.simple_spinner_item);
        adapterSpinnerContent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContent.setAdapter(adapterSpinnerContent);
    }

    /*
        setup listeners to spinner, button and listView
        https://stackoverflow.com/questions/29474829/how-to-make-spinner-depends-on-another-spinner
     */
    private void setupListeners() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerCategory.getSelectedItem().toString();
                if (selectedItem.equals(getString(R.string.milkProduct))){
                    setupContentSpinner(R.array.milkProduct);
                    setSpecification(MILK_PRODUCT_ID);
                } else if (selectedItem.equals(getResources().getString(R.string.meat))){
                    setupContentSpinner(R.array.meat);
                    setSpecification(MEAT_ID);
                } else if (selectedItem.equals(getResources().getString(R.string.herb))){
                    setupContentSpinner(R.array.herb);
                    setSpecification(HERB_ID);
                } else if (selectedItem.equals(getResources().getString(R.string.vegetable))){
                    setupContentSpinner(R.array.vegetable);
                    setSpecification(VEGETABLE_ID);
                } else if (selectedItem.equals(getResources().getString(R.string.liquid))){
                    setupContentSpinner(R.array.liquid);
                    setSpecification(LIQUID_ID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fridgeEntryName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textInput = editTextValue.getText().toString();
                if (!textInput.equals("")) {
                    fridgeContentValue = Double.parseDouble(textInput);
                    if (!entryIsExisting()) {
                        addFridgeEntry();
                    } else {
                        sumUpExistingContent();
                    }
                }
            }
        });
        listViewFridge.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFridgeContent(position);
                return false;
            }
        });
    }

    private void setSpecification(int id) {
        switch (id) {
            case MILK_PRODUCT_ID:
            case MEAT_ID:
            case HERB_ID:
                fridgeEntrySpecification = getResources().getString(R.string.gram);
                break;
            case VEGETABLE_ID:
                fridgeEntrySpecification = getResources().getString(R.string.pieces);
                break;
            case LIQUID_ID:
                fridgeEntrySpecification = getResources().getString(R.string.liter);
                break;
            default:
                fridgeEntrySpecification = "";
                break;
        }
    }

    /*
        compares new fridgeEntryName w/ fridgeEntryName from db
        compares new fridgeEntrySpecification w/ fridgeEntrySpecification from db
        @return true if entry exists
     */
    private boolean entryIsExisting() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                existingFridgeContentName = db.fridgeContentDao().fetchFridgeContentNameByName(fridgeEntryName);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            Log.d("LOG_TAG", e.toString());
        }
        for (String name : existingFridgeContentName) {
            if (name != null) {
                if (name.equals(fridgeEntryName)) {
                    newExistingFridgeContentName = name;
                    return true;
                }
            }

        }
        return false;
    }

    /*
        gets existingFridgeContent
        uses method to sum up old and new value
        creates modifiedFridgeContent
        insert it into db
        removes old fridgeContent from db
     */
    private void sumUpExistingContent() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                existingFridgeContent = db.fridgeContentDao().
                        fetchExistingFridgeContent(newExistingFridgeContentName);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            Log.d("LOG_TAG", e.toString());
        }


        double sumValue = getSumValue();

        deleteOldEntry();

        modifiedFridgeContent = createModifiedFridgeContent(sumValue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.fridgeContentDao().deleteFridgeContentItemByName(newExistingFridgeContentName);
                db.fridgeContentDao().insertSingleFridgeContent(modifiedFridgeContent);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextValue.setText("");
                        enterNewFridgeContent(modifiedFridgeContent);
                    }
                });
            }
        }).start();
    }

    /*
        @param val string of new calculated value
        @return new FridgeContent
     */
    private FridgeContent createModifiedFridgeContent(double val) {
        FridgeContent newModifiedFridgeContent = new FridgeContent();
        newModifiedFridgeContent.setFridgeContentName(newExistingFridgeContentName);
        newModifiedFridgeContent.setFridgeContentValue(val);
        newModifiedFridgeContent.setFridgeContentSpecification(fridgeEntrySpecification);
        return newModifiedFridgeContent;
    }

    /*
        @return string of new calculated value
     */
    private double getSumValue() {
            double oldValue = existingFridgeContent.getFridgeContentValue();
            double newValue = fridgeContentValue;
            return oldValue + newValue;
    }

    /*
        removes fridgeContent if there is one
        else it removes the modifiedFridgeContent
        notifies adapter
     */
    private void deleteOldEntry() {
        if (sameEntryAsBefore()) {
            allFridgeContents.remove(allFridgeContents.size() - 1);
        } else {
            allFridgeContents.remove(searchFridgeContents(existingFridgeContent));
        }
        adapter.notifyDataSetChanged();
    }

    /*
        @param fridgeContentToSearch item to search in arrayList
        @return fridgeContent w/ given position
     */
    private FridgeContent searchFridgeContents(FridgeContent fridgeContentToSearch) {
        int length = allFridgeContents.size();
        for (int i = 0; i < length; i++) {
            if (fridgeContentToSearch.getFridgeContentName().equals(allFridgeContents.get(i).getFridgeContentName())) {
                return allFridgeContents.get(i);
            }
        }
        return null;
    }

    /*
        @return true if entry is the same as before
     */
    private boolean sameEntryAsBefore() {
        FridgeContent lastFridgeContent = allFridgeContents.get(allFridgeContents.size() - 1);
        String lastFridgeContentName = lastFridgeContent.getFridgeContentName();
        if (lastFridgeContentName.equals(fridgeEntryName)) {
            return true;
        } else {
            return false;
        }
    }

    /*
        creates new fridgeContent
        adds fridgeContent to db
     */
    private void addFridgeEntry() {
        fridgeContent = new FridgeContent();
        fridgeContent.setFridgeContentName(fridgeEntryName);
        fridgeContent.setFridgeContentValue(fridgeContentValue);
        fridgeContent.setFridgeContentSpecification(fridgeEntrySpecification);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.fridgeContentDao().insertSingleFridgeContent(fridgeContent);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextValue.setText("");
                        enterNewFridgeContent(fridgeContent);
                    }
                });
            }
        }).start();
    }

    /*
        deletes fridgeContent with given position
        @param position position of item in arrayList to delete
     */
    private void deleteFridgeContent(int position) {
        final FridgeContent fridgeContentToDelete = allFridgeContents.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.fridgeContentDao().deleteFridgeContent(fridgeContentToDelete);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allFridgeContents.remove(fridgeContentToDelete);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /*
        create new Adapter and init it
     */
    private void initAdapter() {
        adapter = new FridgeListAdapter(this, android.R.layout.simple_list_item_1, allFridgeContents);
        listViewFridge.setAdapter(adapter);
    }

    /*
        shows all fridgeContents
     */
    private void showAllFridgeContents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fridgeContents = db.fridgeContentDao().allFridgeContents();
                for (FridgeContent fridgeContent1 : fridgeContents) {
                    enterNewFridgeContent(fridgeContent1);
                }
            }
        }).start();
    }

    /*
        enters new fridgeContent
        @param newFridgeContent newFridgeContent to enter
     */
    private void enterNewFridgeContent(FridgeContent newFridgeContent) {
        allFridgeContents.add(newFridgeContent);
        adapter.notifyDataSetChanged();
    }

    /*
        clears list
     */
    private void clearList() {
        allFridgeContents.clear();
        adapter.notifyDataSetChanged();
    }

    /*
        creates menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fridgelist, menu);
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
                        db.fridgeContentDao().nukeFridgeContents();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
