package pl.cookbook.ui;

import pl.cookbook.database.entities.RecipeProduct;

public interface OnRecipeProductListInteractionListener {
    void onDeleteRecipeProduct(RecipeProduct recipeProduct);
}
