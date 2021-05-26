package pl.cookbook.ui.listeners;

import pl.cookbook.database.entities.Product;

public interface OnProductsListItemInteractionListener {
    void onSelectProduct(Product product);

    void onDeleteProduct(Product product);

    void onEditProduct(Product product);
}
