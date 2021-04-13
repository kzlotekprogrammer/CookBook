package pl.cookbook.ui;

import pl.cookbook.database.entities.Recipe;

public interface OnRecipesListItemInteractionListener {
    void onSelectRecipe(Recipe recipe);
}
