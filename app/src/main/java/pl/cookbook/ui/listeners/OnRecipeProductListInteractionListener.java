package pl.cookbook.ui.listeners;

import pl.cookbook.database.entities.RecipeProduct;

public interface OnRecipeProductListInteractionListener {
    void onDeleteRecipeProduct(RecipeProduct recipeProduct);
}
