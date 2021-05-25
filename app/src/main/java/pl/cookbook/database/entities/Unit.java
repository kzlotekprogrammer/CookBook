package pl.cookbook.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Unit {
    public Unit() {}

    @Ignore
    public Unit(String name) {
        this.name = name;
    }

    @PrimaryKey(autoGenerate = true)
    public long idUnit;

    public String name;

    public static Unit[] populateData() {
        return new Unit[] {
                new Unit("miligram"),
                new Unit("gram"),
                new Unit("dekagram"),
                new Unit("kilogram"),
                new Unit("mililitr"),
                new Unit("litr"),
                new Unit("łyżka"),
                new Unit("łyżeczka"),
                new Unit("szklanka"),
                new Unit("garść")
        };
    }
}
