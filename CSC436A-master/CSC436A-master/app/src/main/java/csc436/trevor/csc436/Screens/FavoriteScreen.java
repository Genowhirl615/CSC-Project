package csc436.trevor.csc436.Screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import csc436.trevor.csc436.DatabaseHelperFavorite;
import csc436.trevor.csc436.R;

public class FavoriteScreen extends AppCompatActivity {
    DatabaseHelperFavorite mDatabaseHelper;
    private ListView mListView;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_screen);

        mListView = findViewById(R.id.favListView);
        mDatabaseHelper = new DatabaseHelperFavorite(this);
        Cursor currentFavorites = mDatabaseHelper.getData();

        if(currentFavorites.moveToFirst()) {
            populateListView();
        } else {
            Intent startIntent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(startIntent);
            toastMessage("Favorites now empty!");
        }

    }

    private void populateListView(){
        // Creates two String Arrays, listData and listDataUrl
        // Steps through contents of Favorites Database
        // Adds the name and url of each item in the Database to the String Arrays
        Cursor data = mDatabaseHelper.getData();
        final ArrayList<String> listData = new ArrayList<>();
        final ArrayList<String> listDataUrl = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
            listDataUrl.add(data.getString(2));

        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        // Click listener for Short Clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Creates two Strings, name and data
                // Upon clicking an item in the ListView, gets the name and url of the item clicked from the Database
                String name = adapterView.getItemAtPosition(i).toString();
                Cursor data = mDatabaseHelper.getItemID(name);
                int itemID = -1;

                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    String url = listDataUrl.get(i);
                    Intent urlLaunch = new Intent(Intent.ACTION_VIEW);
                    urlLaunch.setData(Uri.parse(url));
                    startActivity(urlLaunch);
                }
            }
        });

        // Click listener for Long Clicks
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                name = adapterView.getItemAtPosition(i).toString();

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(FavoriteScreen.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(FavoriteScreen.this);
                }
                // Creates a Dialog Alert, offering the choice of...
                builder.setTitle("Delete favorite")
                        .setMessage("Are you sure you want to delete " + name + " from Favorites?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete item from Favorite Database
                                mDatabaseHelper.deleteName(name);
                                Intent startIntent = new Intent(getApplicationContext(), FavoriteScreen.class);
                                startActivity(startIntent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            // Do not delete from Favorite Database
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
