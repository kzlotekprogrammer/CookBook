package pl.cookbook.database;

import android.content.Context;

import java.util.List;

import pl.cookbook.database.dao.RecipeProductsDao;
import pl.cookbook.database.dao.RecipesDao;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.database.entities.RecipeProduct;

public class DbHelper {
    public static void saveRecipe(Context context, Recipe recipe, List<RecipeProduct> recipeProductsList) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        RecipesDao recipesDao = appDatabase.recipesDao();
        RecipeProductsDao recipeProductsDao = appDatabase.recipeProductsDao();

        deleteRecipe(context, recipe.idRecipe);

        recipe.idRecipe = recipesDao.insert(recipe);

        for (RecipeProduct recipeProduct : recipeProductsList) {
            recipeProduct.idRecipe = recipe.idRecipe;
            recipeProductsDao.insert(recipeProduct);
        }
    }

    public static void deleteRecipe(Context context, long idRecipe) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        RecipesDao recipesDao = appDatabase.recipesDao();
        RecipeProductsDao recipeProductsDao = appDatabase.recipeProductsDao();

        recipeProductsDao.deleteByIdRecipe(idRecipe);
        recipesDao.deleteByIdRecipe(idRecipe);
    }
}
