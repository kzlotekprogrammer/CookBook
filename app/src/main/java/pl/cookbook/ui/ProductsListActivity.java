package pl.cookbook.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.entities.Product;
import pl.cookbook.ui.adapters.ProductsAdapter;
import pl.cookbook.ui.listeners.OnProductsListItemInteractionListener;

import static pl.cookbook.ui.EditAddRecipeActivity.CAMERA_IMAGE;
import static pl.cookbook.ui.EditAddRecipeActivity.IMAGE_URI;
import static pl.cookbook.ui.EditAddRecipeActivity.PICK_IMAGE;

//todo obsłużenie skanowania

public class ProductsListActivity extends AppCompatActivity implements OnProductsListItemInteractionListener {

    List<Product> productList;
    ProductsAdapter productsAdapter;
    RecyclerView recycler;

    public static final String INTENT_PRODUCT_ID = "productId";

    private static final int ADD_NEW_PRODUCT_REQUEST_CODE = 1;
    private static final int EDIT_PRODUCT_REQUEST_CODE = 2;
//    private static final int ADD_NEW_PRODUCT_BY_IMAGE_REQUEST_CODE = 3;
//    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 4;

    private Product currentProduct;

    private AlertDialog dialog;

    Uri imageUri;
    private String currentPhotoPath;



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
                        createChooseCameraGalleryDialog();
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

        else if(requestCode == CAMERA_IMAGE || requestCode == PICK_IMAGE){
            if(resultCode == RESULT_OK){
                Intent intent = new Intent(ProductsListActivity.this, ImageActivity.class);
                intent.putExtra(IMAGE_URI, imageUri.toString());
                intent.putExtra("requestCode", requestCode);
                if(currentPhotoPath != null)
                    intent.putExtra("currentPhotoPath", currentPhotoPath);

                startActivityForResult(intent, ADD_NEW_PRODUCT_REQUEST_CODE);
            }
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



    public void createChooseCameraGalleryDialog(){

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final View chooseCameraGalleryPopupView = getLayoutInflater().inflate(R.layout.camera_gallery_pop_up, null);

        Button galleryBtn = chooseCameraGalleryPopupView.findViewById(R.id.buttonGallery);
        Button cameraBtn = chooseCameraGalleryPopupView.findViewById(R.id.buttonCamera);

        dialogBuilder.setView(chooseCameraGalleryPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        cameraBtn.setOnClickListener(v -> {openCamera(); dialog.dismiss();});

        galleryBtn.setOnClickListener(v -> {openGallery(); dialog.dismiss();});
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                //photoUri = FileProvider.getUriForFile(EditAddRecipeActivity.this, "pl.cookbook.fileprovider", photoFile);
                imageUri = FileProvider.getUriForFile(ProductsListActivity.this, "pl.cookbook.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(takePictureIntent, CAMERA_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String fileName = "IMAGE_" + timeStamp + "_";

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


}