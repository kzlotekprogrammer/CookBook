package pl.cookbook.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.cookbook.database.entities.RecipeProduct;

@Dao
public interface RecipeProductsDao {
    @Query("SELECT * FROM RecipeProduct WHERE idRecipe=:idRecipe")
    List<RecipeProduct> getByIdRecipe(long idRecipe);

    @Query("SELECT * FROM RecipeProduct WHERE idProduct=:idProduct")
    List<RecipeProduct> getByIdProduct(long idProduct);

    @Query("SELECT * FROM RecipeProduct WHERE idRecipe=:idRecipe AND idProduct=:idProduct")
    RecipeProduct getByIdRecipeIdProduct(long idRecipe, long idProduct);

    @Insert
    long insert(RecipeProduct recipeProduct);

    @Update
    int update(RecipeProduct recipeProduct);

    @Query("DELETE FROM RecipeProduct WHERE idRecipe=:idRecipe")
    void deleteByIdRecipe(long idRecipe);

    @Query("DELETE FROM RecipeProduct WHERE idRecipe=:idRecipe AND idProduct=:idProduct")
    void deleteByIdRecipeIdProduct(long idRecipe, long idProduct);
}
