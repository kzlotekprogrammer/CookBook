package pl.cookbook.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Unit {
    @PrimaryKey(autoGenerate = true)
    public long idUnit;

    public String name;
}
