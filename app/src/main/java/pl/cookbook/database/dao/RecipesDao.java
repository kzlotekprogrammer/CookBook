package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.cookbook.database.entities.Recipe;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAll();

    @Insert
    void insertAll(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);

    @Query("DELETE FROM Recipe")
    void deleteAll();
}
