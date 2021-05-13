package pl.cookbook.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import pl.cookbook.R;

public class EditAddRecipeActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private static final int IMAGE_ACTIVITY_REQUEST_CODE = 2;
    Button editImageBtn;
    Button editProductsBtn;
    Button editRecipeBtn;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_recipe);

        buildActivity();
    }

    private void buildActivity() {

        editImageBtn = findViewById(R.id.editImageBtn);
        editImageBtn.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        });
        editProductsBtn = findViewById(R.id.editProductsBtn);
        editProductsBtn.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        });

        editRecipeBtn = findViewById(R.id.editRecipeBtn);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
      /*  if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {

            Intent intent = new Intent(EditAddRecipeActivity.this, ImageActivity.class);
            intent.putExtra("imageUri", photoUri.toString());
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("currentPhotoPath", currentPhotoPath);
            startActivity(intent);
        }*/
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

}
