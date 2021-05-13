package pl.cookbook.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import pl.cookbook.R;

public class EditTextActivity extends AppCompatActivity {

    public static String INTENT_TEXT = "text";

    public static Intent createEditTextActivityIntent(final Context context, final String text) {
        Intent intent = new Intent(context, EditTextActivity.class);
        intent.putExtra(INTENT_TEXT, text);
        return  intent;
    }

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        editText.setText(intent.getStringExtra(INTENT_TEXT));

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent output = new Intent();
            output.putExtra(INTENT_TEXT, editText.getText().toString());
            setResult(RESULT_OK, output);
            finish();
        });

        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        //prevent closing activity
    }
}