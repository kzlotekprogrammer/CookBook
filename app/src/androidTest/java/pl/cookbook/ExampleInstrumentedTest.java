package pl.cookbook;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.DbHelper;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.database.entities.RecipeProduct;

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
    public void recipeSaveAndDeleteTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //recipe
        Recipe newRecipe = new Recipe();
        newRecipe.name = "newRecipeName";
        newRecipe.description = "newRecipeDescription";

        //2 recipe products
        List<RecipeProduct> recipeProductList = new ArrayList<>();
        RecipeProduct recipeProduct = new RecipeProduct();
        recipeProduct.quantity = 1;
        recipeProduct.idProduct = 1;
        recipeProduct.idUnit = 1;
        recipeProductList.add(recipeProduct);
        recipeProduct = new RecipeProduct();
        recipeProduct.quantity = 2;
        recipeProduct.idProduct = 2;
        recipeProduct.idUnit = 2;
        recipeProductList.add(recipeProduct);

        DbHelper.saveRecipe(appContext, newRecipe, recipeProductList);

        AppDatabase appDatabase = AppDatabase.getInstance(appContext);
        assertNotNull(appDatabase.recipesDao().getByIdRecipe(newRecipe.idRecipe));
        assertEquals(2, appDatabase.recipeProductsDao().getByIdRecipe(newRecipe.idRecipe).size());

        DbHelper.deleteRecipe(appContext, newRecipe.idRecipe);
        assertNull(appDatabase.recipesDao().getByIdRecipe(newRecipe.idRecipe));
        assertEquals(0, appDatabase.recipeProductsDao().getByIdRecipe(newRecipe.idRecipe).size());
    }

    @Test
    public void add10TestRecipes() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "testImage.jpg");

        for (int i = 0; i < 10; i++) {
            Recipe recipe = new Recipe();
            recipe.idRecipe = -i;
            recipe.name = "Recipe " + (i + 1);
            if (imageFile.exists() && i % 2 == 0)
                recipe.imageFileName = imageFile.getName();
            DbHelper.saveRecipe(getApplicationContext(), recipe, new ArrayList<>());
        }
    }
}