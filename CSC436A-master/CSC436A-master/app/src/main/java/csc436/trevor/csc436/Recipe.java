package csc436.trevor.csc436;


public class Recipe {
    private String name;
    private String sourceUrl;
    private String image;
    private String[] ingredients;

    public Recipe() {};

    public Recipe(String name, String sourceUrl, String image, String ingredients) {
        this.name = name;
        this.sourceUrl = sourceUrl;
        this.image = image;
        this.ingredients = ingredients.split("\", \"");
    }

    public String getName() {
        return name;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getImage() {
        return image;
    }

    public String[] getIngredients() {
        return ingredients;
    }

}
