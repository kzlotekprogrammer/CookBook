package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.cookbook.database.entities.Recipe;

@Dao
public interface RecipesDao {
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM Recipe WHERE idRecipe=:idRecipe")
    Recipe getByIdRecipe(long idRecipe);

    @Insert
    long insert(Recipe recipe);

    @Update
    int update(Recipe recipe);

    @Query("DELETE FROM Recipe WHERE idRecipe=:idRecipe")
    void deleteByIdRecipe(long idRecipe);
}
