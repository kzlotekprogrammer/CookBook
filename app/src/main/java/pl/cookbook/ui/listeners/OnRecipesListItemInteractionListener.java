package pl.cookbook.ui.listeners;

import pl.cookbook.database.entities.Recipe;

public interface OnRecipesListItemInteractionListener {
    void onSelectRecipe(Recipe recipe);
}
