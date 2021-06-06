package pl.cookbook.ui;

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
import android.widget.EditText;
import android.widget.ImageView;

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
    
    public static final String INTENT_ID_RECIPE = "idRecipe";
    public static final String IMAGE_URI = "imageUri";
    public static final int PICK_IMAGE = 1;
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int ADD_PRODUCT_REQUEST_CODE = 3;
    public static final int CAMERA_IMAGE = 4;
    public int ADD_RECIPE_PHOTO = 0;

    ImageView recipePhotoImageView;
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
        return intent;
    }

//    public static Intent createEditAddRecipeActivityIntentWithImageUri(final Context context, final long idRecipe, String imageUri){
//        Intent intent = new Intent(context, EditAddRecipeActivity.class);
//        intent.putExtra(INTENT_ID_RECIPE, idRecipe);
//        intent.putExtra(IMAGE_URI, imageUri);
//        return intent;
//    }

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cameraBtn;
    private Button galleryBtn;
    private MenuItem deleteRecipeMenuItem;

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

//        String photoPath = intent.getStringExtra(IMAGE_URI);
        recipePhotoImageView = findViewById(R.id.recipeEditAddImage);
//        if(photoPath != null) {
//            recipePhotoImageView.setImageURI(Uri.parse(photoPath));
//        }
        if(recipe.imageFileName != null){
            recipePhotoImageView.setImageURI(Uri.parse(recipe.imageFileName));
        }


        findViewById(R.id.btnSaveRecipe).setOnClickListener(v -> {
            recipe.name = titleEditText.getText().toString();
            recipe.description = executionEditText.getText().toString();
//            if(photoPath != null) {
//                recipe.imageFileName = photoPath;
//            }


            DbHelper.saveRecipe(this, recipe, recipeProductList);
            setResult(RESULT_OK);
            finish();

            startActivity(new Intent(EditAddRecipeActivity.this, MainActivity.class));
        });

        ProductsDao productsDao = appDatabase.productsDao();
        for (RecipeProduct recipeProduct : recipeProductList) {
            Product product = productsDao.getByIdProduct(recipeProduct.idProduct);
            recipeProduct.productName = product.name;
        }

        refreshProductsList();

        editImageBtn = findViewById(R.id.editImageBtn);
        editImageBtn.setOnClickListener(v -> {
            ADD_RECIPE_PHOTO = 1;
            createChooseCameraGalleryDialog();
        });
        editProductsBtn = findViewById(R.id.editProductsBtn);
        editProductsBtn.setOnClickListener(v -> startActivityForResult(new Intent(this, ProductsListActivity.class), ADD_PRODUCT_REQUEST_CODE));

        editRecipeBtn = findViewById(R.id.editRecipeBtn);
        editRecipeBtn.setOnClickListener(v -> createChooseCameraGalleryDialog());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_recipe_option_menu, menu);

        if(recipe != null) {
            MenuItem deleteRecipeMenuItem = menu.findItem(R.id.menu_delete_recipe);

            deleteRecipeMenuItem.setOnMenuItemClickListener(menuItem -> {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_deletion_recipe)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            AppDatabase.getInstance(this).recipesDao().deleteByIdRecipe(recipe.idRecipe);
                            startActivity(new Intent(EditAddRecipeActivity.this, MainActivity.class));
                        })
                        .setNegativeButton(R.string.no, null)
                        .create();
                alertDialog.show();

                return true;
            });
        }

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE && resultCode == RESULT_OK) {

            Intent intent = new Intent(EditAddRecipeActivity.this, ImageActivity.class);
            intent.putExtra(IMAGE_URI, photoUri.toString());
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("currentPhotoPath", currentPhotoPath);
            intent.putExtra("ADD_RECIPE_PHOTO", ADD_RECIPE_PHOTO);

            Intent intentThis = getIntent();
            long idRecipe = intentThis.getLongExtra(INTENT_ID_RECIPE, 0);
            intent.putExtra(INTENT_ID_RECIPE, idRecipe);

            startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            imageUri = data.getData();

            Intent intent = new Intent(EditAddRecipeActivity.this, ImageActivity.class);
            intent.putExtra(IMAGE_URI, imageUri.toString());
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("ADD_RECIPE_PHOTO", ADD_RECIPE_PHOTO);

            Intent intentThis = getIntent();
            long idRecipe = intentThis.getLongExtra(INTENT_ID_RECIPE, 0);
            intent.putExtra(INTENT_ID_RECIPE, idRecipe);

            startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String imageUriStr = data.getStringExtra("imageUri");
                    if (imageUriStr != null) {
                        recipe.imageFileName = imageUriStr;
                        recipePhotoImageView.setImageURI(Uri.parse(recipe.imageFileName));
                    }
                }
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
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}
