package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.cookbook.database.entities.Unit;

@Dao
public interface UnitsDao {
    @Query("SELECT * FROM Unit")
    List<Unit> getAll();

    @Query("SELECT * FROM Unit WHERE idUnit=:idUnit")
    Unit getByIdUnit(long idUnit);

    @Insert
    long insert(Unit unit);

    @Update
    int update(Unit unit);

    @Query("DELETE FROM Unit WHERE idUnit=:idUnit")
    void deleteByIdUnit(long idUnit);
}
