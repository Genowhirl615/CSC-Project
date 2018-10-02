package csc436.trevor.csc436.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import csc436.trevor.csc436.DatabaseHelper;
import csc436.trevor.csc436.R;

public class AddInventoryScreen extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private Button btnAdd;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_screen);
        editText = findViewById(R.id.editText);
        btnAdd = findViewById(R.id.btnAdd);
        mDatabaseHelper = new DatabaseHelper(this);
        Intent intent = getIntent();

        // Takes contents of a Bundle, if one exists
        if(intent.hasExtra("result")){
            String result = intent.getStringExtra("result").toString();
            result = result.replace("'","");
            result = result.replace(",", " ");
            result = result.trim();
            editText.setText(result);
        }

        // If the length of the text entry is greater than 0, replaces all " ' " with " " and adds
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newEntry = editText.getText().toString();
                if(editText.length() != 0){
                    newEntry = newEntry.replace("'","");
                    AddData(newEntry);
                    editText.setText("");
                }else{
                    toastMessage("Enter text");
                }
            }
        });
    }

    // Checks whether item already exists in Inventory Database
    public void AddData(String newEntry){
        newEntry = newEntry.trim();
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if(insertData){
            Intent startIntent = new Intent(getApplicationContext(), ListInventoryScreen.class);
            startActivity(startIntent);
        } else {
            toastMessage(newEntry + " already exists in Inventory");
        }
    }

    private void toastMessage (String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
