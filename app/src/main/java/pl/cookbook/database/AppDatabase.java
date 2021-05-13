package pl.cookbook.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.cookbook.database.dao.ProductsDao;
import pl.cookbook.database.dao.RecipeProductsDao;
import pl.cookbook.database.dao.RecipesDao;
import pl.cookbook.database.dao.UnitsDao;
import pl.cookbook.database.entities.Product;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.database.entities.RecipeProduct;
import pl.cookbook.database.entities.Unit;

@Database(entities = {Product.class, Recipe.class, RecipeProduct.class, Unit.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "cookBook.db";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).
                    allowMainThreadQueries().build();
        }
        return instance;

    }

    public abstract ProductsDao productsDao();

    public abstract RecipesDao recipesDao();

    public abstract RecipeProductsDao recipeProductsDao();

    public abstract UnitsDao unitsDao();
}
