package pl.cookbook.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int idRecipe;

    public String name;

    public String description;

    public String imageFileName;
}
