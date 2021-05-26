package pl.cookbook.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.entities.Product;
import pl.cookbook.ui.adapters.ProductsAdapter;
import pl.cookbook.ui.listeners.OnProductsListItemInteractionListener;

//todo obsłużenie skanowania

public class ProductsListActivity extends AppCompatActivity implements OnProductsListItemInteractionListener {
    List<Product> productList;
    ProductsAdapter productsAdapter;
    RecyclerView recycler;

    public static final String INTENT_PRODUCT_ID = "productId";

    private static final int ADD_NEW_PRODUCT_REQUEST_CODE = 1;
    private static final int EDIT_PRODUCT_REQUEST_CODE = 2;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        recycler = findViewById(R.id.productsRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);

        loadProducts();
        setFilter(getIntent());

        CharSequence[] options = new CharSequence[] {
                getString(R.string.text_scan),
                getString(R.string.typing)
        };
        findViewById(R.id.btnAddProduct).setOnClickListener(v ->  {
            new AlertDialog.Builder(this)
                .setTitle(R.string.adding_method)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        //todo skanowanie
                    } else if (which == 1) {
                        startActivityForResult(EditTextActivity.createEditTextActivityIntent(ProductsListActivity.this,
                                R.string.edit_text_new_product_name,""), ADD_NEW_PRODUCT_REQUEST_CODE);
                    }
                })
                .create()
                .show();
        });
    }

    private void loadProducts() {
        AppDatabase database = AppDatabase.getInstance(this);
        productList = database.productsDao().getAll();
        productsAdapter = new ProductsAdapter(productList, this);
        recycler.setAdapter(productsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW_PRODUCT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Product product = new Product();
                if (data != null) {
                    String productName = data.getStringExtra(EditTextActivity.INTENT_TEXT);
                    if (productName != null && productName.length() > 0) {
                        product.name = productName;
                        AppDatabase.getInstance(this).productsDao().insert(product);
                        refresh();
                    }
                }
            }
        } else if (requestCode == EDIT_PRODUCT_REQUEST_CODE) {
            if (currentProduct == null)
                return;

            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String productName = data.getStringExtra(EditTextActivity.INTENT_TEXT);
                    if (productName != null && productName.length() > 0) {
                        currentProduct.name = productName;
                        AppDatabase.getInstance(this).productsDao().update(currentProduct);
                        refresh();
                    }
                }
            }

            currentProduct = null;
        }
    }

    private void refresh() {
        loadProducts();
        setFilter(getIntent());
    }

    private void setFilter(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null && query.length() > 0)
                productsAdapter.filter(query);
            else
                productsAdapter.clearFilter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_options_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                productsAdapter.clearFilter();
                return true;
            }
        });
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setFilter(intent);

        super.onNewIntent(intent);
    }

    @Override
    public void onSelectProduct(Product product) {
        Intent output = new Intent();
        output.putExtra(INTENT_PRODUCT_ID, product.idProduct);
        setResult(RESULT_OK, output);
        finish();
    }

    @Override
    public void onDeleteProduct(Product product) {
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        int recipeProductsCount = appDatabase.recipeProductsDao().getByIdProduct(product.idProduct).size();

        AlertDialog alertDialog;
        if (recipeProductsCount > 0) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(String.format(getString(R.string.product_cannot_be_removed), recipeProductsCount))
                    .setPositiveButton(R.string.ok, null)
                    .create();
        } else {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_deletion_product)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        AppDatabase.getInstance(this).productsDao().deleteByIdProduct(product.idProduct);
                        refresh();
                    })
                    .setNegativeButton(R.string.no, null)
                    .create();
        }
        alertDialog.show();
    }

    @Override
    public void onEditProduct(Product product) {
        currentProduct = product;
        startActivityForResult(EditTextActivity.createEditTextActivityIntent(this, R.string.edit_product, product.name), EDIT_PRODUCT_REQUEST_CODE);
    }
}