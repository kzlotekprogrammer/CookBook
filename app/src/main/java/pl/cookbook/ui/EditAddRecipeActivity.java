package pl.cookbook.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.FileProvider;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.DbHelper;
import pl.cookbook.database.dao.ProductsDao;
import pl.cookbook.database.entities.Product;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.database.entities.RecipeProduct;
import pl.cookbook.database.entities.Unit;
import pl.cookbook.ui.adapters.RecipeProductsAdapter;
import pl.cookbook.ui.listeners.OnRecipeProductListInteractionListener;

public class EditAddRecipeActivity extends AppCompatActivity implements OnRecipeProductListInteractionListener {
    
    private static final String INTENT_ID_RECIPE = "idRecipe";
    public static final int PICK_IMAGE = 1;
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int ADD_PRODUCT_REQUEST_CODE = 3;
    public static final int CAMERA_IMAGE = 4;

    Button editImageBtn;
    Button editProductsBtn;
    Button editRecipeBtn;
    Uri imageUri;
    RecyclerView recycler;

    Recipe recipe;
    List<RecipeProduct> recipeProductList;
    List<Unit> unitList;

    EditText titleEditText;
    EditText executionEditText;

    public static Intent createEditAddRecipeActivityIntent(final Context context, final long idRecipe) {
        Intent intent = new Intent(context, EditAddRecipeActivity.class);
        intent.putExtra(INTENT_ID_RECIPE, idRecipe);
        return  intent;
    }

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cameraBtn;
    private Button galleryBtn;

    private String currentPhotoPath;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_recipe);

        buildActivity();
    }

    private void buildActivity() {
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        long idRecipe = intent.getLongExtra(INTENT_ID_RECIPE, 0);
        if (idRecipe != 0)
            recipe = appDatabase.recipesDao().getByIdRecipe(idRecipe);

        //if new recipe
        if (recipe == null)
            recipe = new Recipe();
        recipeProductList = appDatabase.recipeProductsDao().getByIdRecipe(recipe.idRecipe);
        unitList = appDatabase.unitsDao().getAll();

        recycler = findViewById(R.id.productsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);

        titleEditText = findViewById(R.id.titleEditText);
        titleEditText.setText(recipe.name);
        executionEditText = findViewById(R.id.executionEditText);
        executionEditText.setText(recipe.description);

        //todo recipe.imageFileName - ustawienie zdjęcia w imageView

        findViewById(R.id.btnSaveRecipe).setOnClickListener(v -> {
            recipe.name = titleEditText.getText().toString();
            recipe.description = executionEditText.getText().toString();
            //todo zapis ścieżki do zdjęcia

            DbHelper.saveRecipe(this, recipe, recipeProductList);
            setResult(RESULT_OK);
            finish();
        });

        ProductsDao productsDao = appDatabase.productsDao();
        for (RecipeProduct recipeProduct : recipeProductList) {
            Product product = productsDao.getByIdProduct(recipeProduct.idProduct);
            recipeProduct.productName = product.name;
        }

        refreshProductsList();

        editImageBtn = findViewById(R.id.editImageBtn);
        editImageBtn.setOnClickListener(v -> {
            createChooseCameraGalleryDialog();
        });
        editProductsBtn = findViewById(R.id.editProductsBtn);
        editProductsBtn.setOnClickListener(v -> {
//            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//            startActivityForResult(gallery, PICK_IMAGE);
            startActivityForResult(new Intent(this, ProductsListActivity.class), ADD_PRODUCT_REQUEST_CODE);
        });

        editRecipeBtn = findViewById(R.id.editRecipeBtn);
        editRecipeBtn.setOnClickListener(v -> {
            createChooseCameraGalleryDialog();
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {

            Intent intent = new Intent(EditAddRecipeActivity.this, ImageActivity.class);
            intent.putExtra("imageUri", photoUri.toString());
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("currentPhotoPath", currentPhotoPath);
            startActivity(intent);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            imageUri = data.getData();

            Intent intent = new Intent(EditAddRecipeActivity.this, ImageActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            intent.putExtra("requestCode", requestCode);

            startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra(EditTextActivity.INTENT_TEXT);
                //todo
//                ((EditText)findViewById(R.id.ProductsEditText)).setText(text);
            }
        } else if (requestCode == ADD_PRODUCT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                long idProduct = data.getLongExtra(ProductsListActivity.INTENT_PRODUCT_ID, 0);
                boolean alreadyAdded = false;
                for (RecipeProduct recipeProduct : recipeProductList) {
                    if (recipeProduct.idProduct == idProduct) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle(R.string.product_already_added)
                                .setPositiveButton(R.string.ok, null)
                                .create();
                        alertDialog.show();
                        alreadyAdded = true;
                        break;
                    }
                }

                if (!alreadyAdded) {
                    Product product = AppDatabase.getInstance(this).productsDao().getByIdProduct(idProduct);
                    RecipeProduct recipeProduct = new RecipeProduct();
                    recipeProduct.idRecipe = recipe.idRecipe;
                    recipeProduct.idProduct = idProduct;
                    recipeProduct.productName = product.name;
                    recipeProductList.add(recipeProduct);
                }
            }

            refreshProductsList();
        }
    }

    private void refreshProductsList() {
        ProductsDao productsDao = AppDatabase.getInstance(this).productsDao();

        List<RecipeProduct> toRemove = new ArrayList<>();
        for (RecipeProduct recipeProduct : recipeProductList) {
            Product product = productsDao.getByIdProduct(recipeProduct.idProduct);
            if (product == null)
                toRemove.add(recipeProduct);
            else
                recipeProduct.productName = product.name;
        }

        for (RecipeProduct recipeProduct : toRemove) {
            recipeProductList.remove(recipeProduct);
        }

        recycler.setAdapter(new RecipeProductsAdapter(this, recipeProductList, unitList, this));
    }

    @Override
    public void onDeleteRecipeProduct(RecipeProduct recipeProduct) {
        recipeProductList.remove(recipeProduct);
        refreshProductsList();
    }

    public void createChooseCameraGalleryDialog(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View chooseCameraGalleryPopupView = getLayoutInflater().inflate(R.layout.camera_gallery_pop_up, null);

        galleryBtn = chooseCameraGalleryPopupView.findViewById(R.id.buttonGallery);
        cameraBtn = chooseCameraGalleryPopupView.findViewById(R.id.buttonCamera);

        dialogBuilder.setView(chooseCameraGalleryPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        cameraBtn.setOnClickListener(v -> {
            openCamera();
        });

        galleryBtn.setOnClickListener(v -> {
            openGallery();
        });
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
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
                photoUri = FileProvider.getUriForFile(EditAddRecipeActivity.this, "pl.cookbook.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
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
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}
