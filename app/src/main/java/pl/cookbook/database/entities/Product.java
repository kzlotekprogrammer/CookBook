package pl.cookbook.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long idProduct;

    public String name;
}
