package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.cookbook.database.entities.RecipeProduct;

@Dao
public interface RecipeProductsDao {
    @Query("SELECT * FROM RecipeProduct")
    List<RecipeProduct> getAll();

    @Insert
    void insertAll(RecipeProduct... recipeProducts);

    @Delete
    void delete(RecipeProduct recipeProduct);

    @Query("DELETE FROM RecipeProduct")
    void deleteAll();
}
