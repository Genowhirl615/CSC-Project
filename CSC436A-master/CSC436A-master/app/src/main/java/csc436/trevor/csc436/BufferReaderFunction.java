package csc436.trevor.csc436;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


// Class that includes both Recipe Output and UPC Output function, to facilitate API calls
public class BufferReaderFunction {

    public ArrayList<Recipe> RecipeOutput(String ingredients) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        // Creates a new ArrayList of type Recipe called recipes
        ArrayList<Recipe> recipes = new ArrayList<>();

        try {
            // Calls the URL Return function in the API Caller class ir order to retrieve the proper URL to query edamam.com
            String currentUrl = new APICaller().URLReturn(ingredients);

            // Opens an HTTP connection to the String currentURL
            URL url = new URL (currentUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Creates an Input Stream from the text returned from edamam.com
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            // Appends each line from the Input Stream to a String Buffer called "buffer"
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            // Creates a JSON Object out of the text from the object "buffer" after casting it toString()
            JSONObject bufferJson = new JSONObject(buffer.toString());
            // Creates a JSON Array out of the contents of "hits" of the JSON Object "bufferJson"
            JSONArray recipesJSON = bufferJson.getJSONArray("hits");

            // Takes the first 10 items in "hits"...
            for (int i = 0; i < 10; i++) {
                // Get the JSON object in index "i"
                JSONObject recipeArrayJSON = recipesJSON.getJSONObject(i);
                // Get the JSON object named "recipe"
                JSONObject recipeJSON = recipeArrayJSON.getJSONObject("recipe");
                // Create a String called "name", and passes it the value of recipeJSON's "label"
                String name = recipeJSON.getString("label");
                // Create a String called "sourceUrl", and passes it the value of recipeJSON's "url"
                String sourceUrl = recipeJSON.getString("url");
                // Create a String called "image", and passes it the value of recipeJSON's "image"
                String image = recipeJSON.getString("image");
                // Creates a String called "ingredients", and adds the contents of recipeJSON's "ingredientLines"
                String ingredientLines = recipeJSON.getJSONArray("ingredientLines").toString();

                // Creates an Object of type Recipe, using the name, sourceUrl, image, and ingredients lines from the aforementioned castings
                Recipe recipe = new Recipe(name, sourceUrl, image, ingredientLines);
                // Adds the "recipe" Object to the "recipes" ArrayList
                recipes.add(recipe);
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return ArrayList<Recipe> called "recipes"
        return recipes;
    }

    public ArrayList<UPC> UPCOutput(String upc) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        // Creates a new ArrayList of type UPC called upcDetails
        ArrayList<UPC> upcDetails = new ArrayList<>();

        try {
            // Calls the URL Return function in the API Caller class ir order to retrieve the proper URL to query barcodelookup.com
            String currentUrl = new APICaller().BarcodeLookup(upc);
            URL url = new URL (currentUrl);
            // Opens an HTTP connection to the String currentURL
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Creates an Input Stream from the text returned from barcodelookup.com
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            // Appends each line from the Input Stream to a String Buffer called "buffer"
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            // Creates a JSON Object out of the text from the object "buffer" after casting it toString()
            JSONObject bufferJson = new JSONObject(buffer.toString());
            // Creates a JSON Array out of the contents of "result" of the JSON Object "bufferJson"
            JSONArray upcJSONArray = bufferJson.getJSONArray("products");

            // Get the first index of the JSON Array upcJSONArray
            JSONObject barcodeJSON = upcJSONArray.getJSONObject(0);
            // Create a String called "barcode", and pass it the value of barcodeJSON's "barcode"

            //String barcode = barcodeJSON.getString("barcode");
            //String name = barcodeJSON.getString("product_name");
            String barcode = barcodeJSON.getString("barcode_number");
            String name = barcodeJSON.getString("product_name");

            // Create new UPC Object named currentUPC, and append it to ArrayList upcDetails
            UPC currentUPC = new UPC(barcode,name);
            upcDetails.add(currentUPC);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return ArrayList<UPC> called "upcDetails"
        return upcDetails;
    }

}