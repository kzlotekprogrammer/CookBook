package pl.cookbook.ui;

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
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.cookbook.R;

public class EditAddRecipeActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;
    public static final int CAMERA_IMAGE = 3;

    Button editImageBtn;
    Button editProductsBtn;
    Button editRecipeBtn;
    Uri imageUri;

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

        editImageBtn = findViewById(R.id.editImageBtn);
        editImageBtn.setOnClickListener(v -> {
            createChooseCameraGalleryDialog();
        });
        editProductsBtn = findViewById(R.id.editProductsBtn);
        editProductsBtn.setOnClickListener(v -> {
            createChooseCameraGalleryDialog();
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

            //todo zweryfikować zmianę
//            startActivity(intent);
            startActivityForResult(intent, IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra(EditTextActivity.INTENT_TEXT);
                ((EditText)findViewById(R.id.ProductsEditText)).setText(text);
            }
        }
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
