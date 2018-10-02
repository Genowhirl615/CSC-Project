package csc436.trevor.csc436.Screens;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

import csc436.trevor.csc436.BufferReaderFunction;
import csc436.trevor.csc436.DatabaseHelper;
import csc436.trevor.csc436.DatabaseHelperFavorite;
import csc436.trevor.csc436.R;
import csc436.trevor.csc436.UPC;

public class HomeScreen extends AppCompatActivity {
    DatabaseHelper ingredientDB;
    DatabaseHelperFavorite favoriteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button inventoryBtn = findViewById(R.id.inventoryBtn);
        Button searchBtn = findViewById(R.id.recipeSearchBtn);
        Button scannerBtn = findViewById(R.id.scannerBtn);
        Button aboutBtn = findViewById(R.id.aboutBtn);
        Button favoriteBtn = findViewById(R.id.favoriteBtn);
        ingredientDB = new DatabaseHelper(this);
        favoriteDB = new DatabaseHelperFavorite(this);

        // Proceeds to Inventory Screen
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor currentInventory = ingredientDB.getData();
                // If Favorites Database is currently empty, instead display Toast Message about Database emptiness
                if(!currentInventory.moveToFirst()){
                    toastMessage("Inventory currently empty!");
                    Intent startIntent = new Intent(getApplicationContext(), AddInventoryScreen.class);
                    startActivity(startIntent);
                }else{
                    Intent startIntent = new Intent(getApplicationContext(), ListInventoryScreen.class);
                    startActivity(startIntent);
                }
            }
        });
        // Proceeds to Search screen
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor currentInventory = ingredientDB.getData();
                // If Inventory Database is currently empty, proceeds to Add Inventory Screen instead
                if(!currentInventory.moveToFirst()){
                    toastMessage("Inventory currently empty!");
                    Intent startIntent = new Intent(getApplicationContext(), AddInventoryScreen.class);
                    startActivity(startIntent);
                }else{
                    Intent startIntent = new Intent(getApplicationContext(), SearchScreen.class);
                    startActivity(startIntent);
                }
            }
        });

        // Proceeds to Barcode Scanning Screen
        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, CameraScreen.class);
                startActivityForResult(intent, 0);
            }
        });

        // Proceeds to About Screen
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AboutScreen.class);
                startActivity(startIntent);
            }
        });

        // Proceeds to Favorite Screen
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor currentFavorite = favoriteDB.getData();
                // If Favorites Database is currently empty, instead display Toast Message about Database emptiness
                if(!currentFavorite.moveToFirst()){
                    toastMessage("Favorites currently empty!");
                }else{
                    Intent startIntent = new Intent(getApplicationContext(), FavoriteScreen.class);
                    startActivity(startIntent);
                }
            }
        });
    }

    // Forces "Back" button to return to Home Screen, to avoid stack issues
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void toastMessage (String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    // When Camera Screen returns a Result Code : Success...
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0) {
            if(resultCode == CommonStatusCodes.SUCCESS){
                // As long as data from Camera Screen is not "null"...
                if(data != null){
                    // New barcode item (barcode) == parcable data from the Camera Screen
                    Barcode barcode = data.getParcelableExtra("barcode");
                    Intent intent = new Intent(getApplicationContext(), AddInventoryScreen.class);
                    Bundle extras = new Bundle();
                    // Creates UPC object name upcInformation, with its contents coming from the UPCOutput function of the BufferReaderFunction class

                    final ArrayList<UPC> upcInformation = new BufferReaderFunction().UPCOutput(barcode.displayValue);


                    String productName = upcInformation.get(0).getName();
                    // As long as the product name coming from upcInformation returns a name from the API call...
                    if (productName != "null") {
                        // Adds String productName to Bundle to be passed to Add Inventory Screen Intent
                        extras.putString("result", productName);
                        intent.putExtras(extras);
                        startActivity(intent);
                    } else {
                        toastMessage("No item found with that barcode!");
                    }

                }
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);}
    }
}
