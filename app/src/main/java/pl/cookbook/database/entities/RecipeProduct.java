package pl.cookbook.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"idRecipe", "idProduct"})
public class RecipeProduct {
    public long idRecipe;

    public long idProduct;

    public long idUnit;

    public int quantity;

    @Ignore
    public String productName;
}
