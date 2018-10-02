package csc436.trevor.csc436.Screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.util.ArrayList;
import csc436.trevor.csc436.DatabaseHelper;
import csc436.trevor.csc436.R;

public class ListInventoryScreen extends AppCompatActivity{

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;
    FloatingActionButton fab;
    String name;
    Integer id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory_screen);
        mListView = findViewById(R.id.listView);
        mDatabaseHelper = new DatabaseHelper(this);
        Cursor currentInventory = mDatabaseHelper.getData();
        fab = findViewById(R.id.fab);

        if(currentInventory.moveToFirst()) {
            populateListView();
        } else {
            Intent startIntent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(startIntent);
            toastMessage("Inventory now empty!");
        }
        populateListView();

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), AddInventoryScreen.class);
                startActivity(startIntent);
            }
        });
    }

    // Pulls all contents from Inventory Database
    private void populateListView(){
        Cursor data = mDatabaseHelper.getData();
        // Each item in Inventory Database is added to a String ArrayList called listData
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        // Click listener for Short Clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Creates a String called 'name' with the value of the Name of the item clicked
                String name = adapterView.getItemAtPosition(i).toString();

                Cursor data = mDatabaseHelper.getItemID(name);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Intent editScreenIntent = new Intent(ListInventoryScreen.this, EditInventoryScreen.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("name",name);
                    startActivity(editScreenIntent);
                }
            }
        });

        // Click listener for Long Clicks
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                name = adapterView.getItemAtPosition(i).toString();
                Cursor data = mDatabaseHelper.getItemID(name);
                int itemID = 0;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                     id = itemID;
                }

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ListInventoryScreen.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ListInventoryScreen.this);
                }
                // Creates a Dialog Alert, offering the choice of...
                builder.setTitle("Delete ingredient")
                        .setMessage("Are you sure you want remove " + name + " from your Inventory?")
                        // Delete item from Favorite Database
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabaseHelper.deleteName(id,name);
                                Intent startIntent = new Intent(getApplicationContext(), ListInventoryScreen.class);
                                startActivity(startIntent);
                            }
                        })
                        // Do not delete from Favorite Database
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .show();

                return true;
            }
        });
    }

    // Forces "Back" button to return to Home Screen, to avoid stack issues
    public void onBackPressed() {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }
    private void toastMessage (String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
