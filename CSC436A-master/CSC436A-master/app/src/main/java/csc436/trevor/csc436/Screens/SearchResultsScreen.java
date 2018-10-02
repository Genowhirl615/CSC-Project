package csc436.trevor.csc436.Screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.Toast;


import com.jgabrielfreitas.core.BlurImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import csc436.trevor.csc436.BufferReaderFunction;
import csc436.trevor.csc436.DatabaseHelperFavorite;
import csc436.trevor.csc436.R;
import csc436.trevor.csc436.Recipe;

public class SearchResultsScreen extends AppCompatActivity {
    TextView recipeName;
    TextView recipeURL;
    TextView ingredientsTxt;
    TextView pageNumber;

    String imageStr;
    ImageView imageView;
    BlurImageView blurImage;
    FloatingActionButton favorite;
    DatabaseHelperFavorite mDatabaseHelper;
    // Initializes a page counter
    int counter = 0;
    ArrayList<Recipe> returnArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle results = getIntent().getExtras();
        String choices = results.getString("choices");

        Button prevBtn = findViewById(R.id.prevBtn);
        Button nextBtn = findViewById(R.id.nextBtn);
        favorite = findViewById(R.id.favoriteFab);
        mDatabaseHelper = new DatabaseHelperFavorite(this);

        recipeName = findViewById(R.id.recipeName);
        recipeURL = findViewById(R.id.recipeURL);
        ingredientsTxt = findViewById(R.id.ingredientTxt);
        ingredientsTxt.setMovementMethod(new ScrollingMovementMethod());
        pageNumber = findViewById(R.id.pageNum);
        imageView = findViewById(R.id.imageView);
        blurImage = findViewById(R.id.blurImage);

        recipeAsync recipesSync = new recipeAsync();
        recipesSync.execute(choices);

        // Click listener that moves forward one recipe in the returnArray ArrayList
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter < returnArray.size() - 1) {
                    counter++;
                } else {
                    counter = 0;
                }
                recipeSearch(counter, returnArray);
            }
        });

        // Click listener that makes backwards one recipe in the returnArray ArrayList
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                if (counter < 0) {
                    counter = returnArray.size() - 1;
                }
                recipeSearch(counter, returnArray);
            }
        });

        // Click listener that adds currently viewed recipe to the Favorite Database
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates String 'name' with the current value of the viewed recipe's name
                String name = returnArray.get(counter).getName();
                // Creates String 'url' with the current value of the viewed recipe's url
                String url = returnArray.get(counter).getSourceUrl();
                // Removes any " ' " from the recipe's name, and replaces with " "
                name = name.replace("'","");
                // Attempts to add recipe to Favorte Database
                boolean insertData = mDatabaseHelper.addData(name,url);
                // If it does not already exist...
                if(insertData) {
                    toastMessage(name + " added to Favorites!");
                // If it already exists
                }else{
                    toastMessage(name + " already a Favorite!");
                }
            }
        });
}
    // Function that sets the XML elements to values derived from returnArray
    protected void recipeSearch(int i, ArrayList<Recipe> recipeArray) {

        // Sets recipeName TextView to the name pulled from the recipe at index 'i' of returnArray
        recipeName.setText(recipeArray.get(i).getName());
        // Sets recipeURL TextView to the url pulled from the recipe at index 'i' of returnArray
        recipeURL.setText(recipeArray.get(i).getSourceUrl());
        // Prepares the url corresponding recipe's image
        imageStr = recipeArray.get(i).getImage();
        // Creates a String Builder using each ingredient pulled from the recipe's ingredient list
        StringBuilder ingredients = new StringBuilder();
        for (String s : recipeArray.get(i).getIngredients()) {
            if (ingredients.length() > 0) {
                ingredients.append(s);
            }
            ingredients.append(s);
        }

        // Pulls a substring of the list of ingredients
        String newIngredients = ingredients.toString().substring(2,ingredients.length()-2);
        // Adjusts formatting
        String finalIngredients = newIngredients.replaceAll("\",\"","\n\n");
        // Sets ingredients scrollable TextView to finallized ingredients list
        ingredientsTxt.setText(finalIngredients.toString());
        // Adjusts page number to be the current index of the returnArray, plus 1 to account for 0-indexing
        pageNumber.setText(Integer.toString(i + 1));
        // Sets ImageView to the results of the loadImageURL function
        loadImageFromURL(imageStr, imageView, blurImage);


        }
    // Creates the recipe image overlayed on the the blurred version of itself
    public boolean loadImageFromURL(String imageStr, ImageView imageView, BlurImageView blurImage){
        try{
            URL imageURL = new URL(imageStr);
            HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
            HttpURLConnection conn2 = (HttpURLConnection) imageURL.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            InputStream ib = conn2.getInputStream();
            Bitmap bitmapSave = BitmapFactory.decodeStream(ib);

            Bitmap imageRounded = Bitmap.createBitmap(bitmapSave.getWidth(), bitmapSave.getHeight(), bitmapSave.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint mpaint = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setShader(new BitmapShader(bitmapSave, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0,0, bitmapSave.getWidth(), bitmapSave.getHeight())), 100, 100, mpaint);

            Bitmap isave = BitmapFactory.decodeStream(is);


            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), isave);
            roundedBitmapDrawable.setCircular(true);

            imageView.setImageDrawable(roundedBitmapDrawable);

            blurImage.setImageBitmap(imageRounded);
            blurImage.setBlur(5);

            return true;
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    // Asynchronous task function, for limiting lock-up during BufferReaderFunction calls
    private class recipeAsync extends AsyncTask<String, String, ArrayList<Recipe>> {

        // Establish a loading symbol
        ProgressDialog progressDialog;

        @Override
        // Runs BufferReaderFunction in the background using the two variables (choiceOne / choiceTwo) from Bundle
        protected ArrayList<Recipe> doInBackground(String... strings) {

            // Creates an ArrayList of type Recipe, that has the values provided by BufferReaderFunction
            ArrayList<Recipe> newArray = new BufferReaderFunction().RecipeOutput(strings[0]);
            // Turns global ArrayList<Recipe> (returnArray) into the results of the local ArrayList<Recipe> (newArray)
            return returnArray = newArray;
        }

        @Override
        // Runs in the onCreate method
        protected void onPreExecute() {
            // Turns on progress bar
            progressDialog = ProgressDialog.show(SearchResultsScreen.this,"Loading","Loading Recipes");
            super.onPreExecute();
        }

        @Override
        // Runs after doInBackground finishes
        protected void onPostExecute(ArrayList<Recipe> recipes) {

            // As long as the returnArray in doInBackground is not of length 0, run RecipeSearch on it
            if(returnArray.size()!= 0) {
                recipeSearch(0, returnArray);
                progressDialog.dismiss();
            // Otherwise, return to the SearchScreen, and alert user to the fact no recipes were found using chosen ingredients
            } else {
                Intent startIntent = new Intent(getApplicationContext(), SearchScreen.class);
                toastMessage("No recipes found using these ingredients!");
                startActivity(startIntent);
            }
            super.onPostExecute(recipes);
        }

    }
    private void toastMessage (String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}

