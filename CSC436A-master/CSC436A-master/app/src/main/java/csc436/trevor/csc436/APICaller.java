package csc436.trevor.csc436;


import android.util.Log;

import java.io.UnsupportedEncodingException;

// Function to return a String containing the necessary URL to query API servers
public class APICaller {

    // Returns a String that can be used as a URL to return JSON text from edamam.com
    public String URLReturn(String ingredients) throws UnsupportedEncodingException {
        // Replaces all " " with "%20" in order to form proper URLs
        ingredients = java.net.URLEncoder.encode(ingredients, "UTF-8").replaceAll(" ", "%20");
        ingredients = ingredients.replace("/","'");

        // Creates a new String containing the query text for edamam.com, concatenated with the two chosen ingredients
        String newURL = "https://api.edamam.com/search?q="
                + ingredients
                + "&app_id="
                + "8cd2295f"
                + "&app_key="
                + "37300b7f024f93aa3e49fb3c37e4e77c";
        Log.d("###################",newURL);
        return newURL;
    }

    // Returns a String that can be used as a URL that returns JSON information from barcodelookup.com
    public String BarcodeLookup(String upc){
        // Creates a new String containing the query text for barcodelookup.com
        String newURL = "https://api.barcodelookup.com/v2/products?barcode="
                + upc
                + "&key="
                + "zbr09a8c5e4qw1pe86n7hz66chohqh";
        return newURL;
    }


}
