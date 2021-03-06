package pl.cookbook.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import pl.cookbook.R;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static pl.cookbook.ui.EditAddRecipeActivity.CAMERA_IMAGE;
import static pl.cookbook.ui.EditAddRecipeActivity.IMAGE_URI;
import static pl.cookbook.ui.EditAddRecipeActivity.INTENT_ID_RECIPE;
import static pl.cookbook.ui.MainActivity.EDIT_ADD_RECIPE_REQUEST_CODE;


public class ImageActivity extends AppCompatActivity {

    private static final int EDIT_TEXT_ACTIVITY_REQUEST_CODE = 1;
    public static final String ADD_RECIPE_PHOTO = "ADD_RECIPE_PHOTO";


    Button imageCropperBtn;
    Button findTextBtn;
    Button addRecipePhoto;
    ImageView photoImageView;

    Intent intent;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
       // FirebaseApp.initializeApp(getApplicationContext());
        intent = getIntent();

        buildImageView();
        manageImageCropperButton();
        manageImage();

        addRecipePhoto = findViewById(R.id.addRecipePhoto_button);
        findTextBtn = findViewById(R.id.findText_button);

        if(intent.getIntExtra(ADD_RECIPE_PHOTO, 0) == 1)
            manageAddRecipePhotoButton();
        else
            manageFindTextButton();
    }

    private void buildImageView() {
        imageCropperBtn = findViewById(R.id.imageCropper_button);
        photoImageView = findViewById(R.id.camera_image);

//        if(intent.getIntExtra(ADD_RECIPE_PHOTO, 0) == 1)
//            manageAddRecipePhotoButton();
//        else
//            manageFindTextButton();
    }


    private void manageFindTextButton() {
        addRecipePhoto.setVisibility(View.GONE);
        findTextBtn.setVisibility(View.VISIBLE);
        findTextBtn.setOnClickListener(v -> findTextOnImage());
    }

    private void manageAddRecipePhotoButton(){
        addRecipePhoto.setVisibility(View.VISIBLE);
        findTextBtn.setVisibility(View.GONE);
        addRecipePhoto.setOnClickListener(v -> {
            /*String imageUriString = intent.getStringExtra("imageUri");
            Intent intent1 = new Intent(ImageActivity.this, EditAddRecipeActivity.class);
            intent1.putExtra("imageUri", imageUriString);
            startActivity(intent1);*/
            Intent intent = new Intent();
            intent.putExtra("imageUri", imageUri.toString());
            setResult(RESULT_OK, intent);
            finish();

//            long idRecipe = intent.getLongExtra(INTENT_ID_RECIPE, 0);
//            startActivityForResult(EditAddRecipeActivity.createEditAddRecipeActivityIntentWithImageUri(this, idRecipe, imageUri.toString()), EDIT_ADD_RECIPE_REQUEST_CODE);

        });
    }


    private void manageImage() {

        int requestCode = intent.getIntExtra("requestCode", 0);

        if (requestCode == CAMERA_IMAGE) {
            String currentPhotoPath = intent.getStringExtra("currentPhotoPath");
            imageUri =  Uri.parse(intent.getStringExtra(IMAGE_URI));
            Bitmap rotatedBitmap = createFinalImage(currentPhotoPath);
            photoImageView.setImageBitmap(rotatedBitmap);
        }


        if (requestCode == EditAddRecipeActivity.PICK_IMAGE){
            imageUri = Uri.parse(intent.getStringExtra(IMAGE_URI));
            photoImageView.setImageURI(imageUri);
        }
    }


    private void manageImageCropperButton() {

        imageCropperBtn.setOnClickListener(v -> CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(ImageActivity.this));

    }


    private void findTextOnImage(){

        BitmapDrawable drawable = (BitmapDrawable)photoImageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer textRecognizer;
        if(isOnline()){
            textRecognizer = FirebaseVision.getInstance()
                    .getCloudTextRecognizer();
            Log.i("\nNet", "\nConnected");
        }else{
            textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            Log.i("\nNet", "\nnot Connected");
        }

        Log.i("\nJestem", "\ntuuuuuuuuuuuuuuuu");

        textRecognizer.processImage(image)
                .addOnSuccessListener(result -> {
                    Log.i("\nJestem w onSuccess", "\njestem\n");
                    String resultText = result.getText();
                    Log.i("\nOCR", resultText);
                    
                    startActivityForResult(EditTextActivity.createEditTextActivityIntent(this, R.string.edit_text_correct_typo, resultText), EDIT_TEXT_ACTIVITY_REQUEST_CODE);
          
                })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            e.printStackTrace();
                            Log.i("error OCR", "OCR error");
                        });
        Log.i("\nPo", "\nfunkcjach");
    }



    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                photoImageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                try {
                    throw new Exception(result.getError());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == EDIT_TEXT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }


    private Bitmap createFinalImage(String photoPath){

        Bitmap finalBitmap = BitmapFactory.decodeFile(photoPath);
        Matrix rotationMatrix = new Matrix();

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }
            rotationMatrix.setRotate(degree);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Bitmap.createBitmap(finalBitmap,0,0,finalBitmap.getWidth(),finalBitmap.getHeight(),rotationMatrix,true);
    }

}
