package csc436.trevor.csc436.Screens;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import csc436.trevor.csc436.DatabaseHelper;
import csc436.trevor.csc436.R;

public class EditInventoryScreen extends AppCompatActivity{

    private EditText editable_item;
    DatabaseHelper mDatabaseHelper;
    private String selectedName;
    private int selectedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory_screen);
        Button btnSave = findViewById(R.id.btnSave);
        editable_item = findViewById(R.id.editable_item);
        mDatabaseHelper = new DatabaseHelper(this);

        Intent recievedIntent = getIntent();

        selectedID = recievedIntent.getIntExtra("id", -1);
        selectedName = recievedIntent.getStringExtra("name");

        editable_item.setText(selectedName);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Converts contents of EditText to String
                String item = editable_item.getText().toString();
                // As long as this string is not empty...
                if(!item.equals("")){
                    try {
                        // Replaces all " ' " with " "
                        item = item.replace("'","");
                        // Trims (removes superfluous spaces at the beginning and end of String
                        item = item.trim();
                        // Updates the name of the item in the Database
                        mDatabaseHelper.updateName(item,selectedID,selectedName);
                        Intent startIntent = new Intent(getApplicationContext(), ListInventoryScreen.class);
                        startActivity(startIntent);
                    } catch (SQLiteConstraintException e){
                        // Protects against adjusting an existing items name to one that already exists
                        toastMessage("Error: " + item + " already in inventory!");
                        Intent startIntent = new Intent(getApplicationContext(), ListInventoryScreen.class);
                        startActivity(startIntent);
                    }
                }else{
                    toastMessage("Enter valid name");
                }
            }
        });

    }
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
