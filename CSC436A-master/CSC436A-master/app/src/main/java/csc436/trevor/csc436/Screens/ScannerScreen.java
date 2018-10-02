package csc436.trevor.csc436.Screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import csc436.trevor.csc436.R;


public class ScannerScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_screen);
    }

    // Proceeds to Camera Screen
    public void scanBarcode(View v){
        Intent intent = new Intent(this, CameraScreen.class);
        startActivityForResult(intent, 0);
    }

    // If the Barcode Scanning activity results in a value being pulled...
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0) {
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    // Barcode is passed as a String extra to the Add Inventory Screen
                    Barcode barcode = data.getParcelableExtra("barcode");
                    Intent intent = new Intent(getApplicationContext(), AddInventoryScreen.class);
                    Bundle extras = new Bundle();
                    extras.putString("result",barcode.displayValue);
                    intent.putExtras(extras);

                    startActivity(intent);
                }
            }
        } else{
        super.onActivityResult(requestCode, resultCode, data);}
    }

    // Forces "Back" button to return to Home Screen, to avoid stack issues
    public void onBackPressed()
    {
        Intent intent = new Intent(this,HomeScreen.class);
        startActivity(intent);
    }

}
