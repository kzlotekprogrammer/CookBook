package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.cookbook.database.entities.Unit;

@Dao
public interface UnitsDao {
    @Query("SELECT * FROM Unit")
    List<Unit> getAll();

    @Insert
    void insertAll(Unit... units);

    @Delete
    void delete(Unit unit);

    @Query("DELETE FROM Unit")
    void deleteAll();
}
