package pl.cookbook.ui;

import pl.cookbook.database.entities.Product;

public interface OnProductsListItemInteractionListener {
    void onSelectProduct(Product product);

    void onDeleteProduct(Product product);
}
