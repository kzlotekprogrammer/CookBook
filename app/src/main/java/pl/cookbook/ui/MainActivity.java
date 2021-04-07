package pl.cookbook.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.dao.ProductsDao;
import pl.cookbook.database.entities.Product;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}