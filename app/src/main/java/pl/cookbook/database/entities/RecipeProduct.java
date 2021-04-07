package pl.cookbook.database.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"idRecipe", "idProduct"})
public class RecipeProduct {
    public int idRecipe;

    public int idProduct;

    public int idUnit;

    public int quantity;
}
