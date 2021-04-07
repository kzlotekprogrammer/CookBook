package pl.cookbook.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import pl.cookbook.database.dao.ProductsDao;
import pl.cookbook.database.dao.RecipeProductsDao;
import pl.cookbook.database.dao.RecipesDao;
import pl.cookbook.database.dao.UnitsDao;
import pl.cookbook.database.entities.Product;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.database.entities.RecipeProduct;
import pl.cookbook.database.entities.Unit;

@Database(entities = {Product.class, Recipe.class, RecipeProduct.class, Unit.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductsDao productsDao();

    public abstract RecipesDao recipesDao();

    public abstract RecipeProductsDao recipeProductsDao();

    public abstract UnitsDao unitsDao();
}
