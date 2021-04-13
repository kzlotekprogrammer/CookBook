package pl.cookbook;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.dao.ProductsDao;
import pl.cookbook.database.dao.RecipesDao;
import pl.cookbook.database.entities.Product;
import pl.cookbook.database.entities.Recipe;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("pl.cookbook", appContext.getPackageName());
    }

    @Test
    public void daoTest() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        ProductsDao productsDao = db.productsDao();

        Product product = new Product();
        product.name = "Test1";

        Product product2 = new Product();
        product.name = "Test2";

        db.productsDao().insertAll(product, product2);

        List<Product> productsList = productsDao.getAll();

        for (Product pro : productsList) {
            productsDao.delete(pro);
        }
    }

    @Test
    public void fillDatabaseWithTestRecipes() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        RecipesDao recipesDao = db.recipesDao();

        recipesDao.deleteAll();

        File imageFile = new File(getApplicationContext().getFilesDir(), "testImage.jpg");

        for (int i = 0; i < 10; i++) {
            Recipe recipe = new Recipe();
            recipe.name = "Test" + (i + 1);
            if (imageFile.exists() && i % 2 == 0)
                recipe.imageFileName = imageFile.getName();
            recipesDao.insertAll(recipe);
        }
    }
}