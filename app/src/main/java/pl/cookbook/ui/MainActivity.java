package pl.cookbook.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import pl.cookbook.R;
import pl.cookbook.database.AppDatabase;
import pl.cookbook.database.entities.Recipe;
import pl.cookbook.ui.adapters.RecipesAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase database = AppDatabase.getInstance(this);
        List<Recipe> recipesList = database.recipesDao().getAll();

        RecipesAdapter recipesAdapter = new RecipesAdapter(recipesList);

        RecyclerView recycler = findViewById(R.id.recipesRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(recipesAdapter);

        findViewById(R.id.btnAddRecipe).setOnClickListener(v -> {
            //todo
        });
    }
}